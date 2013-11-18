package com.automature.zug.gui.reports;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;
import java.util.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: dipu
 * Date: 9/25/13
 * Time: 8:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestsuiteExecutionReport {
    private List<String> loglines;
    private List<JTree> testcaseExecutionReports = null;
    private JTree testcaseTree;
    private Stack moleculeExecutionStack = null;
    private DefaultMutableTreeNode testcaseReportingNode;
    private String currentTestcaseName;
    private DefaultMutableTreeNode currentMoleculeReportingNode;
    private DefaultMutableTreeNode currentAtomReportingNode;


    /**
     * Returns formatted String that can then be shown on the screen.
     * The logStatement argument is expected to be as output by Zug to the console
     * <p>
     * This method parses the logstatement, one input at a time, and
     * builds up an execution history of the testsuite.
     * The testsuite execution is stored as a tree, and can be retrieved later
     *
     * @param  logStatement  the output of Zug as a single line
     * @return      a context sensitive formatted string
     *
     */
    public String Format (String logStatement) {
        LogLineType loglineType = getloglineType(logStatement);         // what sort of log statement is this?
        loglines.add(logStatement);                                     // add the new log statement to the list

        String formattedStatement = null;

        switch  (loglineType) {
            case TESTCASE_BEGINNING:
                currentTestcaseName = getTestCaseNameFromLogLine(logStatement);
                TestcaseExecutionReport testcaseExecutionReport = new TestcaseExecutionReport(currentTestcaseName);
                testcaseReportingNode = new DefaultMutableTreeNode(testcaseExecutionReport);
                testcaseTree = new JTree(testcaseReportingNode);
                testcaseExecutionReports.add(testcaseTree);
                break;
            case TESTCASE_BEGIN:
                break;
            case MOLECULE_BEGIN:
                DefaultMutableTreeNode previousMoleculeReportingNode = currentMoleculeReportingNode;
                moleculeExecutionStack.push(currentMoleculeReportingNode);
                String moleculeName = getMoleculeName(logStatement);
                ExecutionReport moleculeExecutionReport = new ExecutionReport(EntityType.MOLECULE, moleculeName);
                currentMoleculeReportingNode = new DefaultMutableTreeNode(moleculeExecutionReport);
                if ( moleculeExecutionStack.size()==1) {
                    testcaseReportingNode.add(currentMoleculeReportingNode);
                }
                else {
                    previousMoleculeReportingNode.add(currentMoleculeReportingNode);
                }
                break;
            case MOLECULE_END:
                ExecutionReport mExecutionReport = (ExecutionReport) currentMoleculeReportingNode.getUserObject();
                mExecutionReport.reportSuccess();
                currentMoleculeReportingNode = (DefaultMutableTreeNode) moleculeExecutionStack.pop();
                break;
            case ATOM_BEGIN:
                String atomName = getAtomName(logStatement);
                String atomArgs = getAtomArgs(logStatement);
                ExecutionReport atomExecutionReport = new ExecutionReport(EntityType.ATOM, atomName, atomArgs);
                currentAtomReportingNode = new DefaultMutableTreeNode(atomExecutionReport);
                if ( moleculeExecutionStack.size()==1) {
                    testcaseReportingNode.add(currentAtomReportingNode);
                }
                else {
                    currentMoleculeReportingNode.add(currentAtomReportingNode);
                }

                break;
            case ATOM_END:
                ExecutionReport aExecutionReport = (ExecutionReport) currentAtomReportingNode.getUserObject();
                String failureFootprint = getFailureFootprint(logStatement);
                if (failureFootprint.isEmpty()) {
                    aExecutionReport.reportSuccess();
                }
                else {
                    aExecutionReport.reportFailure(failureFootprint);
                }
                break;
            case TESTCASE_END:
                TestcaseExecutionReport tcExecutionReport = (TestcaseExecutionReport) testcaseReportingNode.getUserObject();
                Boolean tcPassed = getTestcaseOutcome(logStatement);
                if (tcPassed) {
                    tcExecutionReport.reportSuccess();
                }
                else {
                    String tcFailure = getTestcaseFailure(logStatement);
                    tcExecutionReport.reportFailure(tcFailure);
                }
                break;
        }


        return null;
    }

    private String getTestcaseFailure(String logStatement) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    private Boolean getTestcaseOutcome(String logStatement) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    private String getFailureFootprint(String logStatement) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    private String getAtomArgs(String logStatement) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    private String getAtomName(String logStatement) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    private String getMoleculeName(String logStatement) {
        String[] lineToken = logStatement.split("\\s+");            // First split to get the first token on line
        int tokenLength = lineToken[0].length();
        String moleculePath = lineToken[0].substring(1, tokenLength-1); // the path looks like [TC_MOL1_MOL2_MOL3]
        String[] moleculePaths = moleculePath.split("_");           // split the path to get to the last molecule
        int pathLength = moleculePath.length();
        String moleculeName = moleculePaths[pathLength-1];          // name of the molecule is the last token
        return moleculeName;
    }

    private String getTestCaseNameFromLogLine(String logStatement) {
        String[] lineToken = logStatement.split("\\s+");
        return lineToken[3];
    }

    private LogLineType getloglineType(String logStatement) {

        if (logStatement.startsWith("Running TestCase ID")) {
            // new testcase started
            return LogLineType.TESTCASE_BEGINNING;
        }

        String[] lineToken = logStatement.split("\\s+");

        if (lineToken[0].startsWith("[") && lineToken[0].endsWith("]")) {

        }
        return null;
    }

    public String getTestcaseSummary() {
        return null;
    }

    public String getTestcaseMoleculeSummary() {
        return null;
    }

}

