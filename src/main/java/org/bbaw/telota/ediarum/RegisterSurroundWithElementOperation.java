/**
 * RegisterSurroundWithElementOperation.java - is a class to surround a selection with an element.
 * It belongs to package ro.sync.ecss.extensions.ediarum for the modification of the Oxygen framework
 * for several projects at the Berlin-Brandenburgische Akademie der Wissenschaften (BBAW) to build a
 * framework for edition projects (Ediarum - die Editionsarbeitsumgebung).
 *
 * @author Martin Fechner
 */
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

import java.awt.Frame;

import static org.korpora.aeet.ediarum.EdiarumArgumentNames.*;

public class RegisterSurroundWithElementOperation implements AuthorOperation {

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
            EdiarumArgumentDescriptor.ARGUMENT_ELEMENT
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

        String urlArgVal = ARGUMENTS_MAP.validateStringArgument(EdiarumArgumentNames.ARGUMENT_URL, args);
        String nodeArgVal = ARGUMENTS_MAP.validateStringArgument(EdiarumArgumentNames.ARGUMENT_NODE, args);
        String namespacesArgVal = ARGUMENTS_MAP.validateStringArgument(EdiarumArgumentNames.ARGUMENT_NAMESPACES, args);
        String expressionArgVal = ARGUMENTS_MAP.validateStringArgument(EdiarumArgumentNames.ARGUMENT_EXPRESSION, args);
        String variableArgVal = ARGUMENTS_MAP.validateStringArgument(EdiarumArgumentNames.ARGUMENT_VARIABLE, args);
        String separationArgVal = ARGUMENTS_MAP.validateStringArgument(EdiarumArgumentNames.ARGUMENT_SEPARATION, args);
        String elementArgVal = ARGUMENTS_MAP.validateStringArgument(EdiarumArgumentNames.ARGUMENT_ELEMENT, args);
        String multipleSelection = (String) args.getArgumentValue(EdiarumArgumentNames.ARGUMENT_MULTIPLE_SELECTION);

        // Wenn im aktuellen Dokument nichts selektiert ist, wird das aktuelle Wort ausgewählt.
        if (!authorAccess.getEditorAccess().hasSelection()) {
            authorAccess.getEditorAccess().selectWord();
        }
        int selStart = authorAccess.getEditorAccess().getSelectionStart();
        int selEnd = authorAccess.getEditorAccess().getSelectionEnd() - 1;

        // Für die spätere Verwendung werden die Variablen für die Registereinträge und IDs erzeugt.
        String[] eintrag = null, id = null;

        // Dann wird das Registerdokument eingelesen, wobei auf die einzelnen Registerelement und ..
        // .. die Ausdrücke für die Einträge und IDs Rücksicht genommen wird.
        ReadListItems register = new ReadListItems((String) urlArgVal, (String) nodeArgVal, (String) expressionArgVal, (String) variableArgVal, (String) namespacesArgVal);
        // Die Arrays für die Einträge und IDs werden an die lokalen Variablen übergeben.
        eintrag = register.getEintrag();
        id = register.getID();

        // Dafür wird der RegisterDialog geöffnet und erhält die Einträge und IDs als Parameter.
        InsertRegisterDialog RegisterDialog = new InsertRegisterDialog((Frame) authorAccess.getWorkspaceAccess().getParentFrame(), eintrag, id, multipleSelection.equals(AuthorConstants.ARG_VALUE_TRUE));
        // Wenn in dem Dialog ein Eintrag ausgewählt wurde, ..
        if (!RegisterDialog.getSelectedID().isEmpty()) {
            // wird im aktuellen Dokument um die Selektion das entsprechende Element mit ID eingesetzt.
            String element = (String) elementArgVal;
            String IDitems = String.join((String) separationArgVal, RegisterDialog.getSelectedIDs());
            element = element.replaceAll("[$]ITEMS", IDitems);
            authorAccess.getDocumentController().surroundInFragment(element, selStart, selEnd);
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
        return "Opens a dialog with a list of index items. An fragment with the selected item id is inserted at the current selection.";
    }
}
