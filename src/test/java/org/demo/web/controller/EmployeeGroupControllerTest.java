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
import org.demo.bean.EmployeeGroup;
import org.demo.test.EmployeeGroupFactoryForTest;

//--- Services 
import org.demo.business.service.EmployeeGroupService;


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
public class EmployeeGroupControllerTest {
	
	@InjectMocks
	private EmployeeGroupController employeeGroupController;
	@Mock
	private EmployeeGroupService employeeGroupService;
	@Mock
	private MessageHelper messageHelper;
	@Mock
	private MessageSource messageSource;

	private EmployeeGroupFactoryForTest employeeGroupFactoryForTest = new EmployeeGroupFactoryForTest();


	private void givenPopulateModel() {
	}

	@Test
	public void list() {
		// Given
		Model model = new ExtendedModelMap();
		
		List<EmployeeGroup> list = new ArrayList<EmployeeGroup>();
		when(employeeGroupService.findAll()).thenReturn(list);
		
		// When
		String viewName = employeeGroupController.list(model);
		
		// Then
		assertEquals("employeeGroup/list", viewName);
		Map<String,?> modelMap = model.asMap();
		assertEquals(list, modelMap.get("list"));
	}
	
	@Test
	public void formForCreate() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		// When
		String viewName = employeeGroupController.formForCreate(model);
		
		// Then
		assertEquals("employeeGroup/form", viewName);
		
		Map<String,?> modelMap = model.asMap();
		
		assertNull(((EmployeeGroup)modelMap.get("employeeGroup")).getEmployeeCode());
		assertNull(((EmployeeGroup)modelMap.get("employeeGroup")).getGroupId());
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/employeeGroup/create", modelMap.get("saveAction"));
		
	}
	
	@Test
	public void formForUpdate() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		EmployeeGroup employeeGroup = employeeGroupFactoryForTest.newEmployeeGroup();
		String employeeCode = employeeGroup.getEmployeeCode();
		Short groupId = employeeGroup.getGroupId();
		when(employeeGroupService.findById(employeeCode, groupId)).thenReturn(employeeGroup);
		
		// When
		String viewName = employeeGroupController.formForUpdate(model, employeeCode, groupId);
		
		// Then
		assertEquals("employeeGroup/form", viewName);
		
		Map<String,?> modelMap = model.asMap();
		
		assertEquals(employeeGroup, (EmployeeGroup) modelMap.get("employeeGroup"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/employeeGroup/update", modelMap.get("saveAction"));
		
	}
	
	@Test
	public void createOk() {
		// Given
		Model model = new ExtendedModelMap();
		
		EmployeeGroup employeeGroup = employeeGroupFactoryForTest.newEmployeeGroup();
		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		EmployeeGroup employeeGroupCreated = new EmployeeGroup();
		when(employeeGroupService.create(employeeGroup)).thenReturn(employeeGroupCreated); 
		
		// When
		String viewName = employeeGroupController.create(employeeGroup, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("redirect:/employeeGroup/form/"+employeeGroup.getEmployeeCode()+"/"+employeeGroup.getGroupId(), viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(employeeGroupCreated, (EmployeeGroup) modelMap.get("employeeGroup"));
		assertEquals(null, modelMap.get("mode"));
		assertEquals(null, modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"save.ok"));
	}

	@Test
	public void createBindingResultErrors() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		EmployeeGroup employeeGroup = employeeGroupFactoryForTest.newEmployeeGroup();
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(true);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		// When
		String viewName = employeeGroupController.create(employeeGroup, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("employeeGroup/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(employeeGroup, (EmployeeGroup) modelMap.get("employeeGroup"));
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/employeeGroup/create", modelMap.get("saveAction"));
		
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

		EmployeeGroup employeeGroup = employeeGroupFactoryForTest.newEmployeeGroup();
		
		Exception exception = new RuntimeException("test exception");
		when(employeeGroupService.create(employeeGroup)).thenThrow(exception);
		
		// When
		String viewName = employeeGroupController.create(employeeGroup, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("employeeGroup/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(employeeGroup, (EmployeeGroup) modelMap.get("employeeGroup"));
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/employeeGroup/create", modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addException(model, "employeeGroup.error.create", exception);
		
	}

	@Test
	public void updateOk() {
		// Given
		Model model = new ExtendedModelMap();
		
		EmployeeGroup employeeGroup = employeeGroupFactoryForTest.newEmployeeGroup();
		String employeeCode = employeeGroup.getEmployeeCode();
		Short groupId = employeeGroup.getGroupId();

		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		EmployeeGroup employeeGroupSaved = new EmployeeGroup();
		employeeGroupSaved.setEmployeeCode(employeeCode);
		employeeGroupSaved.setGroupId(groupId);
		when(employeeGroupService.update(employeeGroup)).thenReturn(employeeGroupSaved); 
		
		// When
		String viewName = employeeGroupController.update(employeeGroup, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("redirect:/employeeGroup/form/"+employeeGroup.getEmployeeCode()+"/"+employeeGroup.getGroupId(), viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(employeeGroupSaved, (EmployeeGroup) modelMap.get("employeeGroup"));
		assertEquals(null, modelMap.get("mode"));
		assertEquals(null, modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"save.ok"));
	}

	@Test
	public void updateBindingResultErrors() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		EmployeeGroup employeeGroup = employeeGroupFactoryForTest.newEmployeeGroup();
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(true);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		// When
		String viewName = employeeGroupController.update(employeeGroup, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("employeeGroup/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(employeeGroup, (EmployeeGroup) modelMap.get("employeeGroup"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/employeeGroup/update", modelMap.get("saveAction"));
		
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

		EmployeeGroup employeeGroup = employeeGroupFactoryForTest.newEmployeeGroup();
		
		Exception exception = new RuntimeException("test exception");
		when(employeeGroupService.update(employeeGroup)).thenThrow(exception);
		
		// When
		String viewName = employeeGroupController.update(employeeGroup, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("employeeGroup/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(employeeGroup, (EmployeeGroup) modelMap.get("employeeGroup"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/employeeGroup/update", modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addException(model, "employeeGroup.error.update", exception);
		
	}
	

	@Test
	public void deleteOK() {
		// Given
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		
		EmployeeGroup employeeGroup = employeeGroupFactoryForTest.newEmployeeGroup();
		String employeeCode = employeeGroup.getEmployeeCode();
		Short groupId = employeeGroup.getGroupId();
		
		// When
		String viewName = employeeGroupController.delete(redirectAttributes, employeeCode, groupId);
		
		// Then
		verify(employeeGroupService).delete(employeeCode, groupId);
		assertEquals("redirect:/employeeGroup", viewName);
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"delete.ok"));
	}

	@Test
	public void deleteException() {
		// Given
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		
		EmployeeGroup employeeGroup = employeeGroupFactoryForTest.newEmployeeGroup();
		String employeeCode = employeeGroup.getEmployeeCode();
		Short groupId = employeeGroup.getGroupId();
		
		Exception exception = new RuntimeException("test exception");
		doThrow(exception).when(employeeGroupService).delete(employeeCode, groupId);
		
		// When
		String viewName = employeeGroupController.delete(redirectAttributes, employeeCode, groupId);
		
		// Then
		verify(employeeGroupService).delete(employeeCode, groupId);
		assertEquals("redirect:/employeeGroup", viewName);
		Mockito.verify(messageHelper).addException(redirectAttributes, "employeeGroup.error.delete", exception);
	}
	
	
}
