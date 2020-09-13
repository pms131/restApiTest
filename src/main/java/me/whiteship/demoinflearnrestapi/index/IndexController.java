package me.whiteship.demoinflearnrestapi.index;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import me.whiteship.demoinflearnrestapi.event.EventController;

@RestController
public class IndexController {

	@GetMapping("/api")
	public RepresentationModel index() {
		var index = new RepresentationModel<>();
		index.add(WebMvcLinkBuilder.linkTo(EventController.class).withRel("events"));
		return index;
	}
}
