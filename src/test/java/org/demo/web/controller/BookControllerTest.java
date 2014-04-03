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
import org.demo.bean.Book;
import org.demo.bean.Author;
import org.demo.bean.Publisher;
import org.demo.test.BookFactoryForTest;
import org.demo.test.AuthorFactoryForTest;
import org.demo.test.PublisherFactoryForTest;

//--- Services 
import org.demo.business.service.BookService;
import org.demo.business.service.AuthorService;
import org.demo.business.service.PublisherService;

//--- List Items 
import org.demo.web.listitem.AuthorListItem;
import org.demo.web.listitem.PublisherListItem;

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
public class BookControllerTest {
	
	@InjectMocks
	private BookController bookController;
	@Mock
	private BookService bookService;
	@Mock
	private MessageHelper messageHelper;
	@Mock
	private MessageSource messageSource;
	@Mock
	private AuthorService authorService; // Injected by Spring
	@Mock
	private PublisherService publisherService; // Injected by Spring

	private BookFactoryForTest bookFactoryForTest = new BookFactoryForTest();
	private AuthorFactoryForTest authorFactoryForTest = new AuthorFactoryForTest();
	private PublisherFactoryForTest publisherFactoryForTest = new PublisherFactoryForTest();

	List<Author> authors = new ArrayList<Author>();
	List<Publisher> publishers = new ArrayList<Publisher>();

	private void givenPopulateModel() {
		Author author1 = authorFactoryForTest.newAuthor();
		Author author2 = authorFactoryForTest.newAuthor();
		List<Author> authors = new ArrayList<Author>();
		authors.add(author1);
		authors.add(author2);
		when(authorService.findAll()).thenReturn(authors);

		Publisher publisher1 = publisherFactoryForTest.newPublisher();
		Publisher publisher2 = publisherFactoryForTest.newPublisher();
		List<Publisher> publishers = new ArrayList<Publisher>();
		publishers.add(publisher1);
		publishers.add(publisher2);
		when(publisherService.findAll()).thenReturn(publishers);

	}

	@Test
	public void list() {
		// Given
		Model model = new ExtendedModelMap();
		
		List<Book> list = new ArrayList<Book>();
		when(bookService.findAll()).thenReturn(list);
		
		// When
		String viewName = bookController.list(model);
		
		// Then
		assertEquals("book/list", viewName);
		Map<String,?> modelMap = model.asMap();
		assertEquals(list, modelMap.get("list"));
	}
	
	@Test
	public void formForCreate() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		// When
		String viewName = bookController.formForCreate(model);
		
		// Then
		assertEquals("book/form", viewName);
		
		Map<String,?> modelMap = model.asMap();
		
		assertNull(((Book)modelMap.get("book")).getId());
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/book/create", modelMap.get("saveAction"));
		
		@SuppressWarnings("unchecked")
		List<AuthorListItem> authorListItems = (List<AuthorListItem>) modelMap.get("listOfAuthorItems");
		assertEquals(2, authorListItems.size());
		
