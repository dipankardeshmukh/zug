package com.automature.spark.engine;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class SparkServer implements Runnable {
	   Socket csocket;
   SparkServer(Socket csocket) {
      this.csocket = csocket;
   }

   public static void startServer() 
   throws Exception {
      ServerSocket ssock = new ServerSocket((45000+Integer
				.parseInt((java.lang.management.ManagementFactory
						.getRuntimeMXBean().getName().split("@"))[0])%10000));

      while (true) {
          Socket sock = ssock.accept();
            	 new Thread(new SparkServer(sock)).start();
      }
   }
   
   
   public void run() {
      try {
    	 InputStream istream=csocket.getInputStream();
    	 byte[] b=new byte[1024];
    	 istream.read(b);
    	 String message = new String(b, "UTF-8");
    	 boolean altered=false;
    	 if(getValueOfKey(message, "method").equals("alter"))
	       {
    		 try{
	            	if((ContextVar.getContextVar(getValueOfKey(message, "name")))!=null)
	            	{
	            	ContextVar.alterContextVar(getValueOfKey(message, "name"), getValueOfKey(message, "value"));
	            	altered=true;
	            	}
    		 }catch(Exception e){e.printStackTrace();}
	       }
         PrintStream pstream = new PrintStream
         (csocket.getOutputStream());
         if(getValueOfKey(message, "method").equals("alter"))
         {
         if(altered)
        	 pstream.println(alterValueOfKey(message, "error", "0"));
	     else
	    	 pstream.println(alterValueOfKey(message, "error", "1"));
         }
         else
         {
         	if((ContextVar.getContextVar(getValueOfKey(message, "name")))!=null)
         		pstream.println(alterValueOfKey(alterValueOfKey(message, "value", ContextVar.getContextVar(getValueOfKey(message, "name"))), "error", "0"));
         	else
         	{
         		pstream.println(alterValueOfKey(message, "error", "1"));
         	}
         }
    	 istream.close();
         pstream.close();
         csocket.close();
      }
      catch (Exception e) {
      }
   }
   
   
   public String getValueOfKey(String JSON_MetadataArray,String key){
	   try {
		JSONObject obj=new JSONObject(JSON_MetadataArray);
		
		return obj.get(key).toString();
		} catch (JSONException e) {
			return "";
		}
	   
	}
   
   
	public String alterValueOfKey(String JSON_MetadataArray,String key,String value){
		String s="";
		try{
		JSONObject jobj=new JSONObject(JSON_MetadataArray);
		jobj.put(key, value);
		s =  jobj.toString();
		}catch(Exception e){e.printStackTrace();}
		return s;
	}
}