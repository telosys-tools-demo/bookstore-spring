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
import org.demo.bean.BookOrder;
import org.demo.bean.Shop;
import org.demo.bean.Employee;
import org.demo.bean.Customer;
import org.demo.test.BookOrderFactoryForTest;
import org.demo.test.ShopFactoryForTest;
import org.demo.test.EmployeeFactoryForTest;
import org.demo.test.CustomerFactoryForTest;

//--- Services 
import org.demo.business.service.BookOrderService;
import org.demo.business.service.ShopService;
import org.demo.business.service.EmployeeService;
import org.demo.business.service.CustomerService;

//--- List Items 
import org.demo.web.listitem.ShopListItem;
import org.demo.web.listitem.EmployeeListItem;
import org.demo.web.listitem.CustomerListItem;

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
public class BookOrderControllerTest {
	
	@InjectMocks
	private BookOrderController bookOrderController;
	@Mock
	private BookOrderService bookOrderService;
	@Mock
	private MessageHelper messageHelper;
	@Mock
	private MessageSource messageSource;
	@Mock
	private ShopService shopService; // Injected by Spring
	@Mock
	private EmployeeService employeeService; // Injected by Spring
	@Mock
	private CustomerService customerService; // Injected by Spring

	private BookOrderFactoryForTest bookOrderFactoryForTest = new BookOrderFactoryForTest();
	private ShopFactoryForTest shopFactoryForTest = new ShopFactoryForTest();
	private EmployeeFactoryForTest employeeFactoryForTest = new EmployeeFactoryForTest();
	private CustomerFactoryForTest customerFactoryForTest = new CustomerFactoryForTest();

	List<Shop> shops = new ArrayList<Shop>();
	List<Employee> employees = new ArrayList<Employee>();
	List<Customer> customers = new ArrayList<Customer>();

	private void givenPopulateModel() {
		Shop shop1 = shopFactoryForTest.newShop();
		Shop shop2 = shopFactoryForTest.newShop();
		List<Shop> shops = new ArrayList<Shop>();
		shops.add(shop1);
		shops.add(shop2);
		when(shopService.findAll()).thenReturn(shops);

		Employee employee1 = employeeFactoryForTest.newEmployee();
		Employee employee2 = employeeFactoryForTest.newEmployee();
		List<Employee> employees = new ArrayList<Employee>();
		employees.add(employee1);
		employees.add(employee2);
		when(employeeService.findAll()).thenReturn(employees);

		Customer customer1 = customerFactoryForTest.newCustomer();
		Customer customer2 = customerFactoryForTest.newCustomer();
		List<Customer> customers = new ArrayList<Customer>();
		customers.add(customer1);
		customers.add(customer2);
		when(customerService.findAll()).thenReturn(customers);

	}

	@Test
	public void list() {
		// Given
		Model model = new ExtendedModelMap();
		
		List<BookOrder> list = new ArrayList<BookOrder>();
		when(bookOrderService.findAll()).thenReturn(list);
		
		// When
		String viewName = bookOrderController.list(model);
		
		// Then
		assertEquals("bookOrder/list", viewName);
		Map<String,?> modelMap = model.asMap();
		assertEquals(list, modelMap.get("list"));
	}
	
	@Test
	public void formForCreate() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		// When
		String viewName = bookOrderController.formForCreate(model);
		
		// Then
		assertEquals("bookOrder/form", viewName);
		
		Map<String,?> modelMap = model.asMap();
		
		assertNull(((BookOrder)modelMap.get("bookOrder")).getId());
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/bookOrder/create", modelMap.get("saveAction"));
		
		@SuppressWarnings("unchecked")
		List<ShopListItem> shopListItems = (List<ShopListItem>) modelMap.get("listOfShopItems");
		assertEquals(2, shopListItems.size());
		
		@SuppressWarnings("unchecked")
		List<EmployeeListItem> employeeListItems = (List<EmployeeListItem>) modelMap.get("listOfEmployeeItems");
		assertEquals(2, employeeListItems.size());
		
