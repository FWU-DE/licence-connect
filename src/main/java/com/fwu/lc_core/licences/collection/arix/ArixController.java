package com.fwu.lc_core.licences.collection.arix;

import com.fwu.lc_core.bilov1.UcsRequestDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArixController {

    @GetMapping("/arix/request")
    private ResponseEntity<String> request(@Valid @RequestBody UcsRequestDto request) {
        String response = "yes";
        return ResponseEntity.ok(response);
    }


}
