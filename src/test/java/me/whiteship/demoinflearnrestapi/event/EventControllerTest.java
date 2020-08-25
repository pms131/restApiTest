package me.whiteship.demoinflearnrestapi.event;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@WebMvcTest
public class EventControllerTest {

	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@MockBean
	EventRepository eventRepository;
	
	@Test
	public void createEvent() throws Exception {
		Event event = Event.builder()
				.name("Spring")
				.description("REST API Development with Spring")
				.beginEnrollmentDateTime(LocalDateTime.of(2020, 8, 24, 16, 45))
				.closeEnrollmentDateTime(LocalDateTime.of(2020, 8, 25, 16, 45))
				.beginEnrollmentDateTime(LocalDateTime.of(2020, 8, 26, 16, 45))
				.endEventDateTime(LocalDateTime.of(2020, 8, 27, 16, 45))
				.baseprice(100)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("강남역 D2 스타트업 팩토리")
				.build();
		
		event.setId(10);
		
		// Mock객체는 return 되는 값이 모두 null -> stubbing (어떤 식으로 동작하라)을 해줘야 한다.
		Mockito.when(eventRepository.save(event)).thenReturn(event);


		mockMvc.perform(post("/api/events/")
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.accept(MediaTypes.HAL_JSON)
						.content(objectMapper.writeValueAsBytes(event)))
			   .andDo(print())
			   .andExpect(status().isCreated())
			   .andExpect(jsonPath("id").exists())
			   .andExpect(header().exists(HttpHeaders.LOCATION))
			   .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
			   ;
	}
}
