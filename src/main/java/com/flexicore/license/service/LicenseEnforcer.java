package com.flexicore.license.service;


import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.events.BaseclassCreated;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.license.exceptions.ExceededQuota;
import com.flexicore.license.holders.UpdateLicensingCache;
import com.flexicore.license.model.LicenseRequest;
import com.flexicore.license.model.LicenseRequestToQuantityFeature;
import com.flexicore.license.request.LicenseRequestFiltering;
import com.flexicore.license.request.LicenseRequestToQuantityFeatureFiltering;
import com.flexicore.model.Baseclass;
import com.flexicore.model.ClazzIdFiltering;
import com.flexicore.model.Tenant;
import com.flexicore.request.BaseclassCountRequest;
import com.flexicore.response.BaseclassCount;
import com.flexicore.service.BaseclassService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@PluginInfo(version = 1)
@Extension
@Component
public class LicenseEnforcer implements ServicePlugin {

    private static AtomicBoolean init = new AtomicBoolean(false);

    private static Cache<String, Integer> requestedLicense = CacheBuilder.newBuilder().build();
    private static Cache<String, LicenseRequest> licensed = CacheBuilder.newBuilder().build();
    private static Cache<String, AtomicLong> cachedCount = CacheBuilder.newBuilder().build();


    private Logger logger = Logger.getLogger(getClass().getCanonicalName());

    @Autowired
    private LicenseRequestToQuantityFeatureService licenseRequestToQuantityFeatureService;

    @Autowired
    private LicenseRequestService licenseRequestService;
    @Autowired
    private BaseclassService baseclassService;


    @EventListener
    public void onBaseclassCreated(BaseclassCreated<Baseclass> baseclassCreated) {
        Baseclass b = baseclassCreated.getBaseclass();
        if (b.getTenant() != null) {
            String canonicalName = b.getClass().getCanonicalName();
            Tenant tenant = b.getTenant();
            String tenantId = tenant.getId();
            String key = getKey(tenantId, canonicalName);
            Integer quantity = requestedLicense.getIfPresent(key);
            if (quantity != null) {
                try {
                    AtomicLong currentQuantity = cachedCount.get(key, () -> new AtomicLong(0));
                    long val = currentQuantity.updateAndGet(operand -> operand > quantity ? operand : operand + 1);
                    LicenseRequest license = licensed.getIfPresent(key);
                    if (license == null) {
                        throw new ExceededQuota("Quota of " + canonicalName + " for tenant +" + tenant.getName() + "(" + tenantId + ") Exceeded - no license");
                    }
                    if (val > quantity) {
                        throw new ExceededQuota("Quota of " + canonicalName + " for tenant +" + tenant.getName() + "(" + tenantId + ") Exceeded , max is " + quantity + " actual is " + val);
                    }
                } catch (ExecutionException e) {
                    logger.log(Level.SEVERE, "failed checking current quantity", e);
                }
            }
        }
    }


    @Async
    @EventListener
    public void updateLicensingCache(UpdateLicensingCache updateLicensingCache) {
        List<LicenseRequest> licenseRequests = licenseRequestService.listAllLicenseRequests(new LicenseRequestFiltering(), null);
        List<LicenseRequestToQuantityFeature> licenseRequestToQuantityFeatures = licenseRequests.isEmpty() ? new ArrayList<>() : licenseRequestToQuantityFeatureService.listAllLicenseRequestToQuantityFeatures(new LicenseRequestToQuantityFeatureFiltering().setLicenseRequests(licenseRequests), null);
        Map<String, List<LicenseRequestToQuantityFeature>> links = licenseRequestToQuantityFeatures.parallelStream().filter(f -> f.getLicenseRequest() != null).collect(Collectors.groupingBy(f -> f.getLicenseRequest().getId()));

        Set<String> clazzNames = new HashSet<>();
        for (Map.Entry<String, List<LicenseRequestToQuantityFeature>> stringListEntry : links.entrySet()) {
            Boolean validated = null;
            for (LicenseRequestToQuantityFeature licenseRequestToQuantityFeature : stringListEntry.getValue()) {
                if (validated == null) {
                    validated = licenseRequestService.isLicenseValid(licenseRequestToQuantityFeature.getLicenseRequest());
                }
                clazzNames.add(licenseRequestToQuantityFeature.getLicensingEntity().getCanonicalName());
                String key = getKey(licenseRequestToQuantityFeature);
                requestedLicense.put(key, licenseRequestToQuantityFeature.getQuantityLimit());
                if (validated) {
                    licensed.put(key, licenseRequestToQuantityFeature.getLicenseRequest());
                }
            }
        }
        List<ClazzIdFiltering> clazzIdFilterings = clazzNames.parallelStream().map(f -> Baseclass.getClazzByName(f)).filter(f -> f != null).map(f -> new ClazzIdFiltering().setId(f.getId())).collect(Collectors.toList());
        List<BaseclassCount> baseclassCounts = clazzIdFilterings.isEmpty() ? new ArrayList<>() : baseclassService.getBaseclassCount(new BaseclassCountRequest().setGroupByTenant(true).setClazzIds(clazzIdFilterings), null);
        for (BaseclassCount baseclassCount : baseclassCounts) {
            cachedCount.put(getKey(baseclassCount.getTenant().getId(), baseclassCount.getCanonicalName()), new AtomicLong(baseclassCount.getCount()));
        }
    }

    private String getKey(LicenseRequestToQuantityFeature licenseRequestToQuantityFeature) {
        return getKey(licenseRequestToQuantityFeature.getLicenseRequest().getLicensedTenant().getId(), licenseRequestToQuantityFeature.getLicensingEntity().getCanonicalName());
    }

    private String getKey(String tenantId, String canonicalName) {
        return tenantId + ":" + canonicalName;
    }

}
