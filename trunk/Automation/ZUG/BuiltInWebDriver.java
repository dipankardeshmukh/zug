/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ZUG;

import java.lang.reflect.Method;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import logs.Log;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.Select;

/**
 *
 * @author Sankho
 */
public class BuiltInWebDriver {
//Map of Webdrivers with the Context variable and WebDriver object

    private static HashMap<String, WebDriver> wdrivermap = new HashMap<String, WebDriver>();
    //String to Containing the WindowHandle
    private static String WindowHandle;
    private boolean method_found_flag;
//Method for calling actual function

    public void BuiltInWebDriverMethod(String method_name, ArrayList<String> inputs) throws Exception {
        try {
            //String method_name = inputs.get(0);
            //printMessage("The Method name-"+method_name);
            //inputs.remove(0);
            Class iClass = Class.forName("ZUG.BuiltInWebDriver");
            //printMessage("The Class is found");
            Method[] iMethods = iClass.getMethods();
            Object iObject = iClass.newInstance();
            Object iParams[] = inputs.toArray();

            method_found_flag=false;
            for (Method lMethod : iMethods) {

                if (lMethod.getName().equalsIgnoreCase(method_name)) {
                    //printMessage("Methods found\t"+lMethod);
                    lMethod.invoke(iObject, iParams);
                    method_found_flag=true;
break;
                } 
            }

            if(method_found_flag==false)
            {
                 Log.Error("ZUG/BultInWebDriver:: Atom Not Found-" + inputs);
throw new AtomNotFoundException();
            }
//            if (inputs.get(0).equalsIgnoreCase("openbrowserwithurl")) {
//                openBrowserWithUrl(inputs.get(1), inputs.get(2), inputs.get(3));
//
//            } else if (inputs.get(0).equalsIgnoreCase("settextbyname")) {
//                setTextByName(inputs.get(1), inputs.get(2), inputs.get(3));
//            } else if (inputs.get(0).equalsIgnoreCase("clickbuttonbyname")) {
//                clickButtonByName(inputs.get(1), inputs.get(2));
//            } else if (inputs.get(0).equalsIgnoreCase("closebrowser")) {
//                closeBrowser(inputs.get(1));
//            } else {
//                Log.Error("Method Not Found");
//            }



        } catch (Exception r) {
           
            throw r;
        }


    }

    public void initialize(String contextvariable, String browser) throws Exception {
        if (browser.equalsIgnoreCase("firefox")) {
            //Initiating WebDriver Object
            WebDriver firefoxdriver=new FirefoxDriver();
             //printMessage("Firefox is the Browser");
            Log.Debug("ZUG/ZUG/BuiltInWebDriver::Firefox is the Browser");
            WindowHandle = firefoxdriver.getWindowHandle();
            ContextVar.setContextVar(contextvariable, WindowHandle);
            //putting the Webdriver object in HashMap with ContextVariable
            wdrivermap.put(WindowHandle, firefoxdriver);
           
            Log.Debug("ZUG/BuiltInWebDriver::Sqltie entry Done");
        } else if (browser.equalsIgnoreCase("chrome")) {
            //Initiating WebDriver Object
            WebDriver chromedriver=new ChromeDriver();
            // printMessage("chrome is the Browser");
            Log.Debug("ZUG/BuiltInWebDriver::Chrome is the Browser");
            WindowHandle = chromedriver.getWindowHandle();
            ContextVar.setContextVar(contextvariable, WindowHandle);
           //putting the Webdriver object in HashMap with ContextVariable
            wdrivermap.put(WindowHandle, chromedriver);
            Log.Debug("ZUG/BuiltInWebDriver::Sqltie entry Done");
        } else if (browser.equalsIgnoreCase("ie")) {
            //Initiating WebDriver Object
            WebDriver iedriver=new InternetExplorerDriver();
            // printMessage("ie is the Browser");
            Log.Debug("ZUG/BuiltInWebDriver::Internet Explorer is the Browser");
            WindowHandle = iedriver.getWindowHandle();
            ContextVar.setContextVar(contextvariable, WindowHandle);
            //putting the Webdriver object in HashMap with ContextVariable
            wdrivermap.put(WindowHandle,iedriver);
            Log.Debug("ZUG/BuiltInWebDriver::Sqltie entry Done");
        } else if (browser.equalsIgnoreCase("htmlunit")) {
            //Initiating WebDriver Object
            WebDriver htmlunitdriver=new HtmlUnitDriver();
           Log.Debug("ZUG/BuiltInWebDriver::Browser Less Automation");
            WindowHandle = htmlunitdriver.getWindowHandle();
            ContextVar.setContextVar(contextvariable, WindowHandle);
          //putting the Webdriver object in HashMap with ContextVariable
            wdrivermap.put(WindowHandle, htmlunitdriver);

            Log.Debug("ZUG/BuiltInWebDriver::Sqltie entry Done");
        } else {
            // printMessage("Browser not found");
            Log.Error("ZUG/BuiltInWebDriver::Browser Not Found");
            throw new Exception();

        }

    }

