/**
 * InsertReferenceForLinkTargetOperation.java - is a class for inserting a link element to a link target from another open file.
 * It belongs to package ro.sync.ecss.extensions.ediarum for the modification of the Oxygen framework
 * for several projects at the Berlin-Brandenburgische Akademie der Wissenschaften (BBAW) to build a
 * framework for edition projects (Ediarum - die Editionsarbeitsumgebung).
 *
 * @author Martin Fechner
 * @version 1.0.2
 */
package org.bbaw.telota.ediarum;

import org.korpora.aeet.ediarum.EdiarumArgumentDescriptor;
import org.korpora.aeet.ediarum.EdiarumArgumentNames;
import org.korpora.aeet.ediarum.EdiarumArguments;
import ro.sync.ecss.extensions.api.*;
import ro.sync.ecss.extensions.api.filter.AuthorFilteredContent;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;

import java.awt.*;
import java.net.URL;

public class InsertReferenceForLinkTargetOperation implements AuthorOperation {

    /**
     * Argument describing the element.
     */
    private static final String ARGUMENT_ELEMENT = "element";

    /**
     * Arguments.
     */

    private static final EdiarumArguments ARGUMENTS_MAP = new EdiarumArguments(new EdiarumArgumentDescriptor[]{
            EdiarumArgumentDescriptor.ARGUMENT_PATH,
            EdiarumArgumentDescriptor.ARGUMENT_XPATH,
            new EdiarumArgumentDescriptor(
                    EdiarumArgumentNames.ARGUMENT_ID,
                    ArgumentDescriptor.TYPE_STRING,
                    "The name of the ID attribute of the target element, e.g. " +
                            "xml:id"),
            new EdiarumArgumentDescriptor(
                    EdiarumArgumentNames.ARGUMENT_IDSTARTPREFIX,
                    ArgumentDescriptor.TYPE_STRING,
                    "The id prefix of the first target element, e.g. " +
                            "start_"),
            new EdiarumArgumentDescriptor(
                    EdiarumArgumentNames.ARGUMENT_IDSTOPPREFIX,
                    ArgumentDescriptor.TYPE_STRING,
                    "The id prefix of the second target element, e.g. " +
                            "stop_"),
            new EdiarumArgumentDescriptor(
                    ARGUMENT_ELEMENT,
                    ArgumentDescriptor.TYPE_STRING,
                    "The new element which should contain the reference to the link target. The variables $FILEPATH, $FILE_ID, $STARTPREFIX, $STOPPREFIX, $ID could be used, e.g. " +
                            "<ref xmlns='http://www.tei-c.org/ns/1.0' target='$FILEPATH/#$STARTPREFIX$ID'/>"),
            new EdiarumArgumentDescriptor(
                    EdiarumArgumentNames.ARGUMENT_ALTELEMENT,
                    ArgumentDescriptor.TYPE_STRING,
                    "The new element, if the link target is a file. The variables $FILEPATH, $FILE_ID could be used, e.g. " +
                            "<ref xmlns='http://www.tei-c.org/ns/1.0' target='$FILEPATH'/>")
    });

    static EdiarumArgumentDescriptor[] ARGUMENTS;

    static {
        ARGUMENTS = ARGUMENTS_MAP.getArguments();
    }


