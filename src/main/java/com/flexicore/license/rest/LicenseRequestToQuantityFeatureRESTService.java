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

import org.pf4j.Extension;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.license.model.LicenseRequestToQuantityFeature;
import com.flexicore.license.request.LicenseRequestToQuantityFeatureCreate;
import com.flexicore.license.request.LicenseRequestToQuantityFeatureFiltering;
import com.flexicore.license.request.LicenseRequestToQuantityFeatureUpdate;
import com.flexicore.security.SecurityContextBase;
import com.flexicore.license.service.LicenseRequestToQuantityFeatureService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;


@Path("/licenseRequestToQuantityFeatures")
@RequestScoped
@Component
@OperationsInside
@Protected
@Extension

@Tag(name = "License")


public class LicenseRequestToQuantityFeatureRESTService implements RestServicePlugin {


    @Autowired

    private LicenseRequestToQuantityFeatureService licenseRequestToQuantityFeatureService;


    @POST
    @Path("/getAllLicenseRequestToQuantityFeatures")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @IOperation(access = Access.allow, Name = "getAllLicenseRequestToQuantityFeatures", Description = "lists LicenseRequestToQuantityFeatures", relatedClazzes = {LicenseRequestToQuantityFeature.class})
    public PaginationResponse<LicenseRequestToQuantityFeature> getAllLicenseRequestToQuantityFeatures(@HeaderParam("authenticationkey") String authenticationkey
            , LicenseRequestToQuantityFeatureFiltering licenseRequestToQuantityFeatureFiltering, @Context SecurityContextBase securityContextBase) {
        licenseRequestToQuantityFeatureService.validate(licenseRequestToQuantityFeatureFiltering, securityContextBase);
        return licenseRequestToQuantityFeatureService.getAllLicenseRequestToQuantityFeatures(licenseRequestToQuantityFeatureFiltering, securityContextBase);

    }

  

    @POST
    @Path("/createLicenseRequestToQuantityFeature")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @IOperation(access = Access.allow, Name = "Creates LicenseRequestToQuantityFeature", Description = "Creates LicenseRequestToQuantityFeature", relatedClazzes = {LicenseRequestToQuantityFeature.class})
    public LicenseRequestToQuantityFeature createLicenseRequestToQuantityFeature(@HeaderParam("authenticationkey") String authenticationkey
            , LicenseRequestToQuantityFeatureCreate licenseRequestToQuantityFeatureCreate, @Context SecurityContextBase securityContextBase) {
        licenseRequestToQuantityFeatureService.validate(licenseRequestToQuantityFeatureCreate, securityContextBase);
        return licenseRequestToQuantityFeatureService.createLicenseRequestToQuantityFeature(licenseRequestToQuantityFeatureCreate, securityContextBase);

    }

    @PUT
    @Path("/updateLicenseRequestToQuantityFeature")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @IOperation(access = Access.allow, Name = "Updates LicenseRequestToQuantityFeature", Description = "Updates LicenseRequestToQuantityFeature", relatedClazzes = {LicenseRequestToQuantityFeature.class})
    public LicenseRequestToQuantityFeature updateLicenseRequestToQuantityFeature(@HeaderParam("authenticationkey") String authenticationkey
            , LicenseRequestToQuantityFeatureUpdate licenseRequestToQuantityFeatureUpdate, @Context SecurityContextBase securityContextBase) {
        String id=licenseRequestToQuantityFeatureUpdate.getId();
        LicenseRequestToQuantityFeature licenseRequestToQuantityFeature=id!=null?licenseRequestToQuantityFeatureService.getByIdOrNull(id,LicenseRequestToQuantityFeature.class,null,securityContextBase):null;
        if(licenseRequestToQuantityFeature==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No LicenseRequestToQuantityFeature with id "+id);
        }
        licenseRequestToQuantityFeatureUpdate.setLicenseRequestToQuantityFeature(licenseRequestToQuantityFeature);
        licenseRequestToQuantityFeatureService.validate(licenseRequestToQuantityFeatureUpdate, securityContextBase);
        return licenseRequestToQuantityFeatureService.updateLicenseRequestToQuantityFeature(licenseRequestToQuantityFeatureUpdate, securityContextBase);

    }

}
