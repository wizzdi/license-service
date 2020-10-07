package com.flexicore.license.service;


import com.flexicore.license.data.LicensingProductRepository;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.license.model.LicensingProduct;
import com.flexicore.license.request.LicensingProductCreate;
import com.flexicore.license.request.LicensingProductFiltering;
import com.flexicore.license.request.LicensingProductUpdate;
import com.flexicore.security.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.pf4j.Extension;
import com.flexicore.annotations.plugins.PluginInfo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;


@PluginInfo(version=1)
@Extension
@Component
public class LicensingProductService implements ServicePlugin {


    @Autowired
    @PluginInfo(version = 1)
    private LicensingProductRepository repository;

    @Autowired
    @PluginInfo(version = 1)
    private LicensingEntityService licensingEntityService;

   private Logger logger = Logger.getLogger(getClass().getCanonicalName());


    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, List<String> batchString, SecurityContext securityContext) {
        return repository.getByIdOrNull(id, c, batchString, securityContext);
    }


    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
        return repository.listByIds(c, ids, securityContext);
    }

    public LicensingProduct createLicensingProduct(LicensingProductCreate pluginCreationContainer, SecurityContext securityContext) {
        LicensingProduct licensingProduct = createLicensingProductNoMerge(pluginCreationContainer, securityContext);
        repository.merge(licensingProduct);
        return licensingProduct;


    }

    public LicensingProduct createLicensingProductNoMerge(LicensingProductCreate licensingProductCreate, SecurityContext securityContext) {
        LicensingProduct licensingProduct = new LicensingProduct(licensingProductCreate.getName(), securityContext);
        updateLicensingProductNoMerge(licensingProduct, licensingProductCreate);
        return licensingProduct;
    }

    public boolean updateLicensingProductNoMerge(LicensingProduct licensingProduct, LicensingProductCreate licensingProductCreate) {
        boolean update = licensingEntityService.updateLicensingEntityNoMerge(licensingProduct, licensingProductCreate);

        return update;
    }


    public LicensingProduct updateLicensingProduct(LicensingProductUpdate licensingProductUpdate, SecurityContext securityContext) {
        LicensingProduct licensingProduct = licensingProductUpdate.getLicensingProduct();
        if (updateLicensingProductNoMerge(licensingProduct, licensingProductUpdate)) {
            repository.merge(licensingProduct);
        }
        return licensingProduct;
    }

    public List<LicensingProduct> listAllLicensingProducts(LicensingProductFiltering licensingProductFiltering, SecurityContext securityContext) {
        return repository.listAllLicensingProducts(licensingProductFiltering, securityContext);
    }

    public void validate(LicensingProductCreate licensingProductCreate, SecurityContext securityContext) {
        licensingEntityService.validate(licensingProductCreate, securityContext);

    }

    public void validate(LicensingProductFiltering licensingProductFiltering, SecurityContext securityContext) {
        licensingEntityService.validate(licensingProductFiltering,securityContext);
    }

    public PaginationResponse<LicensingProduct> getAllLicensingProducts(LicensingProductFiltering licensingProductFiltering, SecurityContext securityContext) {
        List<LicensingProduct> list = listAllLicensingProducts(licensingProductFiltering, securityContext);
        long count = repository.countAllLicensingProducts(licensingProductFiltering, securityContext);
        return new PaginationResponse<>(list, licensingProductFiltering, count);
    }


}