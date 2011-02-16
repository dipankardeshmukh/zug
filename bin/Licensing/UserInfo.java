package Licensing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import java.text.ParseException;

import logs.Log;
public class UserInfo
{
	List<String> physicalAddresses = new ArrayList<String>();
	Calendar expiryDate = null;
	public String companyName = null;
	String email = null;
	List<InetAddress> ipAddresses = new ArrayList<InetAddress>();
	public Calendar getExpiryDate()
	{
		return expiryDate;
	}
	public List<String> getPhysicalAddresses()
	{
		return physicalAddresses;
	}
	
	public boolean validate(BufferedReader userDetails) throws IOException
	{
		String inLine;
		boolean validate=true;
		while(true)
		{
			inLine = userDetails.readLine();
			if(inLine==null) break;
			if(inLine.equalsIgnoreCase("Physical Address:"))
			{
				Log.Debug("Addresses list found...");
				inLine = userDetails.readLine();
				while(!inLine.equalsIgnoreCase("-----------------"))
				{
					Log.Debug("Address "+inLine+" found");
					setAddress(inLine);
					inLine = userDetails.readLine();
				}
			}
			else if(inLine.equalsIgnoreCase("Expiry Date:"))
			{
				Log.Debug("Expiry Date found...");
				inLine = userDetails.readLine();
				setDate(inLine);
				Log.Debug("Date "+ inLine +" found");
				inLine = userDetails.readLine();
				if(inLine.equalsIgnoreCase("-----------------"))
					continue;
				else
				{
					System.out.println("License could not be parsed ");
					validate = false;
				}
					
				
			}
			else if(inLine.equalsIgnoreCase("Company Name:"))
			{
				Log.Debug("Company name found...");
				inLine = userDetails.readLine();
				this.setCompanyName(inLine);
				Log.Debug("Name "+ inLine+" found");
				inLine = userDetails.readLine();
				if(inLine.equalsIgnoreCase("-----------------"))
					continue;
				else
				{
					System.out.println("License could not be parsed ");
					validate = false;
				}
				
			}
			else if(inLine.equalsIgnoreCase("Contact email:"))
			{
				Log.Debug("Email found...");
				inLine = userDetails.readLine();
				if(isEmailValid(inLine))
					this.setEmail(inLine);
				else
					validate = false;
				Log.Debug("Email "+ inLine+" found");
				inLine = userDetails.readLine();
				if(inLine.equalsIgnoreCase("-----------------"))
					Log.Debug("Email Section read...");
				else
				{
					System.out.println("License could not be parsed ");
					validate = false;
				}
			}
			else if(inLine.equalsIgnoreCase("IP Address:"))
			{
				Log.Debug("IP Addresses list found...");
				inLine = userDetails.readLine();
				while(!inLine.equalsIgnoreCase("-----------------"))
				{
					this.setipAddress(inLine);
					Log.Debug("IP Address " + inLine + " found");
					inLine = userDetails.readLine();
				}
				
			}
			else
			{
				System.out.println("License could not be parsed ");
				validate = false;
			}
			if(validate==false)
				break;
		}
		return validate;
	}
/*	public static void main(String[] args)
	{
		try {
			UserInfo userInfo = new UserInfo();
			FileInputStream fis=new FileInputStream(args[0]);
			if(!userInfo.validate(new BufferedReader(new InputStreamReader(fis))))
			{
				System.out.println("Please input file in correct format... ");
			}
			else
				System.out.println("License file read successfully...");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println(args[0]+ " file not found");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
*/
	public void setipAddress(String address)
	{
		try {
			ipAddresses.add(InetAddress.getByName(address));
		} catch (UnknownHostException e) {
			System.out.println("IP address " + address + "is not valid");
			e.printStackTrace();
		}
	}
	public void setEmail(String inputemail)
	{
			this.email = inputemail;
	}
	public void setCompanyName(String name)
	{
		this.companyName=name;
	}
	public void setAddress(String physicalAddress)
	{
		this.physicalAddresses.add(physicalAddress);
	}
	public void setDate(String Date)
	{
//		this.expiryDate = new Calendar();
//		this.expiryDate.clear();
	    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	    try {
	      df.parse(Date);
	      String[] parts = Date.split("-");
	      this.expiryDate = Calendar.getInstance(TimeZone.getDefault());
	      this.expiryDate.set(Integer.parseInt(parts[0]),
          Integer.parseInt(parts[1])-1,
          Integer.parseInt(parts[2]),0,0,0);
	      // get here and we know the format is correct
	    } catch (ParseException e) {
	    	System.out.println("Please put the Date in proper format... yyyy-MM-dd");
	    	e.printStackTrace();
	      // ignore the parse failure
	    }
	}

	public static boolean isEmailValid(String email)
	{
		boolean isValid = false;
		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		CharSequence inputStr = email;
		Pattern pattern = Pattern.compile(expression,Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		if(!matcher.matches())
			System.out.println("The contact email is not valid...");
		else
			isValid = true;
	
		return isValid;
	}
}