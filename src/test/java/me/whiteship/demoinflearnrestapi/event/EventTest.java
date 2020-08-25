package me.whiteship.demoinflearnrestapi.event;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

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

}
