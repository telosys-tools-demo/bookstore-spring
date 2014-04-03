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
import org.demo.bean.Review;
import org.demo.bean.Book;
import org.demo.bean.Customer;
import org.demo.test.ReviewFactoryForTest;
import org.demo.test.BookFactoryForTest;
import org.demo.test.CustomerFactoryForTest;

//--- Services 
import org.demo.business.service.ReviewService;
import org.demo.business.service.BookService;
import org.demo.business.service.CustomerService;

//--- List Items 
import org.demo.web.listitem.BookListItem;
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
public class ReviewControllerTest {
	
	@InjectMocks
	private ReviewController reviewController;
	@Mock
	private ReviewService reviewService;
	@Mock
	private MessageHelper messageHelper;
	@Mock
	private MessageSource messageSource;
	@Mock
	private BookService bookService; // Injected by Spring
	@Mock
	private CustomerService customerService; // Injected by Spring

	private ReviewFactoryForTest reviewFactoryForTest = new ReviewFactoryForTest();
	private BookFactoryForTest bookFactoryForTest = new BookFactoryForTest();
	private CustomerFactoryForTest customerFactoryForTest = new CustomerFactoryForTest();

	List<Book> books = new ArrayList<Book>();
	List<Customer> customers = new ArrayList<Customer>();

	private void givenPopulateModel() {
		Book book1 = bookFactoryForTest.newBook();
		Book book2 = bookFactoryForTest.newBook();
		List<Book> books = new ArrayList<Book>();
		books.add(book1);
		books.add(book2);
		when(bookService.findAll()).thenReturn(books);

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
		
		List<Review> list = new ArrayList<Review>();
		when(reviewService.findAll()).thenReturn(list);
		
		// When
		String viewName = reviewController.list(model);
		
		// Then
		assertEquals("review/list", viewName);
		Map<String,?> modelMap = model.asMap();
		assertEquals(list, modelMap.get("list"));
	}
	
	@Test
	public void formForCreate() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		// When
		String viewName = reviewController.formForCreate(model);
		
		// Then
		assertEquals("review/form", viewName);
		
		Map<String,?> modelMap = model.asMap();
		
		assertNull(((Review)modelMap.get("review")).getCustomerCode());
		assertNull(((Review)modelMap.get("review")).getBookId());
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/review/create", modelMap.get("saveAction"));
		
		@SuppressWarnings("unchecked")
		List<BookListItem> bookListItems = (List<BookListItem>) modelMap.get("listOfBookItems");
		assertEquals(2, bookListItems.size());
		
