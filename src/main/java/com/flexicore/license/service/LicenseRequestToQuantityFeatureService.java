package com.flexicore.license.service;


import com.flexicore.license.data.LicenseRequestToQuantityFeatureRepository;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.license.model.LicenseRequestToQuantityFeature;
import com.flexicore.license.request.LicenseRequestToQuantityFeatureCreate;
import com.flexicore.license.request.LicenseRequestToQuantityFeatureFiltering;
import com.flexicore.license.request.LicenseRequestToQuantityFeatureUpdate;
import com.flexicore.security.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.pf4j.Extension;
import com.flexicore.annotations.plugins.PluginInfo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;


@PluginInfo(version=1)
@Extension
@Component
public class LicenseRequestToQuantityFeatureService implements ServicePlugin {


    @Autowired
    @PluginInfo(version = 1)
    private LicenseRequestToQuantityFeatureRepository repository;

    @Autowired
    @PluginInfo(version = 1)
    private LicenseRequestToFeatureService licenseRequestToFeatureService;

   private Logger logger = Logger.getLogger(getClass().getCanonicalName());


    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, List<String> batchString, SecurityContext securityContext) {
        return repository.getByIdOrNull(id, c, batchString, securityContext);
    }


    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
        return repository.listByIds(c, ids, securityContext);
    }

    @Autowired
    private ApplicationEventPublisher licenseRequestUpdateEventEvent;

    public LicenseRequestToQuantityFeature createLicenseRequestToQuantityFeature(LicenseRequestToQuantityFeatureCreate pluginCreationContainer, SecurityContext securityContext) {
        LicenseRequestToQuantityFeature licenseRequestToQuantityFeature = createLicenseRequestToQuantityFeatureNoMerge(pluginCreationContainer, securityContext);
        repository.merge(licenseRequestToQuantityFeature);
        licenseRequestUpdateEventEvent.publishEvent(new LicenseRequestUpdateEvent().setLicenseRequest(licenseRequestToQuantityFeature.getLicenseRequest()).setSecurityContext(securityContext));
        return licenseRequestToQuantityFeature;


    }

    public LicenseRequestToQuantityFeature createLicenseRequestToQuantityFeatureNoMerge(LicenseRequestToQuantityFeatureCreate licenseRequestToQuantityFeatureCreate, SecurityContext securityContext) {
        LicenseRequestToQuantityFeature licenseRequestToQuantityFeature = new LicenseRequestToQuantityFeature(licenseRequestToQuantityFeatureCreate.getName(), securityContext);
        updateLicenseRequestToQuantityFeatureNoMerge(licenseRequestToQuantityFeature, licenseRequestToQuantityFeatureCreate);
        return licenseRequestToQuantityFeature;
    }

    private boolean updateLicenseRequestToQuantityFeatureNoMerge(LicenseRequestToQuantityFeature licenseRequestToQuantityFeature, LicenseRequestToQuantityFeatureCreate licenseRequestToQuantityFeatureCreate) {
        boolean update = licenseRequestToFeatureService.updateLicenseRequestToFeatureNoMerge(licenseRequestToQuantityFeature, licenseRequestToQuantityFeatureCreate);
        if (licenseRequestToQuantityFeatureCreate.getQuantityLimit() != null && !licenseRequestToQuantityFeatureCreate.getQuantityLimit().equals(licenseRequestToQuantityFeature.getQuantityLimit())) {
            licenseRequestToQuantityFeature.setQuantityLimit(licenseRequestToQuantityFeatureCreate.getQuantityLimit());
            update = true;
        }

        return update;
    }


    public LicenseRequestToQuantityFeature updateLicenseRequestToQuantityFeature(LicenseRequestToQuantityFeatureUpdate licenseRequestToQuantityFeatureUpdate, SecurityContext securityContext) {
        LicenseRequestToQuantityFeature licenseRequestToQuantityFeature = licenseRequestToQuantityFeatureUpdate.getLicenseRequestToQuantityFeature();
        if (updateLicenseRequestToQuantityFeatureNoMerge(licenseRequestToQuantityFeature, licenseRequestToQuantityFeatureUpdate)) {
            repository.merge(licenseRequestToQuantityFeature);
            licenseRequestUpdateEventEvent.publishEvent(new LicenseRequestUpdateEvent().setLicenseRequest(licenseRequestToQuantityFeature.getLicenseRequest()).setSecurityContext(securityContext));

        }
        return licenseRequestToQuantityFeature;
    }

    public List<LicenseRequestToQuantityFeature> listAllLicenseRequestToQuantityFeatures(LicenseRequestToQuantityFeatureFiltering licenseRequestToQuantityFeatureFiltering, SecurityContext securityContext) {
        return repository.listAllLicenseRequestToQuantityFeatures(licenseRequestToQuantityFeatureFiltering, securityContext);
    }

    public void validate(LicenseRequestToQuantityFeatureCreate licenseRequestToQuantityFeatureCreate, SecurityContext securityContext) {
        licenseRequestToFeatureService.validate(licenseRequestToQuantityFeatureCreate, securityContext);


    }

    public void validate(LicenseRequestToQuantityFeatureFiltering licenseRequestToQuantityFeatureFiltering, SecurityContext securityContext) {
        licenseRequestToFeatureService.validate(licenseRequestToQuantityFeatureFiltering, securityContext);
    }

    public PaginationResponse<LicenseRequestToQuantityFeature> getAllLicenseRequestToQuantityFeatures(LicenseRequestToQuantityFeatureFiltering licenseRequestToQuantityFeatureFiltering, SecurityContext securityContext) {
        List<LicenseRequestToQuantityFeature> list = listAllLicenseRequestToQuantityFeatures(licenseRequestToQuantityFeatureFiltering, securityContext);
        long count = repository.countAllLicenseRequestToQuantityFeatures(licenseRequestToQuantityFeatureFiltering, securityContext);
        return new PaginationResponse<>(list, licenseRequestToQuantityFeatureFiltering, count);
    }


}