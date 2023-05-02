package org.korpora.aeet.ediarum;

import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.AuthorConstants;
import ro.sync.ecss.extensions.api.AuthorOperation;

/**
 * Wrapper for Oxygen {@link ArgumentDescriptor}, which allows to state whether
 * an argument is nullable.
 * <p>
 * Weirdly enough, a null (default) value cannot be distinguished
 * from non-occurence in the underlying class.
 * <p>
 * Parameters are the same as for {@link ArgumentDescriptor},
 * but if null default value is provided, the argument is marked as nullable.
 */
public class EdiarumArgumentDescriptor extends ArgumentDescriptor {

    boolean nullable;

    public EdiarumArgumentDescriptor(String name, int type, String description, String defaultValue) {
        super(name, type, description, defaultValue);
        if (defaultValue == null) nullable = true;
    }

    public EdiarumArgumentDescriptor(String name, int type, String description) {
        super(name, type, description);
        nullable = false;
    }

    public EdiarumArgumentDescriptor(String name, int type, String description, String[] allowedValues,
                                     String defaultValue) {
        super(name, type, description, allowedValues, defaultValue);
        if (defaultValue == null) nullable = true;
    }

    public static EdiarumArgumentDescriptor SCHEMA_AWARE_ARGUMENT_DESCRIPTOR =
            new EdiarumArgumentDescriptor(AuthorOperation.SCHEMA_AWARE_ARGUMENT_DESCRIPTOR.getName(),
                    AuthorOperation.SCHEMA_AWARE_ARGUMENT_DESCRIPTOR.getType(),
                    AuthorOperation.SCHEMA_AWARE_ARGUMENT_DESCRIPTOR.getDescription(),
                    AuthorOperation.SCHEMA_AWARE_ARGUMENT_DESCRIPTOR.getAllowedValues(),
                    AuthorOperation.SCHEMA_AWARE_ARGUMENT_DESCRIPTOR.getDefaultValue());

    public static EdiarumArgumentDescriptor ARGUMENT_SEPARATION =
            new EdiarumArgumentDescriptor(EdiarumArgumentNames.ARGUMENT_SEPARATION, ArgumentDescriptor.TYPE_STRING,
                    "The string for separating the item variables. Default value is a space.", " ");
    public static EdiarumArgumentDescriptor ARGUMENT_ELEMENT =
            new EdiarumArgumentDescriptor(EdiarumArgumentNames.ARGUMENT_ELEMENT, ArgumentDescriptor.TYPE_STRING,
                    "The XML fragment which should be inserted at current caret position." +
                            "Multiple list selections will be separated through spaces, e.g.: " +
                            "<persName xmlns='http://www.tei-c.org/ns/1.0' key='$ITEMS' />");

    public static EdiarumArgumentDescriptor ARGUMENT_URL =
            new EdiarumArgumentDescriptor(EdiarumArgumentNames.ARGUMENT_URL, ArgumentDescriptor.TYPE_STRING,
                    "The URL of the file. A local file can be opened with 'file://path-to-file'.");

    public static EdiarumArgumentDescriptor ARGUMENT_URL_LIST =
            new EdiarumArgumentDescriptor(EdiarumArgumentNames.ARGUMENT_URL, ArgumentDescriptor.TYPE_STRING,
                    "The URL to the .xml file with the list, e.g.: " +
                            "http://user:passwort@www.example.com:port/exist/webdav/db/register.xml");

    public static EdiarumArgumentDescriptor ARGUMENT_NODE =
            new EdiarumArgumentDescriptor(EdiarumArgumentNames.ARGUMENT_NODE, ArgumentDescriptor.TYPE_STRING,
                    "An XPath expression for the list items, e.g.: //item",
                    "/li|/tei:li");

    public static EdiarumArgumentDescriptor ARGUMENT_NAMESPACES =
            new EdiarumArgumentDescriptor(EdiarumArgumentNames.ARGUMENT_NAMESPACES, ArgumentDescriptor.TYPE_STRING,
                    "An whitespace separated list of namespace declarations with QNames before a colon, e.g.: tei:http://www.tei-c.org/ns/1.0",
                    null);

