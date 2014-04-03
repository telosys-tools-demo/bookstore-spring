package org.demo.test;

import org.demo.bean.jpa.CountryEntity;

public class CountryEntityFactoryForTest {

	private MockValues mockValues = new MockValues();
	
	public CountryEntity newCountryEntity() {

		String code = mockValues.nextString(2);

		CountryEntity countryEntity = new CountryEntity();
		countryEntity.setCode(code);
		return countryEntity;
	}
	
}
