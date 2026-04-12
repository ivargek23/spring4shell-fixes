package com.example.spring4shell.boot.jar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
public class HelloWorldController {

    private static final Logger logger = LoggerFactory.getLogger(HelloWorldController.class);
    public static final String GREETING = "greeting";
    public static final String MODEL = "model = {}";

    @RequestMapping({ "/", "/helloworld", "spring4shell/helloworld" })
    public Greeting index(Model model) {
        logger.info(MODEL, model);
        Greeting greeting = new Greeting();
        greeting.setId(123);
        greeting.setContent(OffsetDateTime.now().toString() + " " + model);
        model.addAttribute(GREETING, greeting);
        return greeting;
    }

    @GetMapping({ "/test"})
    public Greeting getTest(Model model) {
        logger.info(MODEL, model);
        Greeting greeting = new Greeting();
        greeting.setId(123);
        greeting.setContent(OffsetDateTime.now().toString() + " " + model);
        model.addAttribute(GREETING, greeting);
        return greeting;
    }

    @PostMapping({ "/test"})
    public Greeting postTest(Model model) {
        logger.info(MODEL, model);
        Greeting greeting = new Greeting();
        greeting.setId(123);
        greeting.setContent(OffsetDateTime.now().toString() + " " + model);
        model.addAttribute(GREETING, greeting);
        return greeting;
    }



}
