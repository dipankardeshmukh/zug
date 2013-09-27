package com.automature.zug.gui;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import com.automature.zug.gui.sheets.SpreadSheet;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationActionMap;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.VerticalLayout;


@SuppressWarnings("serial")
public class TaskPane extends JPanel {



    private String parentSpreadSheet;

    private JXButton hide;
    private JXTaskPane run;
    private JXTaskPane executionSummary;
    private JXTaskPane contextVariables;
    private JXTaskPane linkedFiles;
    private JXTaskPane reporting;
    private JXTaskPane reference;

    public TaskPane() {

        super(new BorderLayout());
        parentSpreadSheet = ZugGUI.spreadSheet.getAbsolutePath();
        createTaskPane();
        Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(this);
        bind();
    }

    public String getParentSpreadSheet() {
        return parentSpreadSheet;
    }

    private void createTaskPane() {

        JXTaskPaneContainer tpc = new JXTaskPaneContainer();
        tpc.setLayout(new VerticalLayout(0));
        tpc.setBorder(getBorder());


        //JPanel tpc = new JPanel();
        //tpc.setLayout(new BoxLayout(tpc, BoxLayout.Y_AXIS));
        tpc.setBackground(Color.LIGHT_GRAY);
        tpc.setOpaque(false);
        tpc.setBackgroundPainter(null);
        UIManager.put("TaskPane.titleBackgroundGradientStart", Color.lightGray);
        UIManager.put("TaskPane.titleBackgroundGradientEnd", Color.lightGray);

        hide = new JXButton();
        hide.setText(">>");
        hide.setHorizontalAlignment(SwingConstants.RIGHT);
        hide.setBackground(Color.LIGHT_GRAY);

        //hide.setMargin(new Insets(0,0,0,0));
        tpc.add(hide);

        run = new JXTaskPane();
        run.setName("systemGroup");
        //ImageIcon img = new ImageIcon("C:\\Users\\Debabrata\\Downloads\\zugglyphpngs\\1.png");
        //systemGroup.setIcon(img);
        run.setTitle("Run                                                                ");

        run.setCollapsed(true);
        tpc.add(run);


        executionSummary = new JXTaskPane();
        executionSummary.setName("officeGroup");
        executionSummary.setCollapsed(true);
        executionSummary.setTitle("Execution Summary                                     ");
        tpc.add(executionSummary);


        contextVariables = new JXTaskPane();
        contextVariables.setName("seeAlsoGroup");
        contextVariables.setTitle("Context Variables                                     ");
        contextVariables.setCollapsed(true);
        tpc.add(contextVariables);


        linkedFiles = new JXTaskPane();
        linkedFiles.setName("detailsGroup");
        linkedFiles.setTitle("Linked Files                                               ");
        linkedFiles.setCollapsed(true);
        tpc.add(linkedFiles);

        reporting = new JXTaskPane();
        reporting.setName("seeAlsoGroup");
        reporting.setTitle("Reporting                                                    ");
        reporting.setCollapsed(true);
        tpc.add(reporting);


        reference = new JXTaskPane();
        reference.setName("detailsGroup");
        reference.setTitle("Reference                                                   ");
        reference.setCollapsed(true);
        tpc.add(reference);

        add(new JScrollPane(tpc));
        //add(tpc);
/*        JEditorPane area = new JEditorPane("text/html", "<html>");
        area.setName("detailsArea");

        area.setFont(UIManager.getFont("Label.font"));

        Font defaultFont = UIManager.getFont("Button.font");

        String stylesheet = "body { margin-top: 0; margin-bottom: 0; margin-left: 0; margin-right: 0; font-family: "
                + defaultFont.getName()
                + "; font-size: "
                + defaultFont.getSize()
                + "pt;  }"
                + "a, p, li { margin-top: 0; margin-bottom: 0; margin-left: 0; margin-right: 0; font-family: "
                + defaultFont.getName()
                + "; font-size: "
                + defaultFont.getSize()
                + "pt;  }";
        if (area.getDocument() instanceof HTMLDocument) {
            HTMLDocument doc = (HTMLDocument)area.getDocument();
            try {
                doc.getStyleSheet().loadRules(new java.io.StringReader(stylesheet),
                        null);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        detailsGroup.add(area);*/

    hide.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            ZugGUI.getDisplayPane().removeTaskPane();
        }
    });


    }




    private void bind() {

        ApplicationActionMap map = Application.getInstance().getContext().getActionMap(this);

        run.add(map.get("email"));
        executionSummary.add(map.get("delete"));

        contextVariables.add(map.get("write"));

        JTree tree = new JTree(getSheetTree(ZugGUI.spreadSheet));

        JScrollPane treeView = new JScrollPane(tree);
        treeView.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        treeView.setPreferredSize(new Dimension(100,200));

        linkedFiles.add(treeView);
        reporting.add(map.get("help"));
        reference.add(map.get("help"));

        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                if(e.getNewLeadSelectionPath().getLastPathComponent().toString().equalsIgnoreCase(ZugGUI.spreadSheet.getAbsolutePath())){
                    ZugGUI.addTestSuiteTabToDisplay(ZugGUI.spreadSheet);
                }else{
                    Set<String> filesRead = new HashSet<String>();
                    ZugGUI.addTestSuiteTabToDisplay(ZugGUI.spreadSheet.getIncludeFile(e.getNewLeadSelectionPath().getLastPathComponent().toString(),filesRead ));
                }
            }
        });

    }


    private DefaultMutableTreeNode getSheetTree(SpreadSheet sh){

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(sh.getAbsolutePath());
        Iterator it = sh.getIncludeFiles().keySet().iterator();

        while(it.hasNext()){

            String key = it.next().toString();
            SpreadSheet sheet = sh.getIncludeFiles().get(key);

            node.add(getSheetTree(sheet));
        }
        return node;
    }


    @Action
    public void email() { }

    @Action
    public void delete() { }

    @Action
    public void write() { }

    @Action
    public void exploreInternet() { }

    @Action
    public void help() { }
}

