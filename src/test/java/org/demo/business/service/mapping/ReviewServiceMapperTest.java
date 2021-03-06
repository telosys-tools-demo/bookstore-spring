/*
 * Created on 3 avr. 2014 ( Time 19:39:43 )
 * Generated by Telosys Tools Generator ( version 2.1.0 )
 */
package org.demo.business.service.mapping;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;
import org.demo.bean.Review;
import org.demo.bean.jpa.ReviewEntity;
import org.demo.test.MockValues;

/**
 * Test : Mapping between entity beans and display beans.
 */
public class ReviewServiceMapperTest {

	private ReviewServiceMapper reviewServiceMapper;

	private static ModelMapper modelMapper = new ModelMapper();

	private MockValues mockValues = new MockValues();
	
	
	@BeforeClass
	public static void setUp() {
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
	}
	
	@Before
	public void before() {
		reviewServiceMapper = new ReviewServiceMapper();
		reviewServiceMapper.setModelMapper(modelMapper);
	}
	
	/**
	 * Mapping from 'ReviewEntity' to 'Review'
	 * @param reviewEntity
	 */
	@Test
	public void testMapReviewEntityToReview() {
		// Given
		ReviewEntity reviewEntity = new ReviewEntity();
		reviewEntity.setReviewText(mockValues.nextString(32700));
		reviewEntity.setReviewNote(mockValues.nextInteger());
		reviewEntity.setCreation(mockValues.nextDate());
		reviewEntity.setLastUpdate(mockValues.nextDate());
		
		// When
		Review review = reviewServiceMapper.mapReviewEntityToReview(reviewEntity);
		
		// Then
		assertEquals(reviewEntity.getReviewText(), review.getReviewText());
		assertEquals(reviewEntity.getReviewNote(), review.getReviewNote());
		assertEquals(reviewEntity.getCreation(), review.getCreation());
		assertEquals(reviewEntity.getLastUpdate(), review.getLastUpdate());
	}
	
	/**
	 * Test : Mapping from 'Review' to 'ReviewEntity'
	 */
	@Test
	public void testMapReviewToReviewEntity() {
		// Given
		Review review = new Review();
		review.setReviewText(mockValues.nextString(32700));
		review.setReviewNote(mockValues.nextInteger());
		review.setCreation(mockValues.nextDate());
		review.setLastUpdate(mockValues.nextDate());

		ReviewEntity reviewEntity = new ReviewEntity();
		
		// When
		reviewServiceMapper.mapReviewToReviewEntity(review, reviewEntity);
		
		// Then
		assertEquals(review.getReviewText(), reviewEntity.getReviewText());
		assertEquals(review.getReviewNote(), reviewEntity.getReviewNote());
		assertEquals(review.getCreation(), reviewEntity.getCreation());
		assertEquals(review.getLastUpdate(), reviewEntity.getLastUpdate());
	}

}