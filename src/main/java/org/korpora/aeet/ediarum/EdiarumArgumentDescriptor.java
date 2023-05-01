package org.korpora.aeet.ediarum;

import ro.sync.ecss.extensions.api.ArgumentDescriptor;
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

    public EdiarumArgumentDescriptor(String name, int type, String description, String[] allowedValues, String defaultValue) {
        super(name, type, description, allowedValues, defaultValue);
        if (defaultValue == null) nullable = true;
    }

    public static EdiarumArgumentDescriptor SCHEMA_AWARE_ARGUMENT_DESCRIPTOR =
        new EdiarumArgumentDescriptor(AuthorOperation.SCHEMA_AWARE_ARGUMENT_DESCRIPTOR.getName(),
                AuthorOperation.SCHEMA_AWARE_ARGUMENT_DESCRIPTOR.getType(),
                AuthorOperation.SCHEMA_AWARE_ARGUMENT_DESCRIPTOR.getDescription(),
                AuthorOperation.SCHEMA_AWARE_ARGUMENT_DESCRIPTOR.getAllowedValues(),
                AuthorOperation.SCHEMA_AWARE_ARGUMENT_DESCRIPTOR.getDefaultValue()
        );

    boolean isNullable() {
        return nullable;
    }

    String validateString(Object argumentValue) {
        if (nullable && argumentValue == null) {
        } else if ((argumentValue == null) || !(argumentValue instanceof String)) {
            throw new IllegalArgumentException("The following parameter is not declared or has an invalid value: " + name + ": " + argumentValue);
        }
        return (String) argumentValue;
    }

}
