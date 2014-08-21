package com.automature.spark.engine;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JLabel;


public class sheetTableMapper {
	Vector colheader;
	Vector data;
	List breakPoints=null;
	int maxActArg=0;
	int maxVerArg=0;
	
	sheetTableMapper(){
		colheader=new Vector();
		data=new Vector();
	}

	public void findMaxActionAndVer(TestCase tc){
		for(Action act:tc.actions){
			int i=act.arguments.size();
			maxActArg=maxActArg>i?maxActArg:i;
			for(Verification ver:act.verification){
				int j=ver.arguments.size();
				maxVerArg=maxVerArg>j?maxVerArg:j;
			}
		}
	}

	public void setHeaders(){
		colheader.add("Description");
		colheader.add("property");
		colheader.add("Step");
		colheader.add("Action");
		for(int i=1;i<=maxActArg;i++){
			colheader.add("ActionArg_"+i);
		}
		colheader.add("Verify");
		for(int i=1;i<=maxVerArg;i++){
			colheader.add("VerifyArg_"+i);
		}
		colheader.add(" ");
	}

	public void setTestCaseTable(TestCase tc){
		if(tc.breakpoint){
			breakPoints=tc.breakpoints;
		}
		colheader.add("No.");
		colheader.add("BreakPoint");
		if(tc instanceof Molecule){
			colheader.add("Molecule ID");
		}else{
			colheader.add("TestCase ID");
		}
		findMaxActionAndVer(tc);
		setHeaders();
		int actionArgMax=0;
		int verifyArgMax=0;
		boolean firstAction=true;
		int step=0;
//		System.out.println(tc.breakpoints.toString());		
		for(Action act:tc.actions){
			
			Vector row=new Vector();
			row.add(step+1);
			boolean bpAdded=false;
			if(tc.breakpoint && tc.breakpoints!=null){
				Iterator it=tc.breakpoints.listIterator();
				
				while(it.hasNext()){
				//breakPoints=tc.breakpoints;
					int bp=Integer.parseInt((String)it.next());
					//System.out.println(bp);
					if(--bp==step){
						
	//					JLabel label = new JLabel("label");
	//					label.setIcon(new ImageIcon("C:\\Users\\skhan\\Desktop\\breakpointicon.jpg"));
	//					label.setVisible(true);
						//label.setBackground(Color.WHITE);
		//				row.add(new ImageIcon("C:\\Users\\skhan\\Desktop\\breakpointicon.jpg"));
						row.add(true);
	//					System.out.println("Label added");
						bpAdded=true;
						break;
					}
				}	
				
			}
			step++;
			if(!bpAdded){
				row.add("");
			}
			if(firstAction){
				row.add(tc.testCaseID);
				firstAction=false;
			}else{
				row.add("");
			}
			row.add(act.actionDescription);
			row.add(act.property);
			row.add(act.step);
			row.add(act.name);
			int i=0;
			for(String arg:act.arguments){
				row.add(arg);
				i++;
			}
			//	actionArgMax=actionArgMax>i?actionArgMax:i;
			if(i<maxActArg){
				for(int x=0;x<maxActArg-i;x++){
					row.add("");
				}
			}
			boolean firstVer=true;
	
		//	System.out.println(act.verification);
			if(act.verification==null ||act.verification.size()==0){
				for(int x=0;x<maxVerArg+1;x++){
					row.add("");
				}
				row.add("");
				data.add(row);
			}else{
				for(Verification ver:act.verification){
					if(!firstVer){
						row=new Vector();
						for(int j=0;j<5+maxActArg;j++){
							row.add("");
						}
					}
					//				Vector verify=new Vector();
					row.add(ver.name);
					int k=0;
					for(String arg:ver.arguments){
						row.add(arg);
						k++;
					}
					if(k<maxVerArg){
						for(int x=0;x<maxVerArg-i;x++){
							row.add("");
						}
					}
					row.add("");
					//				verifyArgMax=verifyArgMax>k?verifyArgMax:k;
					data.add(row);
					//firstVer=false;
				}
			}
	//		System.out.println(row.toString());
		}
		//System.out.println(colheader.toString());
	//	System.out.println(data.toString());
	}
}
