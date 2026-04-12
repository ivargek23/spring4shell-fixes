package com.example.spring4shell.boot.war;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.OffsetDateTime;

@Controller
public class HelloWorldController {

    private static final Logger logger = LoggerFactory.getLogger(HelloWorldController.class);
    public static final String MODEL = "model = {}";
    public static final String HELLO_WORLD = "helloworld";
    public static final String MODEL_ATTRIBUTE_NAME = "model";
    @RequestMapping({ "/", "/helloworld", "spring4shell/helloworld" })
    public String index(Model model) {
        logger.info(MODEL, model);
        model.addAttribute(MODEL_ATTRIBUTE_NAME, String.valueOf(model));
        model.addAttribute("time", OffsetDateTime.now().toString());
        return HELLO_WORLD;
    }

    @GetMapping({ "/test"})
    @ResponseBody
    public String getTest(Model model) {
        logger.info(MODEL, model);
        model.addAttribute(MODEL_ATTRIBUTE_NAME, String.valueOf(model));
        model.addAttribute("time", OffsetDateTime.now().toString());
        model.addAttribute("get", "true");
        return HELLO_WORLD;
    }

    @PostMapping({ "/test"})
    @ResponseBody
    public String postTest(Model model) {
        logger.info(MODEL, model);
        model.addAttribute(MODEL_ATTRIBUTE_NAME, String.valueOf(model));
        model.addAttribute("time", OffsetDateTime.now().toString());
        model.addAttribute("post", "true");
        return HELLO_WORLD;
    }

}
