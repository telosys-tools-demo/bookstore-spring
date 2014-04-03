package org.demo.test;

import org.demo.bean.jpa.ReviewEntity;

public class ReviewEntityFactoryForTest {

	private MockValues mockValues = new MockValues();
	
	public ReviewEntity newReviewEntity() {

		String customerCode = mockValues.nextString(5);
		Integer bookId = mockValues.nextInteger();

		ReviewEntity reviewEntity = new ReviewEntity();
		reviewEntity.setCustomerCode(customerCode);
		reviewEntity.setBookId(bookId);
		return reviewEntity;
	}
	
}
