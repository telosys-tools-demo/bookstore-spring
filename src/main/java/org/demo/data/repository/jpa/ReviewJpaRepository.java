package org.demo.data.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.demo.bean.jpa.ReviewEntity;
import org.demo.bean.jpa.ReviewEntityKey;

/**
 * Repository : Review.
 */
public interface ReviewJpaRepository extends PagingAndSortingRepository<ReviewEntity, ReviewEntityKey> {

}
