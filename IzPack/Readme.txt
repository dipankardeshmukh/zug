
This is a text file named "Readme.txt" in the Spark
installation directory, and can be printed directly from any text
editor, once the files have been unpacked.


License Activation:
Once you have successfully installed Zug in your machine, you will have to activate your License to use Zug.
               
Steps to activate your license.
                •	Download the Zug License.key file , which is provided to you in the email address which you had given in your contact information.
                •	Place the license key file in the Zug folder where you installed.
                •	Open a command prompt window
                •	Change the working directory to Zug
                •	Type SetUp.bat -l




System Requirements for Windows, OS X & Linux
Make sure that your system meets the minimum requirements to run Zug installer:
                •  Internet Connection.
                •  0.5 GB of available hard disk space.
                •  Command Prompt.
		            •  Java(TM) SE Runtime Environment(32bit) 1.6 (update 21)
		            •  Microsoft .Net Framework v3.5  (Optional)
                
 Installation
 =============
 
 Windows
 -------- 
 
• To install Spark on windows, download the installation file SparkSetup[version].jar from the company website www.automature.com and save it in a desired location. 

• To run the installer, type the following at the command prompt. 
		 java -jar SparkSetup[version].jar

• The installation welcome screen shows up. The installation setup screens will guide you and through the installation. When On clicking Next, the Spark Setup Installation wizard will guide you step by step through the installation procedure.Once finished, select Done.

• The default installation folder is C:\Program Files\Automature.

• Copy and paste the Spark.Licence.Key in SPARK folder. 

• Launch the Spark GUI by double clicking on the Spark icon created on the desktop.   


OS X
----- 
For installing Spark in OS X 

• Download SparkSetup[version].jar from Automature website (www.automature.com) and save it in a preferred location. 
 
• To run the installer type the following at the command prompt.  
         java -jar SparkSetup[version].jar

• The installation welcome screen shows up. The installation setup screens will guide you and through the installation. When On clicking Next, the Spark Setup Installation wizard will guide you step by step through the installation procedure.Once finished, select Done.

• The default installation folder is /Applications/Automature.


• Copy and paste the Spark.Licence.Key in SPARK folder. 

• Launch the Spark GUI by double clicking on the spark.mac file in the SPARK folder. 


 Linux
 ------
 For installing ZUG in Linux 
 
• Download installZug.jar from Automature website (www.automature.com) and save it in a preferred location. 

• To run the installer, type the following at the command prompt.  
           sudo java -jar installZug.jar

• The installation welcome screen shows up. The installation setup screens will guide you and through the installation. On clicking Next, the ZUG Setup Installation screens will guide you step by step through the installation procedure.Once finished, select Done.

• The default installation folder is /usr/lib/Automature.

• Copy and paste the Zug.Licence.Key in Zug folder. 
 
• Launch the Zug GUI by typing zug -gui at the command prompt. 
            
             
New Feature:
Zug3.0 version now can call the external java jar files as builtin atoms through chur spreadsheet. To make it happen the ZugINI.xml parsing is need. Inside the Zug installation directory the ZugINI.xml file can be edited to by putting the values in the tags of 
<inprocesspackages>
	  <inprocesspackage name="" language="">
	      <file-path></file-path>
	      <jar-package></jar-package>
	      <class-name></class-name>
	</inprocesspackage>
</inprocesspackages>
if no value is written then it will show a warning message while running the ZUG automation.
this xml configuration is in ZUG installation folder. ZugINI.xml

Example:
<inprocesspackages>
	  <inprocesspackage name="Zbrowser" language="java">
	      <file-path>C:\Programfiles\Automature\ZUOZ\Inprocess</file-path>
	      <jar-package>com.automature.zuoz.inprocess.zbrowser</jar-package>
	      <class-name>BrowserOperations</class-name>
	</inprocesspackage>             
</inprocesspackages>
             
             http://attic.automature.com/redmine/boards/3/topics/show/6 for ZUG Release Notes
             
Zug Dependencies:
  
  • Java(TM) SE Runtime Environment(32bit) 1.6 (update 21). Zug only runs in 32bit version of Java Runtime Enviorment. It dont support the 64bit JRE.
  
  • To work with In-process atoms in Chur. ZugINI.xml must be configured. The xml tags.
      
      <inprocesspackage name="">
	      <file-path></file-path>
	      <jar-package></jar-package>
	      <class-name></class-name>
	    </inprocesspackage>
	
	under root//inprocesspackages tag. In the builtinpackage attribute 'name', provide the namespace with which the In-process atoms will be called.
  
  file-path: The direcrtoty path(fully qualified) of the jar file and the lib files necessary for it.
  jar-package: The package architecture of the class file where the in process atoms are written as methods.
  class-name: The class name of the java class file where the methods are defined for automation. In Chur they are called atoms with the namespaces.

 remove if only if you installed Zuoz3.0 or above after Zug installation other wise you ignore this message, It will run the testcases containg external atoms.
   
  •  sqlitejdbc-v056.jar file needed to be copied to current JRE lib/ext folder as it is installed. like - C:\Program Files (x86)\Java\jre6\lib\ext
     Now the installer copies this file to the specific location. If due to any permission reason it fails please copy it manually.
   
  
  
	
            