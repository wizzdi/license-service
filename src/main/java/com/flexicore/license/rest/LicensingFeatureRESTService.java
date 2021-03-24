/*******************************************************************************
 *  Copyright (C) FlexiCore, Inc - All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited
 *  Proprietary and confidential
 *  Written by Avishay Ben Natan And Asaf Ben Natan, October 2015
 ******************************************************************************/
package com.flexicore.license.rest;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.IOperation.Access;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.Protected;

import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.license.model.LicensingFeature;
import com.flexicore.license.request.LicensingFeatureFiltering;
import com.flexicore.license.service.LicensingFeatureService;
import com.flexicore.security.SecurityContextBase;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;


@Path("/licensingFeatures")
@RequestScoped
@Component
@OperationsInside
@Protected
@Extension

@Tag(name = "License")


public class LicensingFeatureRESTService implements RestServicePlugin {


    @Autowired

    private LicensingFeatureService licensingFeatureService;

    @POST
    @Path("/getAllLicensingFeatures")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @IOperation(access = Access.allow, Name = "getAllLicensingFeatures", Description = "lists LicensingFeatures", relatedClazzes = {LicensingFeature.class})
    public PaginationResponse<LicensingFeature> getAllLicensingFeatures(@HeaderParam("authenticationkey") String authenticationkey
            , LicensingFeatureFiltering licensingFeatureFiltering, @Context SecurityContextBase securityContextBase) {
        licensingFeatureService.validate(licensingFeatureFiltering, securityContextBase);
        return licensingFeatureService.getAllLicensingFeatures(licensingFeatureFiltering, securityContextBase);

    }


}
