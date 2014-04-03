package org.demo.test;

import org.demo.bean.jpa.PublisherEntity;

public class PublisherEntityFactoryForTest {

	private MockValues mockValues = new MockValues();
	
	public PublisherEntity newPublisherEntity() {

		Integer code = mockValues.nextInteger();

		PublisherEntity publisherEntity = new PublisherEntity();
		publisherEntity.setCode(code);
		return publisherEntity;
	}
	
}
