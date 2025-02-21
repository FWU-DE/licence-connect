package com.fwu.lc_core.licences;

import com.fwu.lc_core.licences.models.Licence;
import com.fwu.lc_core.licences.models.UnparsedLicences;
import org.w3c.dom.Node;
import reactor.core.publisher.Flux;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class LicencesParser {
    public static Flux<Licence> parse(UnparsedLicences unparsedLicences) {
        return switch (unparsedLicences.source) {
            case "ARIX" -> {
                try {
                    yield Flux.fromStream(parseArix(unparsedLicences));
                } catch (Exception e) {
                    yield Flux.error(e);
                }
            }
            default -> Flux.error(new Exception("Exception in LicenceParser: Unknown source: " + unparsedLicences.source));
        };
    }

    public static Stream<Licence> parseArix(UnparsedLicences unparsedLicences) throws Exception {
        try {
            var document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(unparsedLicences.rawResult.getBytes()));
            var rootElement = document.getDocumentElement();
            if (!Objects.equals(rootElement.getTagName(), "result"))
                throw new Exception("UnparsedLicences with source ARIX and rawResult not containing a <result> Element as root.");

            var rNodes = rootElement.getElementsByTagName("r");
            List<String> extractedStrings = new ArrayList<>();
            for (int i = 0; i < rNodes.getLength(); i++) {
                var rNode = rNodes.item(i);
                extractedStrings.add(getNrOfrNode(rNode));
            }
            return extractedStrings.stream().map(Licence::new);

        } catch (Exception e) {
            throw new Exception("Exception in LicenceParser: " + e.getMessage());
        }
    }

    public static String getNrOfrNode(Node rNode) throws Exception {
        var fElementWithNr = getChildByAttributeValue(rNode, "n", "nr");
        if (fElementWithNr == null)
            throw new Exception("UnparsedLicences with source ARIX and rawResult containing a <r> Element without a <f n=\"nr\"> Element.");

        var textNodeWithNr = fElementWithNr.getFirstChild();
        if (textNodeWithNr == null || textNodeWithNr.getNodeType() != Node.TEXT_NODE)
            throw new Exception("UnparsedLicences with source ARIX and rawResult containing a <f n=\"nr\"> Element without text.");

        return textNodeWithNr.getNodeValue();
    }

    public static Node getChildByAttributeValue(Node node, String attributeName, String attributeValue) {
        var childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            var child = childNodes.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE && child.getAttributes().getNamedItem(attributeName).getNodeValue().equals(attributeValue)) {
                return child;
            }
        }
        return null;
    }
}
