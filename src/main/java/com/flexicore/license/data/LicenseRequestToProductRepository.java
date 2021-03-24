package com.flexicore.license.data;

import com.flexicore.license.model.LicenseRequestToProduct;
import com.flexicore.license.request.LicenseRequestToProductFiltering;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BasicRepository;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component

@Extension
public class LicenseRequestToProductRepository implements Plugin {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private LicenseRequestToEntityRepository licenseRequestToEntityRepository;


	public List<LicenseRequestToProduct> listAllLicenseRequestToProducts(LicenseRequestToProductFiltering licenseRequestToProductFiltering, SecurityContextBase securityContextBase) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<LicenseRequestToProduct> q = cb.createQuery(LicenseRequestToProduct.class);
		Root<LicenseRequestToProduct> r = q.from(LicenseRequestToProduct.class);
		List<Predicate> preds = new ArrayList<>();
		addLicenseRequestToProductsPredicates(licenseRequestToProductFiltering, cb, q, r, preds, securityContextBase);
		q.select(r).where(preds.toArray(Predicate[]::new));
		TypedQuery<LicenseRequestToProduct> query = em.createQuery(q);
		BasicRepository.addPagination(licenseRequestToProductFiltering, query);
		return query.getResultList();
	}

	public <T extends LicenseRequestToProduct> void addLicenseRequestToProductsPredicates(LicenseRequestToProductFiltering licenseRequestToProductFiltering, CriteriaBuilder cb, CommonAbstractCriteria q, From<?, T> r, List<Predicate> preds, SecurityContextBase securityContextBase) {
		licenseRequestToEntityRepository.addLicenseRequestToEntitiesPredicates(licenseRequestToProductFiltering, cb, q, r, preds, securityContextBase);
	}

	public long countAllLicenseRequestToProducts(LicenseRequestToProductFiltering licenseRequestToProductFiltering, SecurityContextBase securityContextBase) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<LicenseRequestToProduct> r = q.from(LicenseRequestToProduct.class);
		List<Predicate> preds = new ArrayList<>();
		addLicenseRequestToProductsPredicates(licenseRequestToProductFiltering, cb, q, r, preds, securityContextBase);
		q.select(cb.count(r)).where(preds.toArray(Predicate[]::new));
		TypedQuery<Long> query = em.createQuery(q);
		return query.getSingleResult();
	}

	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
		return licenseRequestToEntityRepository.listByIds(c, ids, securityContext);
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
		return licenseRequestToEntityRepository.getByIdOrNull(id, c, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return licenseRequestToEntityRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return licenseRequestToEntityRepository.listByIds(c, ids, baseclassAttribute, securityContext);
	}

	public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
		return licenseRequestToEntityRepository.findByIds(c, ids, idAttribute);
	}

	public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
		return licenseRequestToEntityRepository.findByIds(c, requested);
	}

	public <T> T findByIdOrNull(Class<T> type, String id) {
		return licenseRequestToEntityRepository.findByIdOrNull(type, id);
	}

	@Transactional
	public void merge(Object base) {
		licenseRequestToEntityRepository.merge(base);
	}

	@Transactional
	public void massMerge(List<?> toMerge) {
		licenseRequestToEntityRepository.massMerge(toMerge);
	}
}