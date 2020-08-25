package me.whiteship.demoinflearnrestapi.event;

import java.net.URI;

import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {
	
	private final EventRepository eventRepository;
		
	public EventController(EventRepository eventRepository) {
		this.eventRepository = eventRepository;
	}

	@PostMapping()
	public ResponseEntity createEvent(@RequestBody Event event) {
		Event newEvent = this.eventRepository.save(event);
		URI createdUri = WebMvcLinkBuilder.linkTo(EventController.class).slash(newEvent.getId()).toUri();
		event.setId(10);
		return ResponseEntity.created(createdUri).body(event);
	}
}
