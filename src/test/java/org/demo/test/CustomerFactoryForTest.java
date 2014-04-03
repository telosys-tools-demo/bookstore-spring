package org.demo.test;

import org.demo.bean.Customer;

public class CustomerFactoryForTest {

	private MockValues mockValues = new MockValues();
	
	public Customer newCustomer() {

		String code = mockValues.nextString(5);

		Customer customer = new Customer();
		customer.setCode(code);
		return customer;
	}
	
}
