package com.automature.zug.engine;
import com.automature.zug.util.Log;
import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

 
public class XMLPrimitive
{
	private final static String _elementDelimiter = "#";
	private final static String _attributeDelimiter = "$";
	private final static String _listDelimiter = ";";
	
	/**
	 * Read context variable value from attribute.
	 * @param xmlFilePath Input xml file path.
	 * @param keyTagName The hierarchical relationship from the root element to the element in focus.
	 * 	    				For ex. VMNAMES#VM where VMNAMES is root element and VM is element.
	 *  Inavlid i/p:VMNAMES# VM
	 *  Valid i/p:VMNAMES#VM
	 * @param keyIdentity Name of the attribute in the element in focus whose value is compared to input argument.
	 * @param keyValue Value of the attribute mentioned in the �KeyIdentity� to identify the element in focus.
	 * @param attributeName Name of the attribute whose value needs to be read.
	 * For ex. parameter value as follows:
	 * ipaddress --> ipaddress attribute of the parent (VM) element.
	 * HOSTINFO$ipaddress: ipaddress attribute of the child element HOSTINFO.
	 * HOSTINFO#SYSTEMDETAILS$operatingsystem --> operatingsystem attribute of SYSTEMDETAILS element which  is a child of HOSTINFO.
	 * Invalid i/p:HOSTINFO$ ipaddress
	 * Valid i/p:HOSTINFO$ipaddress
	 * @param returnContextVar Name of the context variable in which the return value needs to be stored.
	 * @return Returns ContextVariable object with variable name and its value if found else null.
	 * @throws Exception 
	 */
	
