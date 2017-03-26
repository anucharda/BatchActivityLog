package th.co.ais.cpac.cl.batch.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class GenFileUtil {
	
	public static final String lineSeparator = System.getProperty("line.separator");
	
	public static void genFile(String[] genData,String fileName,String outBoundPath,String encode) throws Exception{
		String fileNamePath = outBoundPath+"/"+fileName;
		 Writer writer = null;
		 try{
			 writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileNamePath), encode));
			 writer.write("Header");
			 for(int i=0; i<genData.length; i++){
				 writer.write(genData[i]);
				 writer.write(lineSeparator);
			 }
			 writer.write("Footer");
		 }catch(Exception e){
			 throw e;
		 }finally{
			 try {writer.close();} catch (Exception ex) {/*ignore*/}
		 }
		 
		//1.Gen Data (fileName)
			//01|20070828_160817
			//genData ->body
			//09|1
		//2.Put to Path
		//3.Create File.sync
		//File Name: fileName.replace นามสกุลเป็น .sync
		//->BodyPLUGIN_ACTV_20080428_000001.dat|1258 (bytes)

	}
	public static String genFileName(String nameFormat){
		Calendar cal = Calendar.getInstance(Locale.US);
		DateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String yyyymmddStr = format.format(cal.getTime());
		return nameFormat.replaceAll("yyyymmdd_hh24miss", yyyymmddStr);
		
	}
	
	public static void createFile(String fileName, String filePath) throws IOException{
		String fileNamePath = filePath+"/"+fileName;
		createFile(fileNamePath);
	}
	
	public static void createFile(String fileNamePath) throws IOException{
        File file = new File(fileNamePath);
        // If file doesn't exists, then create it
        if (!file.exists()) {
            file.createNewFile();
        }
	}
	
}
