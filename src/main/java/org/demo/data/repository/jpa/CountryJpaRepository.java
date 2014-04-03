package org.demo.data.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.demo.bean.jpa.CountryEntity;

/**
 * Repository : Country.
 */
public interface CountryJpaRepository extends PagingAndSortingRepository<CountryEntity, String> {

}
