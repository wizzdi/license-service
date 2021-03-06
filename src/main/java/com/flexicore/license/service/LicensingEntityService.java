package com.flexicore.license.service;


import com.flexicore.license.data.LicensingEntityRepository;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.license.model.LicensingEntity;
import com.flexicore.license.request.LicensingEntityCreate;
import com.flexicore.license.request.LicensingEntityFiltering;
import com.flexicore.license.request.LicensingEntityUpdate;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.BaseclassNewService;
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
public class LicensingEntityService implements ServicePlugin {


    @Autowired
    @PluginInfo(version = 1)
    private LicensingEntityRepository repository;

    @Autowired
    private BaseclassNewService baseclassNewService;

   private Logger logger = Logger.getLogger(getClass().getCanonicalName());


    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, List<String> batchString, SecurityContext securityContext) {
        return repository.getByIdOrNull(id, c, batchString, securityContext);
    }


    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
        return repository.listByIds(c, ids, securityContext);
    }

    public LicensingEntity createLicensingEntity(LicensingEntityCreate pluginCreationContainer, SecurityContext securityContext) {
        LicensingEntity licensingEntity = createLicensingEntityNoMerge(pluginCreationContainer, securityContext);
        repository.merge(licensingEntity);
        return licensingEntity;


    }

    public LicensingEntity createLicensingEntityNoMerge(LicensingEntityCreate licensingEntityCreate, SecurityContext securityContext) {
        LicensingEntity licensingEntity = new LicensingEntity(licensingEntityCreate.getName(), securityContext);
        updateLicensingEntityNoMerge(licensingEntity, licensingEntityCreate);
        return licensingEntity;
    }

    public boolean updateLicensingEntityNoMerge(LicensingEntity licensingEntity, LicensingEntityCreate licensingEntityCreate) {
        boolean update = baseclassNewService.updateBaseclassNoMerge(licensingEntityCreate, licensingEntity);
        if (licensingEntityCreate.getCanonicalName() != null && !licensingEntityCreate.getCanonicalName().equals(licensingEntity.getCanonicalName())) {
            licensingEntity.setCanonicalName(licensingEntityCreate.getCanonicalName());
            update = true;
        }
        return update;
    }


    public LicensingEntity updateLicensingEntity(LicensingEntityUpdate licensingEntityUpdate, SecurityContext securityContext) {
        LicensingEntity licensingEntity = licensingEntityUpdate.getLicensingEntity();
        if (updateLicensingEntityNoMerge(licensingEntity, licensingEntityUpdate)) {
            repository.merge(licensingEntity);
        }
        return licensingEntity;
    }

    public List<LicensingEntity> listAllLicensingEntities(LicensingEntityFiltering licensingEntityFiltering, SecurityContext securityContext) {
        return repository.listAllLicensingEntities(licensingEntityFiltering, securityContext);
    }

    public void validate(LicensingEntityCreate licensingEntityCreate, SecurityContext securityContext) {
        baseclassNewService.validate(licensingEntityCreate, securityContext);

    }

    public void validate(LicensingEntityFiltering licensingEntityFiltering, SecurityContext securityContext) {
        baseclassNewService.validateFilter(licensingEntityFiltering,securityContext);
    }

    public PaginationResponse<LicensingEntity> getAllLicensingEntities(LicensingEntityFiltering licensingEntityFiltering, SecurityContext securityContext) {
        List<LicensingEntity> list = listAllLicensingEntities(licensingEntityFiltering, securityContext);
        long count = repository.countAllLicensingEntities(licensingEntityFiltering, securityContext);
        return new PaginationResponse<>(list, licensingEntityFiltering, count);
    }


}