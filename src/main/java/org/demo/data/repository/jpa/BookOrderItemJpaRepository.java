package org.demo.data.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.demo.bean.jpa.BookOrderItemEntity;
import org.demo.bean.jpa.BookOrderItemEntityKey;

/**
 * Repository : BookOrderItem.
 */
public interface BookOrderItemJpaRepository extends PagingAndSortingRepository<BookOrderItemEntity, BookOrderItemEntityKey> {

}
