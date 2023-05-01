package org.bbaw.telota.ediarum.dates;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;

import org.korpora.aeet.ediarum.EdiarumArgumentNames;
import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorOperation;
import ro.sync.ecss.extensions.api.AuthorOperationException;

public class OpenDateConfigOperation implements AuthorOperation {

	/**
	 * Die Argumente.
	 */
	private static final ArgumentDescriptor[] ARGUMENTS = new ArgumentDescriptor[] {
			new ArgumentDescriptor(EdiarumArgumentNames.ARGUMENT_PATH, ArgumentDescriptor.TYPE_STRING,
					"Path of the config file of the date recognition app.") };

	@Override
	public String getDescription() {
		return "Open the config file of the date recognition app.";
	}

	@Override
	public void doOperation(AuthorAccess arg0, ArgumentsMap arg1)
			throws IllegalArgumentException, AuthorOperationException {
		final Object path = arg1.getArgumentValue(EdiarumArgumentNames.ARGUMENT_PATH);

		if (!(path instanceof String) || ((String) path).isEmpty())
			throw new IllegalArgumentException("The argument \"path\" is not declared.");

		try {
			File f = new File((String) path);
			if (!f.exists())
				throw new FileNotFoundException("Couldn't find file denoted by argument \"path\" - " + path);

			arg0.getWorkspaceAccess().open(new File((String) path).toURI().toURL());
		} catch (MalformedURLException | FileNotFoundException e) {
			arg0.getWorkspaceAccess().showErrorMessage(e.toString());
		}
	}

	@Override
	public ArgumentDescriptor[] getArguments() {
		return ARGUMENTS;
	}

}
