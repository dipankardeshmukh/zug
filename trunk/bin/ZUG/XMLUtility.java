/***
 * XMLUtility.java
 *	This is the XMLUtility class which provides the basic utility to read the XML file and populate the values in proper
 *	DataStructures.
 */

package ZUG;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import logs.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLUtility {

	
	 /// This function validates and reads the contents from the Topology Set XML file
	/// <param name="setupFile">Path of the Topolology Set XML file.</param>
	
    public TopologySet[] ReadTopologySetInformation(String topologySetFile) throws Exception
    {
        Log.Debug("XMLUtility/ReadTopologySetInformation() : Start of Function with Topolology Set XML file as " + topologySetFile);
        File file = new File(topologySetFile);
    
        if (!file.exists())
        {
            String error = "XMLUtility/ReadTopologySetInformation(): Topolology Set XML file required does not exist. FileName :  "+topologySetFile;
            /// If the File doesnot Exists, then Log an error and throw the Exception
            Log.Error(error);
            throw new Exception(error);
        }

        Log.Debug("XMLUtility/ReadTopologySetInformation() : Topolology Set XML file exists. Reading the Contents of the Topolology Set XML file = " + topologySetFile);

        /// First validate the XML file against the XML XSD file.
        if (!ValidateTopology(topologySetFile, "TopologySet.xsd"))
            throw new Exception("XMLUtility/ReadTopologySetInformation() :Topolology Set XML file is not a valid XML file. Filename {0}. Check the XSD. "+ topologySetFile);

        /// If the topologySetFile is a valid XML file then proceed.
        /// Parse the topologySetFile XML file and populate the Object accordingly.
        TopologySet [] topologySet = ParseTopologySetXML(topologySetFile);

        Log.Debug("XMLUtility/ReadTopologySetInformation() : End of Function with topologySetFile as " + topologySetFile);

        if (topologySet.length == 0)
            return new TopologySet[0];

        return topologySet;
    }
    /// Function to read the contents of the Document Library node from the Setup XML file
    /// <param name="docLibNode">Object of Document Library node</param>
    /// <returns>an object of Documentbrary</returns>

    public TopologySet ReadTopologyInformation(Node docLibNode)
    {
        Log.Debug("XMLUtility/ReadTopologyInformation() : Start of Function.");
    
        TopologySet tempTopology = new TopologySet();
        NodeList topologyNode = docLibNode.getChildNodes();
        
        for(int i=0;i<topologyNode.getLength();i++)
        {
            Log.Debug("XMLUtility/ParseTopologySetXML() : Name of the Root Node is : " + topologyNode.item(i).toString().toLowerCase());
        
            if (topologyNode.item(i).getNodeName().toLowerCase().trim().equals("topologyid"))
            {
                tempTopology.topologyID = GetFirstChildValue(topologyNode.item(i));
                Log.Debug("XMLUtility/ReadTopologyInformation()  : Working on Topology[topologyid] = " + tempTopology.topologyID);
            }
            
            if (topologyNode.item(i).getNodeName().toLowerCase().trim().equals("topologyrole"))
            {
                tempTopology.topologyRole = GetFirstChildValue(topologyNode.item(i));
                Log.Debug("XMLUtility/ReadTopologyInformation()  : Working on Topology[topologyrole] = " + tempTopology.topologyRole);
            }
            
            if (topologyNode.item(i).getNodeName().toLowerCase().trim().equals("buildnumber"))
            {
                tempTopology.buildNumber = GetFirstChildValue(topologyNode.item(i));
                Log.Debug("XMLUtility/ReadTopologyInformation()  : Working on Topology[buildNumber] = " + tempTopology.buildNumber);
            }
        }
         
        Log.Debug("XMLUtility/ReadTopologyInformation() : End of Function.");

        return tempTopology;
    }



    /// This function parses the TopologySet XML file and populates the TopologySet Array object
    /// <param name="topologySetFile">topologySet XML File path.</param>
    /// <returns>Returns an object of  TopologySet Array.</returns>
    
    public TopologySet[] ParseTopologySetXML(String topologySetFile) throws Exception
    {
    		Log.Debug("XMLUtility/ParseTopologySetXML() : Start of function with XMLFile as: " + topologySetFile);
    
    		File file = new File(topologySetFile);
    		Log.Debug("XMLUtility/ParseTopologySetXML() : Before loading the XMLFile : " + topologySetFile);
    		
    		/// Load the XML file 
    		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    		DocumentBuilder db = dbf.newDocumentBuilder();
    		Document doc = db.parse(file);
    		doc.getDocumentElement().normalize();
    		
    		ArrayList<TopologySet> topologySetArray = new ArrayList();
    		NodeList root = doc.getChildNodes();
      
    		Log.Debug("XMLUtility/ParseTopologySetXML() : Parsing the Nodes of the XML FIle : " + topologySetFile);
    		for(int i=0;i<root.getLength();i++)
    		{
    			Log.Debug("XMLUtility/ParseTopologySetXML() : Name of the Root Node is : " + root.item(i).toString().toLowerCase());
    		
    			if(root.item(i).getNodeName().toLowerCase().trim().equals("topologyset")){
    			
    				NodeList  XmlNode  =root.item(i).getChildNodes();
    				for(int j=0;j<XmlNode.getLength();j++)
    				{
    					if (XmlNode.item(j).getNodeType()==doc.COMMENT_NODE)
    						continue;
    				
    					TopologySet tempTopologySet = new TopologySet();
    					Log.Debug("XMLUtility/ParseTopologySetXML() : Name of the Child Node is : " + XmlNode.item(j).getNodeName().toLowerCase());
    					
    					if (XmlNode.item(j).getNodeName().toLowerCase().trim().equals("topology"))
    					{
    						tempTopologySet = ReadTopologyInformation(XmlNode.item(j));
    					
    						if(tempTopologySet.topologyID!=null)
    							topologySetArray.add(tempTopologySet);
    					}
                    }
    			}
           }
    		
    		if(topologySetArray.size() == 0)
    			return new TopologySet[0];
    		TopologySet[] retVal = new TopologySet[topologySetArray.size()];
    		topologySetArray.toArray(retVal);
    		
    	  return retVal;
   
   }///ParseTopologySetXML


    /// This function validates an XML file against an XSD file.
    /// <param name="setupXMLFile">Setup XML file</param>
    /// <param name="setupXSDFile">Setup XSD file</param>
    /// <returns> Returns True if the XML file is validated;False otherwise</returns>

    public boolean ValidateTopology(String setupXMLFile, String setupXSDFile)
    {
        try
        {
            Log.Debug("XMLUtility/ValidateTopology() : Start of function with XMLFile as: " + setupXMLFile
                + " and XSDPath as " + setupXSDFile);
            XMLValidator validator = new XMLValidator();

            Log.Debug("XMLUtility/ValidateTopology() : Calling  validator.Validate() with XMLFile as: " + setupXMLFile
                + " and XSDPath as " + setupXSDFile);

            /// The function validates the XML file against the XSD schema file
            /// It throws an exception and that will be displayed on the page.
            validator.Validate(setupXMLFile, setupXSDFile);

            Log.Debug("XMLUtility/ValidateTopology() : Validation Successful with XMLFile as: " + setupXMLFile
                + " and XSDPath as " + setupXSDFile);

            return true;
        }
        catch (Exception ex)
        {
            Log.Error("XMLUtility/ValidateTopology() : Validation failed with XMLFile as: " + setupXMLFile
                + " and XSDPath as " + setupXSDFile + ". Exception is " + ex.getMessage());
            return false;
        }
    }



    /// The function is a Utility function. It parses the XML node and retrieves the value of the First child node
    /// <param name="node">XMLNode object</param>
    /// <returns>Value of the First Child of the Node; Null if the child doesnot exists</returns>
    private String GetFirstChildValue(Node node)
    {
        Log.Debug("XMLUtility/getFirstChildValue() : Start of function ");
        if (node.getFirstChild() != null)
        {
            Log.Debug("XMLUtility/getFirstChildValue() : First Child of the Node Exists ");
            if ((node.getFirstChild().getNodeValue())!=null)
            {
                Log.Debug("XMLUtility/getFirstChildValue() : First Child of the Node Exists and its value is : " + node.getFirstChild().getNodeValue().trim());
                return node.getFirstChild().getNodeValue().trim();
            }
        }
        Log.Debug("XMLUtility/getFirstChildValue() : End of function with Values as NULL");
        return null;
    }	
}