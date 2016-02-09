package com.automature.spark.beans;

import java.util.Date;

import com.automature.spark.gui.controllers.ZugguiController;

import javafx.scene.control.Hyperlink;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.event.Event;

public class TestCaseAndResult {
private Hyperlink testCase;
private String result;
private Long time;
private static Integer d1=null;
public TestCaseAndResult(String testCase,String result, int time) {
	this.testCase=new Hyperlink();
	this.testCase.setText(testCase);
	this.testCase.setOnAction(e->{try{this.clickAction(e);}catch(Exception ex){}});
	
	this.result=result;
	this.time=(long)time;
}

public void setTestCase(String testCase) {
	this.testCase.setText(testCase);
}

public void setResult(String result) {
	this.result = result;
}

public void setTime(Long time) {
	this.time = time;
}

public Hyperlink getTestCase() {
	return testCase;
}

public String getResult() {
	return result;
}

public Long getTime() {
	return time;
}

private void clickAction(Event e){
	
	if(d1!=null)
	{
		
		((Text)ZugguiController.controller.getConsole().getConsoleLayout().getChildren().get(d1)).setFont(Font.font(null, FontWeight.NORMAL, 12));
		if(testCase.getId().equalsIgnoreCase("pass"))
		((Text)ZugguiController.controller.getConsole().getConsoleLayout().getChildren().get(d1+1)).setFont(Font.font(null, FontWeight.BOLD, 12));
	
	}
	double d=0.0;
	Text t = null,t1=null;
	for (int i = 0; i < ZugguiController.controller.getConsole().getConsoleLayout().getChildren().size(); i++) {
		if(testCase.getId().equalsIgnoreCase("pass")&&((Text)ZugguiController.controller.getConsole().getConsoleLayout().getChildren().get(i)).getText().contains("Running TestCase ID")&&((Text)ZugguiController.controller.getConsole().getConsoleLayout().getChildren().get(i+1)).getText().contains(" "+this.testCase.getText()))
		{	

			t=((Text)ZugguiController.controller.getConsole().getConsoleLayout().getChildren().get(i));
			t1=((Text)ZugguiController.controller.getConsole().getConsoleLayout().getChildren().get(i+1));
			d1=i;
			break;
		
		}
		else if(testCase.getId().equalsIgnoreCase("fail")&&((Text)ZugguiController.controller.getConsole().getConsoleLayout().getChildren().get(i)).getText().contains("Error Messages For Test Case "+this.testCase.getText()))
		{	

			t=((Text)ZugguiController.controller.getConsole().getConsoleLayout().getChildren().get(i));
			d1=i;
			break;
		
		}
		else if(((Text)ZugguiController.controller.getConsole().getConsoleLayout().getChildren().get(i)).getText().equals("\n"))
			continue;
		d++;
		
	}
	ZugguiController.controller.getConsole().getScrollPane().setVmin(0.0);
	ZugguiController.controller.getConsole().getScrollPane().setVmax(ZugguiController.controller.getConsole().getConsoleLayout().getChildren().size());
	ZugguiController.controller.getConsole().getScrollPane().setVvalue(d);
	try{
		t.setFont(Font.font(null, FontWeight.BOLD, 12));
		if(testCase.getId().equalsIgnoreCase("pass"))
		t1.setFont(Font.font(null, FontWeight.BOLD, 12));
		
	}catch(Exception ex){}
}

}