		@SuppressWarnings("unchecked")
		List<CustomerListItem> customerListItems = (List<CustomerListItem>) modelMap.get("listOfCustomerItems");
		assertEquals(2, customerListItems.size());
		
	}
	
	@Test
	public void formForUpdate() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		Review review = reviewFactoryForTest.newReview();
		String customerCode = review.getCustomerCode();
		Integer bookId = review.getBookId();
		when(reviewService.findById(customerCode, bookId)).thenReturn(review);
		
		// When
		String viewName = reviewController.formForUpdate(model, customerCode, bookId);
		
		// Then
		assertEquals("review/form", viewName);
		
		Map<String,?> modelMap = model.asMap();
		
		assertEquals(review, (Review) modelMap.get("review"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/review/update", modelMap.get("saveAction"));
		
	}
	
	@Test
	public void createOk() {
		// Given
		Model model = new ExtendedModelMap();
		
		Review review = reviewFactoryForTest.newReview();
		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		Review reviewCreated = new Review();
		when(reviewService.create(review)).thenReturn(reviewCreated); 
		
		// When
		String viewName = reviewController.create(review, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("redirect:/review/form/"+review.getCustomerCode()+"/"+review.getBookId(), viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(reviewCreated, (Review) modelMap.get("review"));
		assertEquals(null, modelMap.get("mode"));
		assertEquals(null, modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"save.ok"));
	}

	@Test
	public void createBindingResultErrors() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		Review review = reviewFactoryForTest.newReview();
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(true);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		// When
		String viewName = reviewController.create(review, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("review/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(review, (Review) modelMap.get("review"));
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/review/create", modelMap.get("saveAction"));
		
		@SuppressWarnings("unchecked")
		List<BookListItem> bookListItems = (List<BookListItem>) modelMap.get("listOfBookItems");
		assertEquals(2, bookListItems.size());
		
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

		Review review = reviewFactoryForTest.newReview();
		
		Exception exception = new RuntimeException("test exception");
		when(reviewService.create(review)).thenThrow(exception);
		
		// When
		String viewName = reviewController.create(review, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("review/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(review, (Review) modelMap.get("review"));
		assertEquals("create", modelMap.get("mode"));
		assertEquals("/review/create", modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addException(model, "review.error.create", exception);
		
		@SuppressWarnings("unchecked")
		List<BookListItem> bookListItems = (List<BookListItem>) modelMap.get("listOfBookItems");
		assertEquals(2, bookListItems.size());
		
		@SuppressWarnings("unchecked")
		List<CustomerListItem> customerListItems = (List<CustomerListItem>) modelMap.get("listOfCustomerItems");
		assertEquals(2, customerListItems.size());
		
	}

	@Test
	public void updateOk() {
		// Given
		Model model = new ExtendedModelMap();
		
		Review review = reviewFactoryForTest.newReview();
		String customerCode = review.getCustomerCode();
		Integer bookId = review.getBookId();

		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		Review reviewSaved = new Review();
		reviewSaved.setCustomerCode(customerCode);
		reviewSaved.setBookId(bookId);
		when(reviewService.update(review)).thenReturn(reviewSaved); 
		
		// When
		String viewName = reviewController.update(review, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("redirect:/review/form/"+review.getCustomerCode()+"/"+review.getBookId(), viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(reviewSaved, (Review) modelMap.get("review"));
		assertEquals(null, modelMap.get("mode"));
		assertEquals(null, modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"save.ok"));
	}

	@Test
	public void updateBindingResultErrors() {
		// Given
		Model model = new ExtendedModelMap();
		
		givenPopulateModel();
		
		Review review = reviewFactoryForTest.newReview();
		BindingResult bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(true);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
		
		// When
		String viewName = reviewController.update(review, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("review/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(review, (Review) modelMap.get("review"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/review/update", modelMap.get("saveAction"));
		
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

		Review review = reviewFactoryForTest.newReview();
		
		Exception exception = new RuntimeException("test exception");
		when(reviewService.update(review)).thenThrow(exception);
		
		// When
		String viewName = reviewController.update(review, bindingResult, model, redirectAttributes, httpServletRequest);
		
		// Then
		assertEquals("review/form", viewName);

		Map<String,?> modelMap = model.asMap();
		
		assertEquals(review, (Review) modelMap.get("review"));
		assertEquals("update", modelMap.get("mode"));
		assertEquals("/review/update", modelMap.get("saveAction"));
		
		Mockito.verify(messageHelper).addException(model, "review.error.update", exception);
		
	}
	

	@Test
	public void deleteOK() {
		// Given
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		
		Review review = reviewFactoryForTest.newReview();
		String customerCode = review.getCustomerCode();
		Integer bookId = review.getBookId();
		
		// When
		String viewName = reviewController.delete(redirectAttributes, customerCode, bookId);
		
		// Then
		verify(reviewService).delete(customerCode, bookId);
		assertEquals("redirect:/review", viewName);
		Mockito.verify(messageHelper).addMessage(redirectAttributes, new Message(MessageType.SUCCESS,"delete.ok"));
	}

	@Test
	public void deleteException() {
		// Given
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		
		Review review = reviewFactoryForTest.newReview();
		String customerCode = review.getCustomerCode();
		Integer bookId = review.getBookId();
		
		Exception exception = new RuntimeException("test exception");
		doThrow(exception).when(reviewService).delete(customerCode, bookId);
		
		// When
		String viewName = reviewController.delete(redirectAttributes, customerCode, bookId);
		
		// Then
		verify(reviewService).delete(customerCode, bookId);
		assertEquals("redirect:/review", viewName);
		Mockito.verify(messageHelper).addException(redirectAttributes, "review.error.delete", exception);
	}
	
	
}
