package org.demo.test;

import org.demo.bean.Review;

public class ReviewFactoryForTest {

	private MockValues mockValues = new MockValues();
	
	public Review newReview() {

		String customerCode = mockValues.nextString(5);
		Integer bookId = mockValues.nextInteger();

		Review review = new Review();
		review.setCustomerCode(customerCode);
		review.setBookId(bookId);
		return review;
	}
	
}