    /*
     * function to get the actual value of ContextVariable with a given name
     * @param ContextVariable name from Chur sheet
     */
    private static String getValue(String contextvariable) {
        try {

            //getting the ContextVariable value from sqlite database
            String contextvariablevalue = ContextVar.getContextVar(contextvariable);

            return contextvariablevalue;
        } catch (Exception er) {
            return er.getLocalizedMessage();
        }
    }
    /*
     * function to get the actual value of ContextVariable with a given name
     * @param ContextVariable name from Chur sheet
     */
    private static String getName(String contextvariablevalue) {
        try {

            //getting the ContextVariable value from sqlite database
            String contextvariablename = ContextVar.getContextVarName(contextvariablevalue);

            return contextvariablename;
        } catch (Exception er) {
            return er.getLocalizedMessage();
        }
    }
    /*
     * Checking a input string can be a url or not
     * @param url from chur sheet value
     */

    private static String checkUrl(String url) {
        if (url.startsWith("http://") || url.startsWith("HTTP://") || url.contains(":") || url.contains(".")) {

            return url;
        } else {

            return "http://www.automature.com";
        }
    }
    /*
     * Printing values
     * @param object to print
     */

    public static void printMessage(Object obj) {
        System.out.println(obj);

    }
    /*
     * open a browser with a url provided
     * @param browser name,url,context variable
     */
       public static void goToUrl(String window_handle, String url) {
        //Url checking
        try {
            //url = checkUrl(url);
            //printMessage("Url Taken");
            //Opens a Browser with a url passed with specific browser
            // String windowHandle = setBrowser(contextvariable,browser);
            //Log.Debug("ZUG/BuiltInWebDriver::Window Handle->" + windowHandle);
            // printMessage("Window Handlle--" + windowHandle);
            wdrivermap.get(window_handle).get(url);

        } catch (Exception e) {
            Log.Primitive("ZUG/BuiltInWebDriver.goToUrl::URL not found. URL="+url+" Exception->" + e.getLocalizedMessage());
        }


    }

     public void setTextByName (String window_handle, String element, String value) throws Exception{
        //Switching To the Window
        // printMessage("Handle=-" + contextvariable);
        try{
        wdrivermap.get(window_handle).switchTo().window(window_handle);
        }
        catch (Exception e)
        {
            Log.Primitive("ZUG/BuiltInWebDriver.setTextByName-Cannot find browser with the given handle or invalid handle. Handle = "+ window_handle);
            throw e;
        }
        // printMessage("Switching to Handle=-" + contextvariable);
        try{
        // Find the text input element by its name
        WebElement textelement = wdrivermap.get(window_handle).findElement(By.name(element));
        //Set ing the Text in the specified element
        textelement.sendKeys(value);
        }
        catch (Exception e)
        {
                Log.Primitive("ZUG/BuiltInWebDriver.setTextByName-Cannot find text field with the given name or invalid text field name. Test Field Name = "+ element);
              throw e;
        }
    }

