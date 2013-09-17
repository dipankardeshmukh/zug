/*
 * Copied from the page:
 * http://www.velocityreviews.com/forums/t123888-re-how-to-get-mac-address-for-local-machine.html
 */
package com.automature.zug.license;


import java.io.*;
import java.util.*;
import java.util.regex.*;
//Internal Imports
import com.automature.zug.engine.Controller;
import com.automature.zug.engine.SysEnv;
import com.automature.zug.util.Log;

public class GetMac {
//    	public static void main(String[] args) throws IOException
//    {
//    List<String> address = new GetMac().getMacAddress();
//   System.out.println(address.toString());
//    }
//

    public static List<String> getMacAddress() throws IOException {
        String macAddress = null;
        String command=null;
        String mac_pattern=null;
        boolean isWindows=false;

        ArrayList<String> macList = new ArrayList<String>();
        if (SysEnv.OS_NAME.toLowerCase().contains("windows"))
        {
            command = "ipconfig -all";
            mac_pattern=".*Physical Address.*: (.*)";
            isWindows=true;
        } else if (SysEnv.OS_NAME.equalsIgnoreCase("linux"))
        {
            command = "ifconfig -a";
            
        } else
        {
            System.out.println("You are using Different Operating System\t"+SysEnv.OS_NAME);
             command="";
        }
		//http://www.windowsitpro.com/article/john-savills-windows-faqs/how-can-i-get-system-information-from-the-command-line-.aspx
		//String command = "systeminfo";
		Process pid = Runtime.getRuntime().exec(command);
		BufferedReader in =
		new BufferedReader(new InputStreamReader(pid.getInputStream()));




           while (true)
		{
			String line = in.readLine();
			//System.out.println(line);
			if (line == null) {
            break;

        }
                        if(isWindows)
                        {
                            Pattern p = Pattern.compile(mac_pattern);
        Matcher m = p.matcher(line);
        if (m.matches()) {
            macAddress = m.group(1);
            if (macAddress.length() == 17) {
                Log.Debug(macAddress);
                macList.add(macAddress);
                //break;
            }
        }
                    }
 else
                        {
                            String macAddr[]=line.split("\\s");
                            for(int i=0;i<macAddr.length;i++)
                            {
                                if(macAddr[i].contains("HWaddr"))
                                    macList.add(macAddr[i+1]);
                            }
                            
 }
    }

    in.close ();
    return
    macList ;
}

}
