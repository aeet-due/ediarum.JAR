/**
 * OpenFileOperation.java - is a class to open a URL with the system browser.
 * It belongs to package ro.sync.ecss.extensions.ediarum for the modification of the Oxygen framework
 * for several projects at the Berlin-Brandenburgische Akademie der Wissenschaften (BBAW) to build a
 * framework for edition projects (Ediarum - die Editionsarbeitsumgebung).
 * @author Martin Fechner
 * @version 1.0.0
 */
package org.bbaw.telota.ediarum;

import java.io.IOException;

import org.bbaw.telota.ediarum.extensions.EdiarumArgumentValidator;

import org.korpora.aeet.ediarum.EdiarumArgumentDescriptor;
import org.korpora.aeet.ediarum.EdiarumArguments;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;

import static org.bbaw.telota.ediarum.EdiarumArgumentNames.*;

public class OpenFileOperation implements AuthorOperation {

	/**
	 * Arguments.
	 */
	private static final EdiarumArguments ARGUMENTS_MAP = new EdiarumArguments(new EdiarumArgumentDescriptor[]{
		new EdiarumArgumentDescriptor(
				ARGUMENT_URL,
				ArgumentDescriptor.TYPE_STRING,
				"The URL of the file. A local file can be opened with 'file://path-to-file'.")
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
			} catch (IOException e) {} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (isLinuxSystem()) {
			// exec linux commands ...
			try {
				Process p = Runtime.getRuntime().exec("xdg-open " + urlArgVal);
				p.waitFor();
			} catch (IOException e) {} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (isMacSystem()) {
			// exec mac commands ...
			try {
				Process p = Runtime.getRuntime().exec("open " + urlArgVal);
				p.waitFor();
			} catch (IOException e) {} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	static boolean isWindowsSystem() {
		String osName = System.getProperty("os.name").toLowerCase();
		return osName.indexOf("windows") >= 0;
	}

	static boolean isLinuxSystem() {
		String osName = System.getProperty("os.name").toLowerCase();
		return osName.indexOf("linux") >= 0;
	}

	static boolean isMacSystem() {
		String osName = System.getProperty("os.name").toLowerCase();
		return osName.indexOf("mac") >= 0;
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
