package th.co.ais.cpac.cl.batch.ssfcc.exemptbldl;

import th.co.ais.cpac.cl.batch.util.LogUtil;
import th.co.ais.cpac.cl.common.Context;

public class GenExemptBlacklistDeblacklistWorker {

	public static void main(String[] args) throws Exception {
		LogUtil.initialLogger();
		Context context = new Context();
		try{

			context.initailLogger("LoggerReceive", "ExemptBlacklistDeblacklistWorker");
			// TODO Auto-generated method stub
			context.getLogger().info("----------------------- Start ExemptBlacklistDeblacklistWorker -----------------------");
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
			 context.getLogger().info("Start GenExemptActivityLogProcess Execute....");
			 context.getLogger().info("Trigger Gen Exempt Blacklist/Deblacklist Process....");
			 new GenExemptBlacklistDeblacklistProcess().executeProcess(context,jobType);
			 context.getLogger().info("End GenExemptActivityLogProcess Execute....");
		}catch(Exception e){
			e.printStackTrace();
			context.getLogger().error(  "Error->"+e.getMessage()+": "+e.getCause().toString() ,e);
		}
	}
	
	
}
