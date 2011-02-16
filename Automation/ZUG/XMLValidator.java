/***
 * XMLUtility.java
 * This is the XMLValidator class which Validates Topology XML file with given dtd schema
 */

package ZUG;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import logs.Log;

import org.xml.sax.InputSource;
public class XMLValidator {

	
	///Function to start validating the XML file
	 public void Validate(String XMLPath, String XSDPath) throws Exception{
		 try{
			 Log.Debug("XMLValidator/Validate() : Entered in Validate()");
			 
			 Schema schema = loadSchema(XSDPath);
			 
			 validateXml(schema, XMLPath);
		 }
		 catch(Exception e){
			 Log.Error("\nXMLValidator/Validate() : Exception occured: "+e.getMessage());
			 throw new Exception("\nXMLValidator/Validate() : Exception occured: "+e.getMessage());
		 }
		 Log.Debug("XMLValidator/Validate() : successfully validated XML file : "+XMLPath);
	 }
	 
	 ///Function to validate XML against Schema
	 public  void validateXml(Schema schema, String xmlName)throws Exception{ 
		 
		 Log.Debug("XMLValidator/validateXml() : Entered in validateXml()");
		    try {
		 
		    	// creating a Validator instance
		      Validator validator = schema.newValidator();
		     
		      Log.Debug("XMLValidator/validateXml() :Validator Class: "
		        + validator.getClass().getName());
		      
		      Log.Debug("XMLValidator/validateXml() Creating SAX source for "+xmlName);
		      // preparing the XML file as a SAX source
		      
		      SAXSource source = new SAXSource(
		        new InputSource(new java.io.FileInputStream(xmlName)));
		      
		      Log.Debug("XMLValidator/validateXml() Validating "+xmlName);
		      // validating the SAX source against the schema
		      
		      validator.validate(source);
		     Log.Debug("XMLValidator/validateXml() Validation passed.");

		    } 
		    catch (Exception e) {
		      // catching all validation exceptions
		    	Log.Error("XMLValidator/validateXml() :Exception occured " +e.getMessage()); 
		    
		    	throw new Exception("\nXMLValidator/validateXml() :Exception occured " +e.getMessage()); 
		    	}
		  }
	 
	 ///Function to load Schema with given XSD source
		  public  Schema loadSchema(String name) throws Exception{
			
			  Log.Debug("XMLValidator/loadSchema(): Entered in function().");
			   Schema schema = null;
			   try {
				   String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
				   Log.Debug("XMLValidator/loadSchema(): language for Schema is : "+language);
		    	
				   Log.Debug("XMLValidator/loadSchema(): Creating SchemaFactory Instance");
				   SchemaFactory factory = SchemaFactory.newInstance(language);
				   Log.Debug("XMLValidator/loadSchema():Creating Schema");
				   schema = factory.newSchema(new File(name));
			   }
			   catch (Exception e) {
				   Log.Error("XMLValidator/loadSchema() :Exception occured " +e.getMessage()); 
				   
				   throw new Exception("XMLValidator/loadSchema() :Exception occured " +e.getMessage());
			   }
			   return schema;
		  }
	 }