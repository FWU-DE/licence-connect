package com.fwu.lc_core.licences.clients.lcHalt;

import com.fwu.lc_core.shared.LicenceHolder;
import com.fwu.lc_core.licences.models.ODRLPolicy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class LCHaltClientTests {

    @Autowired
    LCHaltClient lchaltClient;

//    @Test
//    public void RequestPermissions_GivenUserID_Yields_Result() throws ParserConfigurationException, IOException, SAXException {
//        var permissions = lchaltClient.getPermissions(null, null, null, "currently any user id").block();
//
//        assertThat(permissions).isNotNull();
//        for (ODRLPolicy.Permission p : permissions) {
//            assertThat(p.assigner).isEqualTo(LicenceHolder.LC_HALT);
//        }
//    }
}
