package rdftransformer.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rdftransformer.api.transformer.PerformMagic;
import rdftransformer.api.transformer.action.Classification;

@RestController
public class ApiController {


    @GetMapping("/koderetting")
    public String koderetting() {
        return "Gjør seg ikke sjøl!";
    }

    @PostMapping(path = "/identifier", consumes = "application/json")
    public String identifier(@RequestBody String s) {
        return PerformMagic.magic(s);
    }

    @PostMapping(path = "/classifier", consumes = "application/json")
    public String classifier(@RequestBody String s) {
        return Classification.classification(s);
    }
}
