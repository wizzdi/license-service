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
import com.flexicore.license.model.LicenseRequest;
import com.flexicore.license.request.LicenseRequestCreate;
import com.flexicore.license.request.LicenseRequestFiltering;
import com.flexicore.license.request.LicenseRequestUpdate;
import com.flexicore.security.SecurityContextBase;
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

@Tag(name = "License")


public class LicenseRequestRESTService implements RestServicePlugin {


    @Autowired

    private LicenseRequestService licenseRequestService;


    @POST
    @Path("/getAllLicenseRequests")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @IOperation(access = Access.allow, Name = "getAllLicenseRequests", Description = "lists LicenseRequests", relatedClazzes = {LicenseRequest.class})
    public PaginationResponse<LicenseRequest> getAllLicenseRequests(@HeaderParam("authenticationkey") String authenticationkey
            , LicenseRequestFiltering licenseRequestFiltering, @Context SecurityContextBase securityContextBase) {
        licenseRequestService.validate(licenseRequestFiltering, securityContextBase);
        return licenseRequestService.getAllLicenseRequests(licenseRequestFiltering, securityContextBase);

    }

  

    @POST
    @Path("/createLicenseRequest")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @IOperation(access = Access.allow, Name = "Creates LicenseRequest", Description = "Creates LicenseRequest", relatedClazzes = {LicenseRequest.class})
    public LicenseRequest createLicenseRequest(@HeaderParam("authenticationkey") String authenticationkey
            , LicenseRequestCreate licenseRequestCreate, @Context SecurityContextBase securityContextBase) {
        licenseRequestService.validate(licenseRequestCreate, securityContextBase);
        licenseRequestService.validateCreate(licenseRequestCreate);
        return licenseRequestService.createLicenseRequest(licenseRequestCreate, securityContextBase);

    }

    @PUT
    @Path("/updateLicenseRequest")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @IOperation(access = Access.allow, Name = "Updates LicenseRequest", Description = "Updates LicenseRequest", relatedClazzes = {LicenseRequest.class})
    public LicenseRequest updateLicenseRequest(@HeaderParam("authenticationkey") String authenticationkey
            , LicenseRequestUpdate licenseRequestUpdate, @Context SecurityContextBase securityContextBase) {
        String id=licenseRequestUpdate.getId();
        LicenseRequest licenseRequest=id!=null?licenseRequestService.getByIdOrNull(id,LicenseRequest.class,null,securityContextBase):null;
        if(licenseRequest==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No License request with id "+id);
        }
        licenseRequestUpdate.setLicenseRequest(licenseRequest);
        licenseRequestService.validate(licenseRequestUpdate, securityContextBase);
        return licenseRequestService.updateLicenseRequest(licenseRequestUpdate, securityContextBase);

    }

}
