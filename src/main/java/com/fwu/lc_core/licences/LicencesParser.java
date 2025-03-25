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
                extractedStrings.add(getIdentifierOfrNode(rNode));
            }
            return extractedStrings.stream().map(Licence::new);

        } catch (Exception e) {
            throw new Exception("Exception in LicenceParser: " + e.getMessage());
        }
    }

    public static String getIdentifierOfrNode(Node rNode) throws Exception {
        var identifier = rNode.getAttributes().getNamedItem("identifier");
        if (identifier == null) 
            throw new Exception("UnparsedLicences with source ARIX and rawResult containing <r> Element without identifier.");
        return identifier.getNodeValue();
    }
}
