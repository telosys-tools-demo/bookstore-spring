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
import org.demo.bean.Workgroup;
import org.demo.test.WorkgroupFactoryForTest;

//--- Services 
import org.demo.business.service.WorkgroupService;


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
public class WorkgroupControllerTest {
	
	@InjectMocks
	private WorkgroupController workgroupController;
	@Mock
	private WorkgroupService workgroupService;
	@Mock
	private MessageHelper messageHelper;
	@Mock
	private MessageSource messageSource;

	private WorkgroupFactoryForTest workgroupFactoryForTest = new WorkgroupFactoryForTest();


	private void givenPopulateModel() {
	}

	@Test
	public void list() {
		// Given
		Model model = new ExtendedModelMap();
		
		List<Workgroup> list = new ArrayList<Workgroup>();
		when(workgroupService.findAll()).thenReturn(list);
		
		// When
		String viewName = workgroupController.list(model);
		
		// Then
		assertEquals("workgroup/list", viewName);
		Map<String,?> modelMap = model.asMap();
		assertEquals(list, modelMap.get("list"));
	}
	
	@Test
	public void formForCreate() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		// When
		String viewName = workgroupController.formForCreate(model);
		
		// Then
		assertEquals("workgroup/form", viewName);
		
		Map<String,?> modelMap = model.asMap();
		
		assertNull(((Workgroup)modelMap.get("workgroup")).getId());
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/workgroup/create", modelMap.get("saveAction"));
		
	}
	
	@Test
	public void formForUpdate() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		Workgroup workgroup = workgroupFactoryForTest.newWorkgroup();
		Short id = workgroup.getId();
		when(workgroupService.findById(id)).thenReturn(workgroup);
		
		// When
		String viewName = workgroupController.formForUpdate(model, id);
		
		// Then
		assertEquals("workgroup/form", viewName);
		
		Map<String,?> modelMap = model.asMap();
		
		assertEquals(workgroup, (Workgroup) modelMap.get("workgroup"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/workgroup/update", modelMap.get("saveAction"));
		
	}
	
	@Test
	public void createOk() {
		// Given
		Model model = new ExtendedModelMap();
		
		Workgroup workgroup = workgroupFactoryForTest.newWorkgroup();
		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		Workgroup workgroupCreated = new Workgroup();
		when(workgroupService.create(workgroup)).thenReturn(workgroupCreated); 
		
		// When
		String viewName = workgroupController.create(workgroup, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("redirect:/workgroup/form/"+workgroup.getId(), viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(workgroupCreated, (Workgroup) modelMap.get("workgroup"));
		assertEquals(null, modelMap.get("mode"));
		assertEquals(null, modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"save.ok"));
	}

	@Test
	public void createBindingResultErrors() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		Workgroup workgroup = workgroupFactoryForTest.newWorkgroup();
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(true);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		// When
		String viewName = workgroupController.create(workgroup, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("workgroup/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(workgroup, (Workgroup) modelMap.get("workgroup"));
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/workgroup/create", modelMap.get("saveAction"));
		
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

		Workgroup workgroup = workgroupFactoryForTest.newWorkgroup();
		
		Exception exception = new RuntimeException("test exception");
		when(workgroupService.create(workgroup)).thenThrow(exception);
		
		// When
		String viewName = workgroupController.create(workgroup, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("workgroup/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(workgroup, (Workgroup) modelMap.get("workgroup"));
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/workgroup/create", modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addException(model, "workgroup.error.create", exception);
		
	}

	@Test
	public void updateOk() {
		// Given
		Model model = new ExtendedModelMap();
		
		Workgroup workgroup = workgroupFactoryForTest.newWorkgroup();
		Short id = workgroup.getId();

		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		Workgroup workgroupSaved = new Workgroup();
		workgroupSaved.setId(id);
		when(workgroupService.update(workgroup)).thenReturn(workgroupSaved); 
		
		// When
		String viewName = workgroupController.update(workgroup, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("redirect:/workgroup/form/"+workgroup.getId(), viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(workgroupSaved, (Workgroup) modelMap.get("workgroup"));
		assertEquals(null, modelMap.get("mode"));
		assertEquals(null, modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"save.ok"));
	}

	@Test
	public void updateBindingResultErrors() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		Workgroup workgroup = workgroupFactoryForTest.newWorkgroup();
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(true);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		// When
		String viewName = workgroupController.update(workgroup, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("workgroup/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(workgroup, (Workgroup) modelMap.get("workgroup"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/workgroup/update", modelMap.get("saveAction"));
		
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

		Workgroup workgroup = workgroupFactoryForTest.newWorkgroup();
		
		Exception exception = new RuntimeException("test exception");
		when(workgroupService.update(workgroup)).thenThrow(exception);
		
		// When
		String viewName = workgroupController.update(workgroup, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("workgroup/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(workgroup, (Workgroup) modelMap.get("workgroup"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/workgroup/update", modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addException(model, "workgroup.error.update", exception);
		
	}
	

	@Test
	public void deleteOK() {
		// Given
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		
		Workgroup workgroup = workgroupFactoryForTest.newWorkgroup();
		Short id = workgroup.getId();
		
		// When
		String viewName = workgroupController.delete(redirectAttributes, id);
		
		// Then
		verify(workgroupService).delete(id);
		assertEquals("redirect:/workgroup", viewName);
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"delete.ok"));
	}

	@Test
	public void deleteException() {
		// Given
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		
		Workgroup workgroup = workgroupFactoryForTest.newWorkgroup();
		Short id = workgroup.getId();
		
		Exception exception = new RuntimeException("test exception");
		doThrow(exception).when(workgroupService).delete(id);
		
		// When
		String viewName = workgroupController.delete(redirectAttributes, id);
		
		// Then
		verify(workgroupService).delete(id);
		assertEquals("redirect:/workgroup", viewName);
		Mockito.verify(messageHelper).addException(redirectAttributes, "workgroup.error.delete", exception);
	}
	
	
}
