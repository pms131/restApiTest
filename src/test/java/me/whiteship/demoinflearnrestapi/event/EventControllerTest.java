package me.whiteship.demoinflearnrestapi.event;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.whiteship.demoinflearnrestapi.common.TestDescription;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTest {

	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@Test
	@TestDescription("정상적으로 이벤트를 생성하는 테스트")
	public void createEvent() throws Exception {
		EventDto event = EventDto.builder()
				.name("Spring")
				.description("REST API Development with Spring")
				.beginEnrollmentDateTime(LocalDateTime.of(2020, 8, 24, 16, 45))
				.closeEnrollmentDateTime(LocalDateTime.of(2020, 8, 25, 16, 45))
				.beginEnrollmentDateTime(LocalDateTime.of(2020, 8, 26, 16, 45))
				.endEventDateTime(LocalDateTime.of(2020, 8, 27, 16, 45))
				.basePrice(100)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("강남역 D2 스타트업 팩토리")
				.build();
		
		// Mock객체는 return 되는 값이 모두 null -> stubbing (어떤 식으로 동작하라)을 해줘야 한다.
		// Controller에서 EventDto 객체를 새로 생성하여 주입하여 주기 때문에, event 객체가 아닌 다른 객체가 들어옴 -> 더이상 Slice Test X


		mockMvc.perform(post("/api/events/")
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.accept(MediaTypes.HAL_JSON)
						.content(objectMapper.writeValueAsBytes(event)))
			   .andDo(print())
			   .andExpect(status().isBadRequest())
			   .andExpect(jsonPath("id").exists())
			   .andExpect(header().exists(HttpHeaders.LOCATION))
			   .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
			   .andExpect(jsonPath("id").value(Matchers.not(100)))
			   .andExpect(jsonPath("free").value(Matchers.not(true)))
			   .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
			   ;
	}
	
	
	@Test
	@TestDescription("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
	public void createEvent_Bad_Request() throws Exception {
		Event event = Event.builder()
				.id(100)
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
				.free(true)
				.offline(false)
				.eventStatus(EventStatus.DRAFT)
				.build();
		
		// Mock객체는 return 되는 값이 모두 null -> stubbing (어떤 식으로 동작하라)을 해줘야 한다.
		// Controller에서 EventDto 객체를 새로 생성하여 주입하여 주기 때문에, event 객체가 아닌 다른 객체가 들어옴 -> 더이상 Slice Test X


		mockMvc.perform(post("/api/events/")
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.accept(MediaTypes.HAL_JSON)
						.content(objectMapper.writeValueAsBytes(event)))
			   .andDo(print())
			   .andExpect(status().isCreated())
			   .andExpect(jsonPath("id").exists())
			   .andExpect(header().exists(HttpHeaders.LOCATION))
			   .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
			   .andExpect(jsonPath("id").value(Matchers.not(100)))
			   .andExpect(jsonPath("free").value(Matchers.not(true)))
			   .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
			   ;
	}
	
	@Test
	@TestDescription("입력 값이 비어있는 경우에 에러가 발생하는 테스트")
	public void createEvent_Bad_Request_Empty_input() throws Exception {
		EventDto eventDto = EventDto.builder().build();
		
		this.mockMvc.perform(post("/api/events")
					.contentType(MediaType.APPLICATION_JSON_UTF8)
					.content(this.objectMapper.writeValueAsString(eventDto)))
				.andExpect(status().isBadRequest())
					;
	}
	
	@Test
	@TestDescription("입력 값이 잘못된 경우에 에러가 발생하는 테스트")
	public void createEvent_Bad_Request_Wrong_input() throws Exception {
		EventDto eventDto = EventDto.builder()
				.name("Spring")
				.description("REST API Development with Spring")
				.beginEnrollmentDateTime(LocalDateTime.of(2020, 8, 26, 16, 45))
				.closeEnrollmentDateTime(LocalDateTime.of(2020, 8, 25, 16, 45))
				.beginEnrollmentDateTime(LocalDateTime.of(2020, 8, 24, 16, 45))
				.endEventDateTime(LocalDateTime.of(2020, 8, 23, 16, 45))
				.basePrice(10000)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("강남역 D2 스타트업 팩토리")
				.build()
				;
		
		this.mockMvc.perform(post("/api/events")
					.contentType(MediaType.APPLICATION_JSON_UTF8)
					.content(this.objectMapper.writeValueAsString(eventDto)))
				.andExpect(status().isBadRequest())
					;
	}
	
}
