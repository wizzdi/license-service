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
import com.flexicore.license.model.LicenseRequest;
import com.flexicore.license.request.LicenseRequestCreate;
import com.flexicore.license.request.LicenseRequestFiltering;
import com.flexicore.license.request.LicenseRequestUpdate;
import com.flexicore.security.SecurityContext;
import com.flexicore.license.service.LicenseRequestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;


@Path("/licenseRequests")
@RequestScoped
@Component
@OperationsInside
@Protected
@Extension
@PluginInfo(version=1)
@Tag(name = "License")


public class LicenseRequestRESTService implements RestServicePlugin {


    @Autowired
    @PluginInfo(version = 1)
    private LicenseRequestService licenseRequestService;


    @POST
    @Path("/getAllLicenseRequests")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @IOperation(access = Access.allow, Name = "getAllLicenseRequests", Description = "lists LicenseRequests", relatedClazzes = {LicenseRequest.class})
    public PaginationResponse<LicenseRequest> getAllLicenseRequests(@HeaderParam("authenticationkey") String authenticationkey
            , LicenseRequestFiltering licenseRequestFiltering, @Context SecurityContext securityContext) {
        licenseRequestService.validate(licenseRequestFiltering, securityContext);
        return licenseRequestService.getAllLicenseRequests(licenseRequestFiltering, securityContext);

    }

  

    @POST
    @Path("/createLicenseRequest")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @IOperation(access = Access.allow, Name = "Creates LicenseRequest", Description = "Creates LicenseRequest", relatedClazzes = {LicenseRequest.class})
    public LicenseRequest createLicenseRequest(@HeaderParam("authenticationkey") String authenticationkey
            , LicenseRequestCreate licenseRequestCreate, @Context SecurityContext securityContext) {
        licenseRequestService.validate(licenseRequestCreate, securityContext);
        licenseRequestService.validateCreate(licenseRequestCreate);
        return licenseRequestService.createLicenseRequest(licenseRequestCreate, securityContext);

    }

    @PUT
    @Path("/updateLicenseRequest")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @IOperation(access = Access.allow, Name = "Updates LicenseRequest", Description = "Updates LicenseRequest", relatedClazzes = {LicenseRequest.class})
    public LicenseRequest updateLicenseRequest(@HeaderParam("authenticationkey") String authenticationkey
            , LicenseRequestUpdate licenseRequestUpdate, @Context SecurityContext securityContext) {
        String id=licenseRequestUpdate.getId();
        LicenseRequest licenseRequest=id!=null?licenseRequestService.getByIdOrNull(id,LicenseRequest.class,null,securityContext):null;
        if(licenseRequest==null){
            throw new BadRequestException("No License request with id "+id);
        }
        licenseRequestUpdate.setLicenseRequest(licenseRequest);
        licenseRequestService.validate(licenseRequestUpdate, securityContext);
        return licenseRequestService.updateLicenseRequest(licenseRequestUpdate, securityContext);

    }

}
