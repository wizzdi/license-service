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
import com.flexicore.annotations.plugins.PluginInfo;
import org.pf4j.Extension;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.license.model.LicenseRequestToFeature;
import com.flexicore.license.request.LicenseRequestToFeatureCreate;
import com.flexicore.license.request.LicenseRequestToFeatureFiltering;
import com.flexicore.license.request.LicenseRequestToFeatureUpdate;
import com.flexicore.security.SecurityContext;
import com.flexicore.license.service.LicenseRequestToFeatureService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;


@Path("/licenseRequestToFeatures")
@RequestScoped
@Component
@OperationsInside
@Protected
@Extension
@PluginInfo(version=1)
@Tag(name = "License")


public class LicenseRequestToFeatureRESTService implements RestServicePlugin {


    @Autowired
    @PluginInfo(version = 1)
    private LicenseRequestToFeatureService licenseRequestToFeatureService;

    @POST
    @Path("/getAllLicenseRequestToFeatures")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @IOperation(access = Access.allow, Name = "getAllLicenseRequestToFeatures", Description = "lists LicenseRequestToFeatures", relatedClazzes = {LicenseRequestToFeature.class})
    public PaginationResponse<LicenseRequestToFeature> getAllLicenseRequestToFeatures(@HeaderParam("authenticationkey") String authenticationkey
            , LicenseRequestToFeatureFiltering licenseRequestToFeatureFiltering, @Context SecurityContext securityContext) {
        licenseRequestToFeatureService.validate(licenseRequestToFeatureFiltering, securityContext);
        return licenseRequestToFeatureService.getAllLicenseRequestToFeatures(licenseRequestToFeatureFiltering, securityContext);

    }

  

    @POST
    @Path("/createLicenseRequestToFeature")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @IOperation(access = Access.allow, Name = "Creates LicenseRequestToFeature", Description = "Creates LicenseRequestToFeature", relatedClazzes = {LicenseRequestToFeature.class})
    public LicenseRequestToFeature createLicenseRequestToFeature(@HeaderParam("authenticationkey") String authenticationkey
            , LicenseRequestToFeatureCreate licenseRequestToFeatureCreate, @Context SecurityContext securityContext) {
        licenseRequestToFeatureService.validate(licenseRequestToFeatureCreate, securityContext);
        return licenseRequestToFeatureService.createLicenseRequestToFeature(licenseRequestToFeatureCreate, securityContext);

    }

    @PUT
    @Path("/updateLicenseRequestToFeature")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @IOperation(access = Access.allow, Name = "Updates LicenseRequestToFeature", Description = "Updates LicenseRequestToFeature", relatedClazzes = {LicenseRequestToFeature.class})
    public LicenseRequestToFeature updateLicenseRequestToFeature(@HeaderParam("authenticationkey") String authenticationkey
            , LicenseRequestToFeatureUpdate licenseRequestToFeatureUpdate, @Context SecurityContext securityContext) {
        String id=licenseRequestToFeatureUpdate.getId();
        LicenseRequestToFeature licenseRequestToFeature=id!=null?licenseRequestToFeatureService.getByIdOrNull(id,LicenseRequestToFeature.class,null,securityContext):null;
        if(licenseRequestToFeature==null){
            throw new BadRequestException("No LicenseRequestToFeature with id "+id);
        }
        licenseRequestToFeatureUpdate.setLicenseRequestToFeature(licenseRequestToFeature);
        licenseRequestToFeatureService.validate(licenseRequestToFeatureUpdate, securityContext);
        return licenseRequestToFeatureService.updateLicenseRequestToFeature(licenseRequestToFeatureUpdate, securityContext);

    }

}
