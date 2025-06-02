package com.fwu.lc_core.licences.clients.lcHalt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fwu.lc_core.licences.models.ODRLAction;
import com.fwu.lc_core.licences.models.ODRLPolicy;
import com.fwu.lc_core.shared.LicenceHolder;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
class LCHaltParser {
    static List<ODRLPolicy.Permission> parse(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            var json = objectMapper.readTree(responseBody);
            var licencedMedia = json.path("licenced_media");
            if (licencedMedia.isMissingNode() || !licencedMedia.isArray()) {
                throw new RuntimeException("Invalid response format: 'licenced_media' key is missing or is not an array.");
            }

            List<ODRLPolicy.Permission> permissions = new ArrayList<>();
            for (var media : licencedMedia) {
                permissions.add(new ODRLPolicy.Permission(media.path("id").textValue(), LicenceHolder.LC_HALT, ODRLAction.Use));
            }
            log.info("Found {} licences on `{}`", permissions.size(), LicenceHolder.LC_HALT);
            return permissions;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse responseBody as JSON", e);
        }
    }
}