    public void setTextById(String window_handle, String element, String value) throws Exception{
        //Switching To the Window
        try{
        wdrivermap.get(window_handle).switchTo().window(window_handle);
        }
        catch (Exception e)
        {
            Log.Primitive("ZUG/BuiltInWebDriver.setTextById-Cannot find browser with the given handle or invalid handle. Handle = "+ window_handle);
            throw e;
        }
        try{
        // Find the text input element by its ID
        WebElement textelement = wdrivermap.get(window_handle).findElement(By.id(element) );
        //Set ing the Text in the specified element
        textelement.sendKeys(value);
        }
        catch (Exception e)
        {
                Log.Primitive("ZUG/BuiltInWebDriver.setTextById-Cannot find text field with the given test field identifier or invalid text field identifier. Test Field identifier = "+ element);

		throw e;
        }
    }


    public void clickButtonByName(String window_handle, String element) throws Exception{
        //Switching To the Window
	try{
        wdrivermap.get(window_handle).switchTo().window(window_handle);
	}
	catch (Exception e)
	{
            Log.Primitive("ZUG/BuiltInWebDriver.clickButtonByName-Cannot find browser with the given handle or invalid handle. Handle = "+ window_handle);

	    throw e;
	}

	try{
        WebElement btnelement = wdrivermap.get(window_handle).findElement(By.name(element));
//Button clicked
        btnelement.click();
	 }
	 catch (Exception e)
         {
	      Log.Primitive("ZUG/BuiltInWebDriver.clickButtonByName-Cannot find button with the given button name or invalid button name. Button Name = "+ element);

	      throw e;
	 }
    }


   public void clickButtonById(String window_handle, String element) throws Exception{
        //Switching To the Window
	try{
        wdrivermap.get(window_handle).switchTo().window(window_handle);
	}
	catch (Exception e)
	{
            Log.Primitive("ZUG/BuiltInWebDriver.clickButtonById-Cannot find browser with the given handle or invalid handle. Handle = "+ window_handle);

	    throw e;
	}

	try{
        WebElement btnelement = wdrivermap.get(window_handle).findElement(By.id(element));
//Button clicked
        btnelement.click();
	 }
	 catch (Exception e)
         {
	      Log.Primitive("ZUG/BuiltInWebDriver.clickButtonById-Cannot find button with the given button identifier or invalid button Identifier. Button Identifier = "+ element);

	      throw e;
	 }
    }

public void clickButtonByClassName(String window_handle, String element) throws Exception{
        //Switching To the Window
	try{
        wdrivermap.get(window_handle).switchTo().window(window_handle);
	}
	catch (Exception e)
	{
            Log.Primitive("ZUG/BuiltInWebDriver.clickButtonByClassName-Cannot find browser with the given handle or invalid handle. Handle = "+ window_handle);

	    throw e;
	}

	try{
        WebElement btnelement = wdrivermap.get(window_handle).findElement(By.className(element));
//Button clicked
        btnelement.click();
	 }
	 catch (Exception e)
         {
	      Log.Primitive("ZUG/BuiltInWebDriver.clickButtonByClassName-Cannot find button with the given button class name or invalid button class anme. Button Class Name = "+ element);

	      throw e;
	 }
    }

    public void closeBrowser(String window_handle) throws Exception{
        //Switching To the Window
        	try{
        wdrivermap.get(window_handle).switchTo().window(window_handle);
	}
	catch (Exception e)
	{
            Log.Primitive("ZUG/BuiltInWebDriver.closeBrowser-Cannot find browser with the given handle or invalid handle. Handle = "+ window_handle);

	    throw e;
	}

        //Closing Driver
        wdrivermap.get(window_handle).quit();
    }


