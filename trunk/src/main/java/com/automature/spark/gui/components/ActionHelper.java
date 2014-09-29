package com.automature.spark.gui.components;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import com.automature.spark.beans.ActionInfoBean;
import com.automature.spark.beans.ExistenceMessageBean;
import com.automature.spark.engine.AtomHandler;
import com.automature.spark.gui.sheets.SpreadSheet;

public class ActionHelper {

	private AtomHandler atomHandler;
	private String spreadSheet;



	public ActionHelper(AtomHandler atomHandler, String fileName) {
		super();
		this.atomHandler = atomHandler;
		this.spreadSheet = fileName;
	}

	public NavigableSet<String> getMolecules(String prefix){
		SpreadSheet sp=null;
		int indexOfPackage=0;
		String namespace="";
		String molPrefix=prefix.substring(1);
		NavigableSet<String> set=null;
		if(prefix.contains(".")){
			indexOfPackage=prefix.indexOf('.');
			namespace=prefix.substring(1, indexOfPackage);
			molPrefix=prefix.substring(indexOfPackage+1);
			sp=SpreadSheet.findSpreadSheet(namespace);
			Set<String> moleculeId=sp.getMoleculesSheet().getMoleculeIDs().keySet();
			set=new TreeSet<String>();
			namespace="&"+namespace+".";
			//System.out.println((indexOfPackage+1) +" "+prefix.length());
			if(indexOfPackage+1==prefix.length()){
				for(String s:moleculeId){
					set.add(namespace+s);
				}
			//	System.out.println("set "+set);
			}else{
				for(String s:moleculeId){
					if(s.startsWith(molPrefix)){
						set.add(namespace+s);
					}
				}
			}
		}else{
		//	System.out.println("spreadSheet "+spreadSheet);
			sp = SpreadSheet.getUniqueSheets().get(spreadSheet);

			if(sp!=null){
				Set<String> moleculeId=sp.getMoleculesSheet().getMoleculeIDs().keySet();
		//		System.out.println("moleucle ids "+moleculeId);
				set=new TreeSet<String>();
				for(String s:moleculeId){
					if(s.startsWith(molPrefix)){
						set.add("&"+s);
					}
				}
			}else{
		//		System.out.println("sp "+sp);
			}
		}
	//	System.out.println("returning set "+set);
		return set;
	}



	public ActionInfoBean getMoleculeInfo(String item){
		ActionInfoBean aib =null;

		boolean includeMolecule=true;
		String moleculeName = item.substring(1);
		SpreadSheet sp = null;
		String fileName=null;
		if (!moleculeName.contains(".")) {
			includeMolecule=false;
			sp = SpreadSheet.getUniqueSheets().get(spreadSheet);
		} else {
			fileName = moleculeName.substring(0,
					moleculeName.indexOf("."));
			moleculeName = moleculeName
					.substring(moleculeName.indexOf(".") + 1);
			sp=SpreadSheet.findSpreadSheet(fileName);
		}
		if (sp != null) {
			aib = sp.getMoleculesSheet().moleculeExist(
					moleculeName);
		}

		return aib;
	}


	public ExistenceMessageBean getActionMessageBean(String item) {
		if (StringUtils.isBlank(item)) {
			return null;
		}
		ExistenceMessageBean emb=null;
		try{
			if (item.trim().startsWith("&")) {
				ActionInfoBean aib = getMoleculeInfo(item);
				if (aib != null) {
					emb = new ExistenceMessageBean(true,
							aib.getArguments());
					return emb;
				}else{

					String message;

					if (item.contains(".")){
						String fileName = item.substring(1,
								item.indexOf("."));
						String moleculeName = item
								.substring(item.indexOf(".") + 1);
						message="could not find "+moleculeName+" in "+fileName +"sheet";
					}else{
						message="could not find "+item.substring(1)+" in this testsuite's molecule sheet";
					}
					emb = new ExistenceMessageBean(false,
							message);
					return emb;
				}

			} else {

				return atomHandler.verifyExistence(item.trim());
			}
		}catch(Exception e){
			System.err.println("Error :"+e.getMessage()+"\titem "+item);
			e.printStackTrace();
		}
		return emb;
	}

	public NavigableSet<String> getAtoms(String prefix){
		return atomHandler.getAtoms(prefix);
	}
}
