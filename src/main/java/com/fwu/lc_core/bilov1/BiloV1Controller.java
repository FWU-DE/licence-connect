package com.fwu.lc_core.bilov1;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
public class BiloV1Controller {
    @PostMapping("/v1/ucs/request")
    private String request(@Valid @RequestBody UcsRequestDto request) {
        return "";
    }
}
