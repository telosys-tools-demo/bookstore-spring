package org.demo.data.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.demo.bean.jpa.BadgeEntity;

/**
 * Repository : Badge.
 */
public interface BadgeJpaRepository extends PagingAndSortingRepository<BadgeEntity, Integer> {

}
