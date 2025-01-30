package com.fwu.lc_core.bilov1;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BiloV1Controller {
    @PostMapping("/v1/ucs/request")
    private String request() {
        return "";
    }
}