    /**
     * @see ro.sync.ecss.extensions.api.AuthorOperation#doOperation(AuthorAccess, ArgumentsMap)
     */
    public void doOperation(AuthorAccess authorAccess, ArgumentsMap args) throws AuthorOperationException {
        // Die übergebenen Argumente werden eingelesen.
        String pathArgVal = ARGUMENTS_MAP.validateStringArgument(EdiarumArgumentNames.ARGUMENT_PATH, args);
        String xpathArgVal = ARGUMENTS_MAP.validateStringArgument(EdiarumArgumentNames.ARGUMENT_XPATH, args);
        String idArgVal = ARGUMENTS_MAP.validateStringArgument(EdiarumArgumentNames.ARGUMENT_ID, args);
        String idstartArgVal = ARGUMENTS_MAP.validateStringArgument(
                EdiarumArgumentNames.ARGUMENT_IDSTARTPREFIX, args);
        String idstopArgVal = ARGUMENTS_MAP.validateStringArgument(EdiarumArgumentNames.ARGUMENT_IDSTOPPREFIX, args);
        String elementArgVal = ARGUMENTS_MAP.validateStringArgument(ARGUMENT_ELEMENT, args);
        String altelementArgVal = ARGUMENTS_MAP.validateStringArgument(
                EdiarumArgumentNames.ARGUMENT_ALTELEMENT, args);

        // Wenn im aktuellen Dokument nichts selektiert ist, wird das aktuelle Wort ausgewählt.
        if (!authorAccess.getEditorAccess().hasSelection()) {
            authorAccess.getEditorAccess().selectWord();
        }
        int selStart = authorAccess.getEditorAccess().getSelectionStart();
        int selEnd = authorAccess.getEditorAccess().getSelectionEnd() - 1;

        // Es werden die URLs aller offenen Dateien gelesen.
        URL[] openFiles = authorAccess.getWorkspaceAccess().getAllEditorLocations();
        // Zum Pfadvergleich wird die Pfadvariable gelesen, ..
        String Pfad = (String) pathArgVal;
        // .. und die Arrays für die Einträge und IDs werden vorbereitet.
        String[] alleDateien = new String[openFiles.length];
        String[] alleDateiID = new String[openFiles.length];
        String[][] alleEintraege = new String[openFiles.length][];
        String[][] alleLinkIDs = new String[openFiles.length][];

        // Die Zahl der gültigen Dateien ist zunächst 0.
        int dateiAnzahl = 0;
        // Für jede Datei ..
        for (int i = 0; i < openFiles.length; i++) {
            // .. wird überprüft, ob sie in dem deklarierten Pfad liegt, erst dann ..
            if (openFiles[i].getFile().startsWith(Pfad)) {
                // .. wird sie in die Liste aufgenommen.
                alleDateien[i] = openFiles[i].getFile().replace(Pfad, "");
                dateiAnzahl += 1;
                try {
                    // Von der Datei ..
                    WSEditorPage filePage =
                            authorAccess.getWorkspaceAccess().getEditorAccess(openFiles[i]).getCurrentPage();
                    if (filePage instanceof WSAuthorEditorPage) {
                        WSAuthorEditorPage fileAuthorPage = (WSAuthorEditorPage) filePage;
                        // .. wird die ID in die Liste aufgenommen, ..
                        alleDateiID[i] =
                                fileAuthorPage.getDocumentController().getAuthorDocumentNode().getRootElement().getAttribute(
                                        "xml:id").getValue().toString();
                        // .. es werden alle Referenzziele entsprechend der gesetzten Variablen herausgefiltert und ..
                        AuthorNode[] linkNodes = fileAuthorPage.getDocumentController().findNodesByXPath(
                                (String) xpathArgVal + "[starts-with(@" + (String) idArgVal + ",'" +
                                        (String) idstartArgVal + "')]", false, true, true);
                        // .. die Arrays für die Einträge und IDs entsprechend vorbereitet.
                        alleEintraege[i] = new String[linkNodes.length];
                        alleLinkIDs[i] = new String[linkNodes.length];
                        // Falls es Referenzziele gibt, ..
                        if (linkNodes.length != 0 && linkNodes[0].getType() == AuthorNode.NODE_TYPE_ELEMENT) {
                            // .. wird für jedes ..
                            for (int j = 0; j < linkNodes.length; j++) {
                                // .. die ID im Array gespeichert, ..
                                alleLinkIDs[i][j] = ((AuthorElement) linkNodes[j]).getAttribute(
                                        (String) idArgVal).getValue().toString().substring(
                                        ((String) idstartArgVal).length());
                                // .. weiterhin werden Beginn und Ende des Verweiszieles gefunden ..
                                int linkPosition = linkNodes[j].getEndOffset();
                                int endPosition;
                                if (fileAuthorPage.getDocumentController().findNodesByXPath(
                                        (String) xpathArgVal + "[@" + (String) idArgVal + "='" + (String) idstopArgVal +
                                                alleLinkIDs[i][j] + "']", false, true, true).length != 0) {
                                    endPosition = fileAuthorPage.getDocumentController().findNodesByXPath(
                                            (String) xpathArgVal + "[@" + (String) idArgVal + "='" +
                                                    (String) idstopArgVal + alleLinkIDs[i][j] + "']", false, true,
                                            true)[0].getStartOffset();
                                } else {
                                    endPosition = fileAuthorPage.getDocumentController().getXPathLocationOffset("/",
                                            AuthorConstants.POSITION_AFTER);
                                }
                                // .. und der entsprechende Text des Verweiszieles wird gelesen und als Eintrag im Array gespeichert.
                                AuthorFilteredContent textNode =
                                        fileAuthorPage.getDocumentController().getFilteredContent(linkPosition,
                                                endPosition, null);
                                alleEintraege[i][j] = textNode.toString();
                            }
                        }
                    }
                    // Falls das obige fehlschlägt, etwa weil keine @xml:id im Wurzelknoten vorhanden ist, ..
                } catch (Exception e) {
                    // .. wird die Datei wieder aus der Liste entfernt.
                    alleDateien[i] = "";
                    dateiAnzahl -= 1;
                }
            } else {
                alleDateien[i] = "";
            }
        }
        // Die Arrays für die Dateien aus dem richtigen Pfad werden vorbereitet, ..
        String[] Datei = new String[dateiAnzahl];
        String[] DateiID = new String[dateiAnzahl];
        String[][] Eintrag = new String[dateiAnzahl][];
        String[][] LinkID = new String[dateiAnzahl][];
        // .. und die entsprechenden Daten übernommen.
        for (int i = 0, j = 0; i < alleDateien.length; i++) {
            if (!alleDateien[i].isEmpty()) {
                Datei[j] = alleDateien[i];
                DateiID[j] = alleDateiID[i];
                Eintrag[j] = alleEintraege[i];
                LinkID[j] = alleLinkIDs[i];
                j++;
            }
        }
        // Ein Dialog zur Auswahl der Datei und der Verweiszieles wird geöffnet.
        InsertLinkDialog LinkDialog =
                new InsertLinkDialog((Frame) authorAccess.getWorkspaceAccess().getParentFrame(), Datei, DateiID,
                        Eintrag, LinkID);
        // Falls ein Verweisziel ausgewählt wurde, ..
        if (!LinkDialog.getSelectedID().isEmpty()) {
            // .. wird zunächst der Parameter für das einzufügende Element zerteilt, ..
            String elementString = (String) elementArgVal;
            elementString = elementString.replace("$FILEPATH", "++$FILEPATH++");
            elementString = elementString.replace("$FILE_ID", "++$FILE_ID++");
            elementString = elementString.replace("$STARTPREFIX", "++$STARTPREFIX++");
            elementString = elementString.replace("$STOPPREFIX", "++$STOPPREFIX++");
            elementString = elementString.replace("$ID", "++$ID++");
            String[] elementStrings = elementString.split("[+][+]");
            // .. und dann wird das Element von Null an ..
            elementString = "";
            // .. aus den einzelnen Teilen zusammengesetzt. Die Teile ..
            for (int i = 0; i < elementStrings.length; i++) {
                // .. können den Dateipfad hinter dem allgemeine Pfad bezeichnen, ..
                if (elementStrings[i].equals("$FILEPATH")) {
                    elementString += LinkDialog.getSelectedFile();
                    // .. oder die ID der Datei, ..
                } else if (elementStrings[i].equals("$FILE_ID")) {
                    elementString += LinkDialog.getSelectedFileID();
                    // .. eventuell auch den Startprefix, ..
                } else if (elementStrings[i].equals("$STARTPREFIX")) {
                    elementString += (String) idstartArgVal;
                    // .. den Stopprefix ..
                } else if (elementStrings[i].equals("$STOPPREFIX")) {
                    elementString += (String) idstopArgVal;
                    // .. oder die ID selbst, ..
                } else if (elementStrings[i].equals("$ID")) {
                    elementString += LinkDialog.getSelectedID();
                    // .. alle übrigen Teile werden als Strings übernommen.
                } else {
                    elementString += elementStrings[i];
                }
            }
            // Das so konstruierte Element wird schließlich an der richtigen Stelle eingesetzt.
            authorAccess.getDocumentController().surroundInFragment(elementString, selStart, selEnd);
            // Falls kein Verweisziel ausgewählt wurde, aber eine Datei ausgewählt worden ist, ..
        } else if (!LinkDialog.getSelectedFile().isEmpty()) {
            // .. wird zunächst wieder der Parameter für das einzufügende Element zerteilt, ..
            String elementString = (String) altelementArgVal;
            elementString = elementString.replace("$FILEPATH", "++$FILEPATH++");
            elementString = elementString.replace("$FILE_ID", "++$FILE_ID++");
            String[] elementStrings = elementString.split("[+][+]");
            // .. und dann wird das Element auch von Null an ..
            elementString = "";
            // .. aus den einzelnen Teilen zusammengesetzt. Die Teile ..
            for (int i = 0; i < elementStrings.length; i++) {
                // .. können den Dateipfad hinter dem allgemeine Pfad bezeichnen, ..
                if (elementStrings[i].equals("$FILEPATH")) {
                    elementString += LinkDialog.getSelectedFile();
                    // .. oder die ID der Datei, ..
                } else if (elementStrings[i].equals("$FILE_ID")) {
                    elementString += LinkDialog.getSelectedFileID();
                    // .. alle übrigen Teile werden als Strings übernommen.
                } else {
                    elementString += elementStrings[i];
                }
            }
            // Das so konstruierte Element wird schließlich an der richtigen Stelle eingesetzt.
            authorAccess.getDocumentController().surroundInFragment(elementString, selStart, selEnd);
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
        return "Opens a dialog which shows all opened files. For each file possible link targets are listed. An element with references to the link targets or files is inserted.";
    }

}
