package org.bbaw.telota.ediarum;

import org.korpora.aeet.ediarum.EdiarumArgumentDescriptor;
import org.korpora.aeet.ediarum.EdiarumArguments;
import ro.sync.ecss.extensions.api.*;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;

import javax.swing.text.BadLocationException;
import java.awt.*;

import static org.korpora.aeet.ediarum.EdiarumArgumentNames.*;

/**
 * add an attribute to a selected element.
 * <p>
 * The class belongs to package org.bbaw.telota.ediarum for the modification of the Oxygen framework
 * for several projects at the Berlin-Brandenburgische Akademie der Wissenschaften (BBAW) to build a
 * framework for edition projects (Ediarum - die Editionsarbeitsumgebung).
 *
 * @author Martin Fechner
 */
public class RegisterChangeAttributeOperation implements AuthorOperation {

    /**
     * Arguments.
     */
    private static final EdiarumArguments ARGUMENTS_MAP = new EdiarumArguments(new EdiarumArgumentDescriptor[]{
            EdiarumArgumentDescriptor.ARGUMENT_URL_LIST,
            EdiarumArgumentDescriptor.ARGUMENT_NODE,
            EdiarumArgumentDescriptor.ARGUMENT_NAMESPACES,
            EdiarumArgumentDescriptor.ARGUMENT_EXPRESSION,
            EdiarumArgumentDescriptor.ARGUMENT_VARIABLE,
            EdiarumArgumentDescriptor.ARGUMENT_MULTIPLE_SELECTION,
            EdiarumArgumentDescriptor.ARGUMENT_SEPARATION,
            EdiarumArgumentDescriptor.ARGUMENT_ATTRIBUTENAME,
            EdiarumArgumentDescriptor.ARGUMENT_XPATHFROMSELECTION,
            EdiarumArgumentDescriptor.ARGUMENT_ATTRIBUTEVALUE,
    });


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
        String urlArgVal = ARGUMENTS_MAP.validateStringArgument(ARGUMENT_URL, args);
        String nodeArgVal = ARGUMENTS_MAP.validateStringArgument(ARGUMENT_NODE, args);
        String namespacesArgVal = ARGUMENTS_MAP.validateStringArgument(ARGUMENT_NAMESPACES, args);
        String expressionArgVal = ARGUMENTS_MAP.validateStringArgument(ARGUMENT_EXPRESSION, args);
        String variableArgVal = ARGUMENTS_MAP.validateStringArgument(ARGUMENT_VARIABLE, args);
        String separationArgVal = ARGUMENTS_MAP.validateStringArgument(ARGUMENT_SEPARATION, args);
        String attributenameArgVal = ARGUMENTS_MAP.validateStringArgument(ARGUMENT_ATTRIBUTENAME, args);
        String xpathfromselectionArgVal = ARGUMENTS_MAP.validateStringArgument(ARGUMENT_XPATHFROMSELECTION, args);
        String attributevalArgVal = ARGUMENTS_MAP.validateStringArgument(ARGUMENT_ATTRIBUTEVALUE, args);
        Object multipleSelection = args.getArgumentValue(ARGUMENT_MULTIPLE_SELECTION);

        // Wenn im aktuellen Dokument nichts selektiert ist, wird das aktuelle Wort ausgewählt.
        if (!authorAccess.getEditorAccess().hasSelection()) {
            authorAccess.getEditorAccess().selectWord();
        }
        int selStart = authorAccess.getEditorAccess().getSelectionStart();

        // TODO: show currently selected item
        try {
            AuthorNode selNode = authorAccess.getDocumentController().getNodeAtOffset(selStart);
            AuthorElement selElement = (AuthorElement) (authorAccess.getDocumentController()
                    .findNodesByXPath(xpathfromselectionArgVal, selNode, false, true, true, false))[0];
            var previousAttribute = selElement.getAttribute(attributenameArgVal);

            String previousId = (previousAttribute != null) ? previousAttribute.getValue() : selElement.getTextContent();

            // Das Registerdokument wird eingelesen, wobei auf die einzelnen Registerelement und ..
            // .. die Ausdrücke für die Einträge und IDs Rücksicht genommen wird.
            ReadListItems register =
                    new ReadListItems(urlArgVal, nodeArgVal, expressionArgVal, variableArgVal, namespacesArgVal);
            // Die Arrays für die Einträge und IDs werden an die lokalen Variablen übergeben.
            String[] eintrag = register.getEintrag();
            String[] id = register.getID();

            // Dafür wird der RegisterDialog geöffnet und erhält die Einträge und IDs als Parameter.
            InsertRegisterDialog RegisterDialog =
                    new InsertRegisterDialog((Frame) authorAccess.getWorkspaceAccess().getParentFrame(), eintrag, id,
                            multipleSelection.equals(AuthorConstants.ARG_VALUE_TRUE), previousId, null);
            // Wenn in dem Dialog ein Eintrag ausgewählt wurde, ..
            if (!RegisterDialog.getSelectedID().isEmpty()) {
                // wird im aktuellen Dokument um die Selektion das entsprechende Element mit ID eingesetzt.
                String newAttrValue = attributevalArgVal;
                String IDitems = String.join(separationArgVal, RegisterDialog.getSelectedIDs());
                newAttrValue = newAttrValue.replaceAll("[$]ITEMS", IDitems);

                authorAccess.getDocumentController()
                        .setAttribute(attributenameArgVal, new AttrValue(newAttrValue), selElement);
            }
        } catch (BadLocationException e) {
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
        return "Opens a dialog with a list of index items. An attribute with the selected item id is inserted at the specified location.";
    }
}