    public void setMultipleTextById(String window_handle, String elements, String values) throws Exception{
            //Switching To the Window
	try{
        wdrivermap.get(window_handle).switchTo().window(window_handle);
	}
	catch (Exception e)
	{
            Log.Primitive("ZUG/BuiltInWebDriver.setMultipleTextById-Cannot find browser with the given handle or invalid handle. Handle = "+ window_handle);

	    throw e;
	}

        String[] arrElements=elements.split(":");
        String[] arrValues=values.split(":");
        if(arrElements.length != arrValues.length)
        {
            Log.Primitive("ZUG/BuiltInWebDriver.setMultipleTextById-Unequal number of elements and values present in the list");
        }
        for(int i=0;i<arrElements.length;i++)
        {
            try{
            WebElement textelement = wdrivermap.get(window_handle).findElement(By.id(arrElements[i]));
            //Set ing the Text in the specified element
            textelement.sendKeys(arrValues[i]);
                }
            catch (Exception e)
                {
                Log.Primitive("ZUG/BuiltInWebDriver.setMultipleTextById-Cannot find text field with the given test field identifier or invalid text field identifier. Test Field identifier = "+ arrElements[i]);
                throw e;
                }
        }
    }

    public void setMultipleTextByName(String window_handle, String elements, String values)throws Exception{
            //Switching To the Window
	try{
        wdrivermap.get(window_handle).switchTo().window(window_handle);
	}
	catch (Exception e)
	{
            Log.Primitive("ZUG/BuiltInWebDriver.setMultipleTextByName-Cannot find browser with the given handle or invalid handle. Handle = "+ window_handle);

	    throw e;
	}
        //System.out.println("Hello");
        String[] arrElements=elements.split(":");
        String[] arrValues=values.split(":");
        if(arrElements.length != arrValues.length)
        {
            Log.Primitive("ZUG/BuiltInWebDriver.setMultipleTextByName-Unequal number of elements and values present in the list");
            throw new Exception();
        }
        for(int i=0;i<arrElements.length;i++)
        {
            try{
            WebElement textelement = wdrivermap.get(window_handle).findElement(By.name(arrElements[i]));
            //Set ing the Text in the specified element
            textelement.sendKeys(arrValues[i]);
                }
            catch (Exception e)
                {
                Log.Primitive("ZUG/BuiltInWebDriver.setMultipleTextByName-Cannot find text field with the given test field name or invalid text field name. Test Field Name = "+ arrElements[i]);
                throw e;
              }
        }
    }

    public void setMultipleFieldTypesByName(String window_handle, String elements, String values)throws Exception{
            //Switching To the Window
	try{
        wdrivermap.get(window_handle).switchTo().window(window_handle);
	}
	catch (Exception e)
	{
            Log.Primitive("ZUG/BuiltInWebDriver.setMultipleFieldTypesByName-Cannot find browser with the given handle or invalid handle. Handle = "+ window_handle);

	    throw e;
	}
        
        String[] arrElements=elements.split(":");
        String[] arrValues=values.split(":");
        if(arrElements.length != arrValues.length)
        {
            Log.Primitive("ZUG/BuiltInWebDriver.setMultipleFieldTypesByName-Unequal number of elements and values present in the list");
            throw new Exception();
        }
        for(int i=0;i<arrElements.length;i++)
        {
            try{
            
            WebElement textelement = wdrivermap.get(window_handle).findElement(By.name(arrElements[i]));
            //Set ing the Text in the specified element
            textelement.sendKeys(arrValues[i]);
                }
            catch (Exception e)
                {
                Log.Primitive("ZUG/BuiltInWebDriver.setMultipleFieldTypesByName-Cannot find text field with the given test field name or invalid text field name. Test Field Name = "+ arrElements[i]);
                throw e;
                }
        }
    }


    public void clickOkButtonInAlert(String window_handle) throws Exception{
        //Switching To the Window
try{
    

        Alert alert=wdrivermap.get(window_handle).switchTo().alert();
        alert.getText();
        alert.accept();
} catch (WebDriverException e)
{
    Log.PrimitiveErrors("ZUG/BuiltInWebDriver.clickOkButtonInAlert Error");
    throw e;
    
}
    }
    public void clickCancelButtonInAlert(String window_handle) throws Exception{
        //Switching To the Window
try{


        Alert alert=wdrivermap.get(window_handle).switchTo().alert();
        alert.getText();
        alert.dismiss();
} catch (WebDriverException e)
{
    Log.PrimitiveErrors("ZUG/BuiltInWebDriver.clickCancelButtonInAlert Error");
    throw e;

}
    }

