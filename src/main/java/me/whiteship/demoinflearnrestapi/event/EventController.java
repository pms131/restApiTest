package me.whiteship.demoinflearnrestapi.event;

import java.net.URI;
import java.util.Optional;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import me.whiteship.demoinflearnrestapi.common.ErrorsResource;
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
			return badRequest(errors);
		}
		
		eventValidator.validate(eventDto, errors);
		
		if(errors.hasErrors()) {
			return badRequest(errors);
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
		eventResource.add(Link.of("/docs/index.html#resources-events-create").withRel("profile"));
		return ResponseEntity.created(createdUri).body(eventResource);
	}
	
	@GetMapping
	public ResponseEntity queryEvent(Pageable pageable, PagedResourcesAssembler<Event> assembler) {
		Page<Event> page = this.eventRepository.findAll(pageable);
		var pagedResource = assembler.toModel(page, e -> new EventResource(e));
		pagedResource.add(Link.of("/docs/index.html#resources-events-list").withRel("profile"));
		return ResponseEntity.ok(pagedResource);
	}
	
	
	@GetMapping("/{id}")
	public ResponseEntity getEvent(@PathVariable Integer id ) {
		Optional<Event> optionalEvent =  eventRepository.findById(id);
		if ( optionalEvent.isEmpty() ) {
			return ResponseEntity.notFound().build();
		}
		
		Event event = optionalEvent.get();
		EventResource eventResource = new EventResource(event);
		eventResource.add(Link.of("/docs/index.html#resources-events-get").withRel("profile"));
		
		return ResponseEntity.ok(eventResource);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity updateEvent(@PathVariable Integer id, @RequestBody @Valid EventDto eventDto, Errors errors) {
		Optional<Event> optionalEvent = this.eventRepository.findById(id);
		
		if(optionalEvent.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		
		// @Valid에 의해 도메인 유효성 검증
		if (errors.hasErrors()) {
			return this.badRequest(errors);
		}
		
		eventValidator.validate(eventDto, errors);
		
		// 직접 유효성 체크를 위해 만든 eventValidator.vaildate 메소드에 의해 유효성 검증 
		if (errors.hasErrors()) {
			return this.badRequest(errors);
		}
		
		Event existingEvent = optionalEvent.get();
		
		this.modelMapper.map(eventDto, existingEvent);
		
		Event updateEvent = this.eventRepository.save(existingEvent);
		
		EventResource eventResource = new EventResource(updateEvent);
		eventResource.add(Link.of("/docs/index.html#resources-events-update").withRel("profile"));
		
		return ResponseEntity.ok(eventResource);
		
	}
	

	private ResponseEntity badRequest(Errors errors) {
		return ResponseEntity.badRequest().body(new ErrorsResource(errors));
	}
}
