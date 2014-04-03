package org.demo.test;

import org.demo.bean.jpa.ShopEntity;

public class ShopEntityFactoryForTest {

	private MockValues mockValues = new MockValues();
	
	public ShopEntity newShopEntity() {

		String code = mockValues.nextString(3);

		ShopEntity shopEntity = new ShopEntity();
		shopEntity.setCode(code);
		return shopEntity;
	}
	
}
