package org.demo.test;

import org.demo.bean.Shop;

public class ShopFactoryForTest {

	private MockValues mockValues = new MockValues();
	
	public Shop newShop() {

		String code = mockValues.nextString(3);

		Shop shop = new Shop();
		shop.setCode(code);
		return shop;
	}
	
}
