package org.demo.test;

import org.demo.bean.Workgroup;

public class WorkgroupFactoryForTest {

	private MockValues mockValues = new MockValues();
	
	public Workgroup newWorkgroup() {

		Short id = mockValues.nextShort();

		Workgroup workgroup = new Workgroup();
		workgroup.setId(id);
		return workgroup;
	}
	
}
