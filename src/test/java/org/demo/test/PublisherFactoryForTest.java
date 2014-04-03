package org.demo.test;

import org.demo.bean.Publisher;

public class PublisherFactoryForTest {

	private MockValues mockValues = new MockValues();
	
	public Publisher newPublisher() {

		Integer code = mockValues.nextInteger();

		Publisher publisher = new Publisher();
		publisher.setCode(code);
		return publisher;
	}
	
}
