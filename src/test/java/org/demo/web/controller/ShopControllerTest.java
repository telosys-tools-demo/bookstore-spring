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
import org.demo.bean.Shop;
import org.demo.bean.Employee;
import org.demo.bean.Country;
import org.demo.test.ShopFactoryForTest;
import org.demo.test.EmployeeFactoryForTest;
import org.demo.test.CountryFactoryForTest;

//--- Services 
import org.demo.business.service.ShopService;
import org.demo.business.service.EmployeeService;
import org.demo.business.service.CountryService;

//--- List Items 
import org.demo.web.listitem.EmployeeListItem;
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
public class ShopControllerTest {
	
	@InjectMocks
	private ShopController shopController;
	@Mock
	private ShopService shopService;
	@Mock
	private MessageHelper messageHelper;
	@Mock
	private MessageSource messageSource;
	@Mock
	private EmployeeService employeeService; // Injected by Spring
	@Mock
	private CountryService countryService; // Injected by Spring

	private ShopFactoryForTest shopFactoryForTest = new ShopFactoryForTest();
	private EmployeeFactoryForTest employeeFactoryForTest = new EmployeeFactoryForTest();
	private CountryFactoryForTest countryFactoryForTest = new CountryFactoryForTest();

	List<Employee> employees = new ArrayList<Employee>();
	List<Country> countrys = new ArrayList<Country>();

	private void givenPopulateModel() {
		Employee employee1 = employeeFactoryForTest.newEmployee();
		Employee employee2 = employeeFactoryForTest.newEmployee();
		List<Employee> employees = new ArrayList<Employee>();
		employees.add(employee1);
		employees.add(employee2);
		when(employeeService.findAll()).thenReturn(employees);

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
		
		List<Shop> list = new ArrayList<Shop>();
		when(shopService.findAll()).thenReturn(list);
		
		// When
		String viewName = shopController.list(model);
		
		// Then
		assertEquals("shop/list", viewName);
		Map<String,?> modelMap = model.asMap();
		assertEquals(list, modelMap.get("list"));
	}
	
	@Test
	public void formForCreate() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		// When
		String viewName = shopController.formForCreate(model);
		
		// Then
		assertEquals("shop/form", viewName);
		
		Map<String,?> modelMap = model.asMap();
		
		assertNull(((Shop)modelMap.get("shop")).getCode());
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/shop/create", modelMap.get("saveAction"));
		
		@SuppressWarnings("unchecked")
		List<EmployeeListItem> employeeListItems = (List<EmployeeListItem>) modelMap.get("listOfEmployeeItems");
		assertEquals(2, employeeListItems.size());
		
