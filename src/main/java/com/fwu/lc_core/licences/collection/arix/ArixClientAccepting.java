package com.fwu.lc_core.licences.collection.arix;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ArixClientAccepting extends ArixClient {

    public ArixClientAccepting(@Value("${mocks.arix.accepting.url}") String baseUrlAccepting) {
        super(baseUrlAccepting);
    }
}
