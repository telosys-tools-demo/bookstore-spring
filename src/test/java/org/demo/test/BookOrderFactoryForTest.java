package org.demo.test;

import org.demo.bean.BookOrder;

public class BookOrderFactoryForTest {

	private MockValues mockValues = new MockValues();
	
	public BookOrder newBookOrder() {

		Integer id = mockValues.nextInteger();

		BookOrder bookOrder = new BookOrder();
		bookOrder.setId(id);
		return bookOrder;
	}
	
}
