package org.demo.data.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.demo.bean.jpa.EmployeeGroupEntity;
import org.demo.bean.jpa.EmployeeGroupEntityKey;

/**
 * Repository : EmployeeGroup.
 */
public interface EmployeeGroupJpaRepository extends PagingAndSortingRepository<EmployeeGroupEntity, EmployeeGroupEntityKey> {

}
