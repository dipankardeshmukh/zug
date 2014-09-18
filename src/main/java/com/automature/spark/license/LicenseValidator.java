package com.automature.spark.license;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import com.automature.spark.engine.Spark;

public class LicenseValidator
{
    private static FileInputStream  fis;
    private static String licenseFileName ;
    private static String algorithm="DES";
    private static SecretKey decryptionKey = null;
    private static Cipher cipher = null;
	public static UserInfo userInfo = new UserInfo();

    static void Setup()
    {
        try{
        	SecretKeyFactory decryptKeyFactory = SecretKeyFactory.getInstance(algorithm);
        	String password = "P@$$W0RD";
        	byte[] passwordBytes = password.getBytes();
        	DESKeySpec  decryptKeySpec = new DESKeySpec(passwordBytes);
        	decryptionKey = decryptKeyFactory.generateSecret(decryptKeySpec);
        	cipher = Cipher.getInstance(algorithm);
        }
        catch(Exception e)
        {
        	e.printStackTrace();	
        }
    }
    public  LicenseValidator() throws Exception
	{
    	Setup();
    	licenseFileName = "Spark License.key";
    	BufferedReader licensebf = decrypt(licenseFileName);
    	userInfo.validate(licensebf);
		if(!userInfo.validate(licensebf))
		{
			System.err.println("License not valid ...");
    		System.exit(1);
		}
	}
    public boolean matchMac()
    {
		try{
			List<String> macAddresses = GetMac.getMacAddress();
			ListIterator<String> it = macAddresses.listIterator();
			boolean macMatch = false;
			while(it.hasNext())
			{
				String userMac=it.next();
				List<String> licenseMacAddresses = userInfo.getPhysicalAddresses();
				ListIterator<String> it2 = licenseMacAddresses.listIterator();
				while(it2.hasNext())
				{
					String licenseMac = it2.next();
					if (licenseMac.equals(userMac)||licenseMac.equals("*"))
					{
						macMatch = true;
						return macMatch;
					}
				}
			}
			return macMatch;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
    }
    
    public boolean isDateValid()
    {
    	try
    	{
    		boolean dateValid = false;
    		Calendar today = Calendar.getInstance();
            if (Spark.opts!=null&&Spark.opts.isVersionRequest())
    		System.out.println("Current date is : " +
    			today.get(Calendar.YEAR)
    			+ "-" + (today.get(Calendar.MONTH) + 1)
    			+ "-" + today.get(Calendar.DATE)
    			);
    		
    		Calendar expiryDate = userInfo.getExpiryDate();
            if (Spark.opts!=null&&Spark.opts.isVersionRequest())
    		System.out.println("Expiry date is  : " +
				expiryDate.get(Calendar.YEAR)
	    		+ "-" + (expiryDate.get(Calendar.MONTH) + 1)
	    		+ "-" + expiryDate.get(Calendar.DATE)
	    		);

    		if (today.before(expiryDate))
    		{
    			dateValid = true;
    			return dateValid;
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return false;
    }
    private static BufferedReader decrypt(String fiName)
    {
    	try {
			fis=new FileInputStream(fiName);
		} catch (FileNotFoundException e) {
			System.out.println(fiName+ " file not found");
			//e.printStackTrace();
		}
    	byte[] cipherBytes = new byte[(int)new File(fiName).length()];
    	try {
			fis.read(cipherBytes);
		} catch (IOException e) {
			System.out.println("Can not read file "+fiName);
			e.printStackTrace();
		}
        try {
			cipher.init(Cipher.DECRYPT_MODE, decryptionKey);
		} catch (InvalidKeyException e) {
			System.out.println("Proper Key not formed...");
			e.printStackTrace();
		}
        byte[] recoveredBytes;
		try {
			recoveredBytes = cipher.doFinal(cipherBytes);
	        ByteArrayInputStream bair = new ByteArrayInputStream(recoveredBytes);
	        InputStreamReader isr = new InputStreamReader(bair);
	    	return new BufferedReader(isr);
		} catch (IllegalBlockSizeException e) {
			System.out.println("License File corrupted or unusable...");
			e.printStackTrace();
		} catch (BadPaddingException e) {
			System.out.println("License File corrupted or unusable...");
			e.printStackTrace();
		}
		System.out.println("License File not found...");
		return null;
    }
}
