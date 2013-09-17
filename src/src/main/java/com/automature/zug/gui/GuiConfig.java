package com.automature.zug.gui;

/**
 * Created with IntelliJ IDEA.
 * User: Debabrata
 * Date: 13/9/13
 * Time: 6:27 PM
 * To change this template use File | Settings | File Templates.
 */

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.automature.zug.gui.IconsPanel;
import com.automature.zug.gui.ZugGUI;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class GuiConfig {

    public static void loadIcons() {

        try {

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            DefaultHandler handler = new DefaultHandler() {

                boolean browse = false;
                boolean reload = false;
                boolean options = false;
                boolean moreoptions = false;
                boolean execute = false;
                boolean stop = false;
                boolean clearscreen = false;
                boolean debug = false;

                public void startElement(String uri, String localName,String qName,
                                         Attributes attributes) throws SAXException {


                    if (qName.equalsIgnoreCase("browse-icon")) {
                        browse = true;
                    }

                   if (qName.equalsIgnoreCase("reload-icon")) {
                        reload = true;
                    }

                    if (qName.equalsIgnoreCase("options-icon")) {
                        options = true;
                    }
                    if (qName.equalsIgnoreCase("moreoptions-icon")) {
                        moreoptions = true;
                    }

                    if (qName.equalsIgnoreCase("execute-icon")) {
                        execute = true;
                    }

                    if (qName.equalsIgnoreCase("stop-icon")) {
                        stop = true;
                    }

                    if (qName.equalsIgnoreCase("clear-screen-icon")) {
                        clearscreen = true;
                    }

                    if (qName.equalsIgnoreCase("debugger-icon")) {
                        debug = true;
                    }
                }

                public void endElement(String uri, String localName,
                                       String qName) throws SAXException {

                }

                public void characters(char ch[], int start, int length) throws SAXException {

                    if (browse) {
                        IconsPanel.setBrowse_icon_path(new String(ch, start, length));
                        browse = false;
                    }

                    if (reload) {
                        IconsPanel.setReload_icon_path(new String(ch, start, length));
                        reload=false;
                    }

                    if (options) {
                        IconsPanel.setOptions_icon_path(new String(ch, start, length));
                        options=false;
                    }

                    if (moreoptions) {
                        IconsPanel.setMore_options_icon_path(new String(ch, start, length));
                        moreoptions=false;
                    }

                    if (execute) {
                        IconsPanel.setExecute_icon_path(new String(ch, start, length));
                        execute=false;
                    }

                    if (stop) {
                        IconsPanel.setStop_icon_path(new String(ch, start, length));
                        stop=false;
                    }

                    if (clearscreen) {
                        IconsPanel.setClear_screen_icon_path(new String(ch, start, length));
                        clearscreen=false;
                    }

                    if (debug) {
                        IconsPanel.setDebugger_icon_path(new String(ch, start, length));
                        debug=false;
                    }




                }

            };

            saxParser.parse("GuiSettings.xml", handler);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}