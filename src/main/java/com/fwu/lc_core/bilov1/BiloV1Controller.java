package com.fwu.lc_core.bilov1;

import com.fwu.lc_core.bilov1.DTOs.UcsRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BiloV1Controller {

    @PostMapping("/v1/ucs/request")
    private ResponseEntity<Object> request(@RequestBody UcsRequestDto requestDto) {

        return ResponseEntity.ok(null);
    }
}
