package th.co.ais.cpac.cl.batch.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class GenFileUtil {
	public static void genFile(String[] genData,String fileName,String outBoundPath){
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
	
}
