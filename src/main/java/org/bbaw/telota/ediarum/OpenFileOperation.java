package org.bbaw.telota.ediarum;

import java.io.IOException;

import org.korpora.aeet.ediarum.EdiarumArgumentDescriptor;
import org.korpora.aeet.ediarum.EdiarumArguments;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;

import static org.korpora.aeet.ediarum.EdiarumArgumentNames.*;

/**
 * open a URL with the system browser.
 * <p>
 * The class belongs to package org.bbaw.telota.ediarum for the modification of the Oxygen framework
 * for several projects at the Berlin-Brandenburgische Akademie der Wissenschaften (BBAW) to build a
 * framework for edition projects (Ediarum - die Editionsarbeitsumgebung).
 *
 * @author Martin Fechner
 * @version 1.0.0
 */
public class OpenFileOperation implements AuthorOperation {

    /**
     * Arguments.
     */
    private static final EdiarumArguments ARGUMENTS_MAP = new EdiarumArguments(new EdiarumArgumentDescriptor[]{
            EdiarumArgumentDescriptor.ARGUMENT_URL
    });

    static EdiarumArgumentDescriptor[] ARGUMENTS;

    static {
        ARGUMENTS = ARGUMENTS_MAP.getArguments();
    }

    /**
     * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(AuthorAccess, ArgumentsMap)
     */
    @Override
    public void doOperation(AuthorAccess authorAccess, ArgumentsMap args) throws IllegalArgumentException, AuthorOperationException {
        // Die übergebenen Argumente werden eingelesen.
        String urlArgVal = ARGUMENTS_MAP.validateStringArgument(ARGUMENT_URL, args);

        if (isWindowsSystem()) {
            // exec windows commands ...
            try {
                Process p = Runtime.getRuntime().exec("cmd /c start " + urlArgVal);
                p.waitFor();
            } catch (IOException e) {
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (isLinuxSystem()) {
            // exec linux commands ...
            try {
                Process p = Runtime.getRuntime().exec("xdg-open " + urlArgVal);
                p.waitFor();
            } catch (IOException e) {
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (isMacSystem()) {
            // exec mac commands ...
            try {
                Process p = Runtime.getRuntime().exec("open " + urlArgVal);
                p.waitFor();
            } catch (IOException e) {
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    static boolean isWindowsSystem() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("windows");
    }

    static boolean isLinuxSystem() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("linux");
    }

    static boolean isMacSystem() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("mac");
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
        return "Opens an URL or file with the default system application.";
    }
}
