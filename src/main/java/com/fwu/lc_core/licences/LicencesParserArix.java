package com.fwu.lc_core.licences;

import com.fwu.lc_core.licences.models.LicenceResponse;
import com.fwu.lc_core.licences.models.UnparsedLicences;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import reactor.core.publisher.Flux;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LicencesParserArix extends LicencesParser {
    public static Flux<LicenceResponse> parse(UnparsedLicences unparsedLicences) {
        try {
            var rootElement = extractXmlRootElementFromRawResult(unparsedLicences);
            List<LicenceResponse> licenceResponses = new ArrayList<>();
            for (Node e : extractXmlNodesWithNameR(rootElement))
                licenceResponses.add(createLicenceFromXmlRNode(e));
            return Flux.fromIterable(licenceResponses);
        } catch (Exception e) {
            return Flux.error(new Exception("Exception in LicenceParser: " + e.getMessage()));
        }
    }

    private static LicenceResponse createLicenceFromXmlRNode(Node e) throws Exception {
        return new LicenceResponse(extractLicenceCodeFrom(e), "<VIDIS-ID>", "<ARIX>", "play");
    }

    private static Element extractXmlRootElementFromRawResult(UnparsedLicences unparsedLicences) throws Exception {
        var document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(unparsedLicences.rawResult.getBytes()));
        var rootElement = document.getDocumentElement();
        checkThatContainsResult(rootElement);
        return rootElement;
    }

    private static List<Node> extractXmlNodesWithNameR(Element rootElement) {
        var results = rootElement.getElementsByTagName("r");
        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < results.getLength(); i++)
            nodes.add(results.item(i));
        return nodes;
    }

    private static void checkThatContainsResult(Element rootElement) throws Exception {
        if (!Objects.equals(rootElement.getTagName(), "result"))
            throw new Exception("UnparsedLicences with source ARIX and rawResult not containing a <result> Element as root.");
    }

    public static String extractLicenceCodeFrom(Node rNode) throws Exception {
        var identifier = rNode.getAttributes().getNamedItem("identifier");
        if (identifier == null)
            throw new Exception("UnparsedLicences with source ARIX and rawResult containing <r> Element without identifier.");
        return identifier.getNodeValue();
    }
}
