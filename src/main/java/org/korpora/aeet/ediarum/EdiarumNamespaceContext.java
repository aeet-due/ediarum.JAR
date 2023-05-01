package org.korpora.aeet.ediarum;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.korpora.useful.LangUtilities;

import javax.xml.namespace.NamespaceContext;
import java.io.IOException;
import java.io.InputStream;
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

        ObjectMapper mapper = new ObjectMapper();
        try (InputStream str = LangUtilities.class.getClassLoader()
                .getResourceAsStream("json/default-namespaces.json")) {
            Map defaultNamespaces = mapper.readValue(str,
                    new TypeReference<Map<String, String>>() {
                    });
            this.namespaces.putAll(defaultNamespaces);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

//        this.namespaces.put("xml", "http://www.w3.org/XML/1998/namespace");
//        this.namespaces.put("tei", "http://www.tei-c.org/ns/1.0");
        this.namespaces.putAll(namespaces);
    }

    public String getNamespaceURI(String prefix) {
//					return resolver.getNamespaceForPrefix(prefix);
        return namespaces.get(prefix);
    }

    public String getPrefix(String namespaceURI) {
        for (Map.Entry<String, String> pair : namespaces.entrySet()) {
            if (pair.getValue().equals(namespaceURI))
                return pair.getKey();
        }
        return null;
    }

    public Iterator getPrefixes(String namespaceURI) {
        return namespaces.entrySet().stream().
                filter(pair -> pair.getValue().equals(namespaceURI)).
                map(Map.Entry::getKey).iterator();
    }
}
