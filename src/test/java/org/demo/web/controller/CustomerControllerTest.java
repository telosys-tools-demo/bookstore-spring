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
import org.demo.bean.Customer;
import org.demo.bean.Country;
import org.demo.test.CustomerFactoryForTest;
import org.demo.test.CountryFactoryForTest;

//--- Services 
import org.demo.business.service.CustomerService;
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
public class CustomerControllerTest {
	
	@InjectMocks
	private CustomerController customerController;
	@Mock
	private CustomerService customerService;
	@Mock
	private MessageHelper messageHelper;
	@Mock
	private MessageSource messageSource;
	@Mock
	private CountryService countryService; // Injected by Spring

	private CustomerFactoryForTest customerFactoryForTest = new CustomerFactoryForTest();
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
		
		List<Customer> list = new ArrayList<Customer>();
		when(customerService.findAll()).thenReturn(list);
		
		// When
		String viewName = customerController.list(model);
		
		// Then
		assertEquals("customer/list", viewName);
		Map<String,?> modelMap = model.asMap();
		assertEquals(list, modelMap.get("list"));
	}
	
	@Test
	public void formForCreate() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		// When
		String viewName = customerController.formForCreate(model);
		
		// Then
		assertEquals("customer/form", viewName);
		
		Map<String,?> modelMap = model.asMap();
		
		assertNull(((Customer)modelMap.get("customer")).getCode());
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/customer/create", modelMap.get("saveAction"));
		
		@SuppressWarnings("unchecked")
		List<CountryListItem> countryListItems = (List<CountryListItem>) modelMap.get("listOfCountryItems");
		assertEquals(2, countryListItems.size());
		
	}
	
	@Test
	public void formForUpdate() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		Customer customer = customerFactoryForTest.newCustomer();
		String code = customer.getCode();
		when(customerService.findById(code)).thenReturn(customer);
		
		// When
		String viewName = customerController.formForUpdate(model, code);
		
		// Then
		assertEquals("customer/form", viewName);
		
		Map<String,?> modelMap = model.asMap();
		
		assertEquals(customer, (Customer) modelMap.get("customer"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/customer/update", modelMap.get("saveAction"));
		
		List<CountryListItem> countryListItems = (List<CountryListItem>) modelMap.get("listOfCountryItems");
		assertEquals(2, countryListItems.size());
		
	}
	
	@Test
	public void createOk() {
		// Given
		Model model = new ExtendedModelMap();
		
		Customer customer = customerFactoryForTest.newCustomer();
		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		Customer customerCreated = new Customer();
		when(customerService.create(customer)).thenReturn(customerCreated); 
		
		// When
		String viewName = customerController.create(customer, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("redirect:/customer/form/"+customer.getCode(), viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(customerCreated, (Customer) modelMap.get("customer"));
		assertEquals(null, modelMap.get("mode"));
		assertEquals(null, modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"save.ok"));
	}

	@Test
	public void createBindingResultErrors() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		Customer customer = customerFactoryForTest.newCustomer();
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(true);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		// When
		String viewName = customerController.create(customer, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("customer/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(customer, (Customer) modelMap.get("customer"));
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/customer/create", modelMap.get("saveAction"));
		
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

		Customer customer = customerFactoryForTest.newCustomer();
		
		Exception exception = new RuntimeException("test exception");
		when(customerService.create(customer)).thenThrow(exception);
		
		// When
		String viewName = customerController.create(customer, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("customer/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(customer, (Customer) modelMap.get("customer"));
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/customer/create", modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addException(model, "customer.error.create", exception);
		
		@SuppressWarnings("unchecked")
		List<CountryListItem> countryListItems = (List<CountryListItem>) modelMap.get("listOfCountryItems");
		assertEquals(2, countryListItems.size());
		
	}

	@Test
	public void updateOk() {
		// Given
		Model model = new ExtendedModelMap();
		
		Customer customer = customerFactoryForTest.newCustomer();
		String code = customer.getCode();

		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		Customer customerSaved = new Customer();
		customerSaved.setCode(code);
		when(customerService.update(customer)).thenReturn(customerSaved); 
		
		// When
		String viewName = customerController.update(customer, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("redirect:/customer/form/"+customer.getCode(), viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(customerSaved, (Customer) modelMap.get("customer"));
		assertEquals(null, modelMap.get("mode"));
		assertEquals(null, modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"save.ok"));
	}

	@Test
	public void updateBindingResultErrors() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		Customer customer = customerFactoryForTest.newCustomer();
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(true);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		// When
		String viewName = customerController.update(customer, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("customer/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(customer, (Customer) modelMap.get("customer"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/customer/update", modelMap.get("saveAction"));
		
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

		Customer customer = customerFactoryForTest.newCustomer();
		
		Exception exception = new RuntimeException("test exception");
		when(customerService.update(customer)).thenThrow(exception);
		
		// When
		String viewName = customerController.update(customer, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("customer/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(customer, (Customer) modelMap.get("customer"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/customer/update", modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addException(model, "customer.error.update", exception);
		
		@SuppressWarnings("unchecked")
		List<CountryListItem> countryListItems = (List<CountryListItem>) modelMap.get("listOfCountryItems");
		assertEquals(2, countryListItems.size());
		
	}
	

	@Test
	public void deleteOK() {
		// Given
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		
		Customer customer = customerFactoryForTest.newCustomer();
		String code = customer.getCode();
		
		// When
		String viewName = customerController.delete(redirectAttributes, code);
		
		// Then
		verify(customerService).delete(code);
		assertEquals("redirect:/customer", viewName);
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"delete.ok"));
	}

	@Test
	public void deleteException() {
		// Given
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		
		Customer customer = customerFactoryForTest.newCustomer();
		String code = customer.getCode();
		
		Exception exception = new RuntimeException("test exception");
		doThrow(exception).when(customerService).delete(code);
		
		// When
		String viewName = customerController.delete(redirectAttributes, code);
		
		// Then
		verify(customerService).delete(code);
		assertEquals("redirect:/customer", viewName);
		Mockito.verify(messageHelper).addException(redirectAttributes, "customer.error.delete", exception);
	}
	
	
}
