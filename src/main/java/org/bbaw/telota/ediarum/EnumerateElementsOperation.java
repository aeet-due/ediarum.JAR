package org.bbaw.telota.ediarum;

import org.korpora.aeet.ediarum.EdiarumArgumentDescriptor;
import org.korpora.aeet.ediarum.EdiarumArguments;
import ro.sync.ecss.extensions.api.*;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;

public class EnumerateElementsOperation implements AuthorOperation {

    /**
     * Argument describing the element to be enumerated. E.g. "l"
     */
    private static final String ARGUMENT_ENUMERATE_ELEMENT = "enumerate-element";

    /**
     * German description text for the enumerate-element parameter.
     */
    private static final String DESCRIPTOR_ENUMERATE_ELEMENT =
            "Element which should be enumerated, e.g. 'l'";


    /**
     * Argument describing the element that resets the counter for the enumerate-element. E.g. "pb"
     */
    private static final String ARGUMENT_ENUMERATE_RESET_ELEMENT = "enumerate-reset-element";

    /**
     * German description text for the enumerate-reset-element parameter.
     */
    private static final String DESCRIPTOR_EUMERATE_RESET_ELEMENT =
            "Element which should reset the current enumeration, e.g. 'pb'. " +
                    "If empty the enumeration is never reseted.";

    /**
     * Argument describing the start number for the enumeration. E.g. 1
     */
    private static final String ARGUMENT_ENUMERATE_COUNTER_START = "enumerate-counter-start";

    /**
     * German description text for the enumerate-counter-start parameter.
     */
    private static final String DESCRIPTOR_ENUMERATE_COUNTER_START =
            "First number of enumeration. Default is: 1";

    /**
     * Argument describing the interval for the enumeration. E.g. 1
     */
    private static final String ARGUMENT_ENUMERATE_COUNTER_INTERVAL = "enumerate-interval";

    /**
     * German description text for the enumerate-interval parameter.
     */
    private static final String DESCRIPTOR_ENUMERATE_COUNTER_INTERVAL =
            "Counting interval for the enumeration. Default is: 1";

    /**
     * Arguments.
     */
    private static final EdiarumArguments ARGUMENTS_MAP = new EdiarumArguments(new EdiarumArgumentDescriptor[]{

            // enumerate-element
            new EdiarumArgumentDescriptor(
                    ARGUMENT_ENUMERATE_ELEMENT,
                    ArgumentDescriptor.TYPE_STRING,
                    DESCRIPTOR_ENUMERATE_ELEMENT
            ),

            // enumerate-reset-element
            new EdiarumArgumentDescriptor(
                    ARGUMENT_ENUMERATE_RESET_ELEMENT,
                    ArgumentDescriptor.TYPE_STRING,
                    DESCRIPTOR_EUMERATE_RESET_ELEMENT,
                    ""
            ),

            // enumerate-counter-start
            new EdiarumArgumentDescriptor(
                    ARGUMENT_ENUMERATE_COUNTER_START,
                    ArgumentDescriptor.TYPE_STRING,
                    DESCRIPTOR_ENUMERATE_COUNTER_START,
                    "1"
            ),

            // enumerate-interval
            new EdiarumArgumentDescriptor(
                    ARGUMENT_ENUMERATE_COUNTER_INTERVAL,
                    ArgumentDescriptor.TYPE_STRING,
                    DESCRIPTOR_ENUMERATE_COUNTER_INTERVAL,
                    "1"
            ),
    });

    static EdiarumArgumentDescriptor[] ARGUMENTS;

    static {
        ARGUMENTS = ARGUMENTS_MAP.getArguments();
    }


    /**
     * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(AuthorAccess, ArgumentsMap)
     */
    public void doOperation(AuthorAccess authorAccess, ArgumentsMap args) throws AuthorOperationException {

        // Parse arguments
        String enumerateElementValue = ARGUMENTS_MAP.validateStringArgument(ARGUMENT_ENUMERATE_ELEMENT, args);
        String enumerateResetElementValue = ARGUMENTS_MAP.validateStringArgument(ARGUMENT_ENUMERATE_RESET_ELEMENT, args);
        String enumerateCounterStartValue = ARGUMENTS_MAP.validateStringArgument(ARGUMENT_ENUMERATE_COUNTER_START, args);
        String enumerateIntervalValue = ARGUMENTS_MAP.validateStringArgument(ARGUMENT_ENUMERATE_COUNTER_INTERVAL, args);

        // Read the integer start value as an interger
        int enumerateCounter = Integer.parseInt(enumerateCounterStartValue);

        // Get DocumentController
        AuthorDocumentController authorDocumentController = authorAccess.getDocumentController();

        // Set the enumerator path
        String enumeratorPath = "//" + enumerateElementValue;

        // Set the reset path
        String enumeratorResetPath = "//" + enumerateResetElementValue;

        // Concat both paths to form the xpath query
        String nodePath = enumeratorPath + "|" + enumeratorResetPath;

        // Find the nodes that need to be enumerated
        AuthorNode[] enumerateNodes = authorDocumentController.findNodesByXPath(nodePath, true, true, true);

        // Iterate over all nodes
        for (AuthorNode enumerateNode : enumerateNodes) {

            // Cast the found node to an element
            AuthorElement enumerateElement = (AuthorElement) enumerateNode;


            if (enumerateElement.getLocalName().equals(enumerateElementValue)) {
                // Create a new attribute value from the counter
                AttrValue enumerateValue = new AttrValue(String.valueOf(enumerateCounter));

                // Set a new attribute value
                authorDocumentController.setAttribute("n", enumerateValue, enumerateElement);

                // Increment the counter by 1
                enumerateCounter += 1;
            } else if (enumerateElement.getLocalName().equals(enumerateResetElementValue)) {

                enumerateCounter = Integer.parseInt(enumerateCounterStartValue);

            }


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
        return "Enumerates all elements of one kind and adds a n-attribute.";
    }


}
