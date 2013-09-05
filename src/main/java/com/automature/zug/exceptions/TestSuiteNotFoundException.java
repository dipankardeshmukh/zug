/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.automature.zug.exceptions;

/**
 *
 * @author Sankho
 * Class for TestSuite Not found Exception
 */
public class TestSuiteNotFoundException extends Throwable {
      public TestSuiteNotFoundException(String message)
    {
        super(message);
    }
    public TestSuiteNotFoundException(String message,Throwable t)
    {
        super(message,t);
    }


}
