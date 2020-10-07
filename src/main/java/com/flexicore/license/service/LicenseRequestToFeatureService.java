package com.flexicore.license.service;


import com.flexicore.license.data.LicenseRequestToFeatureRepository;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.license.model.LicenseRequestToFeature;
import com.flexicore.license.model.LicensingFeature;
import com.flexicore.license.request.LicenseRequestToFeatureCreate;
import com.flexicore.license.request.LicenseRequestToFeatureFiltering;
import com.flexicore.license.request.LicenseRequestToFeatureUpdate;
import com.flexicore.security.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.pf4j.Extension;
import com.flexicore.annotations.plugins.PluginInfo;
import org.springframework.stereotype.Component;

import javax.ws.rs.BadRequestException;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;


@PluginInfo(version=1)
@Extension
@Component
public class LicenseRequestToFeatureService implements ServicePlugin {


    @Autowired
    @PluginInfo(version = 1)
    private LicenseRequestToFeatureRepository repository;

    @Autowired
    @PluginInfo(version = 1)
    private LicenseRequestToEntityService licenseRequestToEntityService;

   private Logger logger = Logger.getLogger(getClass().getCanonicalName());


    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, List<String> batchString, SecurityContext securityContext) {
        return repository.getByIdOrNull(id, c, batchString, securityContext);
    }


    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
        return repository.listByIds(c, ids, securityContext);
    }

    @Autowired
    private ApplicationEventPublisher licenseRequestUpdateEventEvent;

    public LicenseRequestToFeature createLicenseRequestToFeature(LicenseRequestToFeatureCreate pluginCreationContainer, SecurityContext securityContext) {
        LicenseRequestToFeature licenseRequestToFeature = createLicenseRequestToFeatureNoMerge(pluginCreationContainer, securityContext);
        repository.merge(licenseRequestToFeature);
        licenseRequestUpdateEventEvent.publishEvent(new LicenseRequestUpdateEvent().setLicenseRequest(licenseRequestToFeature.getLicenseRequest()).setSecurityContext(securityContext));
        return licenseRequestToFeature;


    }

    public LicenseRequestToFeature createLicenseRequestToFeatureNoMerge(LicenseRequestToFeatureCreate licenseRequestToFeatureCreate, SecurityContext securityContext) {
        LicenseRequestToFeature licenseRequestToFeature = new LicenseRequestToFeature(licenseRequestToFeatureCreate.getName(), securityContext);
        updateLicenseRequestToFeatureNoMerge(licenseRequestToFeature, licenseRequestToFeatureCreate);
        return licenseRequestToFeature;
    }

    public boolean updateLicenseRequestToFeatureNoMerge(LicenseRequestToFeature licenseRequestToFeature, LicenseRequestToFeatureCreate licenseRequestToFeatureCreate) {
        boolean update = licenseRequestToEntityService.updateLicenseRequestToEntityNoMerge(licenseRequestToFeature, licenseRequestToFeatureCreate);
        if(licenseRequestToFeatureCreate.getLicensingFeature()!=null && (licenseRequestToFeature.getLicensingEntity()==null || !licenseRequestToFeatureCreate.getLicensingFeature().getId().equals(licenseRequestToFeature.getLicensingEntity().getId()))){
            licenseRequestToFeature.setLicensingEntity(licenseRequestToFeatureCreate.getLicensingFeature());
            update=true;
        }

        return update;
    }


    public LicenseRequestToFeature updateLicenseRequestToFeature(LicenseRequestToFeatureUpdate licenseRequestToFeatureUpdate, SecurityContext securityContext) {
        LicenseRequestToFeature licenseRequestToFeature = licenseRequestToFeatureUpdate.getLicenseRequestToFeature();
        if (updateLicenseRequestToFeatureNoMerge(licenseRequestToFeature, licenseRequestToFeatureUpdate)) {
            repository.merge(licenseRequestToFeature);
            licenseRequestUpdateEventEvent.publishEvent(new LicenseRequestUpdateEvent().setLicenseRequest(licenseRequestToFeature.getLicenseRequest()).setSecurityContext(securityContext));

        }
        return licenseRequestToFeature;
    }

    public List<LicenseRequestToFeature> listAllLicenseRequestToFeatures(LicenseRequestToFeatureFiltering licenseRequestToFeatureFiltering, SecurityContext securityContext) {
        return repository.listAllLicenseRequestToFeatures(licenseRequestToFeatureFiltering, securityContext);
    }

    public void validate(LicenseRequestToFeatureCreate licenseRequestToFeatureCreate, SecurityContext securityContext) {
        licenseRequestToEntityService.validate(licenseRequestToFeatureCreate, securityContext);
        String licensingFeatureId=licenseRequestToFeatureCreate.getLicensingFeatureId();
        LicensingFeature licensingFeature=licensingFeatureId!=null?getByIdOrNull(licensingFeatureId,LicensingFeature.class,null,securityContext):null;
        if(licensingFeature==null && licensingFeatureId!=null){
            throw new BadRequestException("No License feature with id "+licensingFeatureId);
        }
        licenseRequestToFeatureCreate.setLicensingFeature(licensingFeature);

    }

    public void validate(LicenseRequestToFeatureFiltering licenseRequestToFeatureFiltering, SecurityContext securityContext) {
        licenseRequestToEntityService.validate(licenseRequestToFeatureFiltering, securityContext);
    }

    public PaginationResponse<LicenseRequestToFeature> getAllLicenseRequestToFeatures(LicenseRequestToFeatureFiltering licenseRequestToFeatureFiltering, SecurityContext securityContext) {
        List<LicenseRequestToFeature> list = listAllLicenseRequestToFeatures(licenseRequestToFeatureFiltering, securityContext);
        long count = repository.countAllLicenseRequestToFeatures(licenseRequestToFeatureFiltering, securityContext);
        return new PaginationResponse<>(list, licenseRequestToFeatureFiltering, count);
    }


}