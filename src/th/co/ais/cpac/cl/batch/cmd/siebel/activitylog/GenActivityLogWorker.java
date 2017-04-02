package th.co.ais.cpac.cl.batch.cmd.siebel.activitylog;

import th.co.ais.cpac.cl.batch.ConstantsBatchActivity;
import th.co.ais.cpac.cl.batch.util.LogUtil;
import th.co.ais.cpac.cl.batch.util.PropertiesReader;
import th.co.ais.cpac.cl.common.Context;

public class GenActivityLogWorker {

	public static void main(String[] args) throws Exception {
		LogUtil.initialLogger();
		Context context = new Context();
		try{

			context.initailLogger("LoggerReceive", "GenActivityLogWorker");
			// TODO Auto-generated method stub
			context.getLogger().info("----------------------- Start GenActivityLogWorker -----------------------");
			context.getLogger().info("Load configure....");
			String jobType=args[0];//From Parameter

			System.out.println("jobType ->"+jobType);
			//New thread for execute process.
			//new Thread ( () -> execute(context, jobType,ConstantsBatchActivity.smsActivityLog) ).start();
			new Thread (()->execute(context, jobType,ConstantsBatchActivity.smsActivityLog)).start();
			new Thread (()->execute(context, jobType,ConstantsBatchActivity.letterActivityLog)).start();
			new Thread (()->execute(context, jobType,ConstantsBatchActivity.debtActivityLog)).start();
			context.getLogger().info("----------------------- End GenActivityLogWorker ----------------------- ");
		}catch(Exception e){
			e.printStackTrace();
			context.getLogger().error(  "Error->"+e.getMessage()+": "+e.getCause().toString() ,e);
		}
	}
	
	public static void execute(Context context,String jobType,String processName){
		try{
			 context.getLogger().info("Start GenActivityLogWorkerProcess Execute....");
			 context.getLogger().info("Trigger Gen Activity Log Process....");
			 new GenActivityLogProcess().executeProcess(context,jobType,processName);
			 context.getLogger().info("End GenActivityLogWorkerProcess Execute....");
		}catch(Exception e){
			e.printStackTrace();
			context.getLogger().error(  "Error->"+e.getMessage()+": "+e.getCause().toString() ,e);
		}
	}
	
	
}
