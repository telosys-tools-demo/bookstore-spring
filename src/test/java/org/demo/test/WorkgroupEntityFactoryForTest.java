package org.demo.test;

import org.demo.bean.jpa.WorkgroupEntity;

public class WorkgroupEntityFactoryForTest {

	private MockValues mockValues = new MockValues();
	
	public WorkgroupEntity newWorkgroupEntity() {

		Short id = mockValues.nextShort();

		WorkgroupEntity workgroupEntity = new WorkgroupEntity();
		workgroupEntity.setId(id);
		return workgroupEntity;
	}
	
}
