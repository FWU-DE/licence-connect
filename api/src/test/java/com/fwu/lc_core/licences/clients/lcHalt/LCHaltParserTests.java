package com.fwu.lc_core.licences.clients.lcHalt;

import com.fwu.lc_core.licences.models.ODRLAction;
import com.fwu.lc_core.licences.models.ODRLPolicy;
import com.fwu.lc_core.shared.LicenceHolder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
public class LCHaltParserTests {

    @Test
    public void testParseValidResponseWithMultipleMedia() {
        // Test that the parser correctly handles a valid response containing multiple media items.
        String validResponse = """
                {
                  "user_id": "a1b2c3d4-e5f6-7g8h-9i0j-k1l2m3n4o5p6",
                  "bundesland_id": null,
                  "schul_id": null,
                  "licenced_media": [
                    {"id": "abc123xyz0"},
                    {"id": "def456uvw1"},
                    {"id": "ghi789rst2"}
                  ]
                }
                """;

        List<ODRLPolicy.Permission> permissions = LCHaltParser.parse(validResponse);

        assertEquals(3, permissions.size());
        assertEquals("abc123xyz0", permissions.get(0).getTarget());
        assertEquals("def456uvw1", permissions.get(1).getTarget());
        assertEquals("ghi789rst2", permissions.get(2).getTarget());
        permissions.forEach(permission -> {
            assertEquals(LicenceHolder.LC_HALT, permission.getAssigner());
            assertEquals(ODRLAction.Use, permission.getAction());
        });
    }

    @Test
    public void testParseValidResponseWithSingleMedia() {
        // Test that the parser correctly handles a valid response containing a single media item.
        String validResponse = """
                {
                  "user_id": "a1b2c3d4-e5f6-7g8h-9i0j-k1l2m3n4o5p6",
                  "bundesland_id": null,
                  "schul_id": null,
                  "licenced_media": [
                    {"id": "jkl012mno3"}
                  ]
                }
                """;

        List<ODRLPolicy.Permission> permissions = LCHaltParser.parse(validResponse);

        assertEquals(1, permissions.size());
        assertEquals("jkl012mno3", permissions.getFirst().getTarget());
        assertEquals(LicenceHolder.LC_HALT, permissions.getFirst().getAssigner());
        assertEquals(ODRLAction.Use, permissions.getFirst().getAction());
    }

    @Test
    public void testParseValidResponseWithEmptyMediaArray() {
        // Test that the parser correctly handles a valid response with an empty media array.
        String validResponse = """
                {
                  "user_id": "a1b2c3d4-e5f6-7g8h-9i0j-k1l2m3n4o5p6",
                  "bundesland_id": null,
                  "schul_id": null,
                  "licenced_media": []
                }
                """;

        List<ODRLPolicy.Permission> permissions = LCHaltParser.parse(validResponse);

        assertTrue(permissions.isEmpty());
    }

    @Test
    public void testParseMissingLicencedMediaKey() {
        // Test that the parser throws an exception when the "licenced_media" key is missing.
        String invalidResponse = """
                {
                  "user_id": "a1b2c3d4-e5f6-7g8h-9i0j-k1l2m3n4o5p6",
                  "bundesland_id": null,
                  "schul_id": null,
                  "some_other_key": []
                }
                """;

        RuntimeException exception = assertThrows(RuntimeException.class, () -> LCHaltParser.parse(invalidResponse));
        assertTrue(exception.getMessage().contains("Invalid response format"));
        assertTrue(exception.getMessage().contains("licenced_media"));
    }

    @Test
    public void testParseLicencedMediaNotAnArray() {
        // Test that the parser throws an exception when "licenced_media" is not an array.
        String invalidResponse = """
                {
                  "user_id": "a1b2c3d4-e5f6-7g8h-9i0j-k1l2m3n4o5p6",
                  "bundesland_id": null,
                  "schul_id": null,
                  "licenced_media": "not an array"
                }
                """;

        RuntimeException exception = assertThrows(RuntimeException.class, () -> LCHaltParser.parse(invalidResponse));
        assertTrue(exception.getMessage().contains("Invalid response format"));
        assertTrue(exception.getMessage().contains("licenced_media"));
    }

    @Test
    public void testParseInvalidJson() {
        // Test that the parser throws an exception when the input is not valid JSON.
        String invalidJson = "this is not valid JSON";

        RuntimeException exception = assertThrows(RuntimeException.class, () -> LCHaltParser.parse(invalidJson));
        assertTrue(exception.getMessage().contains("Failed to parse responseBody as JSON"));
    }

    @Test
    public void testParseNullResponse() {
        // Test that the parser throws an exception when the input is null.
        RuntimeException exception = assertThrows(RuntimeException.class, () -> LCHaltParser.parse(null));
        assertTrue(exception.getMessage().contains("Failed to parse responseBody as JSON"));
    }
}
