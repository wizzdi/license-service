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
import com.flexicore.license.model.LicensingProduct;
import com.flexicore.license.request.LicensingProductFiltering;
import com.flexicore.license.service.LicensingProductService;
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


@Path("/licensingProducts")
@RequestScoped
@Component
@OperationsInside
@Protected
@Extension
@PluginInfo(version=1)
@Tag(name = "License")


public class LicensingProductRESTService implements RestServicePlugin {


    @Autowired
    @PluginInfo(version = 1)
    private LicensingProductService licensingProductService;


    @POST
    @Path("/getAllLicensingProducts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @IOperation(access = Access.allow, Name = "getAllLicensingProducts", Description = "lists LicensingProducts", relatedClazzes = {LicensingProduct.class})
    public PaginationResponse<LicensingProduct> getAllLicensingProducts(@HeaderParam("authenticationkey") String authenticationkey
            , LicensingProductFiltering licensingProductFiltering, @Context SecurityContext securityContext) {
        licensingProductService.validate(licensingProductFiltering, securityContext);
        return licensingProductService.getAllLicensingProducts(licensingProductFiltering, securityContext);

    }


}
