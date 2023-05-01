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
import ro.sync.ecss.extensions.api.schemaaware.SchemaAwareHandlerResult;
import ro.sync.ecss.extensions.api.schemaaware.SchemaAwareHandlerResultInsertConstants;
import ro.sync.ecss.extensions.commons.operations.MoveCaretUtil;


import java.awt.Frame;

import static org.korpora.aeet.ediarum.EdiarumArgumentNames.*;

/**
 * add an element from an selection to an specified position.
 * 
 * The class belongs to package org.bbaw.telota.ediarum for the modification of the Oxygen framework
 * for several projects at the Berlin-Brandenburgische Akademie der Wissenschaften (BBAW) to build a
 * framework for edition projects (Ediarum - die Editionsarbeitsumgebung).
 *
 * @author Martin Fechner
 */
public class RegisterInsertFragmentOperation implements AuthorOperation {

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
            EdiarumArgumentDescriptor.ARGUMENT_ELEMENT,
            // Argument defining the location where the operation will be executed as an XPath expression.
            EdiarumArgumentDescriptor.ARGUMENT_XPATH_LOCATION,
            // Argument defining the relative position to the node obtained from the XPath location.
            EdiarumArgumentDescriptor.ARGUMENT_RELATIVE_LOCATION,
            EdiarumArgumentDescriptor.SCHEMA_AWARE_ARGUMENT_DESCRIPTOR
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
        String elementArgVal = ARGUMENTS_MAP.validateStringArgument(ARGUMENT_ELEMENT, args);
        Object xpathLocation = args.getArgumentValue(EdiarumArgumentNames.ARGUMENT_XPATH_LOCATION);
        Object relativeLocation = args.getArgumentValue(EdiarumArgumentNames.ARGUMENT_RELATIVE_LOCATION);
        Object multipleSelection = args.getArgumentValue(ARGUMENT_MULTIPLE_SELECTION);

        // Für die spätere Verwendung werden die Variablen für die Registereinträge und IDs erzeugt.
        String[] eintrag = null, id = null;

        // Dann wird das Registerdokument eingelesen, wobei auf die einzelnen Registerelement und ..
        // .. die Ausdrücke für die Einträge und IDs Rücksicht genommen wird.
        ReadListItems register = new ReadListItems(urlArgVal, nodeArgVal, expressionArgVal, variableArgVal, namespacesArgVal);
        // Die Arrays für die Einträge und IDs werden an die lokalen Variablen übergeben.
        eintrag = register.getEintrag();
        id = register.getID();

        // Dafür wird der RegisterDialog geöffnet und erhält die Einträge und IDs als Parameter.
        InsertRegisterDialog RegisterDialog = new InsertRegisterDialog((Frame) authorAccess.getWorkspaceAccess().getParentFrame(), eintrag, id, (multipleSelection).equals(AuthorConstants.ARG_VALUE_TRUE));
        // Wenn in dem Dialog ein Eintrag ausgewählt wurde, ..
        if (!RegisterDialog.getSelectedID().isEmpty()) {
            // wird im aktuellen Dokument um die Selektion das entsprechende Element mit ID eingesetzt.
            String element = elementArgVal;
            String IDitems = String.join(separationArgVal, RegisterDialog.getSelectedIDs());
            String xmlFragment = element.replaceAll("[$]ITEMS", IDitems);

            //The XML may contain an editor template for caret positioning.
            boolean moveCaretToSpecifiedPosition =
                    MoveCaretUtil.hasImposedEditorVariableCaretOffset(xmlFragment);
            int insertionOffset = authorAccess.getEditorAccess().getCaretOffset();

            Object schemaAwareArgumentValue = args.getArgumentValue(SCHEMA_AWARE_ARGUMENT);
            if (AuthorConstants.ARG_VALUE_FALSE.equals(schemaAwareArgumentValue)) {
                // Insert fragment at specified position.
                if (moveCaretToSpecifiedPosition) {
                    //Compute the offset where the insertion will take place.
                    if (xpathLocation != null && ((String) xpathLocation).trim().length() > 0) {
                        // Evaluate the expression and obtain the offset of the first node from the result
                        insertionOffset =
                                authorAccess.getDocumentController().getXPathLocationOffset(
                                        (String) xpathLocation, (String) relativeLocation);
                    }
                }

                authorAccess.getDocumentController().insertXMLFragment(
                        xmlFragment, (String) xpathLocation, (String) relativeLocation);
            } else {
                // Insert fragment schema aware.
                SchemaAwareHandlerResult result =
                        authorAccess.getDocumentController().insertXMLFragmentSchemaAware(
                                xmlFragment, (String) xpathLocation, (String) relativeLocation);
                //Keep the insertion offset.
                if (result != null) {
                    Integer off = (Integer) result.getResult(
                            SchemaAwareHandlerResultInsertConstants.RESULT_ID_HANDLE_INSERT_FRAGMENT_OFFSET);
                    if (off != null) {
                        insertionOffset = off;
                    }
                }
            }

            if (moveCaretToSpecifiedPosition) {
                //Detect the position in the Author page where the caret should be placed.
                MoveCaretUtil.moveCaretToImposedEditorVariableOffset(authorAccess, insertionOffset);
            }

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
        return "Opens a dialog with a list of index items. An fragment with the selected item id is inserted at the specified location.";
    }
}
