package me.whiteship.demoinflearnrestapi.common;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.validation.Errors;

import me.whiteship.demoinflearnrestapi.index.IndexController;

public class ErrorsResource extends EntityModel<Errors>{

	public ErrorsResource(Errors content, Link... links) {
		super(content, links);
		add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(IndexController.class).index()).withRel("index"));
	}

	
}
