/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.automature.zug.exceptions;

/**
 *
 * @author Sankho
 * Class for Testcase execution Exception
 */
public class TestCaseExecutionException extends Throwable {
public TestCaseExecutionException(String message)
    {
    super(message);
}
public TestCaseExecutionException(String message,Throwable t)
    {
    super(message,t);
}
}