	public static ContextVariable GetAttribute(String xmlFilePath, String keyTagName, String keyIdentity, String keyValue,
			String attributeName, String returnContextVar) throws Exception
	{
			
		ContextVariable contextVariable = null;
		Document document = null;
		try
		{
			Log.Debug("XMLPrimitive/GetAttribute() called.");
			Log.Debug("XMLPrimitive/GetAttribute(): xmlFilePath-" + xmlFilePath);
			Log.Debug("XMLPrimitive/GetAttribute(): keyTagName-" + keyTagName);
			Log.Debug("XMLPrimitive/GetAttribute(): keyIdentity-" + keyIdentity);
			Log.Debug("XMLPrimitive/GetAttribute(): keyValue-" + keyValue);
			Log.Debug("XMLPrimitive/GetAttribute(): attributeName-" + attributeName);
			Log.Debug("XMLPrimitive/GetAttribute(): returnContextVar-" + returnContextVar);

			File file= new File(xmlFilePath);

			if(keyIdentity.contains(_attributeDelimiter)||keyIdentity.contains(_elementDelimiter)||keyIdentity.contains(_listDelimiter))
			{
				Log.Error("XMLPrimitive/GetTagValue() : KeyIdentity contains illegal character");
				throw new Exception("GetTagValue contains illegal character");
			}
			if (!file.exists())
			{
				Log.Debug("XMLPrimitive/GetAttribute(): " + xmlFilePath + " XML file does not exist.");
				Log.Error("XMLPrimitive/GetAttribute(): " + xmlFilePath + " XML file does not exist.");
				throw new Exception(xmlFilePath + " XML file does not exist.");
			}
			
			// Load the XML file
			try
			{
				document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
				document.getDocumentElement().normalize();
			}
			catch(Exception FileLoadExcption)
			{
				Log.Debug("XMLPrimitive/GetAttribute(): Failed to load the xml file "+ xmlFilePath);
				Log.Error("XMLPrimitive/GetAttribute(): Failed to load the xml file "+ xmlFilePath);
				throw FileLoadExcption;
			}
			
			Log.Debug("XMLPrimitive/GetAttribute(): " + xmlFilePath + "xml file loded.");

			String elementQuery = keyTagName.replace(_elementDelimiter, "/");
			Log.Debug("XMLPrimitive/GetAttribute(): XPath Query to find element: " + elementQuery);

			//Consider case where attributeName is HOSTINFO#SYSTEMDETAILS$operatingsystem
			/*String attributeElementQuery = attributeName.substring(0,
					attributeName.indexOf(_attributeDelimiter) < 0 ? 0 : attributeName.indexOf(_attributeDelimiter)).replace(
							_elementDelimiter, "[@");
			attributeElementQuery = attributeElementQuery.concat("='" + keyValue + "']");
			*/
			String attributeElementQuery = elementQuery.concat("[@"+keyIdentity+"='"+keyValue + "']");
						
			Node RequiredNode = org.apache.xpath.XPathAPI.selectSingleNode(document, "//"+attributeElementQuery);
			Element elem = (Element)RequiredNode;
			
			//code is not yet at the stage of debug. 
			//But at this print statement the query expected to form like
			//HOSTINFO/SYSTEMDETAILS[@operatingsystem='XP'] which will return 1st node having attribute  operatingsystem with
			//value=XP.
			Log.Debug("XMLPrimitive/GetAttribute(): XPath query to find attribute element: " + attributeElementQuery);
			if(RequiredNode != null)
			{
				
				contextVariable = new ContextVariable();
				contextVariable.setName(returnContextVar);
				//contextVariable.setValue(elem.getAttribute(attributeName));
				contextVariable.setValue(elem.getAttribute(attributeName));
				return contextVariable;
			}
			else
			{
				Log.Debug("XMLPrimitive/GetAttribute(): No XML element find.");
				return contextVariable;
			}
		}
		catch (Exception e)
		{
			Log.Debug("XMLPrimitive/GetAttribute():" + e.toString());
			Log.Error("XMLPrimitive/GetAttribute():" + e.toString());
			throw e;
		}
	}
	//OLD FUNCTION COMMENTED TO IMPLEMENT SAME USING XPATH
	/*
	public static ContextVariable GetAttribute(String xmlFilePath, String keyTagName, String keyIdentity, String keyValue,
			String attributeName, String returnContextVar)
	{
		try
		{
			Log.Debug("XMLPrimitive:ContextVariable() called.");
			Log.Debug("XMLPrimitive:ContextVariable(): xmlFilePath-" + xmlFilePath);
			Log.Debug("XMLPrimitive:ContextVariable(): keyTagName-" + keyTagName);
			Log.Debug("XMLPrimitive:ContextVariable(): keyIdentity-" + keyIdentity);
			Log.Debug("XMLPrimitive:ContextVariable(): keyValue-" + keyValue);
			Log.Debug("XMLPrimitive:ContextVariable(): attributeName-" + attributeName);
			Log.Debug("XMLPrimitive:ContextVariable(): returnContextVar-" + returnContextVar);

			File file= new File(xmlFilePath);

			if (!file.exists())
			{
				Log.Debug("XMLPrimitive:ContextVariable(): " + xmlFilePath + " XML file does not exist.");
				Log.Error("XMLPrimitive:ContextVariable(): " + xmlFilePath + " XML file does not exist.");
				throw new Exception(xmlFilePath + " XML file does not exist.");
			}
			
			// Load the XML file 
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(file);
			document.getDocumentElement().normalize();

			Log.Debug("XMLPrimitive:ContextVariable(): " + xmlFilePath + "xml file loded.");

			String elementQuery = keyTagName.replace(_elementDelimiter, "/");
			Log.Debug("XMLPrimitive:ContextVariable(): XPath Query to find element: " + elementQuery);

			//Consider case where attributeName is HOSTINFO#SYSTEMDETAILS$operatingsystem
			String attributeElementQuery = attributeName.substring(0,
					attributeName.indexOf(_attributeDelimiter) < 0 ? 0 : attributeName.indexOf(_attributeDelimiter)).replace(
							_elementDelimiter, "/");
			Log.Debug("XMLPrimitive:ContextVariable(): XPath query to find attribute element: " + attributeElementQuery);

			String attribute = attributeName.substring(attributeName.indexOf(_attributeDelimiter) + 1);
			Log.Debug("XMLPrimitive:ContextVariable(): Attribute Name: " + attribute);
			NodeList root = document.getChildNodes();
			NodeList xmlNodeList = document.getElementsByTagName(elementQuery);
			if (xmlNodeList != null)
			{
				Attr xmlAttribute=null;

				for (int i = 0; i < xmlNodeList.getLength(); i++)
				{ 
					NamedNodeMap xmlAttributeList =xmlNodeList.item(i).getAttributes(); 
					for(i = 0 ; i<xmlAttributeList.getLength() ; i++) {
						xmlAttribute = (Attr)xmlAttributeList.item(i);     
					}
					if (xmlAttribute != null && xmlAttribute.getValue().compareToIgnoreCase(keyValue) == 0)
					{
						Node xmlNode;
						if (StringUtils.isBlank(attributeElementQuery))
							xmlNode = xmlNodeList.item(i);
						else
						{
							//Assumed that only one xml node get selected.
							xmlNode = xmlNodeList.item(i).
						}
						//XMLNode not find.
						if (xmlNode == null)
						{
							Log.Debug("XMLPrimitive:ContextVariable(): No XML element find.");
							return null;
						}

						ContextVariable contextVariable = new ContextVariable();
						contextVariable.setName(returnContextVar);
						contextVariable.setValue(xmlNode.Attributes[attribute1.Value);
						Log.Debug("XMLPrimitive:ContextVariable(): Context Variable Name-" + contextVariable.getName() +
								", value-" + contextVariable.getValue());

						return contextVariable;
					}
				}
			}
			return null;
		}
		catch (Exception e)
		{
			Log.Debug("XMLPrimitive:ContextVariable():" + e.toString());
			Log.Error("XMLPrimitive:ContextVariable():" + e.toString());
			throw e;
		}
	}*/

	
	/**
	 * Read context variable value from element.
	 * @param xmlFilePath Input xml file path.
	 * @param keyTagName The hierarchical relationship from the root element to the element in focus. 
	 * 					For ex. VMNAMES#VM where VMNAMES is root element and VM is element.
	 * 					Inavlid i/p:VMNAMES# VM
	 * 					Valid i/p:VMNAMES#VM
	 * @param keyIdentity Name of the attribute in the element in focus whose value is compared to input argument.
	 * @param keyValue Value of the attribute mentioned in the �KeyIdentity� to identify the element in focus.
	 * @param tagName Name of the child tag of the parent mentioned in �KeyTagName� whose tag value needs to be read.
	 * 					For ex. parameter value as follows:
	 * 					HOSTINFO--> HOSTINFO is child element of the parent (VM) element.
	 * 					HOSTINFO#systemdetail-->systemdetail is child element of the HOSTINFO element.
	 * 					Invalid i/p:HOSTINFO$ ipaddress
	 * 					Valid i/p:HOSTINFO$ipaddress
	 * @param returnContextVar Name of the context variable in which the return value needs to be stored.
	 * @return Returns ContextVariable object with variable name and its value if found else null.
	 * @throws Exception 
	 */
	public static ContextVariable GetTagValue(String xmlFilePath, String keyTagName, String keyIdentity, String keyValue,
			String tagName, String returnContextVar) throws Exception
	{
		ContextVariable contextVariable = null;
		Document document = null;
		try
		{
			Log.Debug("XMLPrimitive/GetTagValue() called.");
			Log.Debug("XMLPrimitive/GetTagValue() keyTagName-" + keyTagName);
			Log.Debug("XMLPrimitive/GetTagValue() keyIdentity-" + keyIdentity);
			Log.Debug("XMLPrimitive/GetTagValue() keyValue-" + keyValue);
			Log.Debug("XMLPrimitive/GetTagValue() tagName-" + tagName);
			Log.Debug("XMLPrimitive/GetTagValue() returnContextVar-" + returnContextVar);

			File file= new File(xmlFilePath);

			if(keyIdentity.contains(_attributeDelimiter)||keyIdentity.contains(_elementDelimiter)||keyIdentity.contains(_listDelimiter))
			{
				Log.Error("XMLPrimitive/GetTagValue() : KeyIdentity contains illegal character");
				throw new Exception("GetTagValue contains illegal character");
			}
			if (!file.exists())
			{
				Log.Debug("XMLPrimitive/GetTagValue(): " + xmlFilePath + " XML file does not exist.");
				Log.Error("XMLPrimitive/GetTagValue(): " + xmlFilePath + " XML file does not exist.");
				throw new Exception(xmlFilePath + " XML file does not exist.");
			}
			// Load the XML file
			try
			{
				document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
				document.getDocumentElement().normalize();
			}
			catch(Exception FileLoadExcption)
			{
				Log.Debug("XMLPrimitive/GetTagValue(): Failed to load the xml file "+ xmlFilePath);
				Log.Error("XMLPrimitive/GetTagValue(): Failed to load the xml file "+ xmlFilePath);
				throw FileLoadExcption;
			}
			Log.Debug("XMLPrimitive/ContextVariable(): " + xmlFilePath + "xml file loded.");

			String elementQuery = keyTagName.replace(_elementDelimiter, "/");
			Log.Debug("XMLPrimitive/GetTagValue(): XPath Query to find element: " + elementQuery);
			

			//Consider case where attributeName is HOSTINFO#SYSTEMDETAILS$operatingsystem
			String attributeElementQuery ;
			
			/*attributeElementQuery = keyIdentity.substring(0,
					keyIdentity.indexOf(_attributeDelimiter) < 0 ? 0 : keyIdentity.indexOf(_attributeDelimiter)).replace(
							_elementDelimiter, "[@");
			
			attributeElementQuery = attributeElementQuery.concat("='" + keyValue + "']");
			*/
			attributeElementQuery = elementQuery.concat("[@"+keyIdentity+"='"+keyValue + "']");
			
			Node RequiredNode = org.apache.xpath.XPathAPI.selectSingleNode(document, "//"+attributeElementQuery);
			Element elem = (Element)RequiredNode;
			NodeList tagNodes = elem.getElementsByTagName(tagName);
			Node tagNode = tagNodes.item(0);
			


			Log.Debug("XMLPrimitive/GetTagValue(): XPath query to find attribute element: " + attributeElementQuery);
			if(RequiredNode != null)
			{

				contextVariable = new ContextVariable();
				contextVariable.setName(returnContextVar);
				//contextVariable.setValue(elem.getTextContent());
				contextVariable.setValue(tagNode.getTextContent());
				return contextVariable;
			}
			else
			{
				Log.Debug("XMLPrimitive/GetTagValue(): No XML element find.");
				return contextVariable;
			}
		}
		catch (Exception e)
		{
			Log.Debug("XMLPrimitive/GetTagValue(): " + e.getMessage());
			Log.Error("XMLPrimitive/GetTagValue(): " + e.getMessage());
			throw e;
		}
	}
	
	
	//OLD code commented
	/*public static ContextVariable GetTagValue(String xmlFilePath, String keyTagName, String keyIdentity, String keyValue,
			String tagName, String returnContextVar)
	{
		try
		{
			Log.Debug("XMLPrimitive:ContextVariable() called.");
			Log.Debug("XMLPrimitive:ContextVariable() keyTagName-" + keyTagName);
			Log.Debug("XMLPrimitive:ContextVariable() keyIdentity-" + keyIdentity);
			Log.Debug("XMLPrimitive:ContextVariable() keyValue-" + keyValue);
			Log.Debug("XMLPrimitive:ContextVariable() tagName-" + tagName);
			Log.Debug("XMLPrimitive:ContextVariable() returnContextVar-" + returnContextVar);

			XmlDocument document = new XmlDocument();
			if (!File.Exists(xmlFilePath))
			{
				Log.Error("XMLPrimitive:ContextVariable()" + xmlFilePath + " XML file does not exist.");
				Log.Debug("XMLPrimitive:ContextVariable()" + xmlFilePath + " XML file does not exist.");
				throw new Exception(xmlFilePath + " XML file does not exist.");
			}
			document.Load(xmlFilePath);
			Log.Debug("XMLPrimitive:ContextVariable(): " + xmlFilePath + "xml file loded.");

			string elementQuery = keyTagName.Replace(_elementDelimiter, "/");
			Log.Debug("XMLPrimitive:ContextVariable(): XPath Query to find element-" + elementQuery);

			string tagQuery = tagName.Replace(_elementDelimiter, "/");
			Log.Debug("XMLPrimitive:ContextVariable(): XPath Query to find tag element-" + tagQuery);

			XmlNodeList xmlNodeList = document.SelectNodes(elementQuery);

			if (xmlNodeList != null)
			{
				for (int i = 0; i < xmlNodeList.Count; i++)
				{
					XmlAttribute xmlAttribute = xmlNodeList[i].Attributes[keyIdentity];

					if (xmlAttribute != null && string.Compare(xmlAttribute.Value, keyValue, true) == 0)
					{
						XmlNode xmlNode;
						if (string.IsNullOrEmpty(tagQuery))
							xmlNode = xmlNodeList[i];
						else
						{
							//Assumed that only one xml node get selected.
							xmlNode = xmlNodeList[i].SelectNodes(tagQuery)[0];
						}

						//XMLNode not find.
						if (xmlNode == null)
						{
							Log.Debug("XMLPrimitive:ContextVariable(): No XML element find.");
							return null;
						}

						ContextVariable contextVariable = new ContextVariable();
						contextVariable.Name = returnContextVar;
						contextVariable.Value = xmlNode.InnerText;
						Log.Debug("XMLPrimitive:ContextVariable(): Context Variable Name-" + contextVariable.Name +
								",Value-" + contextVariable.Value);

						return contextVariable;
					}
				}
			}
			return null;

		}
		catch (Exception e)
		{
			Log.Debug("XMLPrimitive:ContextVariable(): " + e.ToString());
			Log.Error("XMLPrimitive:ContextVariable(): " + e.ToString());
			throw e;
		}
		return new ContextVariable();
	}*/

