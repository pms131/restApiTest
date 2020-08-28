package me.whiteship.demoinflearnrestapi.event;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;

@RunWith(JUnitParamsRunner.class)
class EventTest {

	@Test
	public void builder() {
		Event event = Event.builder()
				.name("Inflearn Spring REST API")
				.description("REST API development with Spring")
				.build();
		assertThat(event).isNotNull();
	}
	
	@Test
	public void javaBean() {
		// Given
		Event event = new Event();
		String name = "Event";
		String description = "Spring";
		
		// When
		event.setName(name);
		event.setDescription(description);
		
		// Then
		assertThat(event.getName()).isEqualTo(name);
		assertThat(event.getDescription()).isEqualTo(description);
		}
	
	@Test
	@ParameterizedTest
	/*@CsvSource({
	        "0, 0, true",
	        "0, 100, false",
	        "100, 0, false",
	})*/
	@MethodSource
	public void testFree(int basePrice, int maxPrice, boolean isFree) {
		// Given
		Event event = Event.builder()
						   .baseprice(basePrice)
						   .maxPrice(maxPrice)
						   .build();
		
		// When
		event.update();
		
		// Then
		assertThat(event.isFree()).isEqualTo(isFree);		
	}
	
	private static Stream<Arguments> testFree() {
		return Stream.of(
				Arguments.of(0, 0, true),
				Arguments.of(0, 100, false),
				Arguments.of(100, 0, false),
				Arguments.of(100, 100, false)
				);
					 
	}
	
	@Test
	@ParameterizedTest
	@MethodSource
	public void testOffline(String location, boolean isOffline) {
		// Given
		Event event = Event.builder()
						   .location(location)
						   .build();
		
		// When
		event.update();
		
		// Then
		assertThat(event.isOffline()).isEqualTo(isOffline);

	}

	private static Stream<Arguments> testOffline() {
		return Stream.of(
				Arguments.of("강남역", true),
				Arguments.of(null, false),
				Arguments.of("			", false)
				);
	}
}
