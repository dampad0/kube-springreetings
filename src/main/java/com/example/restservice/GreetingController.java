package com.example.restservice;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();

	@GetMapping("/greeting")
	public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		return new Greeting(counter.incrementAndGet(), String.format(template, name));
	}

	//Home page
	@GetMapping("/")
	public String home() {
		// return the home page content in the resources folder
		return "<!DOCTYPE html>\n" +
				"<html>\n" +
				"<head>\n" +
				"    <title>Welcome to the Greetings APP </title>\n" +
				"    <style>\n" +
				"        body {\n" +
				"            background-color: lightblue;\n" +
				"            font-family: Arial, sans-serif;\n" +
				"        }\n" +
				"        h1 {\n" +
				"            color: white;\n" +
				"            text-align: center;\n" +
				"        }\n" +
				"        form {\n" +
				"            text-align: center;\n" +
				"        }\n" +
				"        #greeting {\n" +
				"            text-align: center;\n" +
				"            font-size: 1.5em;\n" +
				"        }\n" +
				"    </style>\n" +
				"</head>\n" +
				"<body>\n" +
				"    <h1>RESTful Web Service with Spring Boot</h1>\n" +
				"    <form>\n" +
				"        <label for=\"name\">Enter your name:</label>\n" +
				"        <input type=\"text\" id=\"name\" name=\"name\">\n" +
				"        <button type=\"button\" onclick=\"fetchGreeting()\">Submit</button>\n" +
				"    </form>\n" +
				"    <div id=\"greeting\"></div>\n" +
				"    <script>\n" +
				"        function fetchGreeting() {\n" +
				"            var name = document.getElementById(\"name\").value;\n" +
				"            fetch(\"/greeting?name=\" + name)\n" +
				"                .then(response => response.text())\n" +
				"                .then(data => {\n" +
				"                    document.getElementById(\"greeting\").textContent = data;\n" +
				"                });\n" +
				"        }\n" +
				"    </script>\n" +
				"</body>\n" +
				"\n" +
				"</html>";
	}
}
