package org.bbaw.telota.ediarum.extensions;

import ro.sync.ecss.extensions.api.AuthorExtensionStateListener;
import ro.sync.ecss.extensions.api.ExtensionsBundle;
import ro.sync.ecss.extensions.api.link.LinkTextResolver;

/**
 * EdiarumExtensionsBundle.java - is a class to for all extensions.
 * It belongs to package org.bbaw.telota.ediarum for the modification of the Oxygen framework
 * for several projects at the Berlin-Brandenburgische Akademie der Wissenschaften (BBAW) to build a
 * framework for edition projects (Ediarum - die Editionsarbeitsumgebung).
 *
 * @author Martin Fechner
 * @version 1.0.0
 */
public class EdiarumExtensionsBundle extends ExtensionsBundle {

    @Override
    public String getDescription() {
        return "org.bbaw.telota.ediarum.documenttype";
    }

    @Override
    public String getDocumentTypeID() {
        return "A custom extensions bundle used for the Ediarum " +
                "Framework document type";
    }

    // TODO: Does this break XML mode for entering attributes with namespaces?
//    @Override
//    public AuthorExtensionStateListener createAuthorExtensionStateListener() {
//        return new EdiarumAuthorExtensionStateListener();
//    }

    //	@Override
//	public AuthorReferenceResolver createAuthorReferenceResolver() {
//		return new EdiarumReferencesResolver();
//	}
//
    @Override
    public LinkTextResolver createLinkTextResolver() {
        return new EdiarumLinkTextResolver();
    }
}
