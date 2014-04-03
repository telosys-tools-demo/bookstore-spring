package org.demo.test;

import org.demo.bean.BookOrderItem;

public class BookOrderItemFactoryForTest {

	private MockValues mockValues = new MockValues();
	
	public BookOrderItem newBookOrderItem() {

		Integer bookOrderId = mockValues.nextInteger();
		Integer bookId = mockValues.nextInteger();

		BookOrderItem bookOrderItem = new BookOrderItem();
		bookOrderItem.setBookOrderId(bookOrderId);
		bookOrderItem.setBookId(bookId);
		return bookOrderItem;
	}
	
}
