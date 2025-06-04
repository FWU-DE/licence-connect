package com.fwu.lc_core.licences.clients.arix;

import com.fwu.lc_core.licences.models.ODRLAction;
import com.fwu.lc_core.licences.models.ODRLPolicy;
import com.fwu.lc_core.shared.LicenceHolder;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class ArixParser {
    public static List<ODRLPolicy.Permission> parse(String responseBody) throws Exception {
        try {
            var rootElement = extractXmlRootElementFromRawResult(responseBody);
            var nodesWithNameR = extractXmlNodesWithNameR(rootElement);
            var permissions = nodesWithNameR.stream().map(node -> {
                try {
                    return new ODRLPolicy.Permission(
                            extractLicenceCodeFrom(node),
                            LicenceHolder.ARIX,
                            ODRLAction.Use
                    );
                } catch (Exception ex) {
                    throw new RuntimeException("Error extracting licence codes: " + node, ex);
                }
            }).toList();
            log.info("Found {} licences on `{}`", permissions.size(), LicenceHolder.ARIX);
            return permissions;
        } catch (Exception ex) {
            throw new RuntimeException("Error extracting licence codes: ", ex);
        }
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
