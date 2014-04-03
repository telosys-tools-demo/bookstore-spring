package org.demo.test;

import org.demo.bean.Book;

public class BookFactoryForTest {

	private MockValues mockValues = new MockValues();
	
	public Book newBook() {

		Integer id = mockValues.nextInteger();

		Book book = new Book();
		book.setId(id);
		return book;
	}
	
}
