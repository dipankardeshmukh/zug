package com.automature.zug.gui.sheets;

import com.automature.zug.engine.Controller;
import com.automature.zug.engine.BuildInAtom;
import com.automature.zug.gui.Excel;
import com.automature.zug.gui.ZugGUI;
import com.automature.zug.util.ExtensionInterpreterSupport;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;

public class SpreadSheet {

    private String absolutePath;

    private ConfigSheet configSheet;
    private MacroSheet macroSheet;
    private TestCasesSheet testCasesSheet;
    private MoleculesSheet moleculesSheet;

    private HashMap<String, SpreadSheet> includeFiles = new HashMap<String, SpreadSheet>();
    private List<String> scriptLocations = new ArrayList<String>();


    public String getAbsolutePath() {
        return absolutePath;
    }

    public ConfigSheet getConfigSheet() {
        return configSheet;
    }

    public MacroSheet getMacroSheet() {
        return macroSheet;
    }

    public TestCasesSheet getTestCasesSheet() {
        return testCasesSheet;
    }

    public MoleculesSheet getMoleculesSheet() {
        return moleculesSheet;
    }

    public HashMap<String, SpreadSheet> getIncludeFiles() {
        return includeFiles;
    }




    public SpreadSheet getIncludeFile(String file, Set<String> readFiles){

        readFiles.add(file);

        if(includeFiles.containsKey(file)){

            return includeFiles.get(file);

        }else{

            Iterator it = includeFiles.keySet().iterator();

            while(it.hasNext()){
                String currentFile = (String) it.next();
                if(readFiles.contains(currentFile)){
                    ZugGUI.message("WARNING: Recursive include file detected! Ignoring file: "+currentFile);
                }else{
                    SpreadSheet sh = includeFiles.get(currentFile);
                    return sh.getIncludeFile(file,readFiles);
                }
            }
            return null;  // Include File not found
        }
    }

    public SpreadSheet(){

    }

    public JPanel getConfigSheetPanel() {
        return configSheet.getPanel();
    }

    public JPanel getMacroSheetPanel() {
        return macroSheet.getPanel();
    }

    public JPanel getTestCasesSheetPanel() {
        try {
            return testCasesSheet.getPanel();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    public JPanel getMoleculesSheetPanel() {
        return moleculesSheet.getPanel();
    }

    private JPanel getPanel(Excel excel){

        JPanel panel = new JPanel(new BorderLayout());

        JTable table = new JTable(excel.getData(), excel.getHeader());

        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane);

        return panel;
    }

    public void readIncludeFiles() throws Exception {

        Iterator it = configSheet.getData().iterator();

        while (it.hasNext()){

            Vector row = null;
            row = (Vector) it.next();
            Iterator cell = row.iterator();

            if(cell.hasNext()){

                cell.next();  // Exclude the line number

                if (cell.next().toString().equalsIgnoreCase("include") && cell.hasNext()){

                    String [] listOfFiles = cell.next().toString().split(";|,");

                    for(String s : listOfFiles){

                        if(s==null || s.isEmpty()) continue;

                        SpreadSheet sp = ZugGUI.spreadSheet.getIncludeFile(s, new HashSet<String>());

                        if(sp!=null){

                            ZugGUI.message("\nWARNING: Recursive include file detected! Ignoring file: " + s + " included by " + this.absolutePath);

                        }else{

                            SpreadSheet sh = new SpreadSheet();
                            File fileToRead = new File(s);

                            if(!fileToRead.isAbsolute()){

                                File f = new File(this.absolutePath);
                                s = f.getParent()+File.separator+s;
                            }

                            sh.readSpreadSheet(s);
                            includeFiles.put(s, sh);

                        }

                    }
                }

            }
        }

    }

    public boolean readSpreadSheet(String filePath) throws Exception {

        if(!new File(filePath).isAbsolute())
            throw new Exception("SpreadSheet/readSpreadSheet expects file paths to be absolute");

        try {
            absolutePath = filePath;

            File file = new File(filePath);
            FileInputStream inputFile = new FileInputStream(file);
            Workbook wb = WorkbookFactory.create(inputFile);

            configSheet = new ConfigSheet();
            configSheet.readData(wb.getSheet("Config"));

            readIncludeFiles();

            macroSheet = new MacroSheet();
            macroSheet.readHeader(wb.getSheet("Macros"));
            macroSheet.readData(wb.getSheet("Macros"));

            testCasesSheet = new TestCasesSheet();
            testCasesSheet.readHeader(wb.getSheet("TestCases"));
            testCasesSheet.readData(wb.getSheet("TestCases"));

            moleculesSheet = new MoleculesSheet();
            moleculesSheet.readHeader(wb.getSheet("Molecules"));
            moleculesSheet.readData(wb.getSheet("Molecules"));

        }catch (FileNotFoundException f){
            ZugGUI.message("\nWARNING: The file could not be found - "+filePath);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean verifyExistence(String step) throws Exception {

        if(step.startsWith("&")){

            if(step.contains(".")){

                String nameSpaceMolecule[] = step.split("\\.");
                String nameSpace =nameSpaceMolecule[0].replaceFirst("&","");
                String mol = step.split("\\.")[1];

                for(String path : ZugGUI.spreadSheet.includeFiles.keySet()){

                    File fName = new File(path);

                    if(fName.getName().contains(nameSpace)){

                        return this.getIncludeFile(path, new HashSet<String>()).getMoleculesSheet().moleculeExist(mol);
                    }
                }

                return false;

            }else{

                return this.getMoleculesSheet().moleculeExist(step.replaceFirst("&",""));

            }

        }else if(step.startsWith("@")){


            String iniSL=null;
            try{
                iniSL= ExtensionInterpreterSupport.getNode("//root//configurations//scriptlocation");
                String[] locations = iniSL.split(";");
                for(String loc : locations){

                    File f = new File(loc+step.replaceFirst("@",""));
                    if(f.exists()) return true;

                }

            }catch(Exception e){
                ZugGUI.message("\nException while reading script location "+ e.getMessage());
            }

            return false;

        }else if(step.contains(".")){

            try{

                String pkgName = step.split("\\.")[0];
                String atomName = step.split("\\.")[1];

                if(Controller.invokeAtoms.containsKey(pkgName.toLowerCase())){
                    return Controller.invokeAtoms.get(pkgName.toLowerCase()).methodExists(atomName);
                }

            }catch (Exception e){

                throw new Exception("Exception while verifying in-process atom: "+ step+e.getMessage());
            }

            return false;

        }else if(BuildInAtom.buildIns.contains(step.toLowerCase())){
            return true;
        }

        return false;
    }



}
