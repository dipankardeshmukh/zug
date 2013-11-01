package com.automature.zug.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.automature.zug.gui.sheets.SpreadSheet;
import com.automature.zug.util.Log;
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

    private JPanel testCasePanel;
    private JCheckBox allTestCases = new JCheckBox(new File(ZugGUI.spreadSheet.getAbsolutePath()).getName(), true);
    private DefaultMutableTreeNode testCaseTreeNode;

    public TaskPane() {

        super(new BorderLayout());
        parentSpreadSheet = ZugGUI.spreadSheet.getAbsolutePath();
        createTaskPane();
        Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(this);
        bind();

        allTestCases.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                boolean flag = allTestCases.isSelected();
                Component[] comps = testCasePanel.getComponents();

                for (int i =1; i<comps.length; i++) {

                    JCheckBox ch = (JCheckBox)comps[i];
                    ch.setSelected(flag);

                }
            }
        });
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
        //tpc.add(run);


        executionSummary = new JXTaskPane();
        executionSummary.setName("officeGroup");
        //executionSummary.setCollapsed(true);
        executionSummary.setTitle("Execution Summary                                     ");
        tpc.add(executionSummary);


        contextVariables = new JXTaskPane();
        contextVariables.setName("seeAlsoGroup");
        contextVariables.setTitle("Context Variables                                     ");
        contextVariables.setCollapsed(true);
        //tpc.add(contextVariables);


        linkedFiles = new JXTaskPane();
        linkedFiles.setName("detailsGroup");
        linkedFiles.setTitle("Linked Files                                               ");
        linkedFiles.setCollapsed(true);
        tpc.add(linkedFiles);

        reporting = new JXTaskPane();
        reporting.setName("seeAlsoGroup");
        reporting.setTitle("Reporting                                                    ");
        reporting.setCollapsed(true);
        //tpc.add(reporting);


        reference = new JXTaskPane();
        reference.setName("detailsGroup");
        reference.setTitle("Reference                                                   ");
        reference.setCollapsed(true);
        //tpc.add(reference);

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



        testCasePanel = getTestCasePanel(ZugGUI.spreadSheet.getTestCasesSheet().getTestCaseIds());
        JScrollPane treeViewTestCase = new JScrollPane(testCasePanel);
        treeViewTestCase.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        treeViewTestCase.setPreferredSize(new Dimension(100,200));
        executionSummary.add(treeViewTestCase);

        JTree tree = new JTree(getSheetTree(ZugGUI.spreadSheet));
        JScrollPane treeView = new JScrollPane(tree);
        treeView.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        treeView.setPreferredSize(new Dimension(100,150));
        linkedFiles.add(treeView);


        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                if(e.getNewLeadSelectionPath().getLastPathComponent().toString().equalsIgnoreCase(ZugGUI.spreadSheet.getAbsolutePath())){
                    try {
                        ZugGUI.bringTestSuiteTabToDisplay(ZugGUI.spreadSheet);
                    } catch (Exception e1) {
                        e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }else{
                    Set<String> filesRead = new HashSet<String>();
                    try {
                        ZugGUI.bringTestSuiteTabToDisplay(ZugGUI.spreadSheet.getIncludeFile(e.getNewLeadSelectionPath().getLastPathComponent().toString(),filesRead ));
                    } catch (Exception e1) {
                        e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }
        });

    }

    private JPanel getTestCasePanel(ArrayList Ids){

        //testCaseTreeNode = new DefaultMutableTreeNode(ZugGUI.spreadSheet.getAbsolutePath());
        Iterator it = Ids.iterator();
        JPanel tcPanel = new JPanel();
        tcPanel.setLayout(new BoxLayout(tcPanel, BoxLayout.Y_AXIS));
        tcPanel.setBackground(Color.white);
        tcPanel.setBorder(new EmptyBorder(10, 10, 10, 10) );

        tcPanel.add(allTestCases);

        while (it.hasNext()){

            JCheckBox id = new JCheckBox(it.next().toString(),true);
            id.setBackground(Color.white);
            if(id.getText().equalsIgnoreCase("init") || id.getText().equalsIgnoreCase("cleanup"))
                id.setEnabled(false);
            tcPanel.add(id.getText(),id);

        }
        return tcPanel;
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

    public void highlightTestCase(String tcID, boolean selected){

        Component[] comps = testCasePanel.getComponents();

        for (int i =1; i<comps.length; i++) {

            JCheckBox ch = (JCheckBox)comps[i];

            if(ch.getText().equalsIgnoreCase(tcID) || tcID.startsWith(ch.getText()+"\\")){
                if(selected)
                    ch.setBackground(Color.LIGHT_GRAY);
                else
                    ch.setBackground(Color.white);
            }
        }
    }

    public void updateTestCaseStatus(String tcID, boolean testCaseStatus){

        try {

            Component[] comps = testCasePanel.getComponents();

            for (int i =1; i<comps.length; i++) {

                JCheckBox ch = (JCheckBox)comps[i];

                if(ch.getText().equalsIgnoreCase(tcID) || tcID.startsWith(ch.getText()+"\\")){

                    if(testCaseStatus && !ch.getForeground().equals(Color.RED))      // ignore if the test case failed previously
                        ch.setForeground(new Color(0x00, 0x70, 0x00));        //  very dark green
                    else
                        ch.setForeground(Color.RED);

                }
            }
        } catch (Exception e) {

            Log.Error("TaskPane/updateTestCaseStatus: Error while updating test case status on Property Bar. "+e.getMessage());
        }

    }


    public String getSelectedTestCases(){

        boolean selective_execution = false;
        String selectedTestCases = "";
        Component[] comps = testCasePanel.getComponents();

        for (int i =1; i<comps.length; i++) {

            JCheckBox ch = (JCheckBox)comps[i];

                if(ch.isSelected()){
                    selectedTestCases+=ch.getText()+",";
                }else{
                    selective_execution=true;
                }
        }

        if(selective_execution){

            if(selectedTestCases.endsWith(","))
                     selectedTestCases = selectedTestCases.substring(0,selectedTestCases.length() - 1);

            return "-testcaseid=" + selectedTestCases;
        }

        return null;
    }

}