    public void matchTextByClassName(String window_handle, String class_name, String text, String itterate) throws Exception{
        //Switching To the Window
        try{
        wdrivermap.get(window_handle).switchTo().window(window_handle);

	}
	catch (Exception e)
	{
            Log.Primitive("ZUG/BuiltInWebDriver.matchTextByClassName-Cannot find browser with the given handle or invalid handle. Handle = "+ window_handle);
            throw e;
        }
        try{

 //System.out.println("hello");
            boolean flag=true;
            //while(flag)
            // System.out.println("String value"+val);
            int value=Integer.parseInt(itterate);
 //System.out.println("Int value"+value);
            for(int i=0;i<value && flag;i++)
            {
                List<WebElement> elementList = wdrivermap.get(window_handle).findElements(By.className(class_name));
                //System.out.println(i);
                Iterator<WebElement> WebelementIterated=elementList.iterator();
                while(WebelementIterated.hasNext())
                {
                    String elementname=WebelementIterated.next().getText();
                    //System.out.println(elementname);
                    if(elementname.equalsIgnoreCase(text))
                    {
                      flag=false;
                    }
                
                }
               wdrivermap.get(window_handle).switchTo().window(window_handle).navigate().refresh();
            //printMessage(wdrivermap.get(window_handle).getWindowHandle());
            }
            if (flag)
            {
                Log.Primitive("ZUG/BuiltInWebDriver.matchTextByClassName-Cannot find required text in the given element. Class Name = "+ class_name);
                throw new Exception();
            }
        }
        catch (Exception e)
        {
            Log.Primitive("ZUG/BuiltInWebDriver.matchTextByClassName-Cannot find element with the given class name o invalid class name. Class Name = "+ class_name);
            throw e;
        }
    }

    public void selectFromDropDownByValue(String window_handle, String element, String value) throws Exception{
        //Switching To the Window
        	try{
        wdrivermap.get(window_handle).switchTo().window(window_handle);
	}
	catch (Exception e)
	{
            Log.Primitive("ZUG/BuiltInWebDriver.selectFromDropDown-Cannot find browser with the given handle or invalid handle. Handle = "+ window_handle);

	    throw e;
	}
        try{
        WebElement textelement = wdrivermap.get(window_handle).findElement(By.name(element));
        //System.out.println("Welcome"+textelement.getText());
        //System.out.println("Page title is: " + wdrivermap.get(window_handle).getTitle());
        Select select_element=new Select(textelement);
       // System.out.println("Page title is: " + dd.selectByVisibleText(value));
        select_element.selectByVisibleText(value);
        }
        catch(Exception e)
        {
        Log.Primitive("ZUG/BuiltInWebDriver.selectFromDropDown-Cannot find dropdown with the given dropdown name. DropDown Name="+element);
        throw e;
        }
    }

    public void fileUpload(String window_handle, String element, String value) throws Exception{
     //Switching To the Window
        // printMessage("Handle=-" + contextvariable);
        try{
        wdrivermap.get(window_handle).switchTo().window(window_handle);
        }
        catch (Exception e)
        {
            Log.Primitive("ZUG/BuiltInWebDriver.fileUpload-Cannot find browser with the given handle or invalid handle. Handle = "+ window_handle);
            throw e;
        }
        // printMessage("Switching to Handle=-" + contextvariable);
        try{
        // Find the text input element by its name
        WebElement textelement = wdrivermap.get(window_handle).findElement(By.name(element));
        //Set ing the Text in the specified element
        textelement.sendKeys(value);
        }
        catch (Exception e)
        {
                Log.Primitive("ZUG/BuiltInWebDriver.fileUpload-Cannot find text field with the given name or invalid text field name. Test Field Name = "+ element);
              throw e;
        }
}
}

class AtomNotFoundException extends Exception

{

    public AtomNotFoundException() {

        System.out.println("AtomNotFoundException Occured");
    throw new WebDriverException();
    
    }
    
    
    
}
