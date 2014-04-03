package org.demo.test;

import org.demo.bean.jpa.BookOrderItemEntity;

public class BookOrderItemEntityFactoryForTest {

	private MockValues mockValues = new MockValues();
	
	public BookOrderItemEntity newBookOrderItemEntity() {

		Integer bookOrderId = mockValues.nextInteger();
		Integer bookId = mockValues.nextInteger();

		BookOrderItemEntity bookOrderItemEntity = new BookOrderItemEntity();
		bookOrderItemEntity.setBookOrderId(bookOrderId);
		bookOrderItemEntity.setBookId(bookId);
		return bookOrderItemEntity;
	}
	
}
