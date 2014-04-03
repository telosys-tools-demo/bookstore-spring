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
import org.demo.bean.BookOrderItem;
import org.demo.bean.BookOrder;
import org.demo.bean.Book;
import org.demo.test.BookOrderItemFactoryForTest;
import org.demo.test.BookOrderFactoryForTest;
import org.demo.test.BookFactoryForTest;

//--- Services 
import org.demo.business.service.BookOrderItemService;
import org.demo.business.service.BookOrderService;
import org.demo.business.service.BookService;

//--- List Items 
import org.demo.web.listitem.BookOrderListItem;
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
public class BookOrderItemControllerTest {
	
	@InjectMocks
	private BookOrderItemController bookOrderItemController;
	@Mock
	private BookOrderItemService bookOrderItemService;
	@Mock
	private MessageHelper messageHelper;
	@Mock
	private MessageSource messageSource;
	@Mock
	private BookOrderService bookOrderService; // Injected by Spring
	@Mock
	private BookService bookService; // Injected by Spring

	private BookOrderItemFactoryForTest bookOrderItemFactoryForTest = new BookOrderItemFactoryForTest();
	private BookOrderFactoryForTest bookOrderFactoryForTest = new BookOrderFactoryForTest();
	private BookFactoryForTest bookFactoryForTest = new BookFactoryForTest();

	List<BookOrder> bookOrders = new ArrayList<BookOrder>();
	List<Book> books = new ArrayList<Book>();

	private void givenPopulateModel() {
		BookOrder bookOrder1 = bookOrderFactoryForTest.newBookOrder();
		BookOrder bookOrder2 = bookOrderFactoryForTest.newBookOrder();
		List<BookOrder> bookOrders = new ArrayList<BookOrder>();
		bookOrders.add(bookOrder1);
		bookOrders.add(bookOrder2);
		when(bookOrderService.findAll()).thenReturn(bookOrders);

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
		
		List<BookOrderItem> list = new ArrayList<BookOrderItem>();
		when(bookOrderItemService.findAll()).thenReturn(list);
		
		// When
		String viewName = bookOrderItemController.list(model);
		
		// Then
		assertEquals("bookOrderItem/list", viewName);
		Map<String,?> modelMap = model.asMap();
		assertEquals(list, modelMap.get("list"));
	}
	
	@Test
	public void formForCreate() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		// When
		String viewName = bookOrderItemController.formForCreate(model);
		
		// Then
		assertEquals("bookOrderItem/form", viewName);
		
		Map<String,?> modelMap = model.asMap();
		
		assertNull(((BookOrderItem)modelMap.get("bookOrderItem")).getBookOrderId());
		assertNull(((BookOrderItem)modelMap.get("bookOrderItem")).getBookId());
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/bookOrderItem/create", modelMap.get("saveAction"));
		
		@SuppressWarnings("unchecked")
		List<BookOrderListItem> bookOrderListItems = (List<BookOrderListItem>) modelMap.get("listOfBookOrderItems");
		assertEquals(2, bookOrderListItems.size());
		
