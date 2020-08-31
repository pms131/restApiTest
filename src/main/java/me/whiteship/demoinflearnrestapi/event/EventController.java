package me.whiteship.demoinflearnrestapi.event;

import java.net.URI;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {
	
	private final EventRepository eventRepository;
	
	private final ModelMapper modelMapper;
	
	private final EventValidator eventValidator;
		
	public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator) {
		this.eventRepository = eventRepository;
		this.modelMapper = modelMapper;
		this.eventValidator = eventValidator;
	}

	@PostMapping
	public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
		
		if(errors.hasErrors()) {
			return ResponseEntity.badRequest().body(errors);
		}
		
		eventValidator.validate(eventDto, errors);
		
		if(errors.hasErrors()) {
			return ResponseEntity.badRequest().body(errors);
		}
		
		Event event = modelMapper.map(eventDto, Event.class);
		
		// 실제 구현할 땐, Service단이 있으면 거기로 빼는것도 좋음
		event.update();
		
		Event newEvent = this.eventRepository.save(event);
		WebMvcLinkBuilder selfLinkBuilder = WebMvcLinkBuilder.linkTo(EventController.class).slash(newEvent.getId());
		URI createdUri = selfLinkBuilder.toUri();
		
		EventResource eventResource = new EventResource(event);
		eventResource.add(WebMvcLinkBuilder.linkTo(EventController.class).withRel("query-events"));
		//eventResource.add(selfLinkBuilder.withSelfRel());
		eventResource.add(selfLinkBuilder.withRel("update-event"));
		return ResponseEntity.created(createdUri).body(eventResource);
	}
}
