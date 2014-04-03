package org.demo.test;

import org.demo.bean.jpa.SynopsisEntity;

public class SynopsisEntityFactoryForTest {

	private MockValues mockValues = new MockValues();
	
	public SynopsisEntity newSynopsisEntity() {

		Integer bookId = mockValues.nextInteger();

		SynopsisEntity synopsisEntity = new SynopsisEntity();
		synopsisEntity.setBookId(bookId);
		return synopsisEntity;
	}
	
}
