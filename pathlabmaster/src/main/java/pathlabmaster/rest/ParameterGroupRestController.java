package pathlabmaster.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/parameterGroup")
public class ParameterGroupRestController {
	@GetMapping("/")
	public String sayHello() {
		return "Parameter Group Service Working !";
	}
}
