package org.demo.data.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.demo.bean.jpa.BookOrderEntity;

/**
 * Repository : BookOrder.
 */
public interface BookOrderJpaRepository extends PagingAndSortingRepository<BookOrderEntity, Integer> {

}
