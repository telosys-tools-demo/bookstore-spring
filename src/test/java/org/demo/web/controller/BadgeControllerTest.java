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
import org.demo.bean.Badge;
import org.demo.test.BadgeFactoryForTest;

//--- Services 
import org.demo.business.service.BadgeService;


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
public class BadgeControllerTest {
	
	@InjectMocks
	private BadgeController badgeController;
	@Mock
	private BadgeService badgeService;
	@Mock
	private MessageHelper messageHelper;
	@Mock
	private MessageSource messageSource;

	private BadgeFactoryForTest badgeFactoryForTest = new BadgeFactoryForTest();


	private void givenPopulateModel() {
	}

	@Test
	public void list() {
		// Given
		Model model = new ExtendedModelMap();
		
		List<Badge> list = new ArrayList<Badge>();
		when(badgeService.findAll()).thenReturn(list);
		
		// When
		String viewName = badgeController.list(model);
		
		// Then
		assertEquals("badge/list", viewName);
		Map<String,?> modelMap = model.asMap();
		assertEquals(list, modelMap.get("list"));
	}
	
	@Test
	public void formForCreate() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		// When
		String viewName = badgeController.formForCreate(model);
		
		// Then
		assertEquals("badge/form", viewName);
		
		Map<String,?> modelMap = model.asMap();
		
		assertNull(((Badge)modelMap.get("badge")).getBadgeNumber());
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/badge/create", modelMap.get("saveAction"));
		
	}
	
	@Test
	public void formForUpdate() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		Badge badge = badgeFactoryForTest.newBadge();
		Integer badgeNumber = badge.getBadgeNumber();
		when(badgeService.findById(badgeNumber)).thenReturn(badge);
		
		// When
		String viewName = badgeController.formForUpdate(model, badgeNumber);
		
		// Then
		assertEquals("badge/form", viewName);
		
		Map<String,?> modelMap = model.asMap();
		
		assertEquals(badge, (Badge) modelMap.get("badge"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/badge/update", modelMap.get("saveAction"));
		
	}
	
	@Test
	public void createOk() {
		// Given
		Model model = new ExtendedModelMap();
		
		Badge badge = badgeFactoryForTest.newBadge();
		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		Badge badgeCreated = new Badge();
		when(badgeService.create(badge)).thenReturn(badgeCreated); 
		
		// When
		String viewName = badgeController.create(badge, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("redirect:/badge/form/"+badge.getBadgeNumber(), viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(badgeCreated, (Badge) modelMap.get("badge"));
		assertEquals(null, modelMap.get("mode"));
		assertEquals(null, modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"save.ok"));
	}

	@Test
	public void createBindingResultErrors() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		Badge badge = badgeFactoryForTest.newBadge();
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(true);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		// When
		String viewName = badgeController.create(badge, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("badge/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(badge, (Badge) modelMap.get("badge"));
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/badge/create", modelMap.get("saveAction"));
		
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

		Badge badge = badgeFactoryForTest.newBadge();
		
		Exception exception = new RuntimeException("test exception");
		when(badgeService.create(badge)).thenThrow(exception);
		
		// When
		String viewName = badgeController.create(badge, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("badge/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(badge, (Badge) modelMap.get("badge"));
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/badge/create", modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addException(model, "badge.error.create", exception);
		
	}

	@Test
	public void updateOk() {
		// Given
		Model model = new ExtendedModelMap();
		
		Badge badge = badgeFactoryForTest.newBadge();
		Integer badgeNumber = badge.getBadgeNumber();

		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		Badge badgeSaved = new Badge();
		badgeSaved.setBadgeNumber(badgeNumber);
		when(badgeService.update(badge)).thenReturn(badgeSaved); 
		
		// When
		String viewName = badgeController.update(badge, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("redirect:/badge/form/"+badge.getBadgeNumber(), viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(badgeSaved, (Badge) modelMap.get("badge"));
		assertEquals(null, modelMap.get("mode"));
		assertEquals(null, modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"save.ok"));
	}

	@Test
	public void updateBindingResultErrors() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		Badge badge = badgeFactoryForTest.newBadge();
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(true);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		// When
		String viewName = badgeController.update(badge, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("badge/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(badge, (Badge) modelMap.get("badge"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/badge/update", modelMap.get("saveAction"));
		
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

		Badge badge = badgeFactoryForTest.newBadge();
		
		Exception exception = new RuntimeException("test exception");
		when(badgeService.update(badge)).thenThrow(exception);
		
		// When
		String viewName = badgeController.update(badge, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("badge/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(badge, (Badge) modelMap.get("badge"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/badge/update", modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addException(model, "badge.error.update", exception);
		
	}
	

	@Test
	public void deleteOK() {
		// Given
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		
		Badge badge = badgeFactoryForTest.newBadge();
		Integer badgeNumber = badge.getBadgeNumber();
		
		// When
		String viewName = badgeController.delete(redirectAttributes, badgeNumber);
		
		// Then
		verify(badgeService).delete(badgeNumber);
		assertEquals("redirect:/badge", viewName);
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"delete.ok"));
	}

	@Test
	public void deleteException() {
		// Given
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		
		Badge badge = badgeFactoryForTest.newBadge();
		Integer badgeNumber = badge.getBadgeNumber();
		
		Exception exception = new RuntimeException("test exception");
		doThrow(exception).when(badgeService).delete(badgeNumber);
		
		// When
		String viewName = badgeController.delete(redirectAttributes, badgeNumber);
		
		// Then
		verify(badgeService).delete(badgeNumber);
		assertEquals("redirect:/badge", viewName);
		Mockito.verify(messageHelper).addException(redirectAttributes, "badge.error.delete", exception);
	}
	
	
}
