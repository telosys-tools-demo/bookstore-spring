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
import org.demo.bean.Employee;
import org.demo.bean.Shop;
import org.demo.bean.Workgroup;
import org.demo.bean.Badge;
import org.demo.test.EmployeeFactoryForTest;
import org.demo.test.ShopFactoryForTest;
import org.demo.test.WorkgroupFactoryForTest;
import org.demo.test.BadgeFactoryForTest;

//--- Services 
import org.demo.business.service.EmployeeService;
import org.demo.business.service.ShopService;
import org.demo.business.service.WorkgroupService;
import org.demo.business.service.BadgeService;

//--- List Items 
import org.demo.web.listitem.ShopListItem;
import org.demo.web.listitem.WorkgroupListItem;
import org.demo.web.listitem.BadgeListItem;

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
public class EmployeeControllerTest {
	
	@InjectMocks
	private EmployeeController employeeController;
	@Mock
	private EmployeeService employeeService;
	@Mock
	private MessageHelper messageHelper;
	@Mock
	private MessageSource messageSource;
	@Mock
	private ShopService shopService; // Injected by Spring
	@Mock
	private WorkgroupService workgroupService; // Injected by Spring
	@Mock
	private BadgeService badgeService; // Injected by Spring

	private EmployeeFactoryForTest employeeFactoryForTest = new EmployeeFactoryForTest();
	private ShopFactoryForTest shopFactoryForTest = new ShopFactoryForTest();
	private WorkgroupFactoryForTest workgroupFactoryForTest = new WorkgroupFactoryForTest();
	private BadgeFactoryForTest badgeFactoryForTest = new BadgeFactoryForTest();

	List<Shop> shops = new ArrayList<Shop>();
	List<Workgroup> workgroups = new ArrayList<Workgroup>();
	List<Badge> badges = new ArrayList<Badge>();

	private void givenPopulateModel() {
		Shop shop1 = shopFactoryForTest.newShop();
		Shop shop2 = shopFactoryForTest.newShop();
		List<Shop> shops = new ArrayList<Shop>();
		shops.add(shop1);
		shops.add(shop2);
		when(shopService.findAll()).thenReturn(shops);

		Workgroup workgroup1 = workgroupFactoryForTest.newWorkgroup();
		Workgroup workgroup2 = workgroupFactoryForTest.newWorkgroup();
		List<Workgroup> workgroups = new ArrayList<Workgroup>();
		workgroups.add(workgroup1);
		workgroups.add(workgroup2);
		when(workgroupService.findAll()).thenReturn(workgroups);

		Badge badge1 = badgeFactoryForTest.newBadge();
		Badge badge2 = badgeFactoryForTest.newBadge();
		List<Badge> badges = new ArrayList<Badge>();
		badges.add(badge1);
		badges.add(badge2);
		when(badgeService.findAll()).thenReturn(badges);

	}

	@Test
	public void list() {
		// Given
		Model model = new ExtendedModelMap();
		
		List<Employee> list = new ArrayList<Employee>();
		when(employeeService.findAll()).thenReturn(list);
		
		// When
		String viewName = employeeController.list(model);
		
		// Then
		assertEquals("employee/list", viewName);
		Map<String,?> modelMap = model.asMap();
		assertEquals(list, modelMap.get("list"));
	}
	
	@Test
	public void formForCreate() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		// When
		String viewName = employeeController.formForCreate(model);
		
		// Then
		assertEquals("employee/form", viewName);
		
		Map<String,?> modelMap = model.asMap();
		
		assertNull(((Employee)modelMap.get("employee")).getCode());
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/employee/create", modelMap.get("saveAction"));
		
		@SuppressWarnings("unchecked")
		List<ShopListItem> shopListItems = (List<ShopListItem>) modelMap.get("listOfShopItems");
		assertEquals(2, shopListItems.size());
		
		@SuppressWarnings("unchecked")
		List<WorkgroupListItem> workgroupListItems = (List<WorkgroupListItem>) modelMap.get("listOfWorkgroupItems");
		assertEquals(2, workgroupListItems.size());
		
