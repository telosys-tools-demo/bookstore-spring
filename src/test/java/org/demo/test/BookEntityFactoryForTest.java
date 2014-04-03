package org.demo.test;

import org.demo.bean.jpa.BookEntity;

public class BookEntityFactoryForTest {

	private MockValues mockValues = new MockValues();
	
	public BookEntity newBookEntity() {

		Integer id = mockValues.nextInteger();

		BookEntity bookEntity = new BookEntity();
		bookEntity.setId(id);
		return bookEntity;
	}
	
}