    public static EdiarumArgumentDescriptor ARGUMENT_EXPRESSION =
            new EdiarumArgumentDescriptor(EdiarumArgumentNames.ARGUMENT_EXPRESSION, ArgumentDescriptor.TYPE_STRING,
                    "A string how the items are rendered in the list. " +
                            "Use $XPATH{expression} for xpath expressions (starting with @, /, //, ./), " +
                            "E.g.: $XPATH{/name}, $XPATH{/vorname} ($XPATH{/lebensdaten})",
                    "$XPATH{/span|/tei:span}");

    public static EdiarumArgumentDescriptor ARGUMENT_ID =
            new EdiarumArgumentDescriptor(EdiarumArgumentNames.ARGUMENT_ID, ArgumentDescriptor.TYPE_STRING,
                    "An ID which can be used multiple times at different places", "");

    public static EdiarumArgumentDescriptor ARGUMENT_ID_TARGET =
            new EdiarumArgumentDescriptor(EdiarumArgumentNames.ARGUMENT_ID, ArgumentDescriptor.TYPE_STRING,
                    "The name of the ID attribute of the target element, e.g. " + "xml:id");

    public static EdiarumArgumentDescriptor ARGUMENT_FIRST_ELEMENT =
            new EdiarumArgumentDescriptor(EdiarumArgumentNames.ARGUMENT_FIRST_ELEMENT, ArgumentDescriptor.TYPE_STRING,
                    "Before the selected text this element is inserted." +
                            "Use $ID for  the reusable id, $XPATH{expression} for xpath expressions (starting with @, /, //, ./, # (for functions)), " +
                            "e.g.: <index xmlns='http://www.tei-c.org/ns/1.0' spantTo='$ID' indexName='persons' corresp='$XPATH{@xml:id}'>" +
                            "<term>$XPATH{/name}, $XPATH{/forename}</term>" + "</index>");

    public static EdiarumArgumentDescriptor ARGUMENT_SECOND_ELEMENT =
            new EdiarumArgumentDescriptor(EdiarumArgumentNames.ARGUMENT_SECOND_ELEMENT, ArgumentDescriptor.TYPE_STRING,
                    "After the selected text this element is inserted." +
                            "Use $ID for  the reusable id, $XPATH{expression} for xpath expressions (starting with @, /, //, ./, # (for functions)), " +
                            "e.g.: <anchor xmlns='http://www.tei-c.org/ns/1.0' xml:id='$ID' />");

    public static EdiarumArgumentDescriptor ARGUMENT_ATTRIBUTENAME =
            new EdiarumArgumentDescriptor(EdiarumArgumentNames.ARGUMENT_ATTRIBUTENAME, ArgumentDescriptor.TYPE_STRING,
                    "The name of the new attribute, e.g.: " + "key");

    public static EdiarumArgumentDescriptor ARGUMENT_XPATH =
            new EdiarumArgumentDescriptor(EdiarumArgumentNames.ARGUMENT_XPATH, ArgumentDescriptor.TYPE_STRING,
                    "The XPath expression to the link target element, e.g.: " + "//anchor");

    public static EdiarumArgumentDescriptor ARGUMENT_XPATHFROMSELECTION =
            new EdiarumArgumentDescriptor(EdiarumArgumentNames.ARGUMENT_XPATHFROMSELECTION,
                    ArgumentDescriptor.TYPE_STRING, "A relative XPath expression from current context node " +
                    "to the element with the new attribute: " + "./child");

    public static EdiarumArgumentDescriptor ARGUMENT_ATTRIBUTEVALUE =
            new EdiarumArgumentDescriptor(EdiarumArgumentNames.ARGUMENT_ATTRIBUTEVALUE, ArgumentDescriptor.TYPE_STRING,
                    "The content of the new attribute, e.g.: " + "some text.. $ITEMS");

    public static EdiarumArgumentDescriptor ARGUMENT_VARIABLE =
            new EdiarumArgumentDescriptor(EdiarumArgumentNames.ARGUMENT_VARIABLE, ArgumentDescriptor.TYPE_STRING,
                    "The item variable which is used for the XML fragment, e.g. per default: $XPATH{@id}",
                    "$XPATH{@xml:id}");


