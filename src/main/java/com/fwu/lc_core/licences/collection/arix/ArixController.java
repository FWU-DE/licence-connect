package com.fwu.lc_core.licences.collection.arix;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArixController {

    @GetMapping("/arix/request")
    private ResponseEntity<String> request() {
        String response = "yes";
        return ResponseEntity.ok(response);
    }

}
