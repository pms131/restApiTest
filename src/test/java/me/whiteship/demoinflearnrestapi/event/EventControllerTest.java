package me.whiteship.demoinflearnrestapi.event;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import me.whiteship.demoinflearnrestapi.common.BaseControllerTest;
import me.whiteship.demoinflearnrestapi.common.TestDescription;

public class EventControllerTest extends BaseControllerTest {
	@Autowired
	EventRepository eventRepository;
	
	@Test
	@TestDescription("정상적으로 이벤트를 생성하는 테스트")
	public void createEvent() throws Exception {
		EventDto event = EventDto.builder()
				.name("Spring")
				.description("REST API Development with Spring")
				.beginEnrollmentDateTime(LocalDateTime.of(2020, 8, 24, 16, 45))
				.closeEnrollmentDateTime(LocalDateTime.of(2020, 8, 25, 16, 45))
				.beginEventDateTime(LocalDateTime.of(2020, 8, 26, 16, 45))
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
			   .andExpect(jsonPath("id").exists())
			   .andExpect(header().exists(HttpHeaders.LOCATION))
			   .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
			   .andExpect(jsonPath("free").value(false))
			   .andExpect(jsonPath("offline").value(true))
			   .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
				/*
				 * .andExpect(jsonPath("_links.self").exists())
				 * .andExpect(jsonPath("_links.update-event").exists())
				 * .andExpect(jsonPath("_links.query-events").exists())
				 */
			   .andDo(document("create-event", 
					   links(
							   linkWithRel("self").description("link to self"),
							   linkWithRel("query-events").description("link to query events"),
							   linkWithRel("update-event").description("link to update an existing event"),
							   linkWithRel("profile").description("link to profile")
							   ),
					   requestHeaders(
							   	headerWithName(HttpHeaders.ACCEPT).description("accept header"),
							   	headerWithName(HttpHeaders.CONTENT_TYPE).description("content type")
							   ),
					   requestFields(
							   fieldWithPath("name").description("Name of new event"),
							   fieldWithPath("description").description("description of new event"),
							   fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
							   fieldWithPath("closeEnrollmentDateTime").description("date time of end of new event"),
							   fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
							   fieldWithPath("endEventDateTime").description("date time of end of new event"),
							   fieldWithPath("location").description("location of new event"),
							   fieldWithPath("basePrice").description("base price of new event"),
							   fieldWithPath("maxPrice").description("max price of new event"),
							   fieldWithPath("limitOfEnrollment").description("limit of enrollment")
							   ),
					   responseHeaders(
							   	headerWithName(HttpHeaders.LOCATION).description("location header"),
							   	headerWithName(HttpHeaders.CONTENT_TYPE).description("content type")
							   ),
					   responseFields(
							   fieldWithPath("id").description("identifier of new event"),
							   fieldWithPath("name").description("Name of new event"),
							   fieldWithPath("description").description("description of new event"),
							   fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
							   fieldWithPath("closeEnrollmentDateTime").description("date time of end of new event"),
							   fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
							   fieldWithPath("endEventDateTime").description("date time of end of new event"),
							   fieldWithPath("location").description("location of new event"),
							   fieldWithPath("basePrice").description("base price of new event"),
							   fieldWithPath("maxPrice").description("max price of new event"),
							   fieldWithPath("limitOfEnrollment").description("limit of enrollment"),
							   fieldWithPath("free").description("it tells is this event is free or not"),
							   fieldWithPath("offline").description("it tells is this event is offline meeting or not"),
							   fieldWithPath("eventStatus").description("event status"),
							   fieldWithPath("_links.self.href").description("link to self"),
							   fieldWithPath("_links.query-events.href").description("link to query event list"),
							   fieldWithPath("_links.update-event.href").description("link to update existing event"),
							   fieldWithPath("_links.profile.href").description("link to profile")
							   )
					   ))
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
				.beginEventDateTime(LocalDateTime.of(2020, 8, 26, 16, 45))
				.endEventDateTime(LocalDateTime.of(2020, 8, 27, 16, 45))
				.basePrice(100)
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
			   .andExpect(status().isBadRequest())
		/*
		 * .andExpect(jsonPath("id").exists())
		 * .andExpect(header().exists(HttpHeaders.LOCATION))
		 * .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
		 * .andExpect(jsonPath("id").value(Matchers.not(100)))
		 * .andExpect(jsonPath("free").value(Matchers.not(true)))
		 * .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
		 */
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
				.beginEventDateTime(LocalDateTime.of(2020, 8, 24, 16, 45))
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
					.andDo(print())
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("content[0].objectName").exists())
					//.andExpect(jsonPath("$[0].field").exists())
					.andExpect(jsonPath("content[0].defaultMessage").exists())
					.andExpect(jsonPath("content[0].code").exists())
					.andExpect(jsonPath("_links.index").exists())
					//.andExpect(jsonPath("$[0].rejectedValue").exists())
					;
	}
	
	@Test
	@TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
	public void queryEvents() throws Exception {
		//Given
		IntStream.range(0, 30).forEach(i -> {
			this.generateEvent(i);
		});
		
		//When
		ResultActions perform = this.mockMvc.perform(get("/api/events")
					.param("page", "1")
					.param("size", "10")
					.param("sort", "name,DESC")
				);
		
		//Then
		perform.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("page").exists())
				.andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
				.andExpect(jsonPath("_links.self").exists())
				.andExpect(jsonPath("_links.profile").exists())
				.andDo(document("query-events"))
				;
	}
	
	@Test
	@TestDescription("기존의 이벤트를 하나 조회하기")
	public void getEvent() throws Exception {
		// Given
		Event event = this.generateEvent(100);
		
		// When & Then
		this.mockMvc.perform(get("/api/events/{id}", event.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("name").exists())
				.andExpect(jsonPath("id").exists())
				.andExpect(jsonPath("_links.self").exists())
				.andExpect(jsonPath("_links.profile").exists())
				.andDo(document("get-an-event"))
				;
		;
	}


	@Test
	@TestDescription("없는 이벤트는 조회했을 때 404 응답받기")
	public void getEvent404() throws Exception {
		// When & Then
		this.mockMvc.perform(get("/api/events/18384"))
				.andExpect(status().isNotFound())
				;
		;
	}
	
	@Test
	@TestDescription("이벤트를 정상적으로 수정하기")
	public void updateEvent() throws Exception {
		// Given
		Event event = this.generateEvent(200);
		
		EventDto eventDto = this.modelMapper.map(event, EventDto.class);
		String eventName = "Updated Event";
		eventDto.setName(eventName);
						
		
		// When & Then
		mockMvc.perform(put("/api/events/{id}", event.getId())
					.contentType(MediaType.APPLICATION_JSON_UTF8)
					.content(this.objectMapper.writeValueAsString(eventDto))
				)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("id").exists())
			.andExpect(jsonPath("name").value(eventName))
			.andExpect(jsonPath("_links.self").exists())
			.andExpect(jsonPath("_links.profile").exists())
			.andDo(document("update-event"))
			;
			
	}
	
	@Test
	@TestDescription("입력값이 비어있는 경우에 이벤트수정 실패")
	public void updateEvent400_Empty() throws Exception {
		// Given
		Event event = this.generateEvent(200);
		
		EventDto eventDto = new EventDto();
						
		
		// When & Then
		mockMvc.perform(put("/api/events/{id}", event.getId())
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(this.objectMapper.writeValueAsString(eventDto))
			)
			.andDo(print())
			.andExpect(status().isBadRequest())
			;
			
	}
	
	@Test
	@TestDescription("입력값이 잘못된 경우에 이벤트수정 실패")
	public void updateEvent400_Wrong() throws Exception {
		// Given
		Event event = this.generateEvent(200);
		
		EventDto eventDto = this.modelMapper.map(event, EventDto.class);
		eventDto.setBasePrice(20000);
		eventDto.setMaxPrice(1000);
		
		// When & Then
		mockMvc.perform(put("/api/events/{id}", event.getId())
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(this.objectMapper.writeValueAsString(eventDto))
			)
			.andDo(print())
			.andExpect(status().isBadRequest())
			;
			
	}
	
	@Test
	@TestDescription("존재하지 않는 이벤트수정 실패")
	public void updateEvent404() throws Exception {
		// Given
		Event event = this.generateEvent(200);
		
		EventDto eventDto = this.modelMapper.map(event, EventDto.class);
		
		// When & Then
		mockMvc.perform(put("/api/events/124345")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(this.objectMapper.writeValueAsString(eventDto))
			)
			.andDo(print())
			.andExpect(status().isNotFound())
			;
			
	}
	
	private Event generateEvent(int index) {
		Event event = Event.builder()
				.name("event" + index)
				.description("test event")
				.beginEnrollmentDateTime(LocalDateTime.of(2020, 8, 24, 16, 45))
				.closeEnrollmentDateTime(LocalDateTime.of(2020, 8, 25, 16, 45))
				.beginEventDateTime(LocalDateTime.of(2020, 8, 26, 16, 45))
				.endEventDateTime(LocalDateTime.of(2020, 8, 27, 16, 45))
				.basePrice(100)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("강남역 D2 스타트업 팩토리")
				.build()
				;
		
		return this.eventRepository.save(event);
		
	}
}
