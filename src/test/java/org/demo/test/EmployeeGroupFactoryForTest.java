package org.demo.test;

import org.demo.bean.EmployeeGroup;

public class EmployeeGroupFactoryForTest {

	private MockValues mockValues = new MockValues();
	
	public EmployeeGroup newEmployeeGroup() {

		String employeeCode = mockValues.nextString(4);
		Short groupId = mockValues.nextShort();

		EmployeeGroup employeeGroup = new EmployeeGroup();
		employeeGroup.setEmployeeCode(employeeCode);
		employeeGroup.setGroupId(groupId);
		return employeeGroup;
	}
	
}
