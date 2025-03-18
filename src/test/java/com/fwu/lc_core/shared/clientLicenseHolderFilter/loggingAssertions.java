package com.fwu.lc_core.shared.clientLicenseHolderFilter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class loggingAssertions{
    public static String findLineContaining(String text, String searchString) {
        return text.lines()
                .filter(line -> line.contains(searchString))
                .findFirst()
                .orElse("");
    }

    public static String extractTraceId(String logLine) {
        Pattern pattern = Pattern.compile("traceID=([^,]+)");
        Matcher matcher = pattern.matcher(logLine);
        return matcher.find() ? matcher.group(1) : "";
    }

    public static void assertThatBothLogsHaveTheSameTraceId(String logs, String expectedFirstLog, String expectedSecondLog) {
        String firstLogLine = findLineContaining(logs, expectedFirstLog);
        String secondLogLine = findLineContaining(logs, expectedSecondLog);
        String traceId1 = extractTraceId(firstLogLine);
        String traceId2 = extractTraceId(secondLogLine);
        assertThat(traceId1).isNotEmpty();
        assertThat(traceId2).isNotEmpty();
        assertThat(traceId1).isNotEqualTo("NONE");
        assertThat(traceId2).isNotEqualTo("NONE");
        assertThat(traceId2).isEqualTo(traceId1);
    }

    public static void assertThatFirstLogComesBeforeSecondLog(String logs, String expectedFirstLog, String expectedSecondLog) {
        assertThat(logs.indexOf(expectedFirstLog)).isLessThan(logs.indexOf(expectedSecondLog));
    }
}
