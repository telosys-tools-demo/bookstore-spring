package org.demo.test;

import org.demo.bean.Synopsis;

public class SynopsisFactoryForTest {

	private MockValues mockValues = new MockValues();
	
	public Synopsis newSynopsis() {

		Integer bookId = mockValues.nextInteger();

		Synopsis synopsis = new Synopsis();
		synopsis.setBookId(bookId);
		return synopsis;
	}
	
}
