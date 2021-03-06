package com.flexicore.license.data;

import com.flexicore.annotations.plugins.PluginInfo;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.license.model.LicensingFeature;
import com.flexicore.license.request.LicensingFeatureFiltering;
import com.flexicore.security.SecurityContext;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Component
@PluginInfo(version=1)
@Extension
public class LicensingFeatureRepository extends AbstractRepositoryPlugin implements ServicePlugin {


    public List<LicensingFeature> listAllLicensingFeatures(LicensingFeatureFiltering licensingFeatureFiltering, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<LicensingFeature> q = cb.createQuery(LicensingFeature.class);
        Root<LicensingFeature> r = q.from(LicensingFeature.class);
        List<Predicate> preds = new ArrayList<>();
        addLicensingFeaturesPredicates(licensingFeatureFiltering,r,cb,preds);
        QueryInformationHolder<LicensingFeature> queryInformationHolder = new QueryInformationHolder<>(licensingFeatureFiltering,LicensingFeature.class, securityContext);
        return getAllFiltered(queryInformationHolder, preds, cb, q, r);
    }

    private void addLicensingFeaturesPredicates(LicensingFeatureFiltering licensingFeatureFiltering, Root<LicensingFeature> r, CriteriaBuilder cb, List<Predicate> preds) {
        LicensingEntityRepository.addLicensingEntitiesPredicates(licensingFeatureFiltering,r,cb,preds);
    }

    public long countAllLicensingFeatures(LicensingFeatureFiltering licensingFeatureFiltering, SecurityContext securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<LicensingFeature> r = q.from(LicensingFeature.class);
        List<Predicate> preds = new ArrayList<>();
        addLicensingFeaturesPredicates(licensingFeatureFiltering,r,cb,preds);
        QueryInformationHolder<LicensingFeature> queryInformationHolder = new QueryInformationHolder<>(licensingFeatureFiltering,LicensingFeature.class, securityContext);
        return countAllFiltered(queryInformationHolder, preds, cb, q, r);
    }
}