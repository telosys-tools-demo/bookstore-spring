package org.demo.test;

import org.demo.bean.jpa.BookOrderEntity;

public class BookOrderEntityFactoryForTest {

	private MockValues mockValues = new MockValues();
	
	public BookOrderEntity newBookOrderEntity() {

		Integer id = mockValues.nextInteger();

		BookOrderEntity bookOrderEntity = new BookOrderEntity();
		bookOrderEntity.setId(id);
		return bookOrderEntity;
	}
	
}
