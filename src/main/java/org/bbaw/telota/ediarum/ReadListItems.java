package org.bbaw.telota.ediarum;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;

import net.sf.saxon.xpath.XPathFactoryImpl;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.korpora.aeet.ediarum.EdiarumNamespaceContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.korpora.useful.XMLUtilities;

import ro.sync.ecss.extensions.api.AuthorOperationException;

/**
 * a class to load and read external Documents for the ListItemOperations.
 * It belongs to package ro.sync.ecss.extensions.ediarum for the modification of the Oxygen framework
 * for several projects at the Berlin-Brandenburgische Akademie der Wissenschaften (BBAW) to build a
 * framework for edition projects (Ediarum - die Editionsarbeitsumgebung).
 *
 * @author Martin Fechner
 * @version 1.0.0
 */

public class ReadListItems {

    /**
     * interne Variablen, die Einträge und IDs des Registers.
     */
    private String[] eintrag, id;


    /**
     * Der Konstruktor liest das Dokument der URL mit den benannten Knoten aus und konstruiert den Eintrag und die Id für jeden Knoten.
     *
     * @param indexURI         Die URL zur Registerdatei
     * @param node             Der X-Path-Ausdruck für die Knoten der einzelnen Registereinträge
     * @param eintragExpString Der Ausdruck um einen Registereintrag zu konstruieren.
     * @param idExpStrings     Der Ausdruck um die ID für einen Registereintrag zu konstruieren. Er setzt sich wie eintragExp zusammen.
     * @throws AuthorOperationException
     */
    public ReadListItems(String indexURI, String node, String eintragExpString, String idExpStrings, String namespaceDecl) {

        try {
            // Das neue Dokument wird vorbereitet.
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true);
            DocumentBuilder builder = domFactory.newDocumentBuilder();

            // Wenn es sich um eine URL mit Authentifizierung handelt, ..
            InputStream is;
            if (indexURI.indexOf('@') > -1) {
                // .. werden die Verbindungsdaten gelesen ..
                String authString = indexURI.substring(indexURI.indexOf("://") + 3, indexURI.indexOf('@'));
                String webPage = indexURI.substring(0, indexURI.indexOf("://") + 3) + indexURI.substring(indexURI.indexOf('@') + 1);
                byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
                String authStringEnc = new String(authEncBytes);

                // .. und eine Verbindung mit Login geöffnet.
                URL url = new URL(webPage);
                URLConnection urlConnection = url.openConnection();
                urlConnection.setRequestProperty("Authorization", "Basic " + authStringEnc);
                is = urlConnection.getInputStream();
            } else {
                // Im anderen Fall wird direkt eine Verbindung geöffnet.
                URL url = new URL(indexURI);
                URLConnection urlConnection = url.openConnection();
                is = urlConnection.getInputStream();
            }

            // Dann wird die Datei gelesen.
            InputSource inputSource = new InputSource(is);
            Document indexDoc = builder.parse(inputSource);
            // Die xPath-Routinen werden vorbereitet.
            XPathFactoryImpl xpathFactory = new XPathFactoryImpl();
            XPath xpath = XPathFactory.newInstance().newXPath();
            // Für Namespaces:
            Map<String, String> namespaces = new ConcurrentHashMap<>();
            if (namespaceDecl != null) {
                String[] namespaceSplit = namespaceDecl.split(" ");
//            String[][] namespaces = new String[namespaceSplit.length][2];
                
                for (int i = 0; i < namespaceSplit.length; i++) {
                    String currentNamespace = namespaceSplit[i];
                    int k = currentNamespace.indexOf(":");
                    namespaces.put(currentNamespace.substring(0, k), currentNamespace.substring(k + 1));
                }
            }
            NamespaceContext ctx = new EdiarumNamespaceContext(namespaces);

            xpath.setNamespaceContext(ctx);
            // Das XPath-Query wird definiert.
            //XPathExpression expr = xpath.compile(node);

            // Die Resultate werden ausgelesen..
            //Object result = expr.evaluate(indexDoc, XPathConstants.NODESET);
            Object result = xpath.evaluate(node, indexDoc, XPathConstants.NODESET);
            NodeList registerNodes = (NodeList) result;
//            System.err.format("Gefunden: %d Einträge [%s] => [%s]\n", registerNodes.getLength(), node, eintragExpString);

            // .. dann werden für die Einträge und IDs entsprechend lange Arrays angelegt.
            eintrag = new String[registerNodes.getLength()];
            id = new String[registerNodes.getLength()];

            List<Object> eintragExpressions = parseExpression(eintragExpString, xpath);
            List<Object> idExpressions = parseExpression(idExpStrings, xpath);

            // Für jeden Knoten ..
            for (int i = 0; i < registerNodes.getLength(); i++) {
                Element currentElement = (Element) registerNodes.item(i);

                // … wird der Eintrag konstruiert:
                eintrag[i] = evaluateExpression(registerNodes.item(i), eintragExpressions, true);

                // … und der einzufügende Wert:
                id[i] = evaluateExpression(registerNodes.item(i), idExpressions, false);
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }


    /**
     * Diese Methode gibt das Array der Einträge aus dem Register zurück.
     *
     * @return das Array der Einträge
     */
    public String[] getEintrag() {
        return eintrag;
    }

    /**
     * Diese Methode gibt das Array der IDs aus dem Register zurück.
     *
     * @return das Array der IDs
     */
    public String[] getID() {
        return id;
    }

    /**
     * parse expression pattern
     *
     * @param expression the expression
     * @param xpath      the Xpath evaluation environment (for namepaces and such)
     * @return list of expressions, containing instances of literal String and XPathExpression
     * <p>
     * allow leading # for compatibility with BBAW
     * allow to quote braces by doubling, breaking compatibility
     */
    public static List<Object> parseExpression(String expression, XPath xpath) {
        ArrayList expressions = new ArrayList<>();
        Pattern xpathPart = Pattern.compile("\\$XPATH\\{#?(?<expression>(?:[^{}]|\\{\\{|\\}\\})*?)\\}");
        Matcher matcher = xpathPart.matcher(expression);
        int start = 0;
        while (matcher.find()) {
            // .. wird der String davor als Text eingefügt, ..
            expressions.add(expression.substring(start, matcher.start()));
            // .. und der Ausdruck selbst ausgewertet:
            String xpathExpression = matcher.group("expression");
            try {
                System.err.println(xpathExpression);
                XPathExpression queryExpr = xpath.compile(xpathExpression);
                expressions.add(queryExpr);
            } catch (XPathExpressionException e) {
                throw new RuntimeException(e);
            }
            start = matcher.end();
        }
        System.err.println("Finally add: " + expression.substring(start));
        expressions.add(expression.substring(start));

        return expressions;
    }

    /**
     * apply list of expressions to node
     *
     * @param node         the document node
     * @param expressions, list of instances of literal String and XPathExpression with patterns
     * @param needString   whether the result is to be used as a string, i.e. elements are not retained and space is normalized
     * @return a string to be inserted
     */
    public static String evaluateExpression(Node node, List<Object> expressions, boolean needString) {
        StringWriter expressionValue = new StringWriter();
        for (Object component : expressions) {
            if (component instanceof String) {
                expressionValue.write((String) component);
            } else if (component instanceof XPathExpression) {
                try {
                    XPathExpression queryExpr = (XPathExpression) component;
                    NodeList subNodes = (NodeList) queryExpr.evaluate(node, XPathConstants.NODESET);
                    for (int i = 0; i < subNodes.getLength(); i++) {
                        Node subNode = subNodes.item(i);
                        if (needString || subNode.getNodeType() == Node.ATTRIBUTE_NODE)
                            expressionValue.write(XMLUtilities.toStringValue(subNode));
                        else
                            // keep structure
                            expressionValue.write(XMLUtilities.nodeToString(subNode));
                    }
                } catch (XPathExpressionException e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new RuntimeException(String.format("Broken expression list component of type %s", component.getClass()));
            }
        }
//        System.err.format("Ergebnis: %s\n", expressionValue.toString());
        if (needString) {
            return StringUtils.normalizeSpace(expressionValue.toString());
        } else return expressionValue.toString();
    }
}