		@SuppressWarnings("unchecked")
		List<PublisherListItem> publisherListItems = (List<PublisherListItem>) modelMap.get("listOfPublisherItems");
		assertEquals(2, publisherListItems.size());
		
	}
	
	@Test
	public void formForUpdate() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		Book book = bookFactoryForTest.newBook();
		Integer id = book.getId();
		when(bookService.findById(id)).thenReturn(book);
		
		// When
		String viewName = bookController.formForUpdate(model, id);
		
		// Then
		assertEquals("book/form", viewName);
		
		Map<String,?> modelMap = model.asMap();
		
		assertEquals(book, (Book) modelMap.get("book"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/book/update", modelMap.get("saveAction"));
		
		List<PublisherListItem> publisherListItems = (List<PublisherListItem>) modelMap.get("listOfPublisherItems");
		assertEquals(2, publisherListItems.size());
		
		List<AuthorListItem> authorListItems = (List<AuthorListItem>) modelMap.get("listOfAuthorItems");
		assertEquals(2, authorListItems.size());
		
	}
	
	@Test
	public void createOk() {
		// Given
		Model model = new ExtendedModelMap();
		
		Book book = bookFactoryForTest.newBook();
		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		Book bookCreated = new Book();
		when(bookService.create(book)).thenReturn(bookCreated); 
		
		// When
		String viewName = bookController.create(book, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("redirect:/book/form/"+book.getId(), viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(bookCreated, (Book) modelMap.get("book"));
		assertEquals(null, modelMap.get("mode"));
		assertEquals(null, modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"save.ok"));
	}

	@Test
	public void createBindingResultErrors() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		Book book = bookFactoryForTest.newBook();
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(true);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		// When
		String viewName = bookController.create(book, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("book/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(book, (Book) modelMap.get("book"));
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/book/create", modelMap.get("saveAction"));
		
		@SuppressWarnings("unchecked")
		List<AuthorListItem> authorListItems = (List<AuthorListItem>) modelMap.get("listOfAuthorItems");
		assertEquals(2, authorListItems.size());
		
		@SuppressWarnings("unchecked")
		List<PublisherListItem> publisherListItems = (List<PublisherListItem>) modelMap.get("listOfPublisherItems");
		assertEquals(2, publisherListItems.size());
		
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

		Book book = bookFactoryForTest.newBook();
		
		Exception exception = new RuntimeException("test exception");
		when(bookService.create(book)).thenThrow(exception);
		
		// When
		String viewName = bookController.create(book, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("book/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(book, (Book) modelMap.get("book"));
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/book/create", modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addException(model, "book.error.create", exception);
		
		@SuppressWarnings("unchecked")
		List<AuthorListItem> authorListItems = (List<AuthorListItem>) modelMap.get("listOfAuthorItems");
		assertEquals(2, authorListItems.size());
		
		@SuppressWarnings("unchecked")
		List<PublisherListItem> publisherListItems = (List<PublisherListItem>) modelMap.get("listOfPublisherItems");
		assertEquals(2, publisherListItems.size());
		
	}

	@Test
	public void updateOk() {
		// Given
		Model model = new ExtendedModelMap();
		
		Book book = bookFactoryForTest.newBook();
		Integer id = book.getId();

		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		Book bookSaved = new Book();
		bookSaved.setId(id);
		when(bookService.update(book)).thenReturn(bookSaved); 
		
		// When
		String viewName = bookController.update(book, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("redirect:/book/form/"+book.getId(), viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(bookSaved, (Book) modelMap.get("book"));
		assertEquals(null, modelMap.get("mode"));
		assertEquals(null, modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"save.ok"));
	}

	@Test
	public void updateBindingResultErrors() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		Book book = bookFactoryForTest.newBook();
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(true);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		// When
		String viewName = bookController.update(book, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("book/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(book, (Book) modelMap.get("book"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/book/update", modelMap.get("saveAction"));
		
		@SuppressWarnings("unchecked")
		List<PublisherListItem> publisherListItems = (List<PublisherListItem>) modelMap.get("listOfPublisherItems");
		assertEquals(2, publisherListItems.size());
		
		@SuppressWarnings("unchecked")
		List<AuthorListItem> authorListItems = (List<AuthorListItem>) modelMap.get("listOfAuthorItems");
		assertEquals(2, authorListItems.size());
		
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

		Book book = bookFactoryForTest.newBook();
		
		Exception exception = new RuntimeException("test exception");
		when(bookService.update(book)).thenThrow(exception);
		
		// When
		String viewName = bookController.update(book, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("book/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(book, (Book) modelMap.get("book"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/book/update", modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addException(model, "book.error.update", exception);
		
		@SuppressWarnings("unchecked")
		List<PublisherListItem> publisherListItems = (List<PublisherListItem>) modelMap.get("listOfPublisherItems");
		assertEquals(2, publisherListItems.size());
		
		@SuppressWarnings("unchecked")
		List<AuthorListItem> authorListItems = (List<AuthorListItem>) modelMap.get("listOfAuthorItems");
		assertEquals(2, authorListItems.size());
		
	}
	

	@Test
	public void deleteOK() {
		// Given
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		
		Book book = bookFactoryForTest.newBook();
		Integer id = book.getId();
		
		// When
		String viewName = bookController.delete(redirectAttributes, id);
		
		// Then
		verify(bookService).delete(id);
		assertEquals("redirect:/book", viewName);
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"delete.ok"));
	}

	@Test
	public void deleteException() {
		// Given
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		
		Book book = bookFactoryForTest.newBook();
		Integer id = book.getId();
		
		Exception exception = new RuntimeException("test exception");
		doThrow(exception).when(bookService).delete(id);
		
		// When
		String viewName = bookController.delete(redirectAttributes, id);
		
		// Then
		verify(bookService).delete(id);
		assertEquals("redirect:/book", viewName);
		Mockito.verify(messageHelper).addException(redirectAttributes, "book.error.delete", exception);
	}
	
	
}
