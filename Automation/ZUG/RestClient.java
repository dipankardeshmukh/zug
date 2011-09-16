/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ZUG;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;



/**
 *
 * @author Sankho
 * Class file for Sending the Request to the Server with
 * Key value pairs
 */

public class RestClient {

    private final String CHARSET = "UTF-8";
    //private static Logger logs;
    private static URL requestUrl;


    private String createURL(String url) {

        try {
            if (url.startsWith("http://") || url.startsWith("HTTP://")||url.contains(":")) {
                requestUrl = new URL(url);
                return "Connection to the Url:  " + url;
            } else {
                requestUrl = new URL("http://www.automature.com/twiki/bin/view/SSRM");
                return "Check Help in SSRM";
            }

        } catch (Exception ex) {
            return "Connection Faliure:" + ex.getLocalizedMessage();
        }

    }

    public static void printMessage(Object obj) {
        System.out.println(obj);
        //logs.log(new LogRecord(Level.INFO, obj.toString()));
    }

    public void sendRequest(String serverUrl, String method, String requestBody) {
        byte buffer[] = new byte[8192];
      int read = 0;
        String messgae = createURL(serverUrl);

        printMessage(messgae);
        try {


                      HttpURLConnection connection = (HttpURLConnection)requestUrl.openConnection();
            connection.setRequestMethod(method.toUpperCase());
             connection.setRequestProperty("Accept-Charset", CHARSET);
             //if (requestBody != null) {
                if (method.equalsIgnoreCase("POST")) {

                    connection.setDoOutput(true);
                   OutputStream output = connection.getOutputStream();
                    output.write(requestBody.getBytes(CHARSET));
                connection.connect();
                 InputStream responseBodyStream = connection.getInputStream();
        StringBuffer responseBody = new StringBuffer();
        while ((read = responseBodyStream.read(buffer)) != -1)
        {
            responseBody.append(new String(buffer,0,read));
        }
        printMessage(responseBody);
                    connection.disconnect();

                }
                if (method.equalsIgnoreCase("GET")) {

                
                   connection.connect();
                   InputStream getRequestStream=connection.getInputStream();
               	StringBuffer responseBody = new StringBuffer();
                while ((read = getRequestStream.read(buffer)) != -1)
                {
                    responseBody.append(new String(buffer,0,read));
                }
                printMessage(responseBody);
                   connection.disconnect();
                }

                    printMessage("Request Sent");
           // }

        } catch (Exception e) {
            printMessage("Its a Faliure:\n" + e.getLocalizedMessage());
        }
    }
}

   /* public static void main(String... inputs) {

RestClient requestToSend=new RestClient();

requestToSend.sendRequest(inputs[0], inputs[1], inputs[2]);


    }
} */
