import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import th.co.ais.cpac.cl.batch.util.GenFileUtil;
import th.co.ais.cpac.cl.batch.util.Utility;

public class Test {

	public static void main(String[] args) throws ParseException {
		// TODO Auto-generated method stub
		System.out.print(GenFileUtil.getMaxFile(80,new BigDecimal("50")));

	}
}
