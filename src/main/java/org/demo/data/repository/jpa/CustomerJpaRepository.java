package org.demo.data.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.demo.bean.jpa.CustomerEntity;

/**
 * Repository : Customer.
 */
public interface CustomerJpaRepository extends PagingAndSortingRepository<CustomerEntity, String> {

}
