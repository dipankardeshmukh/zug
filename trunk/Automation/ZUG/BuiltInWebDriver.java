/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ZUG;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
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
import java.util.Set;
import java.util.concurrent.TimeUnit;
import logs.Log;
import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.Select;

// @author Sankho
class WebDriverMap {

    protected static HashMap<String, WebDriver> wdrivermap = new HashMap<String, WebDriver>();
    protected static HashMap<String, ArrayList<String>> wdriver_children = new HashMap<String, ArrayList<String>>();
    //String to Containing the WindowHandle
    protected static String WindowHandle;

    protected WebDriver getWebDriverObject(String window_handle) throws Exception {
        //System.out.println("getWebDriverObject"+window_handle);
        Set keyset1 = wdriver_children.keySet();
        String braces_handle = "";
        //System.out.println("getWebDriverObject::original list  :"+keyset1);
        //System.out.println("getWebDriverObject::original handle  :"+window_handle);
        if (wdrivermap.containsKey(window_handle)) {
            //System.out.println("getWebDriverObject::object Found in original list  :"+window_handle);
            WebDriver reqOb = null;
            reqOb = wdrivermap.get(window_handle);
            // System.out.println("Name of the Class->"+reqOb.getClass().getName());

            if (reqOb.getClass().getName().equalsIgnoreCase("org.openqa.selenium.firefox.FirefoxDriver")) {
                braces_handle = "{" + window_handle + "}";
            } else {
                braces_handle = window_handle;
            }
            return (wdrivermap.get(window_handle).switchTo().window(braces_handle));
        } else {
            Set keyset = wdriver_children.keySet();
            Boolean found_obj = false;
            WebDriver reqdobj = null;
            for (Iterator<String> it = keyset.iterator(); it.hasNext();) {
                String key = it.next();
                //System.out.println("Itterating through Keys  :"+key);
                ArrayList objArr = wdriver_children.get(key);
                if (objArr.contains(window_handle)) {
                    reqdobj = wdrivermap.get(key);
                    if (reqdobj.getClass().getName().equalsIgnoreCase("org.openqa.selenium.firefox.FirefoxDriver")) {
                        window_handle = "{" + window_handle + "}";

                    }
                    try {
                        reqdobj.switchTo().window(window_handle);
                    } catch (Exception e) {
                        throw new Exception("Cannot attach handle to the Window. Exception:: " + e.getMessage());
                    }
                    found_obj = true;
                    break;
                }
            }
            if (found_obj) {
                Log.Primitive("ZUG/WebDriverMap.Child Window Handle found");
                //System.out.println("getWebDriverObject::object Found in secondary list  :"+window_handle);

                return (reqdobj);
            } else {
                Log.Primitive("ZUG/WebDriverMap.Window Handle not found in list- Window Handle : " + window_handle );
                Log.Error("ZUG/WebDriverMap.Window Handle not found in list- Window Handle : " + window_handle);
                throw new Exception();
            }
        }
    }

    public void putWindowHandle(String key_handle, String new_handle) throws Exception {

        if (new_handle.startsWith("{") && new_handle.endsWith("}")) {
            new_handle = new_handle.substring(1, new_handle.length() - 1);
        }
        Set keyset = wdriver_children.keySet();
        Boolean found_key = false;
        //System.out.println("putWindowHandle");

//System.out.println("oldhandle:  "+key_handle+"  new Handle:  "+new_handle);
        try {
            for (Iterator<String> it = keyset.iterator(); it.hasNext();) {
                String handle_string = it.next();
                // System.out.println("putWindowHandle  :: "+handle_string);
                if (handle_string.equals(key_handle)) {
                    found_key = true;
                    // System.out.println("putWindowHandle:  found key");
                    ArrayList objArr = wdriver_children.get(handle_string);
                    //System.out.println("putWindowHandle: Array found"+objArr);
                    if (!objArr.contains(new_handle)) {
                        //System.out.println("Set in array");
                        objArr.add(new_handle);
                        Log.Primitive("WebDriverMap.Handle added to list- Window Handle : new_handle");
                    }
                }
            }
        } catch (Exception e) {
            Log.Primitive("WebDriverMap.Problem adding new element in Hash list : " + key_handle+".  Exception:: " + e.getMessage());
            Log.Error("WebDriverMap.Problem adding new element in Hash list : " + key_handle + ".  Exception:: " + e.getMessage());
            throw e;
        }
        if (!found_key) {
            Log.Primitive("ZUG/WebDriverMap.Key Element not found.- Key Window Handle : " + key_handle);
            Log.Error("ZUG/WebDriverMap.Key Element not found.- Key Window Handle : " + key_handle);
            throw new Exception();
        }
    }

    public void initialize(String contextvariable, String browser) throws Exception {

        if (browser.equalsIgnoreCase("firefox")) {
            //Initiating WebDriver Object
            WebDriver firefoxdriver=null;
            try{
                firefoxdriver = new FirefoxDriver();
            }
            catch(Exception e){                
                Log.Primitive("WebDriverMap.initialize-Exception while creating FirefoxDriver object. Exception:: "+ e.getMessage());
                Log.Error("WebDriverMap.initialize-Exception while creating FirefoxDriver object. Exception:: "+ e.getMessage());
            }            
            WindowHandle = firefoxdriver.getWindowHandle();            
            if (WindowHandle.startsWith("{") && WindowHandle.endsWith("}")) {
                WindowHandle = WindowHandle.substring(1, WindowHandle.length() - 1);                
            }
            ContextVar.setContextVar(contextvariable, WindowHandle);
            //putting the Webdriver object in HashMap with ContextVariable
            wdrivermap.put(WindowHandle, firefoxdriver);
            wdriver_children.put(WindowHandle, new ArrayList<String>());
            Log.Primitive("WebDriverMap.Firefox Browser");
            
        } else if (browser.equalsIgnoreCase("chrome")) {
            //Initiating WebDriver Object
            WebDriver chromedriver=null;
            try{
                System.setProperty("webdriver.chrome.driver", "/home/automature/Downloads/chromedriver");                
                chromedriver = new ChromeDriver();                
            }
            catch(Exception e){                
                Log.Primitive("WebDriverMap.initialize-Exception while creating ChromeDriver object. Exception:: "+ e.getMessage());
                Log.Error("WebDriverMap.initialize-Exception while creating ChromeDriver object. Exception:: "+ e.getMessage());
            }            
            WindowHandle = chromedriver.getWindowHandle();
            ContextVar.setContextVar(contextvariable, WindowHandle);
            //putting the Webdriver object in HashMap with ContextVariable
            wdrivermap.put(WindowHandle, chromedriver);
            wdriver_children.put(WindowHandle, null);            
            Log.Primitive("WebDriverMap.Chrome Browser");
        } else if (browser.equalsIgnoreCase("ie")) {
            //Initiating WebDriver Object
            WebDriver iedriver=null;
            try{
                iedriver = new InternetExplorerDriver();
            }
                catch(Exception e){                                
                Log.Primitive("WebDriverMap.initialize-Exception while creating IEDriver object. Exception:: "+ e.getMessage());
                Log.Error("WebDriverMap.initialize-Exception while creating IEDriver object. Exception:: "+ e.getMessage());
            }// printMessage("ie is the Browser");           
            WindowHandle = iedriver.getWindowHandle();
            if (WindowHandle.startsWith("{") && WindowHandle.endsWith("}")) {
                WindowHandle = WindowHandle.substring(1, WindowHandle.length() - 1);                
            }
            ContextVar.setContextVar(contextvariable, WindowHandle);
            //putting the Webdriver object in HashMap with ContextVariable
            wdrivermap.put(WindowHandle, iedriver);
            wdriver_children.put(WindowHandle, null);
            Log.Primitive("WebDriverMap.IE Browser");
        } else if (browser.equalsIgnoreCase("htmlunit")) {
            //Initiating WebDriver Object
            HtmlUnitDriver htmlunitdriver=null;
            try{
                htmlunitdriver = new HtmlUnitDriver(true);
                htmlunitdriver.setJavascriptEnabled(true);
                WebClient hh = new WebClient();
            }
            catch(Exception e){                               
                Log.Primitive("WebDriverMap.initialize-Exception while creating HtmlUnitDriver object. Exception:: "+ e.getMessage());
                Log.Error("WebDriverMap.initialize-Exception while creating HtmlUnitDriver object. Exception:: "+ e.getMessage());
            }
            WindowHandle = htmlunitdriver.getWindowHandle();
            ContextVar.setContextVar(contextvariable, WindowHandle);
            //putting the Webdriver object in HashMap with ContextVariable
            wdrivermap.put(WindowHandle, htmlunitdriver);
            wdriver_children.put(WindowHandle, null);            
            Log.Primitive("WebDriverMap.html unit");
        } else {            
            Log.Error("WebDriverMap::Browser Not Found");
            Log.Primitive("WebDriverMap::Browser Not Found");
            throw new Exception();
        }
    }

