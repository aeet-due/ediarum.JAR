/**
 * SurroundWithElement.java - is a class for inserting elements before and after a selection.
 * It belongs to package org.bbaw.telota.ediarum for the modification of the Oxygen framework
 * for several projects at the Berlin-Brandenburgische Akademie der Wissenschaften (BBAW) to build a
 * framework for edition projects (Ediarum - die Editionsarbeitsumgebung).
 *
 * @author Martin Fechner
 * @version 1.0.2
 */
package org.bbaw.telota.ediarum;

import org.korpora.aeet.ediarum.EdiarumArgumentDescriptor;
import org.korpora.aeet.ediarum.EdiarumArguments;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;


import static org.korpora.aeet.ediarum.EdiarumArgumentNames.*;

@SuppressWarnings("unused")
public class SurroundWithDifferentFragmentsOperation implements AuthorOperation {

    /**
     * Arguments.
     */
    private static final EdiarumArguments ARGUMENTS_MAP = new EdiarumArguments(
            new EdiarumArgumentDescriptor[]{EdiarumArgumentDescriptor.ARGUMENT_ID, EdiarumArgumentDescriptor.ARGUMENT_FIRST_ELEMENT, EdiarumArgumentDescriptor.ARGUMENT_SECOND_ELEMENT});
    static EdiarumArgumentDescriptor[] ARGUMENTS;

    static {
        ARGUMENTS = ARGUMENTS_MAP.getArguments();
    }


    /**
     * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(AuthorAccess, ArgumentsMap)
     */
    public void doOperation(AuthorAccess authorAccess, ArgumentsMap args) throws AuthorOperationException {
        String firstElementArgVal = ARGUMENTS_MAP.validateStringArgument(ARGUMENT_FIRST_ELEMENT, args);
        String secondElementArgVal = ARGUMENTS_MAP.validateStringArgument(ARGUMENT_SECOND_ELEMENT, args);
        String idArgVal = ARGUMENTS_MAP.validateStringArgument(ARGUMENT_ID, args);

        // Falls im Text nichts selektiert ist, wird das aktuelle Word ausgewählt.
        if (!authorAccess.getEditorAccess().hasSelection()) {
            authorAccess.getEditorAccess().selectWord();
        }
        int selStart = authorAccess.getEditorAccess().getSelectionStart();
        int selEnd = authorAccess.getEditorAccess().getSelectionEnd();

        // Die ID wird an den entsprechenden Stellen eingefügt.
        String[] firstElementInParts = firstElementArgVal.split("\\$ID");
        StringBuilder firstElementWithID = new StringBuilder(firstElementInParts[0]);
        for (int i = 1; i < firstElementInParts.length; i++) {
            firstElementWithID.append(idArgVal).append(firstElementInParts[i]);
        }

        // Die ID wird an den entsprechenden Stellen eingefügt.
        String[] secondElementInParts = secondElementArgVal.split("\\$ID");
        StringBuilder secondElementWithID = new StringBuilder(secondElementInParts[0]);
        for (int i = 1; i < secondElementInParts.length; i++) {
            secondElementWithID.append(idArgVal).append(secondElementInParts[i]);
        }

        // .. das erste wird vor der Selektion eingefügt, und das zweite dahinter.
        authorAccess.getDocumentController().insertXMLFragment(secondElementWithID.toString(), selEnd);
        authorAccess.getDocumentController().insertXMLFragment(firstElementWithID.toString(), selStart);
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
        return "Inserts before and after the selection different elements.";
    }
}
