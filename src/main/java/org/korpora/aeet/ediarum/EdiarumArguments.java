package org.korpora.aeet.ediarum;

import ro.sync.ecss.extensions.api.ArgumentsMap;

import java.util.TreeMap;

/**
 * just a container for ArgumentDescriptors that makes access to descriptors by name easier
 */
public class EdiarumArguments {
    TreeMap<String, EdiarumArgumentDescriptor> argumentMap;
    public EdiarumArguments(EdiarumArgumentDescriptor[] arguments) {
        argumentMap = new TreeMap<>();
        for (EdiarumArgumentDescriptor descriptor: arguments) {
            argumentMap.put(descriptor.getName(), descriptor);
        }
    }

    public String validateStringArgument(String name, ArgumentsMap args){
        return argumentMap.get(name).validateString(args.getArgumentValue(name));
    }

    public EdiarumArgumentDescriptor[] getArguments(){
        return argumentMap.keySet().toArray(new EdiarumArgumentDescriptor[0]);
    }

    public EdiarumArgumentDescriptor getDescriptor(String name) {
        return argumentMap.get(name);
    }
}
