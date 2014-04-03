package org.demo.data.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.demo.bean.jpa.SynopsisEntity;

/**
 * Repository : Synopsis.
 */
public interface SynopsisJpaRepository extends PagingAndSortingRepository<SynopsisEntity, Integer> {

}
