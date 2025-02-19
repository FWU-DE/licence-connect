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

public class LicencesParser {
    public static Flux<Licence> parse(UnparsedLicences unparsedLicences) {
        if (!Objects.equals(unparsedLicences.source, "ARIX"))
            return Flux.error(createException("Unknown source: " + unparsedLicences.source));

        try {
            var document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(unparsedLicences.rawResult.getBytes()));
            var rootElement = document.getDocumentElement();
            if (!Objects.equals(rootElement.getTagName(), "result"))
                return Flux.error(createException("UnparsedLicences with source ARIX and rawResult not containing a <result> Element as root."));

            var results = rootElement.getElementsByTagName("r");
            List<String> extractedStrings = new ArrayList<>();
            for (int i = 0; i < results.getLength(); i++) {
                var rNode = results.item(i);
                var fNodeWithNr = getChildByAttributeValue(rNode, "n", "nr");
                if (fNodeWithNr == null)
                    return Flux.error(createException("UnparsedLicences with source ARIX and rawResult containing a <r> Element without a <f n=\"nr\"> Element."));

                var textNodeWithNr = fNodeWithNr.getFirstChild();
                if (textNodeWithNr == null || textNodeWithNr.getNodeType() != Node.TEXT_NODE)
                    return Flux.error(createException("UnparsedLicences with source ARIX and rawResult containing a <f n=\"nr\"> Element without text."));

                extractedStrings.add(textNodeWithNr.getNodeValue());
            }
            return Flux.fromStream(extractedStrings.stream().map(Licence::new));

        } catch (Exception e) {
            return Flux.error(createException("Unexpected Exception: " + e.getMessage()));
        }
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

    public static Exception createException(String message) {
        return new Exception("Exception in LicenceParser: " + message);
    }
}
