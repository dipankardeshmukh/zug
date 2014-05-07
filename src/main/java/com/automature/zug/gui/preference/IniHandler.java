package com.automature.zug.gui.preference;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilePermission;
import java.io.IOException;
import java.io.StringWriter;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JTextField;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.automature.zug.gui.model.Configurations;
import com.automature.zug.gui.model.InprocessPackage;
import com.automature.zug.gui.model.LanguagePackage;
import com.automature.zug.gui.model.MvmConfiguration;
import com.automature.zug.gui.model.OutProcessPackage;
import com.automature.zug.gui.model.ReporterParams;
import com.sun.org.apache.bcel.internal.generic.IfInstruction;

public class IniHandler {


	private static final String workingDirectory=System.getProperty("user.dir");;
	private static final String fileName="ZugINI.xml";

	public Set<InprocessPackage> readInprocessPackages() {
		Set<InprocessPackage> inprocessSet=null;
		NodeList nodeList = null, inprocessList = null;


		Document xmlDocument = getDocument();
		nodeList = xmlDocument.getElementsByTagName("inprocesspackages");
		Node inprocesses;
		if (nodeList != null) {
			inprocesses = nodeList.item(0);
			inprocessList = inprocesses.getChildNodes();
			Element e;
			for(int k=0;k<inprocessList.getLength();k++){
				Node n=inprocessList.item(k);
				if(n.getNodeType() == Node.ELEMENT_NODE){
					if (n.getNodeName()
							.equalsIgnoreCase("file-path")) {
						inprocessSet= 	readInprocessPackages(inprocessList);
					} else {
						//inprocessSet= 	readInprocessPackages(inprocessList);
						inprocessSet=	readInprocessPackagesOldFormat(inprocessList);
					}
					break;
				}
			}
		}


		return inprocessSet;
	}

