package org.korpora.aeet.ediarum;

import javax.xml.namespace.NamespaceContext;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * a simple map-based NamespaceContext, which by default already provides the XML and TEI namespaces
 */
public class EdiarumNamespaceContext implements NamespaceContext {

    Map<String, String> namespaces;

    /**
     * create a new namespace context
     *
     * @param namespaces a map of prefix and URL ("tei" and "xml" are mapped by default)
     */
    public EdiarumNamespaceContext(Map<String, String> namespaces) {
        this.namespaces = new HashMap<>();
        this.namespaces.put("xml", "http://www.w3.org/XML/1998/namespace");
        this.namespaces.put("tei", "http://www.tei-c.org/ns/1.0");
        this.namespaces.putAll(namespaces);
    }

    public String getNamespaceURI(String prefix) {
//					return resolver.getNamespaceForPrefix(prefix);
        return namespaces.get(prefix);
    }

    public String getPrefix(String namespaceURI) {
        // TODO Auto-generated method stub
        return null;
    }

    public Iterator getPrefixes(String namespaceURI) {
        // TODO Auto-generated method stub
        return null;
    }


    ;
}
