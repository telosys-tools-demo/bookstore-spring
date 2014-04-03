package org.demo.data.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.demo.bean.jpa.PublisherEntity;

/**
 * Repository : Publisher.
 */
public interface PublisherJpaRepository extends PagingAndSortingRepository<PublisherEntity, Integer> {

}
