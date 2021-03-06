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
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.license.model.LicenseRequestToProduct;
import com.flexicore.license.request.LicenseRequestToProductCreate;
import com.flexicore.license.request.LicenseRequestToProductFiltering;
import com.flexicore.license.request.LicenseRequestToProductUpdate;
import com.flexicore.license.service.LicenseRequestToProductService;
import com.flexicore.security.SecurityContext;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;


@Path("/licenseRequestToProducts")
@RequestScoped
@Component
@OperationsInside
@Protected
@Extension
@PluginInfo(version=1)
@Tag(name = "License")


public class LicenseRequestToProductRESTService implements RestServicePlugin {


    @Autowired
    @PluginInfo(version = 1)
    private LicenseRequestToProductService licenseRequestToProductService;


    @POST
    @Path("/getAllLicenseRequestToProducts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @IOperation(access = Access.allow, Name = "getAllLicenseRequestToProducts", Description = "lists LicenseRequestToProducts", relatedClazzes = {LicenseRequestToProduct.class})
    public PaginationResponse<LicenseRequestToProduct> getAllLicenseRequestToProducts(@HeaderParam("authenticationkey") String authenticationkey
            , LicenseRequestToProductFiltering licenseRequestToProductFiltering, @Context SecurityContext securityContext) {
        licenseRequestToProductService.validate(licenseRequestToProductFiltering, securityContext);
        return licenseRequestToProductService.getAllLicenseRequestToProducts(licenseRequestToProductFiltering, securityContext);

    }

  

    @POST
    @Path("/createLicenseRequestToProduct")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @IOperation(access = Access.allow, Name = "Creates LicenseRequestToProduct", Description = "Creates LicenseRequestToProduct", relatedClazzes = {LicenseRequestToProduct.class})
    public LicenseRequestToProduct createLicenseRequestToProduct(@HeaderParam("authenticationkey") String authenticationkey
            , LicenseRequestToProductCreate licenseRequestToProductCreate, @Context SecurityContext securityContext) {
        licenseRequestToProductService.validate(licenseRequestToProductCreate, securityContext);
        return licenseRequestToProductService.createLicenseRequestToProduct(licenseRequestToProductCreate, securityContext);

    }

    @PUT
    @Path("/updateLicenseRequestToProduct")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @IOperation(access = Access.allow, Name = "Updates LicenseRequestToProduct", Description = "Updates LicenseRequestToProduct", relatedClazzes = {LicenseRequestToProduct.class})
    public LicenseRequestToProduct updateLicenseRequestToProduct(@HeaderParam("authenticationkey") String authenticationkey
            , LicenseRequestToProductUpdate licenseRequestToProductUpdate, @Context SecurityContext securityContext) {
        String id=licenseRequestToProductUpdate.getId();
        LicenseRequestToProduct licenseRequestToProduct=id!=null?licenseRequestToProductService.getByIdOrNull(id,LicenseRequestToProduct.class,null,securityContext):null;
        if(licenseRequestToProduct==null){
            throw new BadRequestException("No LicenseRequestToFeature with id "+id);
        }
        licenseRequestToProductUpdate.setLicenseRequestToProduct(licenseRequestToProduct);
        licenseRequestToProductService.validate(licenseRequestToProductUpdate, securityContext);
        return licenseRequestToProductService.updateLicenseRequestToProduct(licenseRequestToProductUpdate, securityContext);

    }

}
