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
import org.korpora.aeet.ediarum.EdiarumArguments;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorConstants;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;

import java.awt.Frame;

import static org.bbaw.telota.ediarum.EdiarumArgumentNames.*;

public class RegisterSurroundWithElementOperation implements AuthorOperation {

    /**
     * Arguments.
     */
    private static final EdiarumArguments ARGUMENTS_MAP = new EdiarumArguments(new EdiarumArgumentDescriptor[]{
            new EdiarumArgumentDescriptor(
                    ARGUMENT_URL,
                    ArgumentDescriptor.TYPE_STRING,
                    "The URL to the .xml file with the list, e.g.: " +
                            "http://user:passwort@www.example.com:port/exist/webdav/db/register.xml"),
            new EdiarumArgumentDescriptor(
                    ARGUMENT_NODE,
                    ArgumentDescriptor.TYPE_STRING,
                    "An XPath expression for the list items, e.g.: //item"),
            new EdiarumArgumentDescriptor(
                    ARGUMENT_NAMESPACES,
                    ArgumentDescriptor.TYPE_STRING,
                    "An whitespace separated list of namespace declarations with QNames before a colon, e.g.: tei:http://www.tei-c.org/ns/1.0",
                    null),
            new EdiarumArgumentDescriptor(
                    ARGUMENT_EXPRESSION,
                    ArgumentDescriptor.TYPE_STRING,
                    "A string how the items are rendered in the list. "
                            + "Use $XPATH{expression} for xpath expressions (starting with @, /, //, ./, # (for functions)), "
                            + "E.g.: $XPATH{/name}, $XPATH{/vorname} ($XPATH{/lebensdaten})"),
            new EdiarumArgumentDescriptor(
                    ARGUMENT_VARIABLE,
                    ArgumentDescriptor.TYPE_STRING,
                    "The item variable which is used for the XML fragment, e.g.: #$XPATH{@id}"),
            new EdiarumArgumentDescriptor(
                    ARGUMENT_MULTIPLE_SELECTION,
                    ArgumentDescriptor.TYPE_CONSTANT_LIST,
                    "When is enabled, multiple selection will be possible",
                    new String[]{
                            AuthorConstants.ARG_VALUE_TRUE,
                            AuthorConstants.ARG_VALUE_FALSE,
                    },
                    AuthorConstants.ARG_VALUE_TRUE),
            new EdiarumArgumentDescriptor(
                    ARGUMENT_SEPARATION,
                    ArgumentDescriptor.TYPE_STRING,
                    "The string for separating the item variables. Default value is a space."),
            new EdiarumArgumentDescriptor(
                    ARGUMENT_ELEMENT,
                    ArgumentDescriptor.TYPE_STRING,
                    "The XML fragment which should be inserted at current caret position."
                            + "Multiple list selections will be separated through spaces, e.g.: "
                            + "<persName xmlns='http://www.tei-c.org/ns/1.0' key='$ITEMS' />")
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
//		String urlArgVal = EdiarumArgumentValidator.validateStringArgument(ARGUMENT_URL, args);
//		String nodeArgVal = EdiarumArgumentValidator.validateStringArgument(ARGUMENT_NODE, args);
//		String namespacesArgVal = EdiarumArgumentValidator.validateStringArgument(ARGUMENT_NAMESPACES, args, null);
//		String expressionArgVal = EdiarumArgumentValidator.validateStringArgument(ARGUMENT_EXPRESSION, args);
//		String variableArgVal = EdiarumArgumentValidator.validateStringArgument(ARGUMENT_VARIABLE, args);
//		String separationArgVal = EdiarumArgumentValidator.validateStringArgument(ARGUMENT_SEPARATION, args);
//		String elementArgVal = EdiarumArgumentValidator.validateStringArgument(ARGUMENT_ELEMENT, args);
//		Object multipleSelection = args.getArgumentValue(ARGUMENT_MULTIPLE_SELECTION);

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
