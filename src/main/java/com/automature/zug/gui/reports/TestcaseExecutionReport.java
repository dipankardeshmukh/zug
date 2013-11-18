package com.automature.zug.gui.reports;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created with IntelliJ IDEA.
 * User: dipu
 * Date: 9/25/13
 * Time: 8:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestcaseExecutionReport extends ExecutionReport{
    private JTree testcaseExecutionReports;

    public TestcaseExecutionReport(String testcaseName) {
        super(EntityType.TESTCASE, testcaseName);

        DefaultMutableTreeNode testcaseExecutionReportRoot = new DefaultMutableTreeNode(this);

    }
}