    /*
     * function to get the actual value of ContextVariable with a given name
     * @param ContextVariable name from Chur sheet
     */
    public static String getValue(String contextvariable) {
        try {

            //getting the ContextVariable value from sqlite database
            String contextvariablevalue = ContextVar.getContextVar(contextvariable);

            return contextvariablevalue;
        } catch (Exception er) {
            return er.getMessage();
        }
    }
    /*
     * function to get the actual value of ContextVariable with a given name
     * @param ContextVariable name from Chur sheet
     */

    public static String getName(String contextvariablevalue) {
        try {

            //getting the ContextVariable value from sqlite database
            String contextvariablename = ContextVar.getContextVarName(contextvariablevalue);

            return contextvariablename;
        } catch (Exception er) {
            return er.getMessage();
        }
    }
    /*
     * Checking a input string can be a url or not
     * @param url from chur sheet value
     */

    public static String checkUrl(String url) {
        if (url.startsWith("http://") || url.startsWith("HTTP://") || url.contains(":") || url.contains(".")) {

            return url;
        } else {

            return "http://www.automature.com";
        }
    }
}

public class BuiltInWebDriver extends WebDriverMap {

    private boolean method_found_flag;

    public synchronized void BuiltInWebDriverMethod(String method_name, ArrayList<String> inputs) throws Exception {
        try {
            //String method_name = inputs.get(0);
            //printMessage("The Method name-"+method_name);
            //inputs.remove(0);
            Class iClass = Class.forName("ZUG.BuiltInWebDriver");
            //printMessage("The Class is found");
            Method[] iMethods = iClass.getMethods();
            Object iObject = iClass.newInstance();
            Object iParams[] = inputs.toArray();
            method_found_flag = false;
            for (Method lMethod : iMethods) {

                if (lMethod.getName().equalsIgnoreCase(method_name)) {
                    //printMessage("Methods found\t"+lMethod);
                    lMethod.invoke(iObject, iParams);
                    method_found_flag = true;
                    break;
                }
            }
            if (method_found_flag == false) {
                Log.Error("WebDriverMap.BuiltInWebDriverMethod- Atom Not Found-" + inputs);
                Log.Primitive("WebDriverMap.BuiltInWebDriverMethod- Atom Not Found" + inputs);
                throw new AtomNotFoundException();
            }

        } catch (Exception r) {
            Log.Error("WebDriverMap.BuiltInWebDriverMethod- Error Occured-" + method_name + inputs + ". Exception:: "+r.getMessage());
            Log.Primitive("WebDriverMap.BuiltInWebDriverMethod- Error Occured-" + method_name + inputs + ". Exception:: "+r.getMessage());
            throw r;
        }


    }

    /*
     * Printing values
     * @param object to print
     */
    public void printMessage(Object obj) {
        System.out.println(obj);

    }
    /*
     * open a browser with a url provided
     * @param browser name,url,context variable
     */