		@SuppressWarnings("unchecked")
		List<CustomerListItem> customerListItems = (List<CustomerListItem>) modelMap.get("listOfCustomerItems");
		assertEquals(2, customerListItems.size());
		
	}
	
	@Test
	public void formForUpdate() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		BookOrder bookOrder = bookOrderFactoryForTest.newBookOrder();
		Integer id = bookOrder.getId();
		when(bookOrderService.findById(id)).thenReturn(bookOrder);
		
		// When
		String viewName = bookOrderController.formForUpdate(model, id);
		
		// Then
		assertEquals("bookOrder/form", viewName);
		
		Map<String,?> modelMap = model.asMap();
		
		assertEquals(bookOrder, (BookOrder) modelMap.get("bookOrder"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/bookOrder/update", modelMap.get("saveAction"));
		
		List<ShopListItem> shopListItems = (List<ShopListItem>) modelMap.get("listOfShopItems");
		assertEquals(2, shopListItems.size());
		
		List<CustomerListItem> customerListItems = (List<CustomerListItem>) modelMap.get("listOfCustomerItems");
		assertEquals(2, customerListItems.size());
		
		List<EmployeeListItem> employeeListItems = (List<EmployeeListItem>) modelMap.get("listOfEmployeeItems");
		assertEquals(2, employeeListItems.size());
		
	}
	
	@Test
	public void createOk() {
		// Given
		Model model = new ExtendedModelMap();
		
		BookOrder bookOrder = bookOrderFactoryForTest.newBookOrder();
		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		BookOrder bookOrderCreated = new BookOrder();
		when(bookOrderService.create(bookOrder)).thenReturn(bookOrderCreated); 
		
		// When
		String viewName = bookOrderController.create(bookOrder, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("redirect:/bookOrder/form/"+bookOrder.getId(), viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(bookOrderCreated, (BookOrder) modelMap.get("bookOrder"));
		assertEquals(null, modelMap.get("mode"));
		assertEquals(null, modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"save.ok"));
	}

	@Test
	public void createBindingResultErrors() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		BookOrder bookOrder = bookOrderFactoryForTest.newBookOrder();
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(true);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		// When
		String viewName = bookOrderController.create(bookOrder, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("bookOrder/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(bookOrder, (BookOrder) modelMap.get("bookOrder"));
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/bookOrder/create", modelMap.get("saveAction"));
		
		@SuppressWarnings("unchecked")
		List<ShopListItem> shopListItems = (List<ShopListItem>) modelMap.get("listOfShopItems");
		assertEquals(2, shopListItems.size());
		
		@SuppressWarnings("unchecked")
		List<EmployeeListItem> employeeListItems = (List<EmployeeListItem>) modelMap.get("listOfEmployeeItems");
		assertEquals(2, employeeListItems.size());
		
		@SuppressWarnings("unchecked")
		List<CustomerListItem> customerListItems = (List<CustomerListItem>) modelMap.get("listOfCustomerItems");
		assertEquals(2, customerListItems.size());
		
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

		BookOrder bookOrder = bookOrderFactoryForTest.newBookOrder();
		
		Exception exception = new RuntimeException("test exception");
		when(bookOrderService.create(bookOrder)).thenThrow(exception);
		
		// When
		String viewName = bookOrderController.create(bookOrder, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("bookOrder/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(bookOrder, (BookOrder) modelMap.get("bookOrder"));
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/bookOrder/create", modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addException(model, "bookOrder.error.create", exception);
		
		@SuppressWarnings("unchecked")
		List<ShopListItem> shopListItems = (List<ShopListItem>) modelMap.get("listOfShopItems");
		assertEquals(2, shopListItems.size());
		
		@SuppressWarnings("unchecked")
		List<EmployeeListItem> employeeListItems = (List<EmployeeListItem>) modelMap.get("listOfEmployeeItems");
		assertEquals(2, employeeListItems.size());
		
		@SuppressWarnings("unchecked")
		List<CustomerListItem> customerListItems = (List<CustomerListItem>) modelMap.get("listOfCustomerItems");
		assertEquals(2, customerListItems.size());
		
	}

	@Test
	public void updateOk() {
		// Given
		Model model = new ExtendedModelMap();
		
		BookOrder bookOrder = bookOrderFactoryForTest.newBookOrder();
		Integer id = bookOrder.getId();

		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		BookOrder bookOrderSaved = new BookOrder();
		bookOrderSaved.setId(id);
		when(bookOrderService.update(bookOrder)).thenReturn(bookOrderSaved); 
		
		// When
		String viewName = bookOrderController.update(bookOrder, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("redirect:/bookOrder/form/"+bookOrder.getId(), viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(bookOrderSaved, (BookOrder) modelMap.get("bookOrder"));
		assertEquals(null, modelMap.get("mode"));
		assertEquals(null, modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"save.ok"));
	}

	@Test
	public void updateBindingResultErrors() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		BookOrder bookOrder = bookOrderFactoryForTest.newBookOrder();
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(true);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		// When
		String viewName = bookOrderController.update(bookOrder, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("bookOrder/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(bookOrder, (BookOrder) modelMap.get("bookOrder"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/bookOrder/update", modelMap.get("saveAction"));
		
		@SuppressWarnings("unchecked")
		List<ShopListItem> shopListItems = (List<ShopListItem>) modelMap.get("listOfShopItems");
		assertEquals(2, shopListItems.size());
		
		@SuppressWarnings("unchecked")
		List<CustomerListItem> customerListItems = (List<CustomerListItem>) modelMap.get("listOfCustomerItems");
		assertEquals(2, customerListItems.size());
		
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

		BookOrder bookOrder = bookOrderFactoryForTest.newBookOrder();
		
		Exception exception = new RuntimeException("test exception");
		when(bookOrderService.update(bookOrder)).thenThrow(exception);
		
		// When
		String viewName = bookOrderController.update(bookOrder, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("bookOrder/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(bookOrder, (BookOrder) modelMap.get("bookOrder"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/bookOrder/update", modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addException(model, "bookOrder.error.update", exception);
		
		@SuppressWarnings("unchecked")
		List<ShopListItem> shopListItems = (List<ShopListItem>) modelMap.get("listOfShopItems");
		assertEquals(2, shopListItems.size());
		
		@SuppressWarnings("unchecked")
		List<CustomerListItem> customerListItems = (List<CustomerListItem>) modelMap.get("listOfCustomerItems");
		assertEquals(2, customerListItems.size());
		
		@SuppressWarnings("unchecked")
		List<EmployeeListItem> employeeListItems = (List<EmployeeListItem>) modelMap.get("listOfEmployeeItems");
		assertEquals(2, employeeListItems.size());
		
	}
	

	@Test
	public void deleteOK() {
		// Given
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		
		BookOrder bookOrder = bookOrderFactoryForTest.newBookOrder();
		Integer id = bookOrder.getId();
		
		// When
		String viewName = bookOrderController.delete(redirectAttributes, id);
		
		// Then
		verify(bookOrderService).delete(id);
		assertEquals("redirect:/bookOrder", viewName);
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"delete.ok"));
	}

	@Test
	public void deleteException() {
		// Given
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		
		BookOrder bookOrder = bookOrderFactoryForTest.newBookOrder();
		Integer id = bookOrder.getId();
		
		Exception exception = new RuntimeException("test exception");
		doThrow(exception).when(bookOrderService).delete(id);
		
		// When
		String viewName = bookOrderController.delete(redirectAttributes, id);
		
		// Then
		verify(bookOrderService).delete(id);
		assertEquals("redirect:/bookOrder", viewName);
		Mockito.verify(messageHelper).addException(redirectAttributes, "bookOrder.error.delete", exception);
	}
	
	
}