		@SuppressWarnings("unchecked")
		List<BookListItem> bookListItems = (List<BookListItem>) modelMap.get("listOfBookItems");
		assertEquals(2, bookListItems.size());
		
	}
	
	@Test
	public void formForUpdate() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		BookOrderItem bookOrderItem = bookOrderItemFactoryForTest.newBookOrderItem();
		Integer bookOrderId = bookOrderItem.getBookOrderId();
		Integer bookId = bookOrderItem.getBookId();
		when(bookOrderItemService.findById(bookOrderId, bookId)).thenReturn(bookOrderItem);
		
		// When
		String viewName = bookOrderItemController.formForUpdate(model, bookOrderId, bookId);
		
		// Then
		assertEquals("bookOrderItem/form", viewName);
		
		Map<String,?> modelMap = model.asMap();
		
		assertEquals(bookOrderItem, (BookOrderItem) modelMap.get("bookOrderItem"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/bookOrderItem/update", modelMap.get("saveAction"));
		
	}
	
	@Test
	public void createOk() {
		// Given
		Model model = new ExtendedModelMap();
		
		BookOrderItem bookOrderItem = bookOrderItemFactoryForTest.newBookOrderItem();
		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		BookOrderItem bookOrderItemCreated = new BookOrderItem();
		when(bookOrderItemService.create(bookOrderItem)).thenReturn(bookOrderItemCreated); 
		
		// When
		String viewName = bookOrderItemController.create(bookOrderItem, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("redirect:/bookOrderItem/form/"+bookOrderItem.getBookOrderId()+"/"+bookOrderItem.getBookId(), viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(bookOrderItemCreated, (BookOrderItem) modelMap.get("bookOrderItem"));
		assertEquals(null, modelMap.get("mode"));
		assertEquals(null, modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"save.ok"));
	}

	@Test
	public void createBindingResultErrors() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		BookOrderItem bookOrderItem = bookOrderItemFactoryForTest.newBookOrderItem();
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(true);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		// When
		String viewName = bookOrderItemController.create(bookOrderItem, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("bookOrderItem/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(bookOrderItem, (BookOrderItem) modelMap.get("bookOrderItem"));
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/bookOrderItem/create", modelMap.get("saveAction"));
		
		@SuppressWarnings("unchecked")
		List<BookOrderListItem> bookOrderListItems = (List<BookOrderListItem>) modelMap.get("listOfBookOrderItems");
		assertEquals(2, bookOrderListItems.size());
		
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

		BookOrderItem bookOrderItem = bookOrderItemFactoryForTest.newBookOrderItem();
		
		Exception exception = new RuntimeException("test exception");
		when(bookOrderItemService.create(bookOrderItem)).thenThrow(exception);
		
		// When
		String viewName = bookOrderItemController.create(bookOrderItem, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("bookOrderItem/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(bookOrderItem, (BookOrderItem) modelMap.get("bookOrderItem"));
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/bookOrderItem/create", modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addException(model, "bookOrderItem.error.create", exception);
		
		@SuppressWarnings("unchecked")
		List<BookOrderListItem> bookOrderListItems = (List<BookOrderListItem>) modelMap.get("listOfBookOrderItems");
		assertEquals(2, bookOrderListItems.size());
		
		@SuppressWarnings("unchecked")
		List<BookListItem> bookListItems = (List<BookListItem>) modelMap.get("listOfBookItems");
		assertEquals(2, bookListItems.size());
		
	}

	@Test
	public void updateOk() {
		// Given
		Model model = new ExtendedModelMap();
		
		BookOrderItem bookOrderItem = bookOrderItemFactoryForTest.newBookOrderItem();
		Integer bookOrderId = bookOrderItem.getBookOrderId();
		Integer bookId = bookOrderItem.getBookId();

		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		BookOrderItem bookOrderItemSaved = new BookOrderItem();
		bookOrderItemSaved.setBookOrderId(bookOrderId);
		bookOrderItemSaved.setBookId(bookId);
		when(bookOrderItemService.update(bookOrderItem)).thenReturn(bookOrderItemSaved); 
		
		// When
		String viewName = bookOrderItemController.update(bookOrderItem, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("redirect:/bookOrderItem/form/"+bookOrderItem.getBookOrderId()+"/"+bookOrderItem.getBookId(), viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(bookOrderItemSaved, (BookOrderItem) modelMap.get("bookOrderItem"));
		assertEquals(null, modelMap.get("mode"));
		assertEquals(null, modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"save.ok"));
	}

	@Test
	public void updateBindingResultErrors() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		BookOrderItem bookOrderItem = bookOrderItemFactoryForTest.newBookOrderItem();
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(true);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		// When
		String viewName = bookOrderItemController.update(bookOrderItem, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("bookOrderItem/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(bookOrderItem, (BookOrderItem) modelMap.get("bookOrderItem"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/bookOrderItem/update", modelMap.get("saveAction"));
		
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

		BookOrderItem bookOrderItem = bookOrderItemFactoryForTest.newBookOrderItem();
		
		Exception exception = new RuntimeException("test exception");
		when(bookOrderItemService.update(bookOrderItem)).thenThrow(exception);
		
		// When
		String viewName = bookOrderItemController.update(bookOrderItem, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("bookOrderItem/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(bookOrderItem, (BookOrderItem) modelMap.get("bookOrderItem"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/bookOrderItem/update", modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addException(model, "bookOrderItem.error.update", exception);
		
	}
	

	@Test
	public void deleteOK() {
		// Given
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		
		BookOrderItem bookOrderItem = bookOrderItemFactoryForTest.newBookOrderItem();
		Integer bookOrderId = bookOrderItem.getBookOrderId();
		Integer bookId = bookOrderItem.getBookId();
		
		// When
		String viewName = bookOrderItemController.delete(redirectAttributes, bookOrderId, bookId);
		
		// Then
		verify(bookOrderItemService).delete(bookOrderId, bookId);
		assertEquals("redirect:/bookOrderItem", viewName);
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"delete.ok"));
	}

	@Test
	public void deleteException() {
		// Given
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		
		BookOrderItem bookOrderItem = bookOrderItemFactoryForTest.newBookOrderItem();
		Integer bookOrderId = bookOrderItem.getBookOrderId();
		Integer bookId = bookOrderItem.getBookId();
		
		Exception exception = new RuntimeException("test exception");
		doThrow(exception).when(bookOrderItemService).delete(bookOrderId, bookId);
		
		// When
		String viewName = bookOrderItemController.delete(redirectAttributes, bookOrderId, bookId);
		
		// Then
		verify(bookOrderItemService).delete(bookOrderId, bookId);
		assertEquals("redirect:/bookOrderItem", viewName);
		Mockito.verify(messageHelper).addException(redirectAttributes, "bookOrderItem.error.delete", exception);
	}
	
	
}
