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
import org.demo.bean.Synopsis;
import org.demo.bean.Book;
import org.demo.test.SynopsisFactoryForTest;
import org.demo.test.BookFactoryForTest;

//--- Services 
import org.demo.business.service.SynopsisService;
import org.demo.business.service.BookService;

//--- List Items 
import org.demo.web.listitem.BookListItem;

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
public class SynopsisControllerTest {
	
	@InjectMocks
	private SynopsisController synopsisController;
	@Mock
	private SynopsisService synopsisService;
	@Mock
	private MessageHelper messageHelper;
	@Mock
	private MessageSource messageSource;
	@Mock
	private BookService bookService; // Injected by Spring

	private SynopsisFactoryForTest synopsisFactoryForTest = new SynopsisFactoryForTest();
	private BookFactoryForTest bookFactoryForTest = new BookFactoryForTest();

	List<Book> books = new ArrayList<Book>();

	private void givenPopulateModel() {
		Book book1 = bookFactoryForTest.newBook();
		Book book2 = bookFactoryForTest.newBook();
		List<Book> books = new ArrayList<Book>();
		books.add(book1);
		books.add(book2);
		when(bookService.findAll()).thenReturn(books);

	}

	@Test
	public void list() {
		// Given
		Model model = new ExtendedModelMap();
		
		List<Synopsis> list = new ArrayList<Synopsis>();
		when(synopsisService.findAll()).thenReturn(list);
		
		// When
		String viewName = synopsisController.list(model);
		
		// Then
		assertEquals("synopsis/list", viewName);
		Map<String,?> modelMap = model.asMap();
		assertEquals(list, modelMap.get("list"));
	}
	
	@Test
	public void formForCreate() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		// When
		String viewName = synopsisController.formForCreate(model);
		
		// Then
		assertEquals("synopsis/form", viewName);
		
		Map<String,?> modelMap = model.asMap();
		
		assertNull(((Synopsis)modelMap.get("synopsis")).getBookId());
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/synopsis/create", modelMap.get("saveAction"));
		
		@SuppressWarnings("unchecked")
		List<BookListItem> bookListItems = (List<BookListItem>) modelMap.get("listOfBookItems");
		assertEquals(2, bookListItems.size());
		
	}
	
	@Test
	public void formForUpdate() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		Synopsis synopsis = synopsisFactoryForTest.newSynopsis();
		Integer bookId = synopsis.getBookId();
		when(synopsisService.findById(bookId)).thenReturn(synopsis);
		
		// When
		String viewName = synopsisController.formForUpdate(model, bookId);
		
		// Then
		assertEquals("synopsis/form", viewName);
		
		Map<String,?> modelMap = model.asMap();
		
		assertEquals(synopsis, (Synopsis) modelMap.get("synopsis"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/synopsis/update", modelMap.get("saveAction"));
		
	}
	
	@Test
	public void createOk() {
		// Given
		Model model = new ExtendedModelMap();
		
		Synopsis synopsis = synopsisFactoryForTest.newSynopsis();
		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		Synopsis synopsisCreated = new Synopsis();
		when(synopsisService.create(synopsis)).thenReturn(synopsisCreated); 
		
		// When
		String viewName = synopsisController.create(synopsis, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("redirect:/synopsis/form/"+synopsis.getBookId(), viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(synopsisCreated, (Synopsis) modelMap.get("synopsis"));
		assertEquals(null, modelMap.get("mode"));
		assertEquals(null, modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"save.ok"));
	}

	@Test
	public void createBindingResultErrors() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		Synopsis synopsis = synopsisFactoryForTest.newSynopsis();
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(true);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		// When
		String viewName = synopsisController.create(synopsis, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("synopsis/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(synopsis, (Synopsis) modelMap.get("synopsis"));
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/synopsis/create", modelMap.get("saveAction"));
		
		@SuppressWarnings("unchecked")
		List<BookListItem> bookListItems = (List<BookListItem>) modelMap.get("listOfBookItems");
		assertEquals(2, bookListItems.size());
		
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

		Synopsis synopsis = synopsisFactoryForTest.newSynopsis();
		
		Exception exception = new RuntimeException("test exception");
		when(synopsisService.create(synopsis)).thenThrow(exception);
		
		// When
		String viewName = synopsisController.create(synopsis, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("synopsis/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(synopsis, (Synopsis) modelMap.get("synopsis"));
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/synopsis/create", modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addException(model, "synopsis.error.create", exception);
		
		@SuppressWarnings("unchecked")
		List<BookListItem> bookListItems = (List<BookListItem>) modelMap.get("listOfBookItems");
		assertEquals(2, bookListItems.size());
		
	}

	@Test
	public void updateOk() {
		// Given
		Model model = new ExtendedModelMap();
		
		Synopsis synopsis = synopsisFactoryForTest.newSynopsis();
		Integer bookId = synopsis.getBookId();

		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		Synopsis synopsisSaved = new Synopsis();
		synopsisSaved.setBookId(bookId);
		when(synopsisService.update(synopsis)).thenReturn(synopsisSaved); 
		
		// When
		String viewName = synopsisController.update(synopsis, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("redirect:/synopsis/form/"+synopsis.getBookId(), viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(synopsisSaved, (Synopsis) modelMap.get("synopsis"));
		assertEquals(null, modelMap.get("mode"));
		assertEquals(null, modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"save.ok"));
	}

	@Test
	public void updateBindingResultErrors() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		Synopsis synopsis = synopsisFactoryForTest.newSynopsis();
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(true);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		// When
		String viewName = synopsisController.update(synopsis, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("synopsis/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(synopsis, (Synopsis) modelMap.get("synopsis"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/synopsis/update", modelMap.get("saveAction"));
		
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

		Synopsis synopsis = synopsisFactoryForTest.newSynopsis();
		
		Exception exception = new RuntimeException("test exception");
		when(synopsisService.update(synopsis)).thenThrow(exception);
		
		// When
		String viewName = synopsisController.update(synopsis, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("synopsis/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(synopsis, (Synopsis) modelMap.get("synopsis"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/synopsis/update", modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addException(model, "synopsis.error.update", exception);
		
	}
	

	@Test
	public void deleteOK() {
		// Given
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		
		Synopsis synopsis = synopsisFactoryForTest.newSynopsis();
		Integer bookId = synopsis.getBookId();
		
		// When
		String viewName = synopsisController.delete(redirectAttributes, bookId);
		
		// Then
		verify(synopsisService).delete(bookId);
		assertEquals("redirect:/synopsis", viewName);
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"delete.ok"));
	}

	@Test
	public void deleteException() {
		// Given
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		
		Synopsis synopsis = synopsisFactoryForTest.newSynopsis();
		Integer bookId = synopsis.getBookId();
		
		Exception exception = new RuntimeException("test exception");
		doThrow(exception).when(synopsisService).delete(bookId);
		
		// When
		String viewName = synopsisController.delete(redirectAttributes, bookId);
		
		// Then
		verify(synopsisService).delete(bookId);
		assertEquals("redirect:/synopsis", viewName);
		Mockito.verify(messageHelper).addException(redirectAttributes, "synopsis.error.delete", exception);
	}
	
	
}
