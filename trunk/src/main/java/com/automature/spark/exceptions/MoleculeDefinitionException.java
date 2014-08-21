/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.automature.spark.exceptions;

/**
 *
 * @author Administrator
 */
public class MoleculeDefinitionException extends Throwable {
      public MoleculeDefinitionException(String message)
    {
        super(message);
    }
    public MoleculeDefinitionException(String message,Throwable t)
    {
        super(message,t);
    }
    
}
