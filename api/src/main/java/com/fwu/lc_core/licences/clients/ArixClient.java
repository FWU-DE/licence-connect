package com.fwu.lc_core.licences.clients;

import com.fwu.lc_core.licences.models.LicenceHolder;
import com.fwu.lc_core.licences.models.ODRLPolicy;
import com.fwu.lc_core.licences.models.ODRLAction;
import com.fwu.lc_core.shared.Bundesland;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ArixClient {
    private final String apiUrl;

    public ArixClient(@Value("${arix.accepting.url}") String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public Mono<List<ODRLPolicy.Permission>> getPermissions(Bundesland bundesland, String standortnummer, String schulnummer, String userId) {
        return Mono.fromCallable(() -> getPermissionsBlocking(bundesland, standortnummer, schulnummer, userId)).subscribeOn(Schedulers.boundedElastic());
    }

    private List<ODRLPolicy.Permission> getPermissionsBlocking(Bundesland bundesland, String standortnummer, String schulnummer, String userId) throws Exception {
        validateParameters(bundesland, standortnummer, schulnummer, userId);

        WebClient webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .build();

        String uri = Stream
                .of(bundesland.toString(), standortnummer, schulnummer, userId)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("/"));

        String responseBody = webClient.post()
                .uri(uri)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(BodyInserters.fromFormData("xmlstatement", "<search></search>"))
                .exchangeToMono(response -> response.bodyToMono(String.class)).block();

        if (responseBody == null || !responseBody.startsWith("<result"))
            throw new RuntimeException(responseBody);

        return ArixParser.parse(responseBody);
    }

    private static void validateParameters(Bundesland bundesland, String standortnummer, String schulnummer, String userId) {
        if (bundesland == null)
            throw new IllegalArgumentException("You must provide a Bundesland.");
        if ((standortnummer == null && schulnummer != null) || (schulnummer == null && userId != null))
            throw new IllegalArgumentException("If you provide a parameter, you must provide all parameters before it.");
    }
}

@Slf4j
class ArixParser {
    static List<ODRLPolicy.Permission> parse(String responseBody) throws Exception {
        var rootElement = extractXmlRootElementFromRawResult(responseBody);
        var nodesWithNameR = extractXmlNodesWithNameR(rootElement);
        var permissions = nodesWithNameR.stream().map(e -> {
            try {
                return new ODRLPolicy.Permission(
                        extractLicenceCodeFrom(e),
                        LicenceHolder.ARIX,
                        ODRLAction.Use
                );
            } catch (Exception ex) {
                throw new RuntimeException("Error extracting licence code from node: " + e, ex);
            }
        }).toList();
        log.info("Found {} licences on `{}`", permissions.size(), LicenceHolder.ARIX);
        return permissions;
    }

    private static Element extractXmlRootElementFromRawResult(String responseBody) throws Exception {
        var document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(responseBody.getBytes()));
        var rootElement = document.getDocumentElement();

        if (!Objects.equals(rootElement.getTagName(), "result"))
            throw new Exception("UnparsedLicences with source ARIX and rawResult not containing a <result> Element as root.");

        return rootElement;
    }

    private static List<Node> extractXmlNodesWithNameR(Element rootElement) {
        var results = rootElement.getElementsByTagName("r");
        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < results.getLength(); i++)
            nodes.add(results.item(i));
        return nodes;
    }

    private static String extractLicenceCodeFrom(Node rNode) throws Exception {
        var identifier = rNode.getAttributes().getNamedItem("identifier");
        if (identifier == null)
            throw new Exception("UnparsedLicences with source ARIX and rawResult containing <r> Element without identifier.");
        return identifier.getNodeValue();
    }
}
