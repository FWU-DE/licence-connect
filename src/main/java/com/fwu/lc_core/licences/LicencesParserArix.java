package com.fwu.lc_core.licences;

import com.fwu.lc_core.licences.models.LicenceHolder;
import com.fwu.lc_core.licences.models.ODRLLicenceResponse;
import com.fwu.lc_core.licences.models.OdrlAction;
import com.fwu.lc_core.licences.models.UnparsedLicences;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import reactor.core.publisher.Mono;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LicencesParserArix {
    public static Mono<ODRLLicenceResponse> parse(UnparsedLicences unparsedLicences) {
        try {
            var rootElement = extractXmlRootElementFromRawResult(unparsedLicences);
            List<String> licenceCodes = new ArrayList<>();
            for (Node e : extractXmlNodesWithNameR(rootElement))
                licenceCodes.add(extractLicenceCodeFrom(e));
            return Mono.just(new ODRLLicenceResponse(licenceCodes, LicenceHolder.ARIX, OdrlAction.Use));
        } catch (Exception e) {
            return Mono.error(new Exception("Exception in LicenceParser: " + e.getMessage()));
        }
    }

    private static Element extractXmlRootElementFromRawResult(UnparsedLicences unparsedLicences) throws Exception {
        var document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(unparsedLicences.rawResult.getBytes()));
        var rootElement = document.getDocumentElement();
        checkThatXmlContainsResult(rootElement);
        return rootElement;
    }

    private static List<Node> extractXmlNodesWithNameR(Element rootElement) {
        var results = rootElement.getElementsByTagName("r");
        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < results.getLength(); i++)
            nodes.add(results.item(i));
        return nodes;
    }

    private static void checkThatXmlContainsResult(Element rootElement) throws Exception {
        if (!Objects.equals(rootElement.getTagName(), "result"))
            throw new Exception("UnparsedLicences with source ARIX and rawResult not containing a <result> Element as root.");
    }

    private static String extractLicenceCodeFrom(Node rNode) throws Exception {
        var identifier = rNode.getAttributes().getNamedItem("identifier");
        if (identifier == null)
            throw new Exception("UnparsedLicences with source ARIX and rawResult containing <r> Element without identifier.");
        return identifier.getNodeValue();
    }
}
