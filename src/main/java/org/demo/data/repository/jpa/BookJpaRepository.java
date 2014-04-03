package org.demo.data.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.demo.bean.jpa.BookEntity;

/**
 * Repository : Book.
 */
public interface BookJpaRepository extends PagingAndSortingRepository<BookEntity, Integer> {

}
