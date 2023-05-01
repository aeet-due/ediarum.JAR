package org.bbaw.telota.ediarum;

import org.korpora.aeet.ediarum.EdiarumArgumentDescriptor;
import org.korpora.aeet.ediarum.EdiarumArgumentNames;
import org.korpora.aeet.ediarum.EdiarumArguments;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorConstants;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.node.AuthorNode;

import java.util.Arrays;

import javax.swing.text.BadLocationException;

import static org.korpora.aeet.ediarum.EdiarumArgumentNames.ARGUMENT_XPATH_LOCATION;

/**
 * add an element at a specified position after other elements
 * in a way that a predefined order of elements is preserved.
 * 
 * It belongs to package org.bbaw.telota.ediarum for the modification of the Oxygen framework
 * for several projects at the Berlin-Brandenburgische Akademie der Wissenschaften (BBAW) to build a
 * framework for edition projects (Ediarum - die Editionsarbeitsumgebung).
 *
 * @author Martin Fechner
 */
public class InsertFragmentAfterOperation implements AuthorOperation {
    /**
     * Argument describing the insertNode.
     */
    private static final String ARGUMENT_ELEMENT = "element";

    /**
     * Arguments.
     */
    private static final EdiarumArguments ARGUMENTS_MAP =
            new EdiarumArguments(new EdiarumArgumentDescriptor[]{
                    EdiarumArgumentDescriptor.ARGUMENT_ELEMENT,
                    EdiarumArgumentDescriptor.ARGUMENT_XPATH_LOCATION,
                    // Argument defining the relative position to the node obtained from the XPath location.
                    EdiarumArgumentDescriptor.ARGUMENT_XPATH_BEFORE_LOCATIONS,
                    EdiarumArgumentDescriptor.SCHEMA_AWARE_ARGUMENT_DESCRIPTOR});

    static EdiarumArgumentDescriptor[] ARGUMENTS;

    static {
        ARGUMENTS = ARGUMENTS_MAP.getArguments();
    }

    /**
     * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(AuthorAccess, ArgumentsMap)
     */
    public void doOperation(AuthorAccess authorAccess, ArgumentsMap args) throws AuthorOperationException {
        // Die übergebenen Argumente werden eingelesen ..
        // .. und überprüft.
        String elementArgVal = ARGUMENTS_MAP.validateStringArgument(ARGUMENT_ELEMENT, args);
        Object xpathLocation = args.getArgumentValue(ARGUMENT_XPATH_LOCATION);
        String xpathBeforeLocations = ARGUMENTS_MAP.validateStringArgument(
                EdiarumArgumentNames.ARGUMENT_XPATH_BEFORE_LOCATIONS, args);

        String xmlFragment = elementArgVal;

        int insertionOffset = authorAccess.getEditorAccess().getCaretOffset();

        // Insert fragment at specified position.
        //Compute the offset where the insertion will take place.
        if (xpathLocation != null && ((String) xpathLocation).trim().length() > 0) {
            // Das Element soll als letzte Möglichkeit als letztes im Elternelement eingefügt (xpathLocation) werden.
            // Evaluate the expression and obtain the offset of the first node from the result
            insertionOffset = authorAccess.getDocumentController().getXPathLocationOffset((String) xpathLocation,
                    AuthorConstants.POSITION_INSIDE_LAST);
        }
        AuthorNode parentNode;
        try {
            parentNode = authorAccess.getDocumentController().getNodeAtOffset(insertionOffset);
            // Es wird geprüft, ob es weiter vorn eingefügt werden soll.
            // Dazu werden der Reihe nach alle Kinder geprüft, ..
            AuthorNode[] childElements =
                    authorAccess.getDocumentController().findNodesByXPath("./node()", parentNode, true, true, true,
                            true);
            for (AuthorNode child : childElements) {
                // .. wenn sie nicht als gültige Vorgänger in Frage kommen, ..
                AuthorNode[] xpathBeforeNodes =
                        authorAccess.getDocumentController().findNodesByXPath(xpathBeforeLocations, parentNode,
                                true, true, true, true);
                boolean childIsInBeforeNodes = Arrays.asList(xpathBeforeNodes).contains(child);
                if (!childIsInBeforeNodes) {
                    // .. soll das neue Element vor dem letzten ungültigen Kind eingefügt werden.
                    int offsetBeforeChild = child.getStartOffset();
                    insertionOffset = offsetBeforeChild;
                    break;
                }
            }
            // Füge das Element an entsprechende Position ein.
            authorAccess.getDocumentController().insertXMLFragment(xmlFragment, insertionOffset);
        } catch (BadLocationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
     */
    public ArgumentDescriptor[] getArguments() {
        return ARGUMENTS;
    }

    /**
     * @see ro.sync.ecss.extensions.api.AuthorOperation#getDescription()
     */
    public String getDescription() {
        return "Insert a document fragment after allowed preceding siblings.";
    }
}
