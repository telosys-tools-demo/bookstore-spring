package org.demo.test;

import org.demo.bean.jpa.EmployeeEntity;

public class EmployeeEntityFactoryForTest {

	private MockValues mockValues = new MockValues();
	
	public EmployeeEntity newEmployeeEntity() {

		String code = mockValues.nextString(4);

		EmployeeEntity employeeEntity = new EmployeeEntity();
		employeeEntity.setCode(code);
		return employeeEntity;
	}
	
}
