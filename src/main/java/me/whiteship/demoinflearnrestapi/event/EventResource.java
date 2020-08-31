package me.whiteship.demoinflearnrestapi.event;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

public class EventResource extends EntityModel<Event>{

	public EventResource(Event event, Link... links) {
		super(event, links);
		add(WebMvcLinkBuilder.linkTo(EventController.class).slash(event.getId()).withSelfRel());
	}

	
	
	/*
	@JsonUnwrapped
	private Event event;

	public EventResource(Event event) {
		this.event = event;
	}

	public Event getEvent() {
		return event;
	}
	*/
	
}
