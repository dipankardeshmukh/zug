package com.automature.spark.gui;

import java.util.List;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;

import com.automature.spark.engine.Action;
import com.automature.spark.gui.controllers.ZugguiController;
import com.automature.spark.gui.utils.GuiUtils;
import com.automature.spark.util.Log;

public class ExpressionEvaluator {
	
	private MacroEvaluator macroEvaluator;

	private SimpleBooleanProperty expressionEvaluatorMode=new SimpleBooleanProperty(false);
	

	public SimpleBooleanProperty getExpressionEvaluatorMode() {
		return expressionEvaluatorMode;
	}
	public ExpressionEvaluator(){
		//macroEvaluator=new MacroEvaluator(ZugguiController.spreadSheet.getFileName());
	}
	public Action generateAction(Expression expression){
		
		Action action=new Action();
		action.parentTestCaseID="ExpressionEvaluator";
		action.nameSpace=GuiUtils.getNameSpace(macroEvaluator.getFileName());
		action.stackTrace="ExpressionEvaluator";
		action.testCaseID="ExpressionEvaluator";
		action.arguments=expression.getArguments();
		action.name=expression.getAction();
		return action;
		
	}
	
	
	public void setMacroEvaluator(MacroEvaluator macroEvaluator) {
		this.macroEvaluator = macroEvaluator;
	}
	
	private void evaluateExpression(Expression expression){
		
		Action action=generateAction(expression);

		Task<Void> task = new Task<Void>() {

			@Override public Void call() {
				try {
					macroEvaluator.evaluateMacrosValue(action.arguments);
					//System.out.println("Starting expression evaluation "+action);
					action.run();

				} catch(Throwable t){
					Log.Error(t.getMessage());
				}finally{
					expressionEvaluatorMode.setValue(true);
				}
				return null;
			}
		};
		Thread t=new Thread(task,"Expression Evaluator Engine");
		t.start();
		
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void evaluateExpression(List<Expression> expressions){
		expressionEvaluatorMode.setValue(true);
		for(Expression expression:expressions){
			evaluateExpression(expression);
		}
		expressionEvaluatorMode.setValue(false);
	}
	
}
