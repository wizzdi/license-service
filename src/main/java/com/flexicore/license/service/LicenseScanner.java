package com.flexicore.license.service;


import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.events.PluginsLoadedEvent;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.license.annotations.HasFeature;
import com.flexicore.license.model.LicensingFeature;
import com.flexicore.license.model.LicensingProduct;
import com.flexicore.license.request.LicensingFeatureCreate;
import com.flexicore.license.request.LicensingFeatureFiltering;
import com.flexicore.license.request.LicensingProductCreate;
import com.flexicore.license.request.LicensingProductFiltering;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Extension
@PluginInfo(version = 1)
@Component
public class LicenseScanner implements ServicePlugin {


    private static final Logger logger= LoggerFactory.getLogger(LicenseScanner.class);
    @Autowired
    @PluginInfo(version = 1)
    private LicensingFeatureService featureService;
    @Autowired
    @PluginInfo(version = 1)
    private LicensingProductService licensingProductService;
    @Async
    @EventListener
    public void init(PluginsLoadedEvent pluginsLoadedEvent){

    }

    private void addFeature(HasFeature hasFeature) {
        String canonicalName = hasFeature.canonicalName();
        List<LicensingFeature> licensingFeatures = featureService.listAllLicensingFeatures(new LicensingFeatureFiltering().setCanonicalNames(Collections.singleton(canonicalName)), null);

        LicensingFeature licensingFeature = licensingFeatures.isEmpty() ? null : licensingFeatures.get(0);
        if (licensingFeature == null) {
            logger.info("registering feature: " + canonicalName);
            List<LicensingProduct> licensingProducts = licensingProductService.listAllLicensingProducts(new LicensingProductFiltering().setCanonicalNames(Collections.singleton(hasFeature.productCanonicalName())), null);
            LicensingProduct licensingProduct = licensingProducts.isEmpty() ? null : licensingProducts.get(0);
            if (licensingProduct == null) {
                logger.info("registering product: " + hasFeature.productCanonicalName());

                licensingProduct = licensingProductService.createLicensingProduct(new LicensingProductCreate().setCanonicalName(hasFeature.productCanonicalName()).setName(hasFeature.productCanonicalName()), null);
            }
            licensingFeature = featureService.createLicensingFeature(new LicensingFeatureCreate().setLicensingProduct(licensingProduct).setCanonicalName(canonicalName).setName(canonicalName), null);
        }

    }
}
