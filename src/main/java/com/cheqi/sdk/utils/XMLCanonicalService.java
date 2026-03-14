package com.cheqi.sdk.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.crypto.Data;
import javax.xml.crypto.NodeSetData;
import javax.xml.crypto.OctetStreamData;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Service for XML Canonicalization (Exclusive C14N 1.0) and hashing.
 *
 * Uses the JDK's built-in {@link javax.xml.crypto.dsig} API to produce
 * deterministic XML output matching:
 * <ul>
 *   <li>iOS: libxml2's xmlC14NDocDumpMemory with XML_C14N_EXCLUSIVE_1_0</li>
 *   <li>Java backend: Apache Santuario Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS</li>
 * </ul>
 *
 * No external dependencies required — uses only JDK APIs.
 * Thread-safe and can be reused across multiple canonicalization operations.
 */
public class XMLCanonicalService {
    private static final Logger logger = LoggerFactory.getLogger(XMLCanonicalService.class);

    /**
     * Canonicalizes an XML string using Exclusive XML Canonicalization 1.0 (W3C).
     *
     * @param xmlString the XML string to canonicalize
     * @return the canonicalized XML string
     * @throws XMLCanonException if parsing or canonicalization fails
     * @throws IllegalArgumentException if xmlString is null or empty
     */
    public String canonicalize(String xmlString) {
        if (xmlString == null || xmlString.trim().isEmpty()) {
            throw new IllegalArgumentException("XML string cannot be null or empty");
        }
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document doc = dbf.newDocumentBuilder()
                    .parse(new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8)));
            removeWhitespaceOnlyTextNodes(doc);

            List<Node> allNodes = collectAllNodes(doc);

            XMLSignatureFactory sf = XMLSignatureFactory.getInstance("DOM");
            CanonicalizationMethod c14n = sf.newCanonicalizationMethod(
                    CanonicalizationMethod.EXCLUSIVE, (C14NMethodParameterSpec) null);

            NodeSetData<Node> nodeSet = allNodes::iterator;

            Data result = c14n.transform(nodeSet, null);

            if (result instanceof OctetStreamData) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buf = new byte[4096];
                int len;
                while ((len = ((OctetStreamData) result).getOctetStream().read(buf)) != -1) {
                    baos.write(buf, 0, len);
                }
                String canonical = new String(baos.toByteArray(), StandardCharsets.UTF_8);
                logger.debug("XML canonicalization complete ({} bytes)", baos.size());
                return canonical;
            }

            throw new XMLCanonException("Unexpected transform result type", null);
        } catch (XMLCanonException e) {
            throw e;
        } catch (Exception e) {
            throw new XMLCanonException("XML Exclusive C14N canonicalization failed", e);
        }
    }

    /**
     * Removes formatting-only whitespace text nodes between XML elements while preserving
     * meaningful text content inside leaf nodes.
     */
    private void removeWhitespaceOnlyTextNodes(Node node) {
        NodeList children = node.getChildNodes();
        for (int i = children.getLength() - 1; i >= 0; i--) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.TEXT_NODE && child.getTextContent().trim().isEmpty()) {
                node.removeChild(child);
                continue;
            }
            removeWhitespaceOnlyTextNodes(child);
        }
    }

    /**
     * Canonicalizes XML and returns its SHA-256 hash as a lowercase hex string.
     *
     * @param xmlString the XML string to canonicalize and hash
     * @return lowercase hex-encoded SHA-256 hash of the canonicalized XML
     * @throws XMLCanonException if parsing or canonicalization fails
     * @throws IllegalArgumentException if xmlString is null or empty
     */
    public String calculateHash(String xmlString) {
        String canonical = canonicalize(xmlString);
        String hash = HashUtils.sha256Hex(canonical);
        logger.debug("XML canonical hash: {}...", hash.substring(0, Math.min(8, hash.length())));
        return hash;
    }

    /**
     * Recursively collects all nodes in the document (document, elements, attributes, text, etc.)
     * into a flat list for the NodeSetData input required by the C14N transform.
     */
    private List<Node> collectAllNodes(Document doc) {
        List<Node> nodes = new ArrayList<>();
        collectNodes(doc, nodes);
        return nodes;
    }

    private void collectNodes(Node node, List<Node> nodes) {
        nodes.add(node);
        if (node.getAttributes() != null) {
            for (int i = 0; i < node.getAttributes().getLength(); i++) {
                nodes.add(node.getAttributes().item(i));
            }
        }
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            collectNodes(children.item(i), nodes);
        }
    }
}
