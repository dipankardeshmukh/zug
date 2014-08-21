package com.automature.spark.util;



	import com.sun.org.apache.xml.internal.serialize.OutputFormat;
	import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
	import java.io.File;
	import java.io.FileOutputStream;
import java.util.HashSet;

	import javax.xml.parsers.DocumentBuilder;
	import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.lang.StringUtils;
	import org.w3c.dom.Document;
	import org.w3c.dom.Element;
import org.w3c.dom.Text;

	/**
	 *
	 * @author Sankha
	 */
	public class XMLWriter
	{

	    private static Document doc;
	   private static Element rootNode;
	   private static Element includeNode;
	   private static Integer counter=1;

	    public XMLWriter() {
	        try { //Document Building Factory
	            DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory.newInstance();
	            //Document Builder
	            DocumentBuilder docBuild = docBuildFactory.newDocumentBuilder();
	            //Document
	            doc = docBuild.newDocument();
	            //Building XML nodes from the given String input of the method parameter Root Element
	            rootNode = doc.createElement("root");
	            includeNode=doc.createElement("Include-Files");
	        } catch (Exception e) {
	        }
	    }

	    private Element setAttribute(String name, String value, Document doc) {
	        //Child Elements
	        Text fileName = doc.createTextNode(value);
	        Element moleculeNode = doc.createElement(name);
	        moleculeNode.setAttribute("id", counter.toString());
	        moleculeNode.appendChild(fileName);
	        includeNode.appendChild(moleculeNode);
	        counter++;
	        return includeNode;
	        
	    }
//Reform this code for fully qualified path and relative paths
	    public String genarateXML(String input) {
	        try {
	            HashSet<String> inputSets=new HashSet<String>();
	                String[] filepaths=input.split(","); //Spliting the whole path with ',' for multiple occurrence of files
	               //Checking and creating the new nodes for getting the folder names
	                for(String folder:filepaths)
	                {//Spliting the String inputs with the \ for getting the file name
	                	String[] fileNames=folder.split("\\\\");//Checking out the file name with the .extensions
	                	for(String file:fileNames)
	                	{
	                		if(file.contains(".xls"))
	                		{ //Adding a element to the root node
	                			  //rootNode.appendChild(this.setAttribute("Molecule-File", file, doc));
	                			inputSets.add(file);
	                		}
	                	}
	                	if(folder.contains(":")||folder.startsWith("\\")) //if the absolute path is given
                                {//rootNode.appendChild(this.setAttribute("Molecule-Path", folder, doc));
                                    if(folder.endsWith(".xls"))
                                        folder=StringUtils.substringBeforeLast(folder,"\\");
	                	inputSets.add(folder);
                                }
	                	else
	                	{
	                	
	                			String[] folderName=folder.split("\\\\"); //if relative path is given then it takes the names of folders
	                			////TODO: need to Come up with some better Logic to stop the repeating Folder name
	                	//		rootNode.appendChild(this.setAttribute("Molecule-Path", folderName[0], doc));
	                		inputSets.add(folderName[0]);
	                	}
	                	
	                }
                        //System.out.println("The populated hash set "+inputSets);
	               for(String items:inputSets)
	               {
//                           System.out.println("Items are "+items);
//                           System.out.println("Items trimied "+StringUtils.substringAfterLast(items,"\\"));
                           
	            	   if(items.endsWith(".xls"))
	            		   rootNode.appendChild(this.setAttribute("Molecule-File", items, doc));
	            	   else
	            	      rootNode.appendChild(this.setAttribute("Molecule-Path", items, doc));
	            	   
	               }
	               
	                

	        
	return "Genrated Data for xml";
	        } catch (Exception s) {
	            return "This is xml genarating error\t" + s.getLocalizedMessage();
	        }

	    }

	    public String createXMLFile()
	    {
	        try{ 
	        	 //Finally appending a root node to the main xml document
	        	doc.appendChild(rootNode);
	        	
	        	//Setting a Out put Format
	            OutputFormat outFormat = new OutputFormat(doc);
	            outFormat.setIndenting(true);
	        //Creating the File
	        File xmlFile = new File("Nyon-Coms.xml");
	        //FileOutput Stream writing to the file
	        FileOutputStream xmlOut = new FileOutputStream(xmlFile);
	        //Xml serializer
	        XMLSerializer serializer = new XMLSerializer(xmlOut, outFormat);
	        //Serialize the Data
	        serializer.serialize(doc);
	        return "File Is been Created";
	    }
	    catch(Exception e) {
	            return "Problem in Creating XML\t" + e.getLocalizedMessage();
	    }
	}

	

}
