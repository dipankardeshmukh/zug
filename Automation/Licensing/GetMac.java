/*
 * Copied from the page:
 * http://www.velocityreviews.com/forums/t123888-re-how-to-get-mac-address-for-local-machine.html
 */
package Licensing;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import logs.Log;

public class GetMac
{
/*	public static void main(String[] args) throws IOException
	{
		List<String> address = new GetMac().getMacAddress();
		//System.out.println(address.toString());
	}
*/
	public static List<String> getMacAddress() throws IOException
	{
		String macAddress = null;
		ArrayList<String> macList = new ArrayList<String>();
		String command = "ipconfig /all";
		//http://www.windowsitpro.com/article/john-savills-windows-faqs/how-can-i-get-system-information-from-the-command-line-.aspx
		//String command = "systeminfo";
		Process pid = Runtime.getRuntime().exec(command);
		BufferedReader in =
		new BufferedReader(new InputStreamReader(pid.getInputStream()));
		while (true)
		{
			String line = in.readLine();
			//System.out.println(line);
			if (line == null)
			break;
			Pattern p = Pattern.compile(".*Physical Address.*: (.*)");
			Matcher m = p.matcher(line);
			if (m.matches())
			{
				macAddress = m.group(1);
				if(macAddress.length()==17)
				{
					Log.Debug(macAddress);
					macList.add(macAddress);
					//break;
				}
			}
		}
		in.close();
		return macList;
	}
}