    public synchronized void goToUrl(String window_handle, String url) {
        //Url checking
        WebDriver window_object = null;
        try {
            window_object = getWebDriverObject(window_handle);
        } catch (Exception e) {
            Log.Primitive("BuiltInWebDriver.goToUrl-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle + ". Exception:: "+e.getMessage());
            Log.Error("BuiltInWebDriver.goToUrl-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle +". Exception:: "+e.getMessage());
        }
        try {
            //System.out.println("Inside gotoURL");
            window_object.get(url);

        } catch (Exception e) {
            Log.Primitive("BuiltInWebDriver.goToUrl-URL not found. URL=" + url + ". Exception:: " + e.getMessage());
            Log.Error("BuiltInWebDriver.goToUrl-URL not found. URL=" + url + ". Exception:: " + e.getMessage());
        }


    }

    public synchronized void setTextByName(String window_handle, String element, String value) throws Exception {
        //Switching To the Window
        WebDriver window_object;
        try {
            window_object = getWebDriverObject(window_handle);
        } catch (Exception e) {
            Log.Primitive("BuiltInWebDriver.setTextByName-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle+ ". Exception:: " + e.getMessage());
            Log.Error("BuiltInWebDriver.setTextByName-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle+ ". Exception:: " + e.getMessage());
            throw e;
        }
        try {
            WebElement textelement = window_object.findElement(By.name(element));
            //Set ing the Text in the specified element
            textelement.sendKeys(value);
        } catch (Exception e) {
            Log.Primitive("BuiltInWebDriver.setTextByName-Cannot find text field with the given name or invalid text field name. Test Field Name = " + element + ". Exception:: " + e.getMessage());
            Log.Error("BuiltInWebDriver.setTextByName-Cannot find text field with the given name or invalid text field name. Test Field Name = " + element + ". Exception:: " + e.getMessage());
            throw e;
        }
    }

    public synchronized void setTextById(String window_handle, String element, String value) throws Exception {
        //Switching To the Window
        WebDriver window_object;
        try {
            window_object = getWebDriverObject(window_handle);
        } catch (Exception e) {
            Log.Primitive("BuiltInWebDriver.setTextById-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle+ ". Exception:: " + e.getMessage());
            Log.Error("BuiltInWebDriver.setTextById-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle+ ". Exception:: " + e.getMessage());
            throw e;
        }
        try {
            // Find the text input element by its ID
            WebElement textelement = window_object.findElement(By.id(element));
            //Set ing the Text in the specified element
            textelement.sendKeys(value);
        } catch (Exception e) {
            Log.Primitive("BuiltInWebDriver.setTextById-Cannot find text field with the given test field identifier or invalid text field identifier. Test Field identifier = " + element + ". Exception:: " + e.getMessage());
            Log.Error("BuiltInWebDriver.setTextById-Cannot find text field with the given test field identifier or invalid text field identifier. Test Field identifier = " + element + ". Exception:: " + e.getMessage());

            throw e;
        }
    }

    public synchronized void clickButtonByName(String window_handle, String element) throws Exception {
        //Switching To the Window
        WebDriver window_object;
        try {
            window_object = getWebDriverObject(window_handle);
        } catch (Exception e) {
            Log.Primitive("BuiltInWebDriver.clickButtonByName-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle + ". Exception:: " + e.getMessage());
            Log.Error("BuiltInWebDriver.clickButtonByName-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle + ". Exception:: " + e.getMessage());
            throw e;
        }

        try {
            WebElement btnelement = window_object.findElement(By.name(element));
//Button clicked
            btnelement.click();
        } catch (Exception e) {
            Log.Primitive("BuiltInWebDriver.clickButtonByName-Cannot find button with the given button name or invalid button name. Button Name = " + element + ". Exception:: " + e.getMessage());
            Log.Error("BuiltInWebDriver.clickButtonByName-Cannot find button with the given button name or invalid button name. Button Name = " + element + ". Exception:: " + e.getMessage());
            throw e;
        }
    }

    public synchronized void clickButtonById(String window_handle, String element) throws Exception {
        //Switching To the Window
        WebDriver window_object;
        try {
            window_object = getWebDriverObject(window_handle);
        } catch (Exception e) {
            Log.Primitive("BuiltInWebDriver.clickButtonById-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle + ". Exception:: " + e.getMessage());
            Log.Error("BuiltInWebDriver.clickButtonById-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle + ". Exception:: " + e.getMessage());
            throw e;
        }

        try {
            WebElement btnelement = window_object.findElement(By.id(element));
//Button clicked
            btnelement.click();
        } catch (Exception e) {
            Log.Primitive("BuiltInWebDriver.clickButtonById-Cannot find button with the given button identifier or invalid button Identifier. Button Identifier = " + element + ". Exception:: " + e.getMessage());
            Log.Error("BuiltInWebDriver.clickButtonById-Cannot find button with the given button identifier or invalid button Identifier. Button Identifier = " + element + ". Exception:: " + e.getMessage());
            throw e;
        }
    }

    public synchronized void clickButtonByClassName(String window_handle, String element) throws Exception {
        //Switching To the Window
        WebDriver window_object;
        try {
            window_object = getWebDriverObject(window_handle);
        } catch (Exception e) {
            Log.Primitive("BuiltInWebDriver.clickButtonByClassName-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle + ". Exception:: " + e.getMessage());
            Log.Error("BuiltInWebDriver.clickButtonByClassName-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle + ". Exception:: " + e.getMessage());
            throw e;
        }

        try {
            WebElement btnelement = window_object.findElement(By.className(element));
//Button clicked
            btnelement.click();
        } catch (Exception e) {
            Log.Primitive("BuiltInWebDriver.clickButtonByClassName-Cannot find button with the given button class name or invalid button class anme. Button Class Name = " + element + ". Exception:: " + e.getMessage());
            Log.Error("BuiltInWebDriver.clickButtonByClassName-Cannot find button with the given button class name or invalid button class anme. Button Class Name = " + element + ". Exception:: " + e.getMessage());
            throw e;
        }
    }

    public synchronized void closeBrowser(String window_handle) throws Exception {
        //Switching To the Window
        WebDriver window_object = null;
        try {
            window_object = getWebDriverObject(window_handle);
        } catch (Exception e) {
            Log.Primitive("BuiltInWebDriver.closeBrowser-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle + ". Exception:: " + e.getMessage());
            Log.Error("BuiltInWebDriver.closeBrowser-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle + ". Exception:: " + e.getMessage());
            throw e;
        }

        //Closing Driver
        try {
            window_object.quit();
        } catch (Exception e) {
            Log.Primitive("BuiltInWebDriver.closeBrowser-Cannot close browser " + ". Exception:: " + e.getMessage());
            Log.Error("BuiltInWebDriver.closeBrowser-Cannot close browse" + ". Exception:: " + e.getMessage());
            throw e;
        }
    }

    public synchronized void setMultipleTextById(String window_handle, String elements, String values) throws Exception {
        //Switching To the Window
        WebDriver window_object;
        try {
            window_object = getWebDriverObject(window_handle);
        } catch (Exception e) {
            Log.Primitive("BuiltInWebDriver.setMultipleTextById-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle + ". Exception:: " + e.getMessage());
            Log.Error("BuiltInWebDriver.setMultipleTextById-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle + ". Exception:: " + e.getMessage());
            throw e;
        }

        String[] arrElements = elements.split(":");
        String[] arrValues = values.split(":");
        if (arrElements.length != arrValues.length) {
            Log.Primitive("BuiltInWebDriver.setMultipleTextById-Unequal number of elements and values present in the list");
            Log.Error("BuiltInWebDriver.setMultipleTextById-Unequal number of elements and values present in the list");
            throw new Exception();
        }
        for (int i = 0; i < arrElements.length; i++) {
            try {
                WebElement textelement = window_object.findElement(By.id(arrElements[i]));
                //Set ing the Text in the specified element
                textelement.sendKeys(arrValues[i]);
            } catch (Exception e) {
                Log.Primitive("BuiltInWebDriver.setMultipleTextById-Cannot find text field with the given test field identifier or invalid text field identifier. Test Field identifier = " + arrElements[i] + ". Exception:: " + e.getMessage());
                Log.Error("BuiltInWebDriver.setMultipleTextById-Cannot find text field with the given test field identifier or invalid text field identifier. Test Field identifier = " + arrElements[i] + ". Exception:: " + e.getMessage());
                throw e;
            }
        }
    }

    public synchronized void setMultipleTextByName(String window_handle, String elements, String values) throws Exception {
        //Switching To the Window
        WebDriver window_object;
        try {
            window_object = getWebDriverObject(window_handle);
        } catch (Exception e) {
            Log.Primitive("BuiltInWebDriver.setMultipleTextByName-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle + ". Exception:: " + e.getMessage());
            Log.Error("BuiltInWebDriver.setMultipleTextByName-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle + ". Exception:: " + e.getMessage());
            throw e;
        }
        //System.out.println("Hello");
        String[] arrElements = elements.split(":");
        String[] arrValues = values.split(":");
        if (arrElements.length != arrValues.length) {
            Log.Primitive("BuiltInWebDriver.setMultipleTextByName-Unequal number of elements and values present in the list");
            Log.Error("BuiltInWebDriver.setMultipleTextByName-Unequal number of elements and values present in the list");
            throw new Exception();
        }
        for (int i = 0; i < arrElements.length; i++) {
            try {
                WebElement textelement = window_object.findElement(By.name(arrElements[i]));
                //Set ing the Text in the specified element
                textelement.sendKeys(arrValues[i]);
            } catch (Exception e) {
                Log.Primitive("BuiltInWebDriver.setMultipleTextByName-Cannot find text field with the given test field name or invalid text field name. Test Field Name = " + arrElements[i] + ". Exception:: " + e.getMessage());
                Log.Error("BuiltInWebDriver.setMultipleTextByName-Cannot find text field with the given test field name or invalid text field name. Test Field Name = " + arrElements[i] + ". Exception:: " + e.getMessage());
                throw e;
            }
        }
    }

    public synchronized void setMultipleFieldTypesByName(String window_handle, String elements, String values) throws Exception {
        //Switching To the Window
        WebDriver window_object;
        try {
            window_object = getWebDriverObject(window_handle);
        } catch (Exception e) {
            Log.Primitive("BuiltInWebDriver.setMultipleFieldTypesByName-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle + ". Exception:: " + e.getMessage());
            Log.Error("BuiltInWebDriver.setMultipleFieldTypesByName-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle + ". Exception:: " + e.getMessage());
            throw e;
        }

        String[] arrElements = elements.split(":");
        String[] arrValues = values.split(":");
        if (arrElements.length != arrValues.length) {
            Log.Primitive("BuiltInWebDriver.setMultipleFieldTypesByName-Unequal number of elements and values present in the list");
            Log.Error("BuiltInWebDriver.setMultipleFieldTypesByName-Unequal number of elements and values present in the list");

            throw new Exception();
        }
        for (int i = 0; i < arrElements.length; i++) {
            try {

                WebElement textelement = window_object.findElement(By.name(arrElements[i]));

                if (textelement.getTagName().equalsIgnoreCase("select")) {

                    Select select_element = new Select(textelement);
                    select_element.selectByVisibleText(arrValues[i]);
                    continue;
                }

                //Set ing the Text in the specified element
                textelement.sendKeys(arrValues[i]);
            } catch (Exception e) {
                Log.Primitive("BuiltInWebDriver.setMultipleFieldTypesByName-Cannot find text field with the given test field name or invalid text field name. Test Field Name = " + arrElements[i] + ". Exception:: " + e.getMessage());
                Log.Error("BuiltInWebDriver.setMultipleFieldTypesByName-Cannot find text field with the given test field name or invalid text field name. Test Field Name = " + arrElements[i] + ". Exception:: " + e.getMessage());
                throw e;
            }
        }
    }

        public void acceptPopup(String window_handle) throws Exception {

        //((JavascriptExecutor) getWebDriverObject(window_handle)).executeScript("window.confirm = function(msg){returntrue;};");

        boolean PopupNotFound = true;
        Alert alert = null;
        Integer SecondsElapsed = 0;
        Integer TimeOutSecondsInt = 30;
        int i = 0;
        while (PopupNotFound && SecondsElapsed < TimeOutSecondsInt) {
            try {
                //System.out.println("Inside Accept Popup1");
                alert = getWebDriverObject(window_handle).switchTo().alert();
               // System.out.println("Inside Accept Popup2");
                PopupNotFound = false;
                alert.accept();
            }catch(UnsupportedOperationException uop)
             {
                if(getWebDriverObject(window_handle).getClass().getName().equalsIgnoreCase("org.openqa.selenium.htmlunit.HtmlUnitDriver"))
                PopupNotFound=false;
                else
                {
                   Log.Primitive("BuiltInWebDriver.acceptPopup:: Popup not Supported"+ ". Exception:: " + uop.getMessage());
                   Log.Error("BuiltInWebDriver.acceptPopup:: Popup not Supported"+ ". Exception:: " + uop.getMessage());
                   throw new WebDriverException("BuiltInWebDriver.acceptPopup:: Popup not Supported "+uop.getMessage());
                }

            }

            catch (NoAlertPresentException e) {

                SecondsElapsed++;
                Thread.sleep(1000);
                 //System.out.println("My exception:: "+e.getMessage());
                continue;
            }
            catch(Exception x)
            {
                Log.Primitive("BuiltInWebDriver.acceptPopup:: Popup not found within timeout"+ ". Exception:: " + x.getMessage());
                Log.Error("BuiltInWebDriver.acceptPopup:: Popup not found within timeout"+ ". Exception:: " + x.getMessage());
                throw new WebDriverException("BuiltInWebDriver.acceptPopup:"+x.getMessage());
            }

        }

    }

    public void acceptPopup(String window_handle, String TimeOutSeconds) throws Exception {
        
        //((JavascriptExecutor) getWebDriverObject(window_handle)).executeScript("window.confirm = function(msg){returntrue;};");

        boolean PopupNotFound = true;
        Alert alert = null;
        Integer SecondsElapsed = 0;
        Integer TimeOutSecondsInt = Integer.parseInt(TimeOutSeconds);
        int i = 0;
        while (PopupNotFound && SecondsElapsed < TimeOutSecondsInt) {
            try {
                //System.out.println("Inside Accept Popup1");
                alert = getWebDriverObject(window_handle).switchTo().alert();
               // System.out.println("Inside Accept Popup2");
                PopupNotFound = false;
                alert.accept();
            }catch(UnsupportedOperationException uop)
             {
                if(getWebDriverObject(window_handle).getClass().getName().equalsIgnoreCase("org.openqa.selenium.htmlunit.HtmlUnitDriver"))
                PopupNotFound=false;
                else
                {
                   Log.Primitive("BuiltInWebDriver.acceptPopup:: Popup not Supported"+ ". Exception:: " + uop.getMessage());
                   Log.Error("BuiltInWebDriver.acceptPopup:: Popup not Supported"+ ". Exception:: " + uop.getMessage());
                   throw new WebDriverException("BuiltInWebDriver.acceptPopup:: Popup not Supported "+uop.getMessage());
                }
                
            }

            catch (NoAlertPresentException e) {
                
                SecondsElapsed++;
                Thread.sleep(1000);
                 //System.out.println("My exception:: "+e.getMessage());
                continue;
            }
            catch(Exception x)
            {
                Log.Primitive("BuiltInWebDriver.acceptPopup:: Popup not found within timeout"+ ". Exception:: " + x.getMessage());
                Log.Error("BuiltInWebDriver.acceptPopup:: Popup not found within timeout"+ ". Exception:: " + x.getMessage());
                throw new WebDriverException("BuiltInWebDriver.acceptPopup:"+x.getMessage());
            }

        }
        
    }

        public void getTextInPopup(String window_handle, String ContextVariable_return_popupText) throws Exception {
        //Switching To the Window
        boolean PopupNotFound = true;
        Alert alert = null;
        Integer SecondsElapsed = 0;
        Integer TimeOutSecondsInt = 30;
        while (PopupNotFound && SecondsElapsed < TimeOutSecondsInt) {
            try {

                alert = getWebDriverObject(window_handle).switchTo().alert();

                PopupNotFound = false;
        ContextVar.alterContextVar(ContextVariable_return_popupText, alert.getText());
            }catch(UnsupportedOperationException uop)
            {
                 if(getWebDriverObject(window_handle).getClass().getName().equalsIgnoreCase("org.openqa.selenium.htmlunit.HtmlUnitDriver"))
                PopupNotFound=false;
                else
                {
                   Log.Primitive("BuiltInWebDriver.acceptPopup:: Popup not Supported--"+uop.getMessage());
            Log.Error("BuiltInWebDriver.acceptPopup:: Popup not Supported--"+uop.getMessage());
            throw new WebDriverException("BuiltInWebDriver.acceptPopup:: Popup not Supported "+uop.getMessage());
                }
            }

            catch (NoAlertPresentException noal) {


                SecondsElapsed++;
                Thread.sleep(1000);
                continue;
            }
            catch(Exception ex)
            {
                Log.Primitive("BuiltInWebDriver.getTextInPopup:: Popup not found within timeout"+ ". Exception:: " + ex.getMessage());
            Log.Error("BuiltInWebDriver.getTextInPopup:: Popup not found within timeout"+ ". Exception:: " + ex.getMessage());

                throw new WebDriverException("BuiltInWebDriver.getTextInPopup  "+ex.getMessage());
        }

        }
    }

    public void getTextInPopup(String window_handle, String ContextVariable_return_popupText, String TimeOutSeconds) throws Exception {
        //Switching To the Window
        boolean PopupNotFound = true;
        Alert alert = null;
        Integer SecondsElapsed = 0;
        Integer TimeOutSecondsInt = Integer.parseInt(TimeOutSeconds);
        while (PopupNotFound && SecondsElapsed < TimeOutSecondsInt) {
            try {

                alert = getWebDriverObject(window_handle).switchTo().alert();

                PopupNotFound = false;
        ContextVar.alterContextVar(ContextVariable_return_popupText, alert.getText());
            }catch(UnsupportedOperationException uop)
            {
                 if(getWebDriverObject(window_handle).getClass().getName().equalsIgnoreCase("org.openqa.selenium.htmlunit.HtmlUnitDriver"))
                PopupNotFound=false;
                else
                {
                   Log.Primitive("BuiltInWebDriver.acceptPopup:: Popup not Supported--"+uop.getMessage());
            Log.Error("BuiltInWebDriver.acceptPopup:: Popup not Supported--"+uop.getMessage());
            throw new WebDriverException("BuiltInWebDriver.acceptPopup:: Popup not Supported "+uop.getMessage());
                }
            }

            catch (NoAlertPresentException noal) {


                SecondsElapsed++;
                Thread.sleep(1000);
                continue;
            }
            catch(Exception ex)
            {
                Log.Primitive("BuiltInWebDriver.getTextInPopup:: Popup not found within timeout"+ ". Exception:: " + ex.getMessage());
            Log.Error("BuiltInWebDriver.getTextInPopup:: Popup not found within timeout"+ ". Exception:: " + ex.getMessage());

                throw new WebDriverException("BuiltInWebDriver.getTextInPopup  "+ex.getMessage());
        }
            
        }
    }

        public void declinePopup(String window_handle) throws Exception {
        //Switching To the Window
        boolean PopupNotFound = true;
        Alert alert = null;
        Integer SecondsElapsed = 0;
        Integer TimeOutSecondsInt = 30;
        while (PopupNotFound && SecondsElapsed < TimeOutSecondsInt) {
            try {

                alert = getWebDriverObject(window_handle).switchTo().alert();
                PopupNotFound = false;
alert.dismiss();
            } catch (UnsupportedOperationException uop) {

  if(getWebDriverObject(window_handle).getClass().getName().equalsIgnoreCase("org.openqa.selenium.htmlunit.HtmlUnitDriver"))
                PopupNotFound=false;
                else
                {
                   Log.Primitive("BuiltInWebDriver.acceptPopup:: Popup not Supported--"+uop.getMessage());
            Log.Error("BuiltInWebDriver.acceptPopup:: Popup not Supported--"+uop.getMessage());
            throw new WebDriverException("BuiltInWebDriver.acceptPopup:: Popup not Supported "+uop.getMessage());
                }
            }
            catch(NoAlertPresentException noAl)
            {
                SecondsElapsed++;
                Thread.sleep(1000);
                continue;
        }
            catch(Exception e)
            {

                Log.Primitive("BuiltInWebDriver.declinePopup. Error Popup not found within timeout"+ ". Exception:: " + e.getMessage());
            Log.Error("BuiltInWebDriver.declinePopup. Error Popup not found within timeout"+ ". Exception:: " + e.getMessage());
            throw new WebDriverException("BuiltInWebDriver.declinePopup "+e.getMessage());
            }
        }
    }

    public void declinePopup(String window_handle, String TimeOutSeconds) throws Exception {
        //Switching To the Window
        boolean PopupNotFound = true;
        Alert alert = null;
        Integer SecondsElapsed = 0;
        Integer TimeOutSecondsInt = Integer.parseInt(TimeOutSeconds);
        while (PopupNotFound && SecondsElapsed < TimeOutSecondsInt) {
            try {

                alert = getWebDriverObject(window_handle).switchTo().alert();
                PopupNotFound = false;
alert.dismiss();
            } catch (UnsupportedOperationException uop) {

  if(getWebDriverObject(window_handle).getClass().getName().equalsIgnoreCase("org.openqa.selenium.htmlunit.HtmlUnitDriver"))
                PopupNotFound=false;
                else
                {
                   Log.Primitive("BuiltInWebDriver.acceptPopup:: Popup not Supported--"+uop.getMessage());
            Log.Error("BuiltInWebDriver.acceptPopup:: Popup not Supported--"+uop.getMessage());
            throw new WebDriverException("BuiltInWebDriver.acceptPopup:: Popup not Supported "+uop.getMessage());
                }
            }
            catch(NoAlertPresentException noAl)
            {
                SecondsElapsed++;
                Thread.sleep(1000);
                continue;
        }
            catch(Exception e)
            {

                Log.Primitive("BuiltInWebDriver.declinePopup. Error Popup not found within timeout"+ ". Exception:: " + e.getMessage());
            Log.Error("BuiltInWebDriver.declinePopup. Error Popup not found within timeout"+ ". Exception:: " + e.getMessage());
            throw new WebDriverException("BuiltInWebDriver.declinePopup "+e.getMessage());
            }
        }
    }

        public void matchTextByClassName(String window_handle, String class_name, String text) throws Exception {
        //Switching To the Window
        WebDriver window_object;
        try {
            window_object = getWebDriverObject(window_handle);

        } catch (Exception e) {
            Log.Primitive("BuiltInWebDriver.matchTextByClassName-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle+ ". Exception:: " + e.getMessage());
            Log.Error("BuiltInWebDriver.matchTextByClassName-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle+ ". Exception:: " + e.getMessage());
            throw e;
        }
        try {
            boolean flag = true;


            int value = 30;

            for (int i = 0; i < value; i++) {
                List<WebElement> elementList = window_object.findElements(By.className(class_name));

                Iterator<WebElement> WebelementIterated = elementList.iterator();
                while (WebelementIterated.hasNext()) {
                    String elementname = WebelementIterated.next().getText();

                    if (elementname.equalsIgnoreCase(text)) {
                        flag = false;
                        break;
                    }

                }

                if (!flag) {
                    break;
                }
                window_object.navigate().refresh();

            }
            if (flag) {
                Log.Primitive("BuiltInWebDriver.matchTextByClassName-Cannot find required text in the given element. Class Name = " + class_name);
                Log.Error("BuiltInWebDriver.matchTextByClassName-Cannot find required text in the given element. Class Name = " + class_name);
                throw new Exception();
            }
        } catch (Exception e) {
            Log.Primitive("BuiltInWebDriver.matchTextByClassName-Cannot find element with the given class name o invalid class name. Class Name = " + class_name+ ". Exception:: " + e.getMessage());
            Log.Error("BuiltInWebDriver.matchTextByClassName-Cannot find element with the given class name o invalid class name. Class Name = " + class_name+ ". Exception:: " + e.getMessage());
            throw e;
        }
    }

    public void matchTextByClassName(String window_handle, String class_name, String text, String itterate) throws Exception {
        //Switching To the Window
        WebDriver window_object;
        try {
            window_object = getWebDriverObject(window_handle);

        } catch (Exception e) {
            Log.Primitive("BuiltInWebDriver.matchTextByClassName-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle+ ". Exception:: " + e.getMessage());
            Log.Error("BuiltInWebDriver.matchTextByClassName-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle+ ". Exception:: " + e.getMessage());
            throw e;
        }
        try {
            boolean flag = true;
            
            
            int value = Integer.parseInt(itterate);
            
            for (int i = 0; i < value; i++) {
                List<WebElement> elementList = window_object.findElements(By.className(class_name));
                
                Iterator<WebElement> WebelementIterated = elementList.iterator();
                while (WebelementIterated.hasNext()) {
                    String elementname = WebelementIterated.next().getText();
                    
                    if (elementname.equalsIgnoreCase(text)) {
                        flag = false;
                        break;
                    }

                }

                if (!flag) {
                    break;
                }
                window_object.navigate().refresh();
                
            }
            if (flag) {
                Log.Primitive("BuiltInWebDriver.matchTextByClassName-Cannot find required text in the given element. Class Name = " + class_name);
                Log.Error("BuiltInWebDriver.matchTextByClassName-Cannot find required text in the given element. Class Name = " + class_name);
                throw new Exception();
            }
        } catch (Exception e) {
            Log.Primitive("BuiltInWebDriver.matchTextByClassName-Cannot find element with the given class name o invalid class name. Class Name = " + class_name+ ". Exception:: " + e.getMessage());
            Log.Error("BuiltInWebDriver.matchTextByClassName-Cannot find element with the given class name o invalid class name. Class Name = " + class_name+ ". Exception:: " + e.getMessage());
            throw e;
        }
    }

    public synchronized void selectFromDropDownByName(String window_handle, String element, String value) throws Exception {
        //Switching To the Window
        WebDriver window_object;
        try {
            window_object = getWebDriverObject(window_handle);
        } catch (Exception e) {
            Log.Primitive("BuiltInWebDriver.selectFromDropDownByName-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle+ ". Exception:: " + e.getMessage());
            Log.Error("BuiltInWebDriver.selectFromDropDownByName-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle+ ". Exception:: " + e.getMessage());

            throw e;
        }
        try {
            WebElement textelement = window_object.findElement(By.name(element));
            //System.out.println("Welcome"+textelement.getText());
            //System.out.println("Page title is: " + getWebDriverObject(window_handle).getTitle());
            Select select_element = new Select(textelement);
            // System.out.println("Page title is: " + dd.selectByVisibleText(value));
            select_element.selectByVisibleText(value);
        } catch (Exception e) {
            Log.Primitive("BuiltInWebDriver.selectFromDropDownByName-Cannot find dropdown with the given dropdown name. DropDown Name=" + element + ". Exception:: " + e.getMessage());
            Log.Error("BuiltInWebDriver.selectFromDropDownByName-Cannot find dropdown with the given dropdown name. DropDown Name=" + element + ". Exception:: " + e.getMessage());
            throw e;
        }
    }

    public synchronized void fileUpload(String window_handle, String element, String value) throws Exception {
        //Switching To the Window
        WebDriver window_object;
        try {
            window_object = getWebDriverObject(window_handle);
        } catch (Exception e) {
            Log.Primitive("BuiltInWebDriver.fileUpload-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle + ". Exception:: " + e.getMessage());
            Log.Error("BuiltInWebDriver.fileUpload-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle + ". Exception:: " + e.getMessage());
            throw e;
        }
        // printMessage("Switching to Handle=-" + contextvariable);
        try {
            // Find the text input element by its name
            WebElement textelement = window_object.findElement(By.name(element));
            //Set ing the Text in the specified element
            textelement.sendKeys(value);
        } catch (Exception e) {
            Log.Primitive("BuiltInWebDriver.fileUpload-Cannot find text field with the given name or invalid text field name. Test Field Name = " + element + ". Exception:: " + e.getMessage());
            Log.Error("BuiltInWebDriver.fileUpload-Cannot find text field with the given name or invalid text field name. Test Field Name = " + element + window_handle+ ". Exception:: " + e.getMessage());
            throw e;
        }
    }

    public synchronized void clickLinkByText(String window_handle, String element) throws Exception {
        //Switching To the Window
        WebDriver window_object;
        try {
            window_object = getWebDriverObject(window_handle);
        } catch (Exception e) {
            Log.Primitive("BuiltInWebDriver.clickLinkByText-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle + ". Exception:: " + e.getMessage());
            Log.Error("BuiltInWebDriver.clickLinkByText-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle + ". Exception:: " + e.getMessage());
            throw e;
        }

        try {
            WebElement linkelement = window_object.findElement(By.linkText(element));
//Button clicked
            linkelement.click();
        } catch (Exception e) {
            Log.Primitive("BuiltInWebDriver.clickLinkByText-Cannot find link with the given text or invalid link text. Link Text = " + element + ". Exception:: " + e.getMessage());
            Log.Error("BuiltInWebDriver.clickLinkByText-Cannot find link with the given text or invalid link text. Link Text = " + element +  ". Exception:: " + e.getMessage());
            throw e;
        }
    }

    public synchronized void clickLinkByImageTitle(String window_handle, String element) throws Exception {
        //Switching To the Window
        WebDriver window_object;
        try {
            window_object = getWebDriverObject(window_handle);
        } catch (Exception e) {
            Log.Primitive("BuiltInWebDriver.clickLinkByImageTitle-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle + ". Exception:: " + e.getMessage());
            Log.Error("BuiltInWebDriver.clickLinkByImageTitle-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle + ". Exception:: " + e.getMessage());
            throw e;
        }
        Boolean found_img = false;
        try {
            List<WebElement> images = window_object.findElements(By.tagName("img"));
            for (WebElement img : images) {
                if (img.getAttribute("title").equalsIgnoreCase(element)) {
                    img.click();
                    found_img = true;
                    break;
                }
            }
            if (!found_img) {
                Log.Primitive("BuiltInWebDriver.clickLinkByImageTitle-Cannot find Image with title. Title=" + element);
                Log.Error("BuiltInWebDriver.clickLinkByImageTitle-Cannot find Image with title. Title=" + element);
                throw new Exception();
            }
            //linkelement.click();
        } catch (Exception e) {
            Log.Primitive("BuiltInWebDriver.clickLinkByImageTitle-Cannot find link with the given text or invalid link text. Link Text = " + element + ". Exception:: " + e.getMessage());
            Log.Error("BuiltInWebDriver.clickLinkByImageTitle-Cannot find link with the given text or invalid link text. Link Text = " + element + ". Exception:: " + e.getMessage());
            throw e;
        }
    }

    public synchronized void getTableColumnNrByColumnName(String window_handle, String table_id, String column_name, String contextVar_colm_nr) throws Exception {
        //Switching To the Window
        WebDriver window_object;
        try {
            //System.out.println("Found");
            window_object = getWebDriverObject(window_handle);
            //  System.out.println("Found");
        } catch (Exception e) {
            Log.Primitive("BuiltInWebDriver.getTableColumnNrByColumnName-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle + ". Exception:: " + e.getMessage());
            Log.Error("BuiltInWebDriver.getTableColumnNrByColumnName-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle + ". Exception:: " + e.getMessage());
            throw e;
        }
        List<WebElement> resultsTable = (List<WebElement>) window_object.findElements(By.tagName("table"));
        Boolean find_table = false, found_column = false;
        Integer col_nr = 0;
        for (WebElement table : resultsTable) {
            if (table.getAttribute("id").equalsIgnoreCase(table_id)) {
                find_table = true;
                // System.out.println("table found");
                //WebElement table = getWebDriverObject(window_handle).findElement(By.id(table_name));
                WebElement tr = table.findElement(By.xpath("//tbody/tr[1]"));
                List<WebElement> tds = tr.findElements(By.xpath("//th"));
                // System.out.println("row found");
                for (WebElement td : tds) {
                    col_nr++;
                    //System.out.println(td.getText());
                    //System.out.println("Val "+column_name);
                    if (td.getText().equalsIgnoreCase(column_name)) {
                        // System.out.println("Column Found");
                        ContextVar.alterContextVar(contextVar_colm_nr, col_nr.toString());
                        found_column = true;
                        break;
                    }
                }
                if (!found_column) {
                    Log.Primitive("BuiltInWebDriver.getTableColumnNrByColumnName-Cannot find column name in the first row of the table. Column Name " + column_name);
                    Log.Error("BuiltInWebDriver.getTableColumnNrByColumnName-Cannot find column name in the first row of the table. Column Name " + column_name);
                    throw new Exception("BuiltInWebDriver.getTableColumnNrByColumnName-Cannot find column name in the first row of the table. Column Name " + column_name);
                }
            }
            if (find_table) {
                break;
            }
        }
        if (!find_table) {
            Log.Primitive("BuiltInWebDriver.getTableColumnNrByColumnName-Cannot find table with the given identifier or invalid table identifier. Table ID " + table_id);
            Log.Error("BuiltInWebDriver.getTableColumnNrByColumnName-Cannot find table with the given identifier or invalid table identifier. Table ID " + table_id);
            throw new Exception();
        }
        //System.out.println("Column Nr" + col_nr);
    }

    public synchronized void getTableRowNrByColumnNameAndText(String window_handle, String table_id, String column_name, String textual_Content, String contextVar_row_nr) throws Exception {
        //Switching To the Window
        WebDriver window_object;
        try {

            window_object = getWebDriverObject(window_handle);
        } catch (Exception e) {
            Log.Primitive("BuiltInWebDriver.getTableRowNrByColumnNameAndText-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle);
            Log.Error("BuiltInWebDriver.getTableRowNrByColumnNameAndText-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle);
            throw e;
        }
        //System.out.println("moving");
        List<WebElement> resultsTable = (List<WebElement>) window_object.findElements(By.tagName("table"));
        Boolean find_table = false;
        Integer col_nr = 0;
        Boolean found_column = false;
        //System.out.println("moving");
        for (WebElement table : resultsTable) {
            if (table.getAttribute("id").equalsIgnoreCase(table_id)) {
                find_table = true;
                // System.out.println("table found");
                //WebElement table = getWebDriverObject(window_handle).findElement(By.id(table_name));
                WebElement tr = table.findElement(By.xpath("//tbody/tr[1]"));
                List<WebElement> tds = tr.findElements(By.xpath("//th"));
                //System.out.println("row found");
                for (WebElement td : tds) {//System.out.println("searching");
                    //System.out.println("cell  "+td.getText());
                    //System.out.println(column_name);
                    col_nr++;
                    if (td.getText().equalsIgnoreCase(column_name)) {
                        found_column = true;
                        break;
                    }
                }
                if (!found_column) {
                    Log.Primitive("BuiltInWebDriver.getTableRowNrByColumnNameAndText-Cannot find column name in the first row of the table. Column Name " + column_name);
                    Log.Error("BuiltInWebDriver.getTableRowNrByColumnNameAndText-Cannot find column name in the first row of the table. Column Name " + column_name);
                    throw new Exception("BuiltInWebDriver.getTableRowNrByColumnNameAndText-Cannot find column name in the first row of the table. Column Name " + column_name);
                }
                Integer row_no = 0;
                Boolean found_cell = false;
                List<WebElement> rows = table.findElements(By.xpath("//tbody/tr"));
                //System.out.println("2nd phase  "+textual_Content);
                //System.out.println("Column ::: "+col_nr);
                for (WebElement row : rows) {
                    List<WebElement> cells = row.findElements(By.xpath("//td[" + col_nr + "]"));
                    //  System.out.println(cell.getText());
                    for (WebElement cell : cells) {
                        row_no++;
                        //System.out.println(cell.getText());
                        if (cell.getText().equalsIgnoreCase(textual_Content)) {
                            //System.out.println("Row No: " + row_no);
                            ContextVar.alterContextVar(contextVar_row_nr, row_no.toString());
                            found_cell = true;
                            //System.out.println("Row Nr"+row_no);
                            break;
                        }
                    }
                    if (found_cell) {
                        break;
                    }
                }
                if (!found_cell) {
                    Log.Primitive("BuiltInWebDriver.getTableRowNrByColumnNameAndText-Cannot find cell with the given text. Text " + textual_Content);
                    Log.Error("BuiltInWebDriver.getTableRowNrByColumnNameAndText-Cannot find cell with the given text. Text " + textual_Content);
                    throw new Exception("BuiltInWebDriver.getTableRowNrByColumnNameAndText-Cannot find cell with the given text. Text " + textual_Content);
                }

            }

            if (find_table) {
                break;
            }
        }
        if (!find_table) {
            Log.Primitive("BuiltInWebDriver.getTableRowNrByColumnNameAndText-Cannot find table with the given identifier or invalid table identifier. Table ID " + table_id);
            Log.Error("BuiltInWebDriver.getTableRowNrByColumnNameAndText-Cannot find table with the given identifier or invalid table identifier. Table ID " + table_id);
            throw new Exception();
        }

    }

    public synchronized void clickLinkInTableByRowColumnAndLinkIndex(String window_handle, String table_id, String row_no, String column_no, String link_index) throws Exception {
        //Switching To the Window
        WebDriver window_object;
        try {

            window_object = getWebDriverObject(window_handle);
        } catch (Exception e) {
            Log.Primitive("BuiltInWebDriver.clickLinkInTableByRowColumnAndLinkIndex-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle+ ". Exception:: " + e.getMessage());
            Log.Error("BuiltInWebDriver.clickLinkInTableByRowColumnAndLinkIndex-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle+ ". Exception:: " + e.getMessage());
            throw e;
        }
        //System.out.println("moving");
        List<WebElement> resultsTable = (List<WebElement>) window_object.findElements(By.tagName("table"));
        Boolean find_table = false;


        //System.out.println("moving");
        for (WebElement table : resultsTable) {
            if (table.getAttribute("id").equalsIgnoreCase(table_id)) {
                find_table = true;
                // System.out.println("table found");
                //WebElement table = getWebDriverObject(window_handle).findElement(By.id(table_name));
                WebElement cell = null, link = null;
                try {
                    cell = table.findElement(By.xpath("//tbody/tr[" + row_no + "]/td[" + column_no + "]"));
                } catch (WebDriverException e) {
                    Log.Primitive("BuiltInWebDriver.clickLinkInTableByRowColumnAndLinkIndex-Cannot find cell with this row number and column number in the table. Row Number: " + row_no + " Column number: " + column_no+ ". Exception:: " + e.getMessage());
                    Log.Error("BuiltInWebDriver.clickLinkInTableByRowColumnAndLinkIndex-Cannot find cell with this row number and column number in the table. Row Number: " + row_no + " Column number: " + column_no+ ". Exception:: " + e.getMessage());
                }
                try {
                    link = cell.findElements(By.tagName("img")).get(Integer.parseInt(link_index));
                    //cell.findElements(By.tagName("img")).get(1).click()
                } catch (WebDriverException e) {
                    Log.Primitive("BuiltInWebDriver.clickLinkInTableByRowColumnAndLinkIndex-Cannot find link with this link index. Link Index: " + link_index);
                    Log.Error("BuiltInWebDriver.clickLinkInTableByRowColumnAndLinkIndex-Cannot find link with this link index. Link Index: " + link_index);
                }
                link.click();
            }

        }
        if (!find_table) {
            Log.Primitive("BuiltInWebDriver.clickLinkInTableByRowColumnAndLinkIndex-Cannot find table with the given identifier or invalid table identifier. Table ID " + table_id);
            Log.Error("BuiltInWebDriver.clickLinkInTableByRowColumnAndLinkIndex-Cannot find table with the given identifier or invalid table identifier. Table ID " + table_id);
            throw new Exception();
        }
    }

    public void findBrowserWithTitle(String window_handle, String title, String ContextVariable_return_window_handle) throws Exception {
        //Url checking
        WebDriver window_object = null;
        try {

            window_object = getWebDriverObject(window_handle);
        } catch (Exception e) {
            Log.Primitive("BuiltInWebDriver.findBrowserWithTitle-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle+ ". Exception:: " + e.getMessage());
            Log.Error("BuiltInWebDriver.findBrowserWithTitle-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle+ ". Exception:: " + e.getMessage());
        }
        Set window_handle_list = window_object.getWindowHandles();
        //wdrivermap.get(browser_handle).getTitle();
        //List<WebElement> elementList = getWebDriverObject(window_handle).findElements(By.className(class_name));
        //System.out.println("findBrowserWithTitle");
        Iterator set = window_handle_list.iterator();
        while (set.hasNext()) {
            String put_window_handle = set.next().toString();
            if (!put_window_handle.equals(window_handle)) {
                //System.out.println("Handles found  :"+put_window_handle);
                putWindowHandle(window_handle, put_window_handle);
            }

        }
        Boolean found_window = false;
        Iterator set1 = window_handle_list.iterator();
        while (set1.hasNext()) {
            String new_window_handle = set1.next().toString();
            if (!new_window_handle.equals(window_handle)) {
                //System.out.println("Titles and Handles"+new_window_handle+" And Titles "+getWebDriverObject(new_window_handle).switchTo().window(new_window_handle).getTitle().equalsIgnoreCase(title));
                if (getWebDriverObject(new_window_handle).switchTo().window(new_window_handle).getTitle().equalsIgnoreCase(title)) {
                    ContextVar.setContextVar(ContextVariable_return_window_handle, new_window_handle);
                    found_window = true;
                    break;
                }
            }
        }
        if (!found_window) {
            Log.Primitive("BuiltInWebDriver.findBrowserWithTitle-Cannot find new window with the given title. Handle = Window Title :" + title);
            Log.Error("BuiltInWebDriver.findBrowserWithTitle-Cannot find new window with the given title. Handle = Window Title :" + title);
            throw new Exception();
        }



    }

    public synchronized void getRowCountInTable(String window_handle, String table_id, String contextVar_row_count) throws Exception {
        //Switching To the Window
        WebDriver window_object;
        try {

            window_object = getWebDriverObject(window_handle);
        } catch (Exception e) {
            Log.Primitive("BuiltInWebDriver.getRowCountInTable-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle+ ". Exception:: " + e.getMessage());
            Log.Error("BuiltInWebDriver.getRowCountInTable-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle+ ". Exception:: " + e.getMessage());
            throw e;
        }
        //System.out.println("moving");
        List<WebElement> resultsTable = (List<WebElement>) window_object.findElements(By.tagName("table"));
        Boolean find_table = false;
        Integer col_nr = 0;
        Boolean found_column = false;
        //System.out.println("moving");
        for (WebElement table : resultsTable) {
            if (table.getAttribute("id").equalsIgnoreCase(table_id)) {
                find_table = true;
                Integer row_count = 0;
                List<WebElement> rows = table.findElements(By.xpath(".//tr"));
                //System.out.println("Inside rows  ");
                //System.out.println("number of rows   ::: "+rows.size());
                for (WebElement row : rows) {
                    System.out.println("Row Count ::: " + row.getAttribute("class"));

                    row_count++;
                }
                // System.out.println("Row Count ::: "+row_count);
                ContextVar.setContextVar(contextVar_row_count, row_count.toString());

                if (find_table) {
                    break;
                }
            }
        }
        if (!find_table) {
            Log.Primitive("BuiltInWebDriver.getRowCountInTable-Cannot find table with the given identifier or invalid table identifier. Table ID " + table_id);
            Log.Error("BuiltInWebDriver.getRowCountInTable-Cannot find table with the given identifier or invalid table identifier. Table ID " + table_id);
            throw new Exception();
        }

    }

    public synchronized void matchRowValuesInTable(String window_handle, String table_id, String column_names, String values, String row_nr) throws Exception {
        //Switching To the Window
        WebDriver window_object;
        try {
            //System.out.println("Found");
            window_object = getWebDriverObject(window_handle);
            //  System.out.println("Found");
        } catch (Exception e) {
            Log.Primitive("BuiltInWebDriver.MatchRowValuesInTable-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle+ ". Exception:: " + e.getMessage());
            Log.Error("BuiltInWebDriver.MatchRowValuesInTable-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle+ ". Exception:: " + e.getMessage());
            throw e;
        }
        String[] column_name_arr = column_names.split(":");
        String[] values_arr = values.split(":");

        if (column_name_arr.length != values_arr.length) {
            Log.Primitive("BuiltInWebDriver.MatchRowValuesInTable-Unequal number of values passed in list. Column Name: " + column_name_arr.length + " Values Length" + values_arr.length);
            Log.Error("BuiltInWebDriver.MatchRowValuesInTable-Unequal number of values passed in list. Column Name: " + column_name_arr.length + " Values Length" + values_arr.length);
            throw new Exception();
        }
        List<WebElement> resultsTable = (List<WebElement>) window_object.findElements(By.tagName("table"));
        Boolean find_table = false, found_column = false;
        Integer col_nr = 0;
        for (WebElement table : resultsTable) {
            if (table.getAttribute("id").equalsIgnoreCase(table_id)) {
                find_table = true;
                //System.out.println("table found");
                //WebElement table = getWebDriverObject(window_handle).findElement(By.id(table_name))

                int itteration = 0, column_nr;
                while (itteration < column_name_arr.length) {
                    found_column = false;
                    column_nr = 0;
                    WebElement header_row = table.findElement(By.xpath("//tbody/tr[1]"));
                    List<WebElement> header_cells = header_row.findElements(By.xpath("//th"));
                    for (WebElement header : header_cells) {
                        column_nr++;
                        //System.out.println(td.getText());
                        //System.out.println("Val "+column_name);
                        if (header.getText().equalsIgnoreCase(column_name_arr[itteration])) {
                            found_column = true;
                            WebElement req_row = table.findElement(By.xpath("//tbody/tr[" + row_nr + "]/td[" + column_nr + "]"));
                            //System.out.println("Column Name "+header.getText()+"Value in cell"+req_row.getText()+"Expected Value"+values_arr[itteration]);
                            if (!req_row.getText().equalsIgnoreCase(values_arr[itteration])) {
                                Log.Primitive("BuiltInWebDriver.MatchRowValuesInTable-Column Name " + header.getText() + " doesnot contain value" + values_arr[itteration] + " in row." + row_nr);
                                Log.Error("BuiltInWebDriver.MatchRowValuesInTable-Column Name " + header.getText() + " doesnot contain value" + values_arr[itteration] + " in row." + row_nr);
                                throw new Exception();
                            } else {
                                break;
                            }
                        }
                    }
                    if (!found_column) {
                        Log.Primitive("BuiltInWebDriver.MatchRowValuesInTable-Column not found" + column_name_arr[itteration]);
                        Log.Error("BuiltInWebDriver.MatchRowValuesInTable-Column not found" + column_name_arr[itteration]);
                        throw new Exception();
                    }
                    itteration++;
                }
            }
            if (find_table) {
                break;
            }
        }
        if (!find_table) {
            Log.Primitive("BuiltInWebDriver.MatchRowValuesInTable-Cannot find table with the given identifier or invalid table identifier. Table ID " + table_id);
            Log.Error("BuiltInWebDriver.MatchRowValuesInTable-Cannot find table with the given identifier or invalid table identifier. Table ID " + table_id);
            throw new Exception();
        }
        //System.out.println("Column Nr" + col_nr);
    }

    public void findDivText(String window_handle, String class_name, String req_text, String timeout) throws Exception {
        WebDriver window_object;
        try {
            //System.out.println("Found");
            window_object = getWebDriverObject(window_handle);
            //  System.out.println("Found");
        } catch (Exception e) {
            Log.Primitive("BuiltInWebDriver.FindDivText-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle+ ". Exception:: " + e.getMessage());
            Log.Error("BuiltInWebDriver.FindDivText-Cannot find browser with the given handle or invalid handle. Handle = " + window_handle+ ". Exception:: " + e.getMessage());
            throw e;
        }
        int time_out = Integer.parseInt(timeout);

        int i = 0;
        boolean found_text = false;
        while (i < time_out && !found_text) {
            List<WebElement> div_elements = window_object.findElements(By.className(class_name));
            for (WebElement div : div_elements) {

                if (div.getText().trim().equalsIgnoreCase((req_text.trim()))) {
                    found_text = true;
                    break;
                }
            }
            if (!found_text) {
                window_object.navigate().refresh();
            } else {
                break;
            }
            i++;

            Thread.sleep(5000);
        }
        if (!found_text) {
            throw new Exception("BuiltInWebDriver.FindDivText: Text not found in div tag");
        }

    }
}

class AtomNotFoundException extends Exception {

    public AtomNotFoundException() {

        System.out.println("AtomNotFoundException Occured");
        throw new WebDriverException();

    }
}
