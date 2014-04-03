package org.demo.test;

import org.demo.bean.jpa.EmployeeGroupEntity;

public class EmployeeGroupEntityFactoryForTest {

	private MockValues mockValues = new MockValues();
	
	public EmployeeGroupEntity newEmployeeGroupEntity() {

		String employeeCode = mockValues.nextString(4);
		Short groupId = mockValues.nextShort();

		EmployeeGroupEntity employeeGroupEntity = new EmployeeGroupEntity();
		employeeGroupEntity.setEmployeeCode(employeeCode);
		employeeGroupEntity.setGroupId(groupId);
		return employeeGroupEntity;
	}
	
}
