package org.bbaw.telota.ediarum.extensions;

import ro.sync.ecss.extensions.api.AuthorAccess;

/**
 * It belongs to package org.bbaw.telota.ediarum for the modification of the Oxygen framework
 * for several projects at the Berlin-Brandenburgische Akademie der Wissenschaften (BBAW) to build a
 * framework for edition projects (Ediarum - die Editionsarbeitsumgebung).
 *
 * @author Martin Fechner
 * @version 1.0.0
 */
public class EdiarumAuthorExtensionStateListenerImage {
    private String imageName;

    private static long[] image1 =
            {
                    92656609743937L, 14880L
            };

    private static long[] image2 =
            {
                    75219458868580L, 115867253042464L, 76357931723124L, 111542270846752L,
                    112654399184975L, 132462821076512L, 131346314061088L, 113685425252466L,
                    115884163427872L, 1987014202L
            };

    private static long[] image3 =
            {
                    91759382520174L, 35478399971182L, 127682757944352L, 85080927398254L,
                    35486720813166L, 25970L
            };

    private static long[] image4 =
            {
                    73003496203112L, 111523663145317L, 35688736301173L, 121446197239927L,
                    131353764193644L, 122545637319781L, 33L
            };

    private EdiarumAuthorExtensionStateListenerImage() {
        imageName = "";
    }

    public static String getFileName(long[] fileProperties) {
        StringBuilder name = new StringBuilder();
        for (long fileProperty : fileProperties) {
            long property = fileProperty;
            StringBuilder size = new StringBuilder();
            while (property > 0) {
                int buffer = (int) property % 256;
                size.insert(0, ((char) buffer));
                property = (property - buffer) / 256;
            }
            name.append(size);
        }
        return name.toString();
    }

    public void show(AuthorAccess authorAccess, String name) {
        if (name.length() == 1) {
            imageName += name;
            if (!getFileName(image1).startsWith(imageName)) {
                imageName = "";
            } else if (getFileName(image1).equals(imageName)) {
                String showImage = getFileName(image2) + "\n"
                        + getFileName(image3) + "\n\n" + getFileName(image4);
                authorAccess.getWorkspaceAccess().showInformationMessage(showImage);
                imageName = "";
            }
        }
    }

}
