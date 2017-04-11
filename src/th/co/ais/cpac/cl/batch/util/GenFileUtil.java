package th.co.ais.cpac.cl.batch.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import th.co.ais.cpac.cl.common.Context;

public class GenFileUtil {
	
	public static final String lineSeparator = System.getProperty("line.separator");
	
	public static void genFile(String[] genData,String dataFileName,String outBoundPath,String encode,String header,String footer,int environment,String processPath,String syncFileName,Context context) throws Exception{
		String fileNamePath = processPath+"/"+dataFileName;
		 Writer writer = null;
		 try{
			 if(environment==1 || environment==3 ){//1-Prod,3-SIT
				 if("ANSI".equals(encode)){
					 encode="TIS-620";
				 }
			 }
			 else{
				 if("ANSI".equals(encode)){
					 encode="TIS-620";
				 }
			}
			
			 writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileNamePath), encode));
			 if(!ValidateUtil.isNull(header)){
				 writer.write(header);
			 	 writer.write(lineSeparator);
			 }
			 for(int i=0; i<genData.length; i++){
				 writer.write(genData[i]);
				 writer.write(lineSeparator);
			 }
			 if(!ValidateUtil.isNull(footer))
			 {
				 writer.write(footer);
			 }
		 }catch(Exception e){
			 e.printStackTrace();
			 throw e;
		 }finally{
			 try {writer.close();} catch (Exception ex) {/*ignore*/}
		 }
		 
		String dataFileSize=getFileSize(fileNamePath);
		 
		
		
		/*Copy data*/
		File dataSource = new File(processPath+"/"+dataFileName);
		context.getLogger().info("Outbound--> "+outBoundPath+"/"+dataFileName);
		if(dataSource.exists()){
			File dataDest = new File(outBoundPath+"/"+dataFileName);
			FileUtil.copyFile(dataSource, dataDest);
			context.getLogger().info("Copy file to process directory successed --> "+dataFileName);
		}else{
			context.getLogger().info("Cannot data file");
		}
		/*Copy File Sync*/
		 if(!ValidateUtil.isNull(syncFileName)){
			 createSyncFile(syncFileName, processPath,dataFileName,dataFileSize,environment,encode);
			 File syncSource = new File(processPath+"/"+syncFileName);
			 if(syncSource.exists()){
				 File syncDest = new File(outBoundPath+"/"+syncFileName);
					FileUtil.copyFile(syncSource, syncDest);
					context.getLogger().info("Copy file to process directory successed --> "+syncFileName);
				}else{
					context.getLogger().info("Cannot data file");
				}
		 }
			 

	}
	public synchronized static String genFileName(String nameFormat) throws InterruptedException{
		Thread.sleep(1000);
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.US);
		String yyyymmddStr = dateFormat.format(date);
//		Calendar cal = Calendar.getInstance(Locale.US);
//		DateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
//		String yyyymmddStr = format.format(cal.getTime());
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
	
	public static void createSyncFile(String syncfileName, String filePath,String dataFileName,String dataFileSize,int environment,String encode) throws IOException{
		String fileNamePath = filePath+"/"+syncfileName;
		 Writer writer = null;
		 try{
			 if(environment==1 || environment==3 ){//1-Prod,3-SIT
				 if("ANSI".equals(encode)){
					 encode="Cp1252";
				 }
			 }
			 else{
				 if("ANSI".equals(encode)){
					 encode="Cp1252";
				 }
			}
			
			 writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileNamePath), encode));
			 writer.write(dataFileName+"|"+dataFileSize);
		 }catch(Exception e){
			 e.printStackTrace();
			 throw e;
		 }finally{
			 try {writer.close();} catch (Exception ex) {/*ignore*/}
		 }
	}
	public static String getFileSize(String filePath){
		File file =new File(filePath);

		if(file.exists()){
			return  String.valueOf(file.length());
		}else{
			return "0";
		}
	}
	public static BigDecimal getMaxFile(int totalRecord,BigDecimal maxRecord){
		int maxLoop=totalRecord/maxRecord.intValue();
		if(totalRecord%maxRecord.intValue()>0){
			maxLoop=maxLoop+1;
		}
		return BigDecimal.valueOf(maxLoop);
	}
}
