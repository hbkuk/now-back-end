package com.now.common.doc;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Email;

@RestController
@RequestMapping("/test")
public class CommonDocController {

    @PostMapping("/error")
    public void errorSample(@RequestBody @Valid SampleRequest dto) {
    }

    public static class SampleRequest {

        private String name;
        @Email
        private String email;

        public SampleRequest(String name, String email) {
            this.name = name;
            this.email = email;
        }
    }
}
