package org.demo.data.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.demo.bean.jpa.ShopEntity;

/**
 * Repository : Shop.
 */
public interface ShopJpaRepository extends PagingAndSortingRepository<ShopEntity, String> {

}
