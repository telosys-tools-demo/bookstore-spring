package org.demo.web.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

//--- Entities
import org.demo.bean.Country;
import org.demo.test.CountryFactoryForTest;

//--- Services 
import org.demo.business.service.CountryService;


import org.demo.web.common.Message;
import org.demo.web.common.MessageHelper;
import org.demo.web.common.MessageType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RunWith(MockitoJUnitRunner.class)
public class CountryControllerTest {
	
	@InjectMocks
	private CountryController countryController;
	@Mock
	private CountryService countryService;
	@Mock
	private MessageHelper messageHelper;
	@Mock
	private MessageSource messageSource;

	private CountryFactoryForTest countryFactoryForTest = new CountryFactoryForTest();


	private void givenPopulateModel() {
	}

	@Test
	public void list() {
		// Given
		Model model = new ExtendedModelMap();
		
		List<Country> list = new ArrayList<Country>();
		when(countryService.findAll()).thenReturn(list);
		
		// When
		String viewName = countryController.list(model);
		
		// Then
		assertEquals("country/list", viewName);
		Map<String,?> modelMap = model.asMap();
		assertEquals(list, modelMap.get("list"));
	}
	
	@Test
	public void formForCreate() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		// When
		String viewName = countryController.formForCreate(model);
		
		// Then
		assertEquals("country/form", viewName);
		
		Map<String,?> modelMap = model.asMap();
		
		assertNull(((Country)modelMap.get("country")).getCode());
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/country/create", modelMap.get("saveAction"));
		
	}
	
	@Test
	public void formForUpdate() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		Country country = countryFactoryForTest.newCountry();
		String code = country.getCode();
		when(countryService.findById(code)).thenReturn(country);
		
		// When
		String viewName = countryController.formForUpdate(model, code);
		
		// Then
		assertEquals("country/form", viewName);
		
		Map<String,?> modelMap = model.asMap();
		
		assertEquals(country, (Country) modelMap.get("country"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/country/update", modelMap.get("saveAction"));
		
	}
	
	@Test
	public void createOk() {
		// Given
		Model model = new ExtendedModelMap();
		
		Country country = countryFactoryForTest.newCountry();
		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		Country countryCreated = new Country();
		when(countryService.create(country)).thenReturn(countryCreated); 
		
		// When
		String viewName = countryController.create(country, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("redirect:/country/form/"+country.getCode(), viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(countryCreated, (Country) modelMap.get("country"));
		assertEquals(null, modelMap.get("mode"));
		assertEquals(null, modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"save.ok"));
	}

	@Test
	public void createBindingResultErrors() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		Country country = countryFactoryForTest.newCountry();
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(true);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		// When
		String viewName = countryController.create(country, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("country/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(country, (Country) modelMap.get("country"));
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/country/create", modelMap.get("saveAction"));
		
	}

	@Test
	public void createException() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(false);

		Country country = countryFactoryForTest.newCountry();
		
		Exception exception = new RuntimeException("test exception");
		when(countryService.create(country)).thenThrow(exception);
		
		// When
		String viewName = countryController.create(country, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("country/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(country, (Country) modelMap.get("country"));
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/country/create", modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addException(model, "country.error.create", exception);
		
	}

	@Test
	public void updateOk() {
		// Given
		Model model = new ExtendedModelMap();
		
		Country country = countryFactoryForTest.newCountry();
		String code = country.getCode();

		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		Country countrySaved = new Country();
		countrySaved.setCode(code);
		when(countryService.update(country)).thenReturn(countrySaved); 
		
		// When
		String viewName = countryController.update(country, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("redirect:/country/form/"+country.getCode(), viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(countrySaved, (Country) modelMap.get("country"));
		assertEquals(null, modelMap.get("mode"));
		assertEquals(null, modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"save.ok"));
	}

	@Test
	public void updateBindingResultErrors() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		Country country = countryFactoryForTest.newCountry();
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(true);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		// When
		String viewName = countryController.update(country, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("country/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(country, (Country) modelMap.get("country"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/country/update", modelMap.get("saveAction"));
		
	}

	@Test
	public void updateException() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(false);

		Country country = countryFactoryForTest.newCountry();
		
		Exception exception = new RuntimeException("test exception");
		when(countryService.update(country)).thenThrow(exception);
		
		// When
		String viewName = countryController.update(country, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("country/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(country, (Country) modelMap.get("country"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/country/update", modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addException(model, "country.error.update", exception);
		
	}
	

	@Test
	public void deleteOK() {
		// Given
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		
		Country country = countryFactoryForTest.newCountry();
		String code = country.getCode();
		
		// When
		String viewName = countryController.delete(redirectAttributes, code);
		
		// Then
		verify(countryService).delete(code);
		assertEquals("redirect:/country", viewName);
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"delete.ok"));
	}

	@Test
	public void deleteException() {
		// Given
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		
		Country country = countryFactoryForTest.newCountry();
		String code = country.getCode();
		
		Exception exception = new RuntimeException("test exception");
		doThrow(exception).when(countryService).delete(code);
		
		// When
		String viewName = countryController.delete(redirectAttributes, code);
		
		// Then
		verify(countryService).delete(code);
		assertEquals("redirect:/country", viewName);
		Mockito.verify(messageHelper).addException(redirectAttributes, "country.error.delete", exception);
	}
	
	
}
