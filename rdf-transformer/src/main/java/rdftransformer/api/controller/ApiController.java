package rdftransformer.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rdftransformer.api.transformer.PerformMagic;

@RestController
public class ApiController {


    @GetMapping("/koderetting")
    public String koderetting() {
        return "Gjør seg ikke sjøl!";
    }

    @PostMapping(path = "/post", consumes = "application/json")
    public String post(@RequestBody String s) {
        return PerformMagic.magic(s);
    }
}
