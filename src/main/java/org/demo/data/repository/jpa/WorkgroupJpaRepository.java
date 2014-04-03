package org.demo.data.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.demo.bean.jpa.WorkgroupEntity;

/**
 * Repository : Workgroup.
 */
public interface WorkgroupJpaRepository extends PagingAndSortingRepository<WorkgroupEntity, Short> {

}
