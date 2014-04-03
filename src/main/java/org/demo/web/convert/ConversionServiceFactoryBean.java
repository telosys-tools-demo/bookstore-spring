package org.demo.web.convert;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.stereotype.Component;

@Component
public class ConversionServiceFactoryBean extends FormattingConversionServiceFactoryBean {


	public void afterPropertiesSet() {
        super.afterPropertiesSet();
        installTypeConverters(getObject());
    }
	
	protected void installTypeConverters(FormatterRegistry registry) {
		registry.addConverter(getStringToStringConverter());
	}
    
	public Converter<String, String> getStringToStringConverter() {
        return new Converter<String, String>() {
 
            public String convert(String source) {
                return StringUtils.trimToNull(source);
            }
        };
    }

}
