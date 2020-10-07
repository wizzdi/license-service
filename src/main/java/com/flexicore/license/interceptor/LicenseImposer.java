/*******************************************************************************
 *  Copyright (C) FlexiCore, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Avishay Ben Natan And Asaf Ben Natan, October 2015
 ******************************************************************************/
package com.flexicore.license.interceptor;

import com.flexicore.annotations.HasFeature;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.data.jsoncontainers.OperationInfo;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.license.model.LicensingFeature;
import com.flexicore.license.request.LicensingFeatureFiltering;
import com.flexicore.license.service.LicenseRequestService;
import com.flexicore.license.service.LicensingFeatureService;
import com.flexicore.model.Tenant;
import com.flexicore.model.User;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.SecurityService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclarePrecedence;
import org.aspectj.lang.reflect.MethodSignature;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.CloseReason;
import javax.websocket.Session;
import javax.ws.rs.ForbiddenException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * uses Aspect Oriented Programming (through JavaEE support) to enforce security
 * Access granularity is specified in a separate UML diagram
 *
 * @author Avishay Ben Natan
 */


@Aspect
@Component
@Extension
@DeclarePrecedence("SecurityImposer,LicenseImposer")
public class LicenseImposer implements ServicePlugin {

    private static final Logger logger = LoggerFactory.getLogger(LicenseImposer.class);
    @Autowired
    private LicenseRequestService licenseRequestService;
    @Autowired
    private LicensingFeatureService licensingFeatureService;

    @Autowired
    private SecurityService securityService;

    @Around("execution(@com.flexicore.annotations.Protected * *(..)) || within(@(@com.flexicore.annotations.Protected *) *)|| within(@com.flexicore.annotations.Protected *)")
    public Object transformReturn(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Session websocketSession;
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] parameters = joinPoint.getArgs();
        String methodName = method.getName();
        logger.info("Method is: " + methodName + " , on Thread " + Thread.currentThread().getName());
        websocketSession = getWebsocketSession(parameters);
        SecurityContext securityContext;
        if (websocketSession != null) {
            securityContext = (SecurityContext) websocketSession.getUserProperties().get("securityContext");
        } else {
            securityContext = (SecurityContext) parameters[parameters.length - 1];

        }

        User user = securityContext.getUser();
        List<Tenant> tenants = securityContext.getTenants();
        OperationInfo operationInfo = securityService.getIOperation(method);

        List<HasFeature> features = new ArrayList<>();
        OperationsInside operationsInside = method.getDeclaringClass().getAnnotation(OperationsInside.class);
        if (operationsInside != null) {
            features.addAll(Arrays.asList(operationsInside.features()));
        }

        if (operationInfo.getiOperation() != null && user != null && tenants != null) {
            if (operationInfo.getiOperation().noOtherLicenseRequired()) {
                features.clear();
            }
            features.addAll(Arrays.asList(operationInfo.getiOperation().features()));
            Set<String> canonicalNames = features.parallelStream().map(HasFeature::canonicalName).collect(Collectors.toSet());
            List<LicensingFeature> licensingFeatures = canonicalNames.isEmpty() ? new ArrayList<>() : licensingFeatureService.listAllLicensingFeatures(new LicensingFeatureFiltering().setCanonicalNames(canonicalNames), null);

            for (LicensingFeature feature : licensingFeatures) {
                if (!licenseRequestService.isFeatureLicensed(user, tenants, feature)) {
                    return denyLicense(feature, websocketSession);
                }
            }


        }


        return joinPoint.proceed(parameters);


    }

    private Session getWebsocketSession(Object[] parameters) {
        return parameters != null ? Stream.of(parameters).filter(f -> f instanceof Session).map(f -> (Session) f).findAny().orElse(null) : null;
    }


    private void closeWSIfNecessary(Session websocketSession, String reason) {
        if (websocketSession != null && websocketSession.isOpen()) {
            try {
                String id = websocketSession.getId();

                websocketSession.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, reason));
                logger.warn("Closed WS " + id + " for being unauthorized");
            } catch (Exception e) {
                logger.error("failed closing WS", e);
            }
        }
    }

    private Object denyLicense(LicensingFeature feature, Session websocketSession) {
        String s = "invalid Feature License for Feature: " + feature.getCanonicalName();
        closeWSIfNecessary(websocketSession, s);

        throw new ForbiddenException(s);
    }
}