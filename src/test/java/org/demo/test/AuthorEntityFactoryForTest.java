package org.demo.test;

import org.demo.bean.jpa.AuthorEntity;

public class AuthorEntityFactoryForTest {

	private MockValues mockValues = new MockValues();
	
	public AuthorEntity newAuthorEntity() {

		Integer id = mockValues.nextInteger();

		AuthorEntity authorEntity = new AuthorEntity();
		authorEntity.setId(id);
		return authorEntity;
	}
	
}
