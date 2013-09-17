/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.automature.zug.exceptions;

/**
 *
 * @author Sankho
 * Class for License Checking
 */
public class LicenseNotFoundException extends Throwable {

    public LicenseNotFoundException(String message)
    {
        super(message);
    }
    public LicenseNotFoundException(String message,Throwable t)
    {
        super(message,t);
    }

}
