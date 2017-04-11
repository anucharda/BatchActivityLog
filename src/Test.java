import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import th.co.ais.cpac.cl.batch.util.GenFileUtil;

public class Test {

	public static void main(String[] args) throws ParseException {
		// TODO Auto-generated method stub
		System.out.print(GenFileUtil.getMaxFile(80,new BigDecimal("50")));
		Date date = new Date();
		System.out.println(date.toString());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String yyyymmddStr = dateFormat.format(date);
		System.out.println(yyyymmddStr);
	}
}