		@SuppressWarnings("unchecked")
		List<BadgeListItem> badgeListItems = (List<BadgeListItem>) modelMap.get("listOfBadgeItems");
		assertEquals(2, badgeListItems.size());
		
	}
	
	@Test
	public void formForUpdate() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		Employee employee = employeeFactoryForTest.newEmployee();
		String code = employee.getCode();
		when(employeeService.findById(code)).thenReturn(employee);
		
		// When
		String viewName = employeeController.formForUpdate(model, code);
		
		// Then
		assertEquals("employee/form", viewName);
		
		Map<String,?> modelMap = model.asMap();
		
		assertEquals(employee, (Employee) modelMap.get("employee"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/employee/update", modelMap.get("saveAction"));
		
		List<ShopListItem> shopListItems = (List<ShopListItem>) modelMap.get("listOfShopItems");
		assertEquals(2, shopListItems.size());
		
		List<BadgeListItem> badgeListItems = (List<BadgeListItem>) modelMap.get("listOfBadgeItems");
		assertEquals(2, badgeListItems.size());
		
	}
	
	@Test
	public void createOk() {
		// Given
		Model model = new ExtendedModelMap();
		
		Employee employee = employeeFactoryForTest.newEmployee();
		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		Employee employeeCreated = new Employee();
		when(employeeService.create(employee)).thenReturn(employeeCreated); 
		
		// When
		String viewName = employeeController.create(employee, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("redirect:/employee/form/"+employee.getCode(), viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(employeeCreated, (Employee) modelMap.get("employee"));
		assertEquals(null, modelMap.get("mode"));
		assertEquals(null, modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"save.ok"));
	}

	@Test
	public void createBindingResultErrors() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		Employee employee = employeeFactoryForTest.newEmployee();
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(true);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		// When
		String viewName = employeeController.create(employee, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("employee/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(employee, (Employee) modelMap.get("employee"));
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/employee/create", modelMap.get("saveAction"));
		
		@SuppressWarnings("unchecked")
		List<ShopListItem> shopListItems = (List<ShopListItem>) modelMap.get("listOfShopItems");
		assertEquals(2, shopListItems.size());
		
		@SuppressWarnings("unchecked")
		List<WorkgroupListItem> workgroupListItems = (List<WorkgroupListItem>) modelMap.get("listOfWorkgroupItems");
		assertEquals(2, workgroupListItems.size());
		
		@SuppressWarnings("unchecked")
		List<BadgeListItem> badgeListItems = (List<BadgeListItem>) modelMap.get("listOfBadgeItems");
		assertEquals(2, badgeListItems.size());
		
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

		Employee employee = employeeFactoryForTest.newEmployee();
		
		Exception exception = new RuntimeException("test exception");
		when(employeeService.create(employee)).thenThrow(exception);
		
		// When
		String viewName = employeeController.create(employee, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("employee/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(employee, (Employee) modelMap.get("employee"));
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/employee/create", modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addException(model, "employee.error.create", exception);
		
		@SuppressWarnings("unchecked")
		List<ShopListItem> shopListItems = (List<ShopListItem>) modelMap.get("listOfShopItems");
		assertEquals(2, shopListItems.size());
		
		@SuppressWarnings("unchecked")
		List<WorkgroupListItem> workgroupListItems = (List<WorkgroupListItem>) modelMap.get("listOfWorkgroupItems");
		assertEquals(2, workgroupListItems.size());
		
		@SuppressWarnings("unchecked")
		List<BadgeListItem> badgeListItems = (List<BadgeListItem>) modelMap.get("listOfBadgeItems");
		assertEquals(2, badgeListItems.size());
		
	}

	@Test
	public void updateOk() {
		// Given
		Model model = new ExtendedModelMap();
		
		Employee employee = employeeFactoryForTest.newEmployee();
		String code = employee.getCode();

		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		Employee employeeSaved = new Employee();
		employeeSaved.setCode(code);
		when(employeeService.update(employee)).thenReturn(employeeSaved); 
		
		// When
		String viewName = employeeController.update(employee, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("redirect:/employee/form/"+employee.getCode(), viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(employeeSaved, (Employee) modelMap.get("employee"));
		assertEquals(null, modelMap.get("mode"));
		assertEquals(null, modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"save.ok"));
	}

	@Test
	public void updateBindingResultErrors() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		Employee employee = employeeFactoryForTest.newEmployee();
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(true);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		// When
		String viewName = employeeController.update(employee, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("employee/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(employee, (Employee) modelMap.get("employee"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/employee/update", modelMap.get("saveAction"));
		
		@SuppressWarnings("unchecked")
		List<ShopListItem> shopListItems = (List<ShopListItem>) modelMap.get("listOfShopItems");
		assertEquals(2, shopListItems.size());
		
		@SuppressWarnings("unchecked")
		List<BadgeListItem> badgeListItems = (List<BadgeListItem>) modelMap.get("listOfBadgeItems");
		assertEquals(2, badgeListItems.size());
		
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

		Employee employee = employeeFactoryForTest.newEmployee();
		
		Exception exception = new RuntimeException("test exception");
		when(employeeService.update(employee)).thenThrow(exception);
		
		// When
		String viewName = employeeController.update(employee, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("employee/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(employee, (Employee) modelMap.get("employee"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/employee/update", modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addException(model, "employee.error.update", exception);
		
		@SuppressWarnings("unchecked")
		List<ShopListItem> shopListItems = (List<ShopListItem>) modelMap.get("listOfShopItems");
		assertEquals(2, shopListItems.size());
		
		@SuppressWarnings("unchecked")
		List<BadgeListItem> badgeListItems = (List<BadgeListItem>) modelMap.get("listOfBadgeItems");
		assertEquals(2, badgeListItems.size());
		
	}
	

	@Test
	public void deleteOK() {
		// Given
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		
		Employee employee = employeeFactoryForTest.newEmployee();
		String code = employee.getCode();
		
		// When
		String viewName = employeeController.delete(redirectAttributes, code);
		
		// Then
		verify(employeeService).delete(code);
		assertEquals("redirect:/employee", viewName);
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"delete.ok"));
	}

	@Test
	public void deleteException() {
		// Given
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		
		Employee employee = employeeFactoryForTest.newEmployee();
		String code = employee.getCode();
		
		Exception exception = new RuntimeException("test exception");
		doThrow(exception).when(employeeService).delete(code);
		
		// When
		String viewName = employeeController.delete(redirectAttributes, code);
		
		// Then
		verify(employeeService).delete(code);
		assertEquals("redirect:/employee", viewName);
		Mockito.verify(messageHelper).addException(redirectAttributes, "employee.error.delete", exception);
	}
	
	
}
