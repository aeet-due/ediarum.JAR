package org.bbaw.telota.ediarum;

import org.korpora.aeet.ediarum.EdiarumArgumentDescriptor;
import org.korpora.aeet.ediarum.EdiarumArguments;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;

import java.awt.Frame;

import static org.korpora.aeet.ediarum.EdiarumArgumentNames.*;


/**
 * surround a selection with a register elements.
 * <p>
 * The class belongs to package org.bbaw.telota.ediarum for the modification of the Oxygen framework
 * for several projects at the Berlin-Brandenburgische Akademie der Wissenschaften (BBAW) to build a
 * framework for edition projects (Ediarum - die Editionsarbeitsumgebung).
 *
 * @author Martin Fechner
 * @version 1.0.5
 */
public class RegisterSurroundWithDifferentFragmentsOperation implements AuthorOperation {
    /**
     * Arguments.
     */
    private static final EdiarumArguments ARGUMENTS_MAP = new EdiarumArguments(new EdiarumArgumentDescriptor[]{
            EdiarumArgumentDescriptor.ARGUMENT_URL_LIST,
            EdiarumArgumentDescriptor.ARGUMENT_NODE,
            EdiarumArgumentDescriptor.ARGUMENT_NAMESPACES,
            EdiarumArgumentDescriptor.ARGUMENT_EXPRESSION,
            EdiarumArgumentDescriptor.ARGUMENT_ID,
            EdiarumArgumentDescriptor.ARGUMENT_FIRST_ELEMENT,
            EdiarumArgumentDescriptor.ARGUMENT_SECOND_ELEMENT,
    });

    static EdiarumArgumentDescriptor[] ARGUMENTS;

    static {
        ARGUMENTS = ARGUMENTS_MAP.getArguments();
    }

    /**
     * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(AuthorAccess, ArgumentsMap)
     */
    public void doOperation(AuthorAccess authorAccess, ArgumentsMap args) throws AuthorOperationException {
        // Die übergebenen Argumente werden eingelesen.
        String urlArgVal = ARGUMENTS_MAP.validateStringArgument(ARGUMENT_URL, args);
        String nodeArgVal = ARGUMENTS_MAP.validateStringArgument(ARGUMENT_NODE, args);
        String namespacesArgVal = ARGUMENTS_MAP.validateStringArgument(ARGUMENT_NAMESPACES, args);
        String expressionArgVal = ARGUMENTS_MAP.validateStringArgument(ARGUMENT_EXPRESSION, args
        );
        String idArgVal = ARGUMENTS_MAP.validateStringArgument(ARGUMENT_ID, args);
        String firstElementArgVal = ARGUMENTS_MAP.validateStringArgument(ARGUMENT_FIRST_ELEMENT, args);
        String secondElementArgVal = ARGUMENTS_MAP.validateStringArgument(ARGUMENT_SECOND_ELEMENT, args);

        // Wenn im aktuellen Dokument nichts selektiert ist, wird das aktuelle Wort ausgewählt.
        if (!authorAccess.getEditorAccess().hasSelection()) {
            authorAccess.getEditorAccess().selectWord();
        }
        int selStart = authorAccess.getEditorAccess().getSelectionStart();
        int selEnd = authorAccess.getEditorAccess().getSelectionEnd();

        // Für die spätere Verwendung werden die Variablen für die Registereinträge und Elemente erzeugt.
        String[] eintrag = null, elements = null;

        //  Der später einzufügende Ausdruck wird gebaut.
        String variable = firstElementArgVal + "$SELECTION" + secondElementArgVal;

        // Dann wird das Registerdokument eingelesen, wobei auf die einzelnen Registerelement und
        // die Ausdrücke für die Einträge und Elemente Rücksicht genommen wird.
        ReadListItems register = new ReadListItems(urlArgVal, nodeArgVal, expressionArgVal, variable, namespacesArgVal);

        // Die Arrays für die Einträge und IDs werden an die lokalen Variablen übergeben.
        eintrag = register.getEintrag();
        elements = register.getID();

        // Dafür wird der RegisterDialog geöffnet und erhält die Einträge und IDs als Parameter.
        InsertRegisterDialog RegisterDialog =
                new InsertRegisterDialog((Frame) authorAccess.getWorkspaceAccess().getParentFrame(), eintrag, elements,
                        false);
        // Wenn in dem Dialog ein Eintrag ausgewählt wurde, ..
        if (!RegisterDialog.getSelectedID().isEmpty()) {
            // .. wird in den entsprechenden Elementen die eingestellte ID eingefügt, ..
            String[] selectedIDInParts = RegisterDialog.getSelectedID().split("\\$ID");
            StringBuilder selectedID = new StringBuilder(selectedIDInParts[0]);
            for (int i = 1; i < selectedIDInParts.length; i++) {
                selectedID.append(idArgVal).append(selectedIDInParts[i]);
            }
            // .. und dann werden im aktuellen Dokument um die Selektion die entsprechenden Elemente eingesetzt.
            String[] surroundElements = selectedID.toString().split("\\$SELECTION");
            authorAccess.getDocumentController().insertXMLFragment(surroundElements[1], selEnd);
            authorAccess.getDocumentController().insertXMLFragment(surroundElements[0], selStart);
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
        return "Opens a dialog to choose an entry from an external index file. The elements with the specified id is inserted around the selection.";
    }
}
