package org.demo.test;

import org.demo.bean.Badge;

public class BadgeFactoryForTest {

	private MockValues mockValues = new MockValues();
	
	public Badge newBadge() {

		Integer badgeNumber = mockValues.nextInteger();

		Badge badge = new Badge();
		badge.setBadgeNumber(badgeNumber);
		return badge;
	}
	
}
