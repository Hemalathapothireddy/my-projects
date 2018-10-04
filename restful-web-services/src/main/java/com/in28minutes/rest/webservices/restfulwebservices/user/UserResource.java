package com.in28minutes.rest.webservices.restfulwebservices.user;

import java.net.URI;
import java.util.List;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;
import javax.validation.Valid;

import org.aspectj.weaver.AjAttribute.MethodDeclarationLineNumberAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class UserResource {

	  @Autowired
	  private UserDaoService service;

	@RequestMapping(method=RequestMethod.GET,path="/users")
	public List<User> retrieveAllUsers(){
		return service.findAll();
	}

	@RequestMapping(method=RequestMethod.GET,path="/users/{id}")
	public Resource<User>  retrieveUser(@PathVariable int id) throws UserNotFoundException{
		User user =service.findOne(id);
		if(user==null) {
			throw new UserNotFoundException("id-"+id);
		}
		//HATEOS
		Resource<User> resource = new Resource<User>(user);
		ControllerLinkBuilder linkTo=
				linkTo(methodOn(this.getClass()).retrieveAllUsers());
		
		resource.add(linkTo.withRel("all-users"));
		
		return resource;
	}
	
/*	@RequestMapping(method=RequestMethod.GET,path="/users?{name}")
	public User retrieveUserByName(@RequestParam(value="name") String name) throws UserNotFoundException{
		User user =service.findByName(name);
		if(user==null) {
			throw new UserNotFoundException("name-"+name);
		}
		return user;
	}*/
	
	@DeleteMapping("/users/{id}")
	public void deleteUser(@PathVariable int id) throws UserNotFoundException{
		User user =service.deleteById(id);
		if(user==null) 
			throw new UserNotFoundException("id-"+id);
	}
	
	@RequestMapping(method=RequestMethod.POST,path="/users")
	public ResponseEntity<Object> createUser(@Valid @RequestBody User user) {
		User savedUser =service.save(user);
		
		URI location =ServletUriComponentsBuilder
			.fromCurrentRequest()
			.path("/{id}")
			.buildAndExpand(savedUser.getId()).toUri();
		return ResponseEntity.created(location).build();
	}
}
