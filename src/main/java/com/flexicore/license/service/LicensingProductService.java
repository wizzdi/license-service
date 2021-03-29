package com.flexicore.license.service;


import com.flexicore.license.data.LicensingProductRepository;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.file.service.FileResourceService;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import com.flexicore.model.Baseclass;
import com.flexicore.license.model.LicensingProduct;
import com.flexicore.license.request.LicensingProductCreate;
import com.flexicore.license.request.LicensingProductFiltering;
import com.flexicore.license.request.LicensingProductUpdate;
import com.flexicore.security.SecurityContextBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.pf4j.Extension;
import org.springframework.transaction.annotation.Transactional;


import javax.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;



@Extension
@Component
public class LicensingProductService implements Plugin {


    @Autowired

    private LicensingProductRepository repository;

    @Autowired

    private LicensingEntityService licensingEntityService;

   private Logger logger = Logger.getLogger(getClass().getCanonicalName());


   public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
        return repository.listByIds(c, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
        return repository.getByIdOrNull(id, c, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
        return repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
        return repository.listByIds(c, ids, baseclassAttribute, securityContext);
    }

    public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
        return repository.findByIds(c, ids, idAttribute);
    }

    public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
        return repository.findByIds(c, requested);
    }

    public <T> T findByIdOrNull(Class<T> type, String id) {
        return repository.findByIdOrNull(type, id);
    }

    @Transactional
    public void merge(Object base) {
        repository.merge(base);
    }

    @Transactional
    public void massMerge(List<?> toMerge) {
        repository.massMerge(toMerge);
    }
    public LicensingProduct createLicensingProduct(LicensingProductCreate pluginCreationContainer, SecurityContextBase securityContext) {
        LicensingProduct licensingProduct = createLicensingProductNoMerge(pluginCreationContainer, securityContext);
        repository.merge(licensingProduct);
        return licensingProduct;


    }

    public LicensingProduct createLicensingProductNoMerge(LicensingProductCreate licensingProductCreate, SecurityContextBase securityContext) {
        LicensingProduct licensingProduct = new LicensingProduct();
        licensingProduct.setId(Baseclass.getBase64ID());
        updateLicensingProductNoMerge(licensingProduct, licensingProductCreate);
        BaseclassService.createSecurityObjectNoMerge(licensingProduct,securityContext);
        return licensingProduct;
    }

    public boolean updateLicensingProductNoMerge(LicensingProduct licensingProduct, LicensingProductCreate licensingProductCreate) {
        boolean update = licensingEntityService.updateLicensingEntityNoMerge(licensingProduct, licensingProductCreate);

        return update;
    }


    public LicensingProduct updateLicensingProduct(LicensingProductUpdate licensingProductUpdate, SecurityContextBase securityContext) {
        LicensingProduct licensingProduct = licensingProductUpdate.getLicensingProduct();
        if (updateLicensingProductNoMerge(licensingProduct, licensingProductUpdate)) {
            repository.merge(licensingProduct);
        }
        return licensingProduct;
    }

    public List<LicensingProduct> listAllLicensingProducts(LicensingProductFiltering licensingProductFiltering, SecurityContextBase securityContext) {
        return repository.listAllLicensingProducts(licensingProductFiltering, securityContext);
    }

    public void validate(LicensingProductCreate licensingProductCreate, SecurityContextBase securityContext) {
        licensingEntityService.validate(licensingProductCreate, securityContext);

    }

    public void validate(LicensingProductFiltering licensingProductFiltering, SecurityContextBase securityContext) {
        licensingEntityService.validate(licensingProductFiltering,securityContext);
    }

    public PaginationResponse<LicensingProduct> getAllLicensingProducts(LicensingProductFiltering licensingProductFiltering, SecurityContextBase securityContext) {
        List<LicensingProduct> list = listAllLicensingProducts(licensingProductFiltering, securityContext);
        long count = repository.countAllLicensingProducts(licensingProductFiltering, securityContext);
        return new PaginationResponse<>(list, licensingProductFiltering, count);
    }


}