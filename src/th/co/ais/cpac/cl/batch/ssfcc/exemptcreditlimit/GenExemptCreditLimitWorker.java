package th.co.ais.cpac.cl.batch.ssfcc.exemptcreditlimit;

import th.co.ais.cpac.cl.batch.util.LogUtil;
import th.co.ais.cpac.cl.common.Context;

public class GenExemptCreditLimitWorker {

	public static void main(String[] args) throws Exception {
		LogUtil.initialLogger();
		Context context = new Context();
		try{

			context.initailLogger("LoggerReceive", "GenExemptCreditLimitWorker");
			// TODO Auto-generated method stub
			context.getLogger().info("----------------------- Start GenExemptCreditLimitWorker -----------------------");
			context.getLogger().info("Load configure....");
			String jobType=args[0];//From Parameter

			System.out.println("jobType ->"+jobType);
			//New thread for execute process.
			new Thread (()->execute(context, jobType)).start();
			context.getLogger().info("----------------------- End GenExemptActivityLogWorker ----------------------- ");
		}catch(Exception e){
			e.printStackTrace();
			context.getLogger().error(  "Error->"+e.getMessage()+": "+e.getCause().toString() ,e);
		}
	}
	
	public static void execute(Context context,String jobType){
		try{
			 context.getLogger().info("Start GenExemptCreditLimitProcess Execute....");
			 context.getLogger().info("Trigger Gen Exempt Credit Limit Process....");
			 new GenExemptCreditLimitProcess().executeProcess(context,jobType);
			 context.getLogger().info("End GenExemptCreditLimitProcess Execute....");
		}catch(Exception e){
			e.printStackTrace();
			context.getLogger().error(  "Error->"+e.getMessage()+": "+e.getCause().toString() ,e);
		}
	}
	
	
}
