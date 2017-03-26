package th.co.ais.cpac.cl.batch.cmd.siebel.exemptlog;

import th.co.ais.cpac.cl.batch.util.LogUtil;
import th.co.ais.cpac.cl.common.Context;

public class GenExemptActivityLogWorker {

	public static void main(String[] args) throws Exception {
		LogUtil.initialLogger();
		Context context = new Context();
		try{

			context.initailLogger("LoggerReceive", "GenExemptActivityLogWorker");
			// TODO Auto-generated method stub
			context.getLogger().info("----------------------- Start GenExemptActivityLogWorker -----------------------");
			context.getLogger().info("Load configure....");
			String jobType=args[0];//From Parameter

			System.out.println("jobType ->"+jobType);
			//New thread for execute process.
			//new Thread ( () -> execute(context, jobType,ConstantsBatchActivity.smsActivityLog) ).start();
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
			 context.getLogger().info("Trigger Gen Activity Exmept Log Process....");
			 new GenExemptActivityLogWorkerProcess().executeProcess(context,jobType);
			 context.getLogger().info("End GenExemptActivityLogProcess Execute....");
		}catch(Exception e){
			e.printStackTrace();
			context.getLogger().error(  "Error->"+e.getMessage()+": "+e.getCause().toString() ,e);
		}
	}
	
	
}
