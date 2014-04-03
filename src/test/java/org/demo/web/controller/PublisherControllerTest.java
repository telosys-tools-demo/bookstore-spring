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
import org.demo.bean.Publisher;
import org.demo.bean.Country;
import org.demo.test.PublisherFactoryForTest;
import org.demo.test.CountryFactoryForTest;

//--- Services 
import org.demo.business.service.PublisherService;
import org.demo.business.service.CountryService;

//--- List Items 
import org.demo.web.listitem.CountryListItem;

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
public class PublisherControllerTest {
	
	@InjectMocks
	private PublisherController publisherController;
	@Mock
	private PublisherService publisherService;
	@Mock
	private MessageHelper messageHelper;
	@Mock
	private MessageSource messageSource;
	@Mock
	private CountryService countryService; // Injected by Spring

	private PublisherFactoryForTest publisherFactoryForTest = new PublisherFactoryForTest();
	private CountryFactoryForTest countryFactoryForTest = new CountryFactoryForTest();

	List<Country> countrys = new ArrayList<Country>();

	private void givenPopulateModel() {
		Country country1 = countryFactoryForTest.newCountry();
		Country country2 = countryFactoryForTest.newCountry();
		List<Country> countrys = new ArrayList<Country>();
		countrys.add(country1);
		countrys.add(country2);
		when(countryService.findAll()).thenReturn(countrys);

	}

	@Test
	public void list() {
		// Given
		Model model = new ExtendedModelMap();
		
		List<Publisher> list = new ArrayList<Publisher>();
		when(publisherService.findAll()).thenReturn(list);
		
		// When
		String viewName = publisherController.list(model);
		
		// Then
		assertEquals("publisher/list", viewName);
		Map<String,?> modelMap = model.asMap();
		assertEquals(list, modelMap.get("list"));
	}
	
	@Test
	public void formForCreate() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		// When
		String viewName = publisherController.formForCreate(model);
		
		// Then
		assertEquals("publisher/form", viewName);
		
		Map<String,?> modelMap = model.asMap();
		
		assertNull(((Publisher)modelMap.get("publisher")).getCode());
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/publisher/create", modelMap.get("saveAction"));
		
		@SuppressWarnings("unchecked")
		List<CountryListItem> countryListItems = (List<CountryListItem>) modelMap.get("listOfCountryItems");
		assertEquals(2, countryListItems.size());
		
	}
	
	@Test
	public void formForUpdate() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		Publisher publisher = publisherFactoryForTest.newPublisher();
		Integer code = publisher.getCode();
		when(publisherService.findById(code)).thenReturn(publisher);
		
		// When
		String viewName = publisherController.formForUpdate(model, code);
		
		// Then
		assertEquals("publisher/form", viewName);
		
		Map<String,?> modelMap = model.asMap();
		
		assertEquals(publisher, (Publisher) modelMap.get("publisher"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/publisher/update", modelMap.get("saveAction"));
		
		List<CountryListItem> countryListItems = (List<CountryListItem>) modelMap.get("listOfCountryItems");
		assertEquals(2, countryListItems.size());
		
	}
	
	@Test
	public void createOk() {
		// Given
		Model model = new ExtendedModelMap();
		
		Publisher publisher = publisherFactoryForTest.newPublisher();
		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		Publisher publisherCreated = new Publisher();
		when(publisherService.create(publisher)).thenReturn(publisherCreated); 
		
		// When
		String viewName = publisherController.create(publisher, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("redirect:/publisher/form/"+publisher.getCode(), viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(publisherCreated, (Publisher) modelMap.get("publisher"));
		assertEquals(null, modelMap.get("mode"));
		assertEquals(null, modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"save.ok"));
	}

	@Test
	public void createBindingResultErrors() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		Publisher publisher = publisherFactoryForTest.newPublisher();
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(true);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		// When
		String viewName = publisherController.create(publisher, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("publisher/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(publisher, (Publisher) modelMap.get("publisher"));
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/publisher/create", modelMap.get("saveAction"));
		
		@SuppressWarnings("unchecked")
		List<CountryListItem> countryListItems = (List<CountryListItem>) modelMap.get("listOfCountryItems");
		assertEquals(2, countryListItems.size());
		
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

		Publisher publisher = publisherFactoryForTest.newPublisher();
		
		Exception exception = new RuntimeException("test exception");
		when(publisherService.create(publisher)).thenThrow(exception);
		
		// When
		String viewName = publisherController.create(publisher, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("publisher/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(publisher, (Publisher) modelMap.get("publisher"));
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/publisher/create", modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addException(model, "publisher.error.create", exception);
		
		@SuppressWarnings("unchecked")
		List<CountryListItem> countryListItems = (List<CountryListItem>) modelMap.get("listOfCountryItems");
		assertEquals(2, countryListItems.size());
		
	}

	@Test
	public void updateOk() {
		// Given
		Model model = new ExtendedModelMap();
		
		Publisher publisher = publisherFactoryForTest.newPublisher();
		Integer code = publisher.getCode();

		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		Publisher publisherSaved = new Publisher();
		publisherSaved.setCode(code);
		when(publisherService.update(publisher)).thenReturn(publisherSaved); 
		
		// When
		String viewName = publisherController.update(publisher, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("redirect:/publisher/form/"+publisher.getCode(), viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(publisherSaved, (Publisher) modelMap.get("publisher"));
		assertEquals(null, modelMap.get("mode"));
		assertEquals(null, modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"save.ok"));
	}

	@Test
	public void updateBindingResultErrors() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		Publisher publisher = publisherFactoryForTest.newPublisher();
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(true);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		// When
		String viewName = publisherController.update(publisher, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("publisher/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(publisher, (Publisher) modelMap.get("publisher"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/publisher/update", modelMap.get("saveAction"));
		
		@SuppressWarnings("unchecked")
		List<CountryListItem> countryListItems = (List<CountryListItem>) modelMap.get("listOfCountryItems");
		assertEquals(2, countryListItems.size());
		
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

		Publisher publisher = publisherFactoryForTest.newPublisher();
		
		Exception exception = new RuntimeException("test exception");
		when(publisherService.update(publisher)).thenThrow(exception);
		
		// When
		String viewName = publisherController.update(publisher, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("publisher/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(publisher, (Publisher) modelMap.get("publisher"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/publisher/update", modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addException(model, "publisher.error.update", exception);
		
		@SuppressWarnings("unchecked")
		List<CountryListItem> countryListItems = (List<CountryListItem>) modelMap.get("listOfCountryItems");
		assertEquals(2, countryListItems.size());
		
	}
	

	@Test
	public void deleteOK() {
		// Given
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		
		Publisher publisher = publisherFactoryForTest.newPublisher();
		Integer code = publisher.getCode();
		
		// When
		String viewName = publisherController.delete(redirectAttributes, code);
		
		// Then
		verify(publisherService).delete(code);
		assertEquals("redirect:/publisher", viewName);
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"delete.ok"));
	}

	@Test
	public void deleteException() {
		// Given
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		
		Publisher publisher = publisherFactoryForTest.newPublisher();
		Integer code = publisher.getCode();
		
		Exception exception = new RuntimeException("test exception");
		doThrow(exception).when(publisherService).delete(code);
		
		// When
		String viewName = publisherController.delete(redirectAttributes, code);
		
		// Then
		verify(publisherService).delete(code);
		assertEquals("redirect:/publisher", viewName);
		Mockito.verify(messageHelper).addException(redirectAttributes, "publisher.error.delete", exception);
	}
	
	
}