	private Set<InprocessPackage> readInprocessPackages(NodeList inprocessList) {
		// TODO Auto-generated method stub
		Set<InprocessPackage> inprocesses=new LinkedHashSet<InprocessPackage>();
		for (int j = 0; j < inprocessList.getLength(); j++) {
			Node node1 = inprocessList.item(j);
			String filePath="";
			if (node1.getNodeType() == Node.ELEMENT_NODE){
				Element eElement = (Element) node1;		
				filePath= eElement.getAttribute("dir");
			}
			NodeList ippList=node1.getChildNodes();
			for(int i=0;i<ippList.getLength();i++){
				InprocessPackage ipp = new InprocessPackage();
				Node node = ippList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) node;
					ipp.setPackageName(eElement.getAttribute("name"));
					ipp.setLanguage(eElement.getAttribute("language"));

					ipp.setPath(filePath);

					ipp.setJarPackage(eElement.getElementsByTagName("jar-package")
							.item(0).getTextContent());
					ipp.setClassName(eElement.getElementsByTagName("class-name")
							.item(0).getTextContent());
					inprocesses.add(ipp);
				}
			}
		}
		return inprocesses;

	}

	private Set<InprocessPackage> readInprocessPackagesOldFormat(NodeList inprocessList) {
		// TODO Auto-generated method stub
		Set<InprocessPackage> inprocesses=new LinkedHashSet<InprocessPackage>();
		for (int i = 0; i < inprocessList.getLength(); i++) {
			InprocessPackage ipp = new InprocessPackage();
			Node node = inprocessList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) node;
				ipp.setPackageName(eElement.getAttribute("name"));
				ipp.setLanguage(eElement.getAttribute("language"));

				ipp.setPath(eElement.getElementsByTagName("file-path").item(0)
						.getTextContent());

				ipp.setJarPackage(eElement.getElementsByTagName("jar-package")
						.item(0).getTextContent());
				ipp.setClassName(eElement.getElementsByTagName("class-name")
						.item(0).getTextContent());
				inprocesses.add(ipp);
			}
		}
		return inprocesses;
	}


	private String convertDomToString(Document doc) throws TransformerException {
		DOMSource domSource = new DOMSource(doc);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
		StringWriter sw = new StringWriter();
		StreamResult sr = new StreamResult(sw);
		transformer.transform(domSource, sr);
		return sr.getWriter().toString();
	}

	private Map<String,Set<InprocessPackage>> getInprocessMap(Set<InprocessPackage> inprocessPackages){
		Map<String,Set<InprocessPackage>> inprocessMap=new LinkedHashMap<String, Set<InprocessPackage>>();
		for(InprocessPackage ip:inprocessPackages){
			if(inprocessMap.containsKey(ip.getPath())){
				Set s=inprocessMap.get(ip.getPath());
				s.add(ip);
			}else{
				Set<InprocessPackage> s=new LinkedHashSet<InprocessPackage>();
				s.add(ip);
				inprocessMap.put(ip.getPath(), s);
			}
		}
		return inprocessMap;
	}

	public String getInprocessPackagesAsXML(Set<InprocessPackage> inprocessPackages) throws Exception
	{

		DocumentBuilderFactory factory=null;
		DocumentBuilder builder=null;
		Document doc=null;
		Element results=null;
		try{
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			doc = builder.newDocument();
			results = doc.createElement("inprocesspackages");
			doc.appendChild(results);
		}catch(Exception e)
		{
			throw new Exception("XML creation error "+e.getMessage());
		}

		Map<String,Set<InprocessPackage>> inprocessMap=getInprocessMap(inprocessPackages);

		Set<String> keys = inprocessMap.keySet();
		for(String path:keys){
			Element file_pathNode=doc.createElement("file-path");
			file_pathNode.setAttribute("dir", path);
			Set<InprocessPackage> inprocess=inprocessMap.get(path);
			for(InprocessPackage ip:inprocess){
				Element node = doc.createElement("inprocesspackage");
				Element jarPackageNode=doc.createElement("jar-package");
				Element class_nameNode=doc.createElement("class-name");

				node.setAttribute("name",ip.getPackageName());
				node.setAttribute("language",ip.getLanguage());
				jarPackageNode.appendChild(doc.createTextNode(ip.getJarPackage()));
				class_nameNode.appendChild(doc.createTextNode(ip.getClassName()));
				//file_pathNode.appendChild(doc.createTextNode(ip.));
				//node.appendChild(file_pathNode);
				node.appendChild(jarPackageNode);
				node.appendChild(class_nameNode);
				file_pathNode.appendChild(node);
			}
			results.appendChild(file_pathNode);

		}
		return convertDomToString(doc);
	}


	public Set<String> readScriptLocation(){
		Set<String> dirs=new HashSet<String>();
		Document xmlDocument = getDocument();
		NodeList nodeList=xmlDocument.getElementsByTagName("scriptlocation");
		for (int itr = 0; itr < nodeList.getLength(); itr++) {
			Node node = nodeList.item(itr);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) node;
				String path=eElement.getTextContent();
				String []paths=path.split(";");
				for(String p:paths){
					if(p!=null && !p.isEmpty())
						dirs.add(p);
				}
			}
		}
		return dirs;

	}

	public Set<LanguagePackage> readOutProcess() 
	{
		Set<LanguagePackage> langPacks=null;
		NodeList nodeList1 = null;
		Document xmlDocument = getDocument();


		NodeList nodeList = xmlDocument.getElementsByTagName("languages");
		langPacks=new LinkedHashSet<LanguagePackage>();
		for (int itr = 0; itr < nodeList.getLength(); itr++) {
			Node node = nodeList.item(itr);

			nodeList1 = xmlDocument.getElementsByTagName("language");


			for (int itr1 = 0; itr1 < nodeList1.getLength(); itr1++) {
				Node node1 = nodeList1.item(itr1);

				if (node1.getNodeType() == Node.ELEMENT_NODE) {
					LanguagePackage lp=new LanguagePackage();
					Element eElement = (Element) node1;
					lp.setLanguage(eElement.getAttribute("name"));
					lp.setExtension( eElement.getElementsByTagName("extension").item(0).getTextContent());
					lp.setInterpreter( eElement.getElementsByTagName("interpreter").item(0).getTextContent());
					lp.setPath(eElement.getElementsByTagName("path").item(0).getTextContent());
					lp.setOptions(eElement.getElementsByTagName("option").item(0).getTextContent());
					langPacks.add(lp);
				}
			}

		}
		return langPacks;

	}

	private Document getDocument(){
		Document xmlDocument=null;
		try {
			FileInputStream file = new FileInputStream(new File(workingDirectory+File.separator+fileName));
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory
					.newInstance();

			DocumentBuilder builder = builderFactory.newDocumentBuilder();

			xmlDocument = builder.parse(file);

			xmlDocument.getDocumentElement().normalize();
			file.close();
		}catch (FileNotFoundException e) {
			System.err.println("Error: Could not found file "+workingDirectory+File.separator+fileName);
		} catch (SAXException e) {
			System.err.println("Error: getting file "+e.getMessage());
		}
		catch (ParserConfigurationException e) {
			System.err.println("Error: parsing "+workingDirectory+File.separator+fileName+" file");
		}
		catch (IOException e) {
			System.err.println("Error: while reading "+workingDirectory+File.separator+fileName+" file");
		} 
		return xmlDocument;
	}

	public OutProcessPackage getOutProcessPackages(){
		OutProcessPackage opp=new OutProcessPackage();
		try {
			opp.setLangPacks(this.readOutProcess());
			opp.setScriptLocations(this.readScriptLocation());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return opp;
	}

	public String getLanguagesAsXML(Set<LanguagePackage> langPacks) throws Exception
	{
		DocumentBuilderFactory factory=null;
		DocumentBuilder builder=null;
		Document doc=null;
		Element results=null;
		try{
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			doc = builder.newDocument();
			results = doc.createElement("languages");
			doc.appendChild(results);
		}catch(Exception e)
		{
			throw new Exception("XML creation error "+e.getMessage());
		}
		for(LanguagePackage lp:langPacks){
			Element languagenode = doc.createElement("language");
			Element extensionNode=doc.createElement("extension");
			Element interpreterNode=doc.createElement("interpreter");
			Element pathNode=doc.createElement("path");
			Element optionNode=doc.createElement("option");
			languagenode.setAttribute("name",lp.getLanguage());

			extensionNode.appendChild(doc.createTextNode(lp.getExtension()));
			interpreterNode.appendChild(doc.createTextNode(lp.getInterpreter()));
			pathNode.appendChild(doc.createTextNode(lp.getPath()));
			optionNode.appendChild(doc.createTextNode(lp.getOptions()));
			languagenode.appendChild(extensionNode);
			languagenode.appendChild(interpreterNode);
			languagenode.appendChild(pathNode);
			languagenode.appendChild(optionNode);
			results.appendChild(languagenode);
		}

		return convertDomToString(doc);
	}

	public Configurations readConfigurations()
	{

		Configurations config=new Configurations();
		Document xmlDocument = getDocument();

		NodeList nodeList = xmlDocument.getElementsByTagName("configurations");
		for (int itr = 0; itr < nodeList.getLength(); itr++) {
			Node node = nodeList.item(itr);
			if (node.getNodeType() == Node.ELEMENT_NODE) 
			{
				ReporterParams rp=new ReporterParams();
				Element eElement = (Element) node;
				rp.setDbhostname(eElement.getElementsByTagName("dbhostname").item(0).getTextContent());
				rp.setDbusername(eElement.getElementsByTagName("dbusername").item(0).getTextContent());
				rp.setDbuserpassword(eElement.getElementsByTagName("dbuserpassword").item(0).getTextContent());
				rp.setDbname(eElement.getElementsByTagName("dbname").item(0).getTextContent());
				config.setReporterParams(rp);
				NodeList mvmConfigNodes=eElement.getElementsByTagName("mvm-configuration");
				//System.out.println(mvmConfigNodes);
				List<MvmConfiguration> mvmConfigs=new ArrayList<MvmConfiguration>();
				for (int i = 0; i < mvmConfigNodes.getLength(); i++) {
					Node mvmConfigNode = mvmConfigNodes.item(i);
					//System.out.println(mvmConfigNode.toString());
					if (mvmConfigNode.getNodeType() == Node.ELEMENT_NODE) {

						Element element = (Element) mvmConfigNode;
						MvmConfiguration mvmConfig=new MvmConfiguration();
						mvmConfig.setMemSize(element.getAttribute("jvm-max-memorysize"));
						mvmConfig.setValue(element.getTextContent());
						mvmConfigs.add(mvmConfig);
					}

				}
				config.setMvmConfigurations(mvmConfigs);
				NodeList adapterParamNodes=eElement.getElementsByTagName("adapter-param");
				Map<String,String> adapterParams=new HashMap<String, String>();
				for (int i = 0; i < adapterParamNodes.getLength(); i++) {
					Node adapterParamNode = adapterParamNodes.item(i);
					//System.out.println(mvmConfigNode.toString());
					if (adapterParamNode.getNodeType() == Node.ELEMENT_NODE) {

						Element element = (Element) adapterParamNode;
						adapterParams.put(element.getAttribute("name"),element.getTextContent() );

					}

				}
				config.setAdapterParams(adapterParams);

			}

		}
		return config;

	}

	public String getConfigurationAsXML(Configurations config,String scriptLocation) throws Exception
	{
		//text box to XML is left,done- creating xml from hashmap
		DocumentBuilderFactory factory=null;
		DocumentBuilder builder=null;
		Document doc=null;
		Element results=null;
		try{
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			doc = builder.newDocument();
			results = doc.createElement("configurations");
			doc.appendChild(results);
		}catch(Exception e)
		{
			throw new Exception("XML creation error "+e.getMessage());
		}
		//System.out.println("----------------------------------ZUG INI---------------------------------------");
		Element dbhostnameNode=doc.createElement("dbhostname");
		Element dbnameNode=doc.createElement("dbname");
		Element dbusernameNode=doc.createElement("dbusername");
		Element dbuserpasswordNode=doc.createElement("dbuserpassword");
		Element scriptlocationNode=doc.createElement("scriptlocation");
		dbnameNode.appendChild(doc.createTextNode(config.getReporterParams().getDbname()));


		dbhostnameNode.appendChild(doc.createTextNode(config.getReporterParams().getDbhostname()));
		dbusernameNode.appendChild(doc.createTextNode(config.getReporterParams().getDbusername()));
		dbuserpasswordNode.appendChild(doc.createTextNode(config.getReporterParams().getDbuserpassword()));
		scriptlocationNode.appendChild(doc.createTextNode(scriptLocation));

		results.appendChild(scriptlocationNode);
		results.appendChild(dbhostnameNode);
		results.appendChild(dbnameNode);
		results.appendChild(dbusernameNode);
		results.appendChild(dbuserpasswordNode);
		for(MvmConfiguration mvmConfig:Configurations.getMvmConfigurations()){
			Element mvm=doc.createElement("mvm-configuration");
			mvm.setAttribute("jvm-max-memorysize", mvmConfig.getMemSize());
			mvm.appendChild(doc.createTextNode(mvmConfig.getValue()));
			results.appendChild(mvm);
		}
		Set<String> adapterParams=config.getAdapterParams().keySet();
		for(String param:adapterParams){
			Element aParam=doc.createElement("adapter-param");
			aParam.setAttribute("name", param);
			aParam.appendChild(doc.createTextNode(Configurations.getAdapterParams().get(param)));
			results.appendChild(aParam);
		}
		return convertDomToString(doc);
	}



	/*	public static void main(String []args){
		IniHandler ini=new IniHandler();
		Set s=ini.readInprocessPackages();
		OutProcessPackage opp=ini.getOutProcessPackages();
		Configurations config=ini.readConfigurations();
		System.out.println(config);
		try {
				System.out.println(ini.getInprocessPackagesAsXML(s));
				System.out.println(ini.getLanguagesAsXML(opp.getLangPacks()));
			System.out.println(ini.getConfigurationAsXML(config, opp.getScriptLocations().toString()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

	public void saveFile(Set<InprocessPackage> ipp, Set<String> scriptLocation,
			Set<LanguagePackage> lps, ReporterParams rps) {
		// TODO Auto-generated method stub
		try {
			String inprocessText=getInprocessPackagesAsXML(ipp);
			String oppText=getLanguagesAsXML(lps);
			Configurations config=new Configurations();
			config.setReporterParams(rps);
			String scLocation="";
			if(!scriptLocation.isEmpty()){
				for(String s:scriptLocation){
					if(s!=null && !s.isEmpty())
						scLocation+=s+";";
				}
				if(scLocation.length()>1){
					scLocation=scLocation.substring(0,scLocation.length()-1);					
				}	
			}
			String configText=getConfigurationAsXML(config, scLocation);
			String iniXML="<root>"+oppText+inprocessText+configText+"</root>";
			File ini=new File(workingDirectory+File.separator+fileName);
			try {  
				if(ini.exists()){
					
					FileOutputStream fop = new FileOutputStream(ini,false);  					
					byte[] contentInBytes = iniXML.getBytes();  
					fop.write(contentInBytes);  
					fop.flush();  
					fop.close(); 												

				}else{
					System.err.println("Error: Could not locate "+workingDirectory+File.separator+fileName+" File");
				}
			} catch (IOException e) {  
				System.err.println("Error: saving ini config to file"+e.getMessage());
			}  
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("Error: saving ini config to file"+e.getMessage());
		}

	}
}
