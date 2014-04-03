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
import org.demo.bean.Author;
import org.demo.test.AuthorFactoryForTest;

//--- Services 
import org.demo.business.service.AuthorService;


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
public class AuthorControllerTest {
	
	@InjectMocks
	private AuthorController authorController;
	@Mock
	private AuthorService authorService;
	@Mock
	private MessageHelper messageHelper;
	@Mock
	private MessageSource messageSource;

	private AuthorFactoryForTest authorFactoryForTest = new AuthorFactoryForTest();


	private void givenPopulateModel() {
	}

	@Test
	public void list() {
		// Given
		Model model = new ExtendedModelMap();
		
		List<Author> list = new ArrayList<Author>();
		when(authorService.findAll()).thenReturn(list);
		
		// When
		String viewName = authorController.list(model);
		
		// Then
		assertEquals("author/list", viewName);
		Map<String,?> modelMap = model.asMap();
		assertEquals(list, modelMap.get("list"));
	}
	
	@Test
	public void formForCreate() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		// When
		String viewName = authorController.formForCreate(model);
		
		// Then
		assertEquals("author/form", viewName);
		
		Map<String,?> modelMap = model.asMap();
		
		assertNull(((Author)modelMap.get("author")).getId());
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/author/create", modelMap.get("saveAction"));
		
	}
	
	@Test
	public void formForUpdate() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		Author author = authorFactoryForTest.newAuthor();
		Integer id = author.getId();
		when(authorService.findById(id)).thenReturn(author);
		
		// When
		String viewName = authorController.formForUpdate(model, id);
		
		// Then
		assertEquals("author/form", viewName);
		
		Map<String,?> modelMap = model.asMap();
		
		assertEquals(author, (Author) modelMap.get("author"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/author/update", modelMap.get("saveAction"));
		
	}
	
	@Test
	public void createOk() {
		// Given
		Model model = new ExtendedModelMap();
		
		Author author = authorFactoryForTest.newAuthor();
		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		Author authorCreated = new Author();
		when(authorService.create(author)).thenReturn(authorCreated); 
		
		// When
		String viewName = authorController.create(author, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("redirect:/author/form/"+author.getId(), viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(authorCreated, (Author) modelMap.get("author"));
		assertEquals(null, modelMap.get("mode"));
		assertEquals(null, modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"save.ok"));
	}

	@Test
	public void createBindingResultErrors() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		Author author = authorFactoryForTest.newAuthor();
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(true);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		// When
		String viewName = authorController.create(author, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("author/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(author, (Author) modelMap.get("author"));
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/author/create", modelMap.get("saveAction"));
		
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

		Author author = authorFactoryForTest.newAuthor();
		
		Exception exception = new RuntimeException("test exception");
		when(authorService.create(author)).thenThrow(exception);
		
		// When
		String viewName = authorController.create(author, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("author/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(author, (Author) modelMap.get("author"));
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/author/create", modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addException(model, "author.error.create", exception);
		
	}

	@Test
	public void updateOk() {
		// Given
		Model model = new ExtendedModelMap();
		
		Author author = authorFactoryForTest.newAuthor();
		Integer id = author.getId();

		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		Author authorSaved = new Author();
		authorSaved.setId(id);
		when(authorService.update(author)).thenReturn(authorSaved); 
		
		// When
		String viewName = authorController.update(author, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("redirect:/author/form/"+author.getId(), viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(authorSaved, (Author) modelMap.get("author"));
		assertEquals(null, modelMap.get("mode"));
		assertEquals(null, modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"save.ok"));
	}

	@Test
	public void updateBindingResultErrors() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		Author author = authorFactoryForTest.newAuthor();
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(true);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		// When
		String viewName = authorController.update(author, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("author/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(author, (Author) modelMap.get("author"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/author/update", modelMap.get("saveAction"));
		
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

		Author author = authorFactoryForTest.newAuthor();
		
		Exception exception = new RuntimeException("test exception");
		when(authorService.update(author)).thenThrow(exception);
		
		// When
		String viewName = authorController.update(author, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("author/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(author, (Author) modelMap.get("author"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/author/update", modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addException(model, "author.error.update", exception);
		
	}
	

	@Test
	public void deleteOK() {
		// Given
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		
		Author author = authorFactoryForTest.newAuthor();
		Integer id = author.getId();
		
		// When
		String viewName = authorController.delete(redirectAttributes, id);
		
		// Then
		verify(authorService).delete(id);
		assertEquals("redirect:/author", viewName);
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"delete.ok"));
	}

	@Test
	public void deleteException() {
		// Given
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		
		Author author = authorFactoryForTest.newAuthor();
		Integer id = author.getId();
		
		Exception exception = new RuntimeException("test exception");
		doThrow(exception).when(authorService).delete(id);
		
		// When
		String viewName = authorController.delete(redirectAttributes, id);
		
		// Then
		verify(authorService).delete(id);
		assertEquals("redirect:/author", viewName);
		Mockito.verify(messageHelper).addException(redirectAttributes, "author.error.delete", exception);
	}
	
	
}
