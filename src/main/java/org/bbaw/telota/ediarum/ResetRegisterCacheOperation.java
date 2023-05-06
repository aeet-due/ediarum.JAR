package org.bbaw.telota.ediarum;

import ro.sync.ecss.extensions.api.*;

/**
 * reset cache for {@link ReadListItems}
 */
public class ResetRegisterCacheOperation implements AuthorOperation {

    @Override
    public void doOperation(AuthorAccess authorAccess, ArgumentsMap argumentsMap)
            throws IllegalArgumentException, AuthorOperationException {
        ReadListItems.resetCache();
    }

    @Override
    public ArgumentDescriptor[] getArguments() {
        return new ArgumentDescriptor[0];
    }

    @Override
    public String getDescription() {
        return "reset the cache for Ediarum registers";
    }
}
