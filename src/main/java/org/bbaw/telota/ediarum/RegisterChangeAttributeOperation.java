/**
 * RegisterChangeAttributeOperation.java - is a class to add an attribute to a selected element.
 * It belongs to package ro.sync.ecss.extensions.ediarum for the modification of the Oxygen framework
 * for several projects at the Berlin-Brandenburgische Akademie der Wissenschaften (BBAW) to build a
 * framework for edition projects (Ediarum - die Editionsarbeitsumgebung).
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
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;

import java.awt.Frame;

import javax.swing.text.BadLocationException;

import org.bbaw.telota.ediarum.extensions.EdiarumArgumentValidator;

import static org.bbaw.telota.ediarum.EdiarumArgumentNames.*;

public class RegisterChangeAttributeOperation implements AuthorOperation{

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
				+ "Use $XPATH{expression} for xpath expressions (starting with @, /, //, ./), "
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
				ARGUMENT_ATTRIBUTENAME,
				ArgumentDescriptor.TYPE_STRING,
				"The name of the new attribute, e.g.: " +
				"key"),
		new EdiarumArgumentDescriptor(
				ARGUMENT_XPATHFROMSELECTION,
				ArgumentDescriptor.TYPE_STRING,
				"A relative XPath expression from current context node "
				+ "to the element with the new attribute: " +
				"./child"),
		new EdiarumArgumentDescriptor(
				ARGUMENT_ATTRIBUTEVALUE,
				ArgumentDescriptor.TYPE_STRING,
				"The content of the new attribute, e.g.: " +
				"some text.. $ITEMS")
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
		String urlArgVal = EdiarumArgumentValidator.validateStringArgument(ARGUMENT_URL, args);
		String nodeArgVal = EdiarumArgumentValidator.validateStringArgument(ARGUMENT_NODE, args);
		String namespacesArgVal = EdiarumArgumentValidator.validateStringArgument(ARGUMENT_NAMESPACES, args, null);
		String expressionArgVal = EdiarumArgumentValidator.validateStringArgument(ARGUMENT_EXPRESSION, args);
		String variableArgVal = EdiarumArgumentValidator.validateStringArgument(ARGUMENT_VARIABLE, args);
		String separationArgVal = EdiarumArgumentValidator.validateStringArgument(ARGUMENT_SEPARATION, args);
		String attributenameArgVal = EdiarumArgumentValidator.validateStringArgument(ARGUMENT_ATTRIBUTENAME, args);
		String xpathfromselectionArgVal = EdiarumArgumentValidator.validateStringArgument(ARGUMENT_XPATHFROMSELECTION, args);
		String attributevalArgVal = EdiarumArgumentValidator.validateStringArgument(ARGUMENT_ATTRIBUTEVALUE, args);
		Object multipleSelection = args.getArgumentValue(ARGUMENT_MULTIPLE_SELECTION);

		// Wenn im aktuellen Dokument nichts selektiert ist, wird das aktuelle Wort ausgewählt.
		if (!authorAccess.getEditorAccess().hasSelection()) {
			authorAccess.getEditorAccess().selectWord();
		}
		int selStart = authorAccess.getEditorAccess().getSelectionStart();

		// Für die spätere Verwendung werden die Variablen für die Registereinträge und IDs erzeugt.
		String[] eintrag = null, id = null;

		// Dann wird das Registerdokument eingelesen, wobei auf die einzelnen Registerelement und ..
		// .. die Ausdrücke für die Einträge und IDs Rücksicht genommen wird.
		ReadListItems register = new ReadListItems(urlArgVal, nodeArgVal, expressionArgVal, variableArgVal, namespacesArgVal);
		// Die Arrays für die Einträge und IDs werden an die lokalen Variablen übergeben.
		eintrag = register.getEintrag();
		id = register.getID();

		// Dafür wird der RegisterDialog geöffnet und erhält die Einträge und IDs als Parameter.
		InsertRegisterDialog RegisterDialog = new InsertRegisterDialog((Frame) authorAccess.getWorkspaceAccess().getParentFrame(), eintrag, id, ((String) multipleSelection).equals(AuthorConstants.ARG_VALUE_TRUE));
		// Wenn in dem Dialog ein Eintrag ausgewählt wurde, ..
		if (!RegisterDialog.getSelectedID().isEmpty()){
			// wird im aktuellen Dokument um die Selektion das entsprechende Element mit ID eingesetzt.
			AuthorElement selElement;
			try {
				AuthorNode selNode = authorAccess.getDocumentController().getNodeAtOffset(selStart);
				selElement = (AuthorElement) (authorAccess.getDocumentController().findNodesByXPath((String) xpathfromselectionArgVal, selNode, false, true, true, false))[0];
				String newAttrValue = attributevalArgVal;
				String IDitems = String.join((String)separationArgVal, RegisterDialog.getSelectedIDs());
				newAttrValue = newAttrValue.replaceAll("[$]ITEMS", IDitems);

				authorAccess.getDocumentController().setAttribute((String) attributenameArgVal, new AttrValue(newAttrValue), selElement);
			} catch (BadLocationException e) {}
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
