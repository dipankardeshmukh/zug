package com.automature.zug.util;
/*
	Class to zip file(s).
	Please specify absolute path names as its parameters while running this program.
*/
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.lang.StringUtils;

public class ZipUtility {
 
	ZipOutputStream cpZipOutputStream = null;
	String strSource = StringUtils.EMPTY;
	String strTarget = StringUtils.EMPTY;
	
	
	public void zip(String strSource,String strTarget){
		System.out.println("\nZipping Files : "+strSource);
		try
		{
			this.strSource=strSource;
			this.strTarget=strTarget;
			File cpFile = new File (this.strSource);
			if (!cpFile.exists()) 
			{
				System.out.println("\nSource file/directory Not Found!");
				Log.Error("\nSource file/directory Not Found!");
				return;
			}
		cpZipOutputStream = new ZipOutputStream(new FileOutputStream(new File(this.strTarget)));
		cpZipOutputStream.setLevel(9);
		zipFiles( cpFile);
		cpZipOutputStream.finish();
		cpZipOutputStream.close();
		//System.out.println("\nFinished creating zip file " + strTarget + " from source " + strSource);
		//Log.Debug("\nFinished creating zip file " + strTarget + " from source " + strSource);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
 
	private void  zipFiles(File cpFile) {
		if (cpFile.isDirectory()) {
			File [] fList = cpFile.listFiles() ;
			for (int i=0; i< fList.length; i++){
				zipFiles(fList[i]) ;
			}
		} else {
			try {
				String strAbsPath = cpFile.getAbsolutePath();
				String strZipEntryName = strAbsPath.substring(strSource.length()+1, strAbsPath.length());
				byte[] b = new byte[ (int)(cpFile.length()) ];
				FileInputStream cpFileInputStream = new FileInputStream (cpFile) ;
 
				int i = cpFileInputStream.read(b, 0, (int)cpFile.length());
 
				ZipEntry cpZipEntry = new ZipEntry(strZipEntryName);
				cpFileInputStream.close();
				cpZipOutputStream.putNextEntry(cpZipEntry );
 
				cpZipOutputStream.write(b, 0, (int)cpFile.length());
				cpZipOutputStream.closeEntry() ;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
 