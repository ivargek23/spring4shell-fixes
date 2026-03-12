package com.example.spring4shell.controller;

import com.example.spring4shell.model.HelloWorld;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

/**
 * Spring MVC Controller vulnerable to CVE-2022-22965 via classloader injection
 *
 * @see com.example.spring4shell.controller.mitigated.SafeHelloWorldController
 * @see com.example.spring4shell.controller.mitigated.BinderControllerAdvice
 * @deprecated upgrade spring-core >=5.3.18, >=5.2.20, and Spring Boot >=2.5.12 and >=2.6.6 or deny class injections
 *             explicitly via BinderControllerAdvice.class
 */
@Deprecated
@Controller
public class HelloWorldController {
    private static final String MODEL_ATTRIBUTE_NAME = "model=";
    private static final String REQUEST_PATH = "request";

    @RequestMapping({ "/", "/request", }) // RequestMapping implements automatically all CRUD (HTTP) verbs
    public String handler(HelloWorld model) {
        HelloWorld helloWorld = new HelloWorld();
        helloWorld.setMessage(MODEL_ATTRIBUTE_NAME + model);
        helloWorld.setEndpoint("/request");
        helloWorld.setIsVulnerable(true);
        helloWorld.setDateTime(LocalDateTime.now().toString());
        return REQUEST_PATH;
    }

    @GetMapping({ "/get"})
    @ResponseBody
    public String doGet(HelloWorld model) {
        HelloWorld helloWorld = new HelloWorld();
        helloWorld.setMessage(MODEL_ATTRIBUTE_NAME + model);
        helloWorld.setEndpoint("/get");
        helloWorld.setIsVulnerable(false);
        helloWorld.setDateTime(LocalDateTime.now().toString());
        return REQUEST_PATH;
    }

    /** @deprecated */
    @PostMapping({ "/post"})
    @ResponseBody
    public String doPost(HelloWorld model) {
        HelloWorld helloWorld = new HelloWorld();
        helloWorld.setMessage(MODEL_ATTRIBUTE_NAME + model);
        helloWorld.setEndpoint("/post");
        helloWorld.setIsVulnerable(true);
        helloWorld.setDateTime(LocalDateTime.now().toString());
        return REQUEST_PATH;
    }

    @PutMapping({ "/put"})
    @ResponseBody
    public String doPut(HelloWorld model) {
        HelloWorld helloWorld = new HelloWorld();
        helloWorld.setMessage(MODEL_ATTRIBUTE_NAME + model);
        helloWorld.setEndpoint("/post");
        helloWorld.setIsVulnerable(false);
        helloWorld.setDateTime(LocalDateTime.now().toString());
        return REQUEST_PATH;
    }
}