	/// <summary>
	/// Read context variable value from list of element and attribute.
	/// </summary>
	/// <param name="xmlFilePath">Input xml file path.</param>
	/// <param name="keyTagName">The hierarchical relationship from the root element to the element in focus. 
	/// For ex. VMNAMES#VM where VMNAMES is root element and VM is element.
	/// Inavlid i/p:VMNAMES# VM
	/// Valid i/p:VMNAMES#VM</param>
	/// <param name="keyIdentity">Name of the attribute in the element in focus whose value is compared to input argument.</param>
	/// <param name="keyValue">Value of the attribute mentioned in the �KeyIdentity� to identify the element in focus.</param>
	/// <param name="attributeNames">A ";" separated list of attributes whose values need to be retrieved.
	/// Note that the �AttributeNames� should contain only the names of the attributes whose values are to be returned,
	/// or the paths from the parent element.
	/// Invalid i/p:HOSTINFO$adminuser; name
	/// Valid i/p:HOSTINFO$adminuser;name</param>
	/// <param name="tagNames">A ";" separated list of tags whose values need to be retrieved.
	/// Note that the �TagNames� should contain only the names of the tags whose values are to be returned,
	/// or the paths from the parent element.
	/// Invalid i/p:HOSTINFO#systemdetail; VMIMAGEPATH
	/// Valid i/p:HOSTINFO#systemdetail;VMIMAGEPATH</param>
	/// <param name="returnAttrContextVars">A ";" separated list of context variables in which the return values of attributes
	/// need to be stored.
	/// Invalid i/p:ADMINUSER; NAME
	/// Valid i/p:ADMINUSER;NAME</param>
	/// <param name="returnTagContextVars">A ";" separated list of context variables in which the return values of tags need
	/// to be stored.
	/// Invalid i/p:SYSTEMDETAIL; VMIMAGEPATH
	/// Valid i/p:SYSTEMDETAIL;VMIMAGEPATH
	/// </param>
	/// <returns>Returns ContextVariable object list with variable name and its value if found else empty list.</returns>
	public static ArrayList<ContextVariable> GetAttributeorTagList(String xmlFilePath, String keyTagName, String keyIdentity,
			String keyValue, String attributeNames, String tagNames, String returnAttrContextVars, String returnTagContextVars) throws Exception
	{
/* This is old code which was commented
		 * 
		 * try
		{
			Log.Debug("XMLPrimitive:GetAttributeorTagList() called.");
			Log.Debug("XMLPrimitive:GetAttributeorTagList(): keyTagName-" + keyTagName);
			Log.Debug("XMLPrimitive:GetAttributeorTagList(): keyIdentity-" + keyIdentity);
			Log.Debug("XMLPrimitive:GetAttributeorTagList(): keyValue-" + keyValue);
			Log.Debug("XMLPrimitive:GetAttributeorTagList(): attributeNames-" + attributeNames);
			Log.Debug("XMLPrimitive:GetAttributeorTagList(): tagNames-" + tagNames);
			Log.Debug("XMLPrimitive:GetAttributeorTagList(): returnAttrContextVars-" + returnAttrContextVars);
			Log.Debug("XMLPrimitive:GetAttributeorTagList(): returnTagContextVars-" + returnTagContextVars);


			XmlDocument document = new XmlDocument();
			if (!File.Exists(xmlFilePath))
			{
				Log.Debug("XMLPrimitive:GetAttributeorTagList():" + xmlFilePath + " XML file does not exist.");
				Log.Error("XMLPrimitive:GetAttributeorTagList():" + xmlFilePath + " XML file does not exist.");
				throw new Exception(xmlFilePath + " XML file does not exist.");
			}
			document.Load(xmlFilePath);
			Log.Debug("XMLPrimitive:GetAttributeorTagList():" + xmlFilePath + "xml file loded.");

			string elementQuery = keyTagName.Replace(_elementDelimiter, "/");
			Log.Debug("XMLPrimitive:GetAttributeorTagList(): XPath Query to find element-" + elementQuery);

			//Get all attribute names and corresponding context variable name.
			string[] attributes = attributeNames.Split(new string[] { _listDelimiter },
					StringSplitOptions.RemoveEmptyEntries);
			string[] attContextVars = returnAttrContextVars.Split(new string[] { _listDelimiter },
					StringSplitOptions.RemoveEmptyEntries);

			//Get all element names and corresponding context variable name.
			string[] tags = tagNames.Split(new string[] { _listDelimiter }, StringSplitOptions.RemoveEmptyEntries);
			string[] tagContextVars = returnTagContextVars.Split(new string[] { _listDelimiter },
					StringSplitOptions.RemoveEmptyEntries);

			ArrayList<ContextVariable> contextVariableList = new ArrayList<ContextVariable>();

			for (int i = 0; i < attributes.length; i++)
			{
				//Get Context variable value for each attribute.
				ContextVariable variable = GetAttribute(xmlFilePath, keyTagName, keyIdentity, keyValue, attributes[i],
						attContextVars[i]);
				if (variable != null)
				{
					contextVariableList.Add(variable);
					Log.Debug("XMLPrimitive:GetAttributeorTagList(): Context Variable Added in List: Name-" + variable.getName() +
							",Value-" + variable.getValue());
				}
			}

			for (int i = 0; i < tags.Length; i++)
			{
				//Get Context variable value for each element.
				ContextVariable variable = GetTagValue(xmlFilePath, keyTagName, keyIdentity, keyValue, tags[i],
						tagContextVars[i]);
				if (variable != null)
				{
					contextVariableList.add(variable);
					Log.Debug("XMLPrimitive:GetAttributeorTagList(): Context Variable Added in List: Name-" + variable.getName()+
							",Value-" + variable.getValue());
				}
			}

			return contextVariableList;

		}
		catch (Exception e)
		{
			Log.Error("XMLPrimitive:GetAttributeorTagList(): " + e.getMessage());
			throw new Exception("XMLPrimitive:GetAttributeorTagList(): " + e.getMessage());
		}
*/
		try
		{
			Log.Debug("XMLPrimitive:GetAttributeorTagList() called.");
			Log.Debug("XMLPrimitive:GetAttributeorTagList(): keyTagName-" + keyTagName);
			Log.Debug("XMLPrimitive:GetAttributeorTagList(): keyIdentity-" + keyIdentity);
			Log.Debug("XMLPrimitive:GetAttributeorTagList(): keyValue-" + keyValue);
			Log.Debug("XMLPrimitive:GetAttributeorTagList(): attributeNames-" + attributeNames);
			Log.Debug("XMLPrimitive:GetAttributeorTagList(): tagNames-" + tagNames);
			Log.Debug("XMLPrimitive:GetAttributeorTagList(): returnAttrContextVars-" + returnAttrContextVars);
			Log.Debug("XMLPrimitive:GetAttributeorTagList(): returnTagContextVars-" + returnTagContextVars);
			ArrayList<ContextVariable> contextVariableList = new ArrayList<ContextVariable>();
			Document document = null;
			if(keyIdentity.contains(_attributeDelimiter)||keyIdentity.contains(_elementDelimiter)||keyIdentity.contains(_listDelimiter))
			{
				Log.Error("XMLPrimitive/GetTagValue() : KeyIdentity contains illegal character");
				throw new Exception("GetTagValue contains illegal character");
			}
			File file= new File(xmlFilePath);
			if (!file.exists())
			{
				Log.Debug("XMLPrimitive/GetTagValue(): " + xmlFilePath + " XML file does not exist.");
				Log.Error("XMLPrimitive/GetTagValue(): " + xmlFilePath + " XML file does not exist.");
				throw new Exception(xmlFilePath + " XML file does not exist.");
			}
			// Load the XML file
			try
			{
				document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
				document.getDocumentElement().normalize();
			}
			catch(Exception FileLoadExcption)
			{
				Log.Debug("XMLPrimitive/GetTagValue(): Failed to load the xml file "+ xmlFilePath);
				Log.Error("XMLPrimitive/GetTagValue(): Failed to load the xml file "+ xmlFilePath);
				throw FileLoadExcption;
			}
			Log.Debug("XMLPrimitive/ContextVariable(): " + xmlFilePath + "xml file loded.");
			
			String elementQuery = keyTagName.replace(_elementDelimiter, "/");
			Log.Debug("XMLPrimitive:GetAttributeorTagList(): XPath Query to find element-" + elementQuery);
			//Get all attribute names and corresponding context variable name.
			String[] attributes = attributeNames.split( _listDelimiter );
			String[] attContextVars = returnAttrContextVars.split(_listDelimiter );
			if(attributes.length<attContextVars.length)
			{
				Log.Error("XMLPrimitive/GetAttributeorTagList: AttributeNames missing in the command" );
				throw new Exception("XMLPrimitive/GetAttributeorTagList: AttributeNames missing in the command");
			}
			else if(attributes.length>attContextVars.length)
			{
				Log.Error("XMLPrimitive/GetAttributeorTagList: Attribute Context Variable missing in the command");
				throw new Exception("XMLPrimitive/GetAttributeorTagList: Attribute Context Variable missing in the command");
			}
			

			//Get all element names and corresponding context variable name.
			String[] tags = tagNames.split(_listDelimiter );
			String[] tagContextVars = returnTagContextVars.split(_listDelimiter );
			if(tags.length<tagContextVars.length)
			{
				Log.Error("XMLPrimitive/GetAttributeorTagList: TagNames missing in the command");
				throw new Exception("XMLPrimitive/GetAttributeorTagList: TagNames missing in the command");
			}
			else if(tags.length>tagContextVars.length)
			{
				Log.Error("XMLPrimitive/GetAttributeorTagList: Tag Context Variable missing in the command");
				throw new Exception("XMLPrimitive/GetAttributeorTagList: Tag Context Variable missing in the command"); 
			}
			for (int i = 0; i < attributes.length; i++)
			{
				//Get Context variable value for each attribute.
				if(attributes[i].isEmpty())
					continue;
				ContextVariable variable = GetAttribute(xmlFilePath, keyTagName, keyIdentity, keyValue, attributes[i],attContextVars[i]);
				if (variable != null)
				{
					contextVariableList.add(variable);
					Log.Debug("XMLPrimitive:GetAttributeorTagList(): Context Variable Added in List: Name-" + variable.getName() +
							",Value-" + variable.getValue());
				}
			}
			
			for (int i = 0; i < tags.length; i++)
			{
				//Get Context variable value for each element.
				if(tags[i].isEmpty())
					continue;
				ContextVariable variable = GetTagValue(xmlFilePath, keyTagName, keyIdentity, keyValue, tags[i],	tagContextVars[i]);
				if (variable != null)
				{
					contextVariableList.add(variable);
					Log.Debug("XMLPrimitive:GetAttributeorTagList(): Context Variable Added in List: Name-" + variable.getName()+
							",Value-" + variable.getValue());
				}
			}
			return contextVariableList;


		}
		catch (Exception e)
		{
			Log.Error("XMLPrimitive:GetAttributeorTagList(): " + e.getMessage());
			throw new Exception("XMLPrimitive:GetAttributeorTagList(): " + e.getMessage());
		}
	}
	
}

class ContextVariable
{
	private String _name;
	private String _value;
	/// Context Variable Name.
	public String getName()
	{
		return _name; 
	}
	public void setName(String name)
	{
		_name = name; 
	}
	/// Context variable value.
	public String getValue()
	{
		return _value; 
	}
	public void setValue(String value)
	{
		_value = value; 
	}

}