		@SuppressWarnings("unchecked")
		List<CountryListItem> countryListItems = (List<CountryListItem>) modelMap.get("listOfCountryItems");
		assertEquals(2, countryListItems.size());
		
	}
	
	@Test
	public void formForUpdate() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		Shop shop = shopFactoryForTest.newShop();
		String code = shop.getCode();
		when(shopService.findById(code)).thenReturn(shop);
		
		// When
		String viewName = shopController.formForUpdate(model, code);
		
		// Then
		assertEquals("shop/form", viewName);
		
		Map<String,?> modelMap = model.asMap();
		
		assertEquals(shop, (Shop) modelMap.get("shop"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/shop/update", modelMap.get("saveAction"));
		
		List<CountryListItem> countryListItems = (List<CountryListItem>) modelMap.get("listOfCountryItems");
		assertEquals(2, countryListItems.size());
		
		List<EmployeeListItem> employeeListItems = (List<EmployeeListItem>) modelMap.get("listOfEmployeeItems");
		assertEquals(2, employeeListItems.size());
		
	}
	
	@Test
	public void createOk() {
		// Given
		Model model = new ExtendedModelMap();
		
		Shop shop = shopFactoryForTest.newShop();
		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		Shop shopCreated = new Shop();
		when(shopService.create(shop)).thenReturn(shopCreated); 
		
		// When
		String viewName = shopController.create(shop, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("redirect:/shop/form/"+shop.getCode(), viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(shopCreated, (Shop) modelMap.get("shop"));
		assertEquals(null, modelMap.get("mode"));
		assertEquals(null, modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"save.ok"));
	}

	@Test
	public void createBindingResultErrors() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		Shop shop = shopFactoryForTest.newShop();
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(true);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		// When
		String viewName = shopController.create(shop, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("shop/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(shop, (Shop) modelMap.get("shop"));
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/shop/create", modelMap.get("saveAction"));
		
		@SuppressWarnings("unchecked")
		List<EmployeeListItem> employeeListItems = (List<EmployeeListItem>) modelMap.get("listOfEmployeeItems");
		assertEquals(2, employeeListItems.size());
		
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

		Shop shop = shopFactoryForTest.newShop();
		
		Exception exception = new RuntimeException("test exception");
		when(shopService.create(shop)).thenThrow(exception);
		
		// When
		String viewName = shopController.create(shop, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("shop/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(shop, (Shop) modelMap.get("shop"));
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/shop/create", modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addException(model, "shop.error.create", exception);
		
		@SuppressWarnings("unchecked")
		List<EmployeeListItem> employeeListItems = (List<EmployeeListItem>) modelMap.get("listOfEmployeeItems");
		assertEquals(2, employeeListItems.size());
		
		@SuppressWarnings("unchecked")
		List<CountryListItem> countryListItems = (List<CountryListItem>) modelMap.get("listOfCountryItems");
		assertEquals(2, countryListItems.size());
		
	}

	@Test
	public void updateOk() {
		// Given
		Model model = new ExtendedModelMap();
		
		Shop shop = shopFactoryForTest.newShop();
		String code = shop.getCode();

		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		Shop shopSaved = new Shop();
		shopSaved.setCode(code);
		when(shopService.update(shop)).thenReturn(shopSaved); 
		
		// When
		String viewName = shopController.update(shop, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("redirect:/shop/form/"+shop.getCode(), viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(shopSaved, (Shop) modelMap.get("shop"));
		assertEquals(null, modelMap.get("mode"));
		assertEquals(null, modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"save.ok"));
	}

	@Test
	public void updateBindingResultErrors() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		Shop shop = shopFactoryForTest.newShop();
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(true);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		// When
		String viewName = shopController.update(shop, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("shop/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(shop, (Shop) modelMap.get("shop"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/shop/update", modelMap.get("saveAction"));
		
		@SuppressWarnings("unchecked")
		List<CountryListItem> countryListItems = (List<CountryListItem>) modelMap.get("listOfCountryItems");
		assertEquals(2, countryListItems.size());
		
		@SuppressWarnings("unchecked")
		List<EmployeeListItem> employeeListItems = (List<EmployeeListItem>) modelMap.get("listOfEmployeeItems");
		assertEquals(2, employeeListItems.size());
		
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

		Shop shop = shopFactoryForTest.newShop();
		
		Exception exception = new RuntimeException("test exception");
		when(shopService.update(shop)).thenThrow(exception);
		
		// When
		String viewName = shopController.update(shop, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("shop/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(shop, (Shop) modelMap.get("shop"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/shop/update", modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addException(model, "shop.error.update", exception);
		
		@SuppressWarnings("unchecked")
		List<CountryListItem> countryListItems = (List<CountryListItem>) modelMap.get("listOfCountryItems");
		assertEquals(2, countryListItems.size());
		
		@SuppressWarnings("unchecked")
		List<EmployeeListItem> employeeListItems = (List<EmployeeListItem>) modelMap.get("listOfEmployeeItems");
		assertEquals(2, employeeListItems.size());
		
	}
	

	@Test
	public void deleteOK() {
		// Given
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		
		Shop shop = shopFactoryForTest.newShop();
		String code = shop.getCode();
		
		// When
		String viewName = shopController.delete(redirectAttributes, code);
		
		// Then
		verify(shopService).delete(code);
		assertEquals("redirect:/shop", viewName);
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"delete.ok"));
	}

	@Test
	public void deleteException() {
		// Given
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		
		Shop shop = shopFactoryForTest.newShop();
		String code = shop.getCode();
		
		Exception exception = new RuntimeException("test exception");
		doThrow(exception).when(shopService).delete(code);
		
		// When
		String viewName = shopController.delete(redirectAttributes, code);
		
		// Then
		verify(shopService).delete(code);
		assertEquals("redirect:/shop", viewName);
		Mockito.verify(messageHelper).addException(redirectAttributes, "shop.error.delete", exception);
	}
	
	
}
