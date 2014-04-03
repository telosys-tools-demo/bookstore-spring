package org.demo.test;

import org.demo.bean.Author;

public class AuthorFactoryForTest {

	private MockValues mockValues = new MockValues();
	
	public Author newAuthor() {

		Integer id = mockValues.nextInteger();

		Author author = new Author();
		author.setId(id);
		return author;
	}
	
}
