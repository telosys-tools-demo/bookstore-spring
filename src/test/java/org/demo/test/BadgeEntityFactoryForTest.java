package org.demo.test;

import org.demo.bean.jpa.BadgeEntity;

public class BadgeEntityFactoryForTest {

	private MockValues mockValues = new MockValues();
	
	public BadgeEntity newBadgeEntity() {

		Integer badgeNumber = mockValues.nextInteger();

		BadgeEntity badgeEntity = new BadgeEntity();
		badgeEntity.setBadgeNumber(badgeNumber);
		return badgeEntity;
	}
	
}
