package org.demo.test;

import org.demo.bean.Employee;

public class EmployeeFactoryForTest {

	private MockValues mockValues = new MockValues();
	
	public Employee newEmployee() {

		String code = mockValues.nextString(4);

		Employee employee = new Employee();
		employee.setCode(code);
		return employee;
	}
	
}
