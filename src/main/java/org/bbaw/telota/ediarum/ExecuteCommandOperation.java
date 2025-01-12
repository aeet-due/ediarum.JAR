package org.bbaw.telota.ediarum;

import java.io.IOException;

import org.korpora.aeet.ediarum.EdiarumArgumentDescriptor;
import org.korpora.aeet.ediarum.EdiarumArgumentNames;
import org.korpora.aeet.ediarum.EdiarumArguments;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;

/**
 * an operation for executing an external program
 * <p>
 * The class belongs to package org.bbaw.telota.ediarum for the modification of the Oxygen framework
 * for several projects at the Berlin-Brandenburgische Akademie der Wissenschaften (BBAW) to build a
 * framework for edition projects (Ediarum - die Editionsarbeitsumgebung).
 *
 * @author Martin Fechner
 * @version 1.0.0
 */
public class ExecuteCommandOperation implements AuthorOperation {

    /**
     * Arguments.
     */
    private static final EdiarumArguments ARGUMENTS_MAP = new EdiarumArguments(new EdiarumArgumentDescriptor[]{
            EdiarumArgumentDescriptor.ARGUMENT_COMMAND,
    });

    static EdiarumArgumentDescriptor[] ARGUMENTS;

    static {
        ARGUMENTS = ARGUMENTS_MAP.getArguments();
    }

    /**
     * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(AuthorAccess, ArgumentsMap)
     */
    @Override
    public void doOperation(AuthorAccess authorAccess, ArgumentsMap args) throws AuthorOperationException {
        // Die übergebenen Argumente werden eingelesen ..
        String cmdArgVal = ARGUMENTS_MAP.validateStringArgument(EdiarumArgumentNames.ARGUMENT_COMMAND, args);
        // .. und überprüft.
        try {
            Process p = Runtime.getRuntime().exec(cmdArgVal);
            p.waitFor();
        } catch (IOException e) {
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @see ro.sync.ecss.extensions.api.AuthorOperation#getArguments()
     */
    @Override
    public ArgumentDescriptor[] getArguments() {
        return ARGUMENTS;
    }

    /**
     * @see ro.sync.ecss.extensions.api.AuthorOperation#getDescription()
     */
    @Override
    public String getDescription() {
        return "Executes an external command.";
    }
}
