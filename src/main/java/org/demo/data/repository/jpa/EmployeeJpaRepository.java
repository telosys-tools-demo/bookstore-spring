package org.demo.data.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.demo.bean.jpa.EmployeeEntity;

/**
 * Repository : Employee.
 */
public interface EmployeeJpaRepository extends PagingAndSortingRepository<EmployeeEntity, String> {

}
