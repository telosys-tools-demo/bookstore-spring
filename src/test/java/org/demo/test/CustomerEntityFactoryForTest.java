package org.demo.test;

import org.demo.bean.jpa.CustomerEntity;

public class CustomerEntityFactoryForTest {

	private MockValues mockValues = new MockValues();
	
	public CustomerEntity newCustomerEntity() {

		String code = mockValues.nextString(5);

		CustomerEntity customerEntity = new CustomerEntity();
		customerEntity.setCode(code);
		return customerEntity;
	}
	
}
