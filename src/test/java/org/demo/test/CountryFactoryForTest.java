package org.demo.test;

import org.demo.bean.Country;

public class CountryFactoryForTest {

	private MockValues mockValues = new MockValues();
	
	public Country newCountry() {

		String code = mockValues.nextString(2);

		Country country = new Country();
		country.setCode(code);
		return country;
	}
	
}