    public static EdiarumArgumentDescriptor ARGUMENT_MULTIPLE_SELECTION =
            new EdiarumArgumentDescriptor(EdiarumArgumentNames.ARGUMENT_MULTIPLE_SELECTION,
                    ArgumentDescriptor.TYPE_CONSTANT_LIST, "When is enabled, multiple selection will be possible",
                    new String[]{AuthorConstants.ARG_VALUE_TRUE, AuthorConstants.ARG_VALUE_FALSE,},
                    AuthorConstants.ARG_VALUE_TRUE);
    public static EdiarumArgumentDescriptor ARGUMENT_MULTIPLE_SELECTION_FALSE =
            new EdiarumArgumentDescriptor(EdiarumArgumentNames.ARGUMENT_MULTIPLE_SELECTION,
                    ArgumentDescriptor.TYPE_CONSTANT_LIST, "When is enabled, multiple selection will be possible",
                    new String[]{AuthorConstants.ARG_VALUE_TRUE, AuthorConstants.ARG_VALUE_FALSE,},
                    AuthorConstants.ARG_VALUE_FALSE);

    public static EdiarumArgumentDescriptor ARGUMENT_XPATH_LOCATION =
            new EdiarumArgumentDescriptor(EdiarumArgumentNames.ARGUMENT_XPATH_LOCATION,
                    ArgumentDescriptor.TYPE_XPATH_EXPRESSION,
                    "An XPath expression indicating the insert location for the fragment.\n" +
                            "Note: If it is not defined then the insert location will be at the caret.");

    public static EdiarumArgumentDescriptor ARGUMENT_XPATH_BEFORE_LOCATIONS =
            new EdiarumArgumentDescriptor(EdiarumArgumentNames.ARGUMENT_XPATH_BEFORE_LOCATIONS,
                    ArgumentDescriptor.TYPE_STRING,
                    "A comma separated list of XPath expressions which are allowed as preceding siblings.");

    public static EdiarumArgumentDescriptor ARGUMENT_RELATIVE_LOCATION =
            new EdiarumArgumentDescriptor(EdiarumArgumentNames.ARGUMENT_RELATIVE_LOCATION,
                    ArgumentDescriptor.TYPE_CONSTANT_LIST,
                    "The insert position relative to the node determined by the XPath expression.\n" + "Can be: " +
                            AuthorConstants.POSITION_BEFORE + ", " + AuthorConstants.POSITION_INSIDE_FIRST + ", " +
                            AuthorConstants.POSITION_INSIDE_LAST + " or " + AuthorConstants.POSITION_AFTER + ".\n" +
                            "Note: If the XPath expression is not defined this argument is ignored",
                    new String[]{AuthorConstants.POSITION_BEFORE, AuthorConstants.POSITION_INSIDE_FIRST,
                            AuthorConstants.POSITION_INSIDE_LAST, AuthorConstants.POSITION_AFTER,},
                    AuthorConstants.POSITION_INSIDE_FIRST);

    public static EdiarumArgumentDescriptor ARGUMENT_COMMAND =
            new EdiarumArgumentDescriptor(EdiarumArgumentNames.ARGUMENT_COMMAND, ArgumentDescriptor.TYPE_STRING,
                    "The command which should be executed.");

    public static EdiarumArgumentDescriptor ARGUMENT_PATH =
            new EdiarumArgumentDescriptor(EdiarumArgumentNames.ARGUMENT_PATH, ArgumentDescriptor.TYPE_STRING,
                        "Starting string of the files which contain link targets. This part isn't displayed as title. Usually the database path, e.g. /exist/webdav/db/.");

    boolean isNullable() {
        return nullable;
    }

    String validateString(Object argumentValue) {
        if (nullable && argumentValue == null) {
        } else if (!(argumentValue instanceof String)) {
            throw new IllegalArgumentException(
                    "The following parameter is not declared or has an invalid value: " + name + ": " + argumentValue);
        }
        return (String) argumentValue;
    }

}
