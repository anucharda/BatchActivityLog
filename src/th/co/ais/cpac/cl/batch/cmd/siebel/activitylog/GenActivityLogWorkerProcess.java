package th.co.ais.cpac.cl.batch.cmd.siebel.activitylog;

import java.math.BigDecimal;
import java.util.Date;

import th.co.ais.cpac.cl.batch.ConstantsBatchActivity;
import th.co.ais.cpac.cl.batch.ConstantsDB;
import th.co.ais.cpac.cl.batch.db.CLBatch;
import th.co.ais.cpac.cl.batch.db.CLBatch.CLBatchPathResponse;
import th.co.ais.cpac.cl.batch.db.CLBatch.ExecuteResponse;
import th.co.ais.cpac.cl.batch.db.CLBatch.GetCLBatchVersionResponse;
import th.co.ais.cpac.cl.batch.db.CLTmpActSiebel;
import th.co.ais.cpac.cl.batch.db.CLTmpActSiebel.CLTmpActSiebelInfo;
import th.co.ais.cpac.cl.batch.db.CLTmpActSiebel.CLTmpActSiebelResponse;
import th.co.ais.cpac.cl.batch.template.ProcessTemplate;
import th.co.ais.cpac.cl.batch.util.BatchUtil;
import th.co.ais.cpac.cl.batch.util.FileUtil;
import th.co.ais.cpac.cl.batch.util.GenFileUtil;
import th.co.ais.cpac.cl.batch.util.Utility;
import th.co.ais.cpac.cl.common.Context;

public class GenActivityLogWorkerProcess extends ProcessTemplate {
	@Override
	protected String getPathDatabase() {
		// TODO Auto-generated method stub
		String dbPath="";
		try{
			dbPath=FileUtil.getDBPath();
		}catch(Exception e){
			context.getLogger().info("Error->"+e.getMessage()+": "+e.getCause().toString());
		}
		return  dbPath;
	}

	public void executeProcess(Context context, String jobType,String processName) throws Exception {
		context.getLogger().info("Start GenActivityLogWorkerProcess.executeProcess");
		execute();
		BigDecimal batchTypeId=BatchUtil.getBatchTypeId(jobType);
		if("SMS–Outbound".equals(processName)){
			generateSMSBound(context,jobType,batchTypeId);
		}
		context.getLogger().info("End GenActivityLogWorkerProcess.executeProcess");
	}

	public void generateSMSBound(Context context, String jobType,BigDecimal batchTypeId) {
		// อ่าน file ทีละ record
		// เช็คว่า record แรกของไฟล์เป็น order type ไหน
		// จบไฟล์ค่อย update treatement โดย grouping ตาม BA,update batch ต่อ
		try {
			context.getLogger().info("Start GenActivityLogWorkerProcess.generateSMSBound");
			CLTmpActSiebel tmpActSiebelDB= new CLTmpActSiebel(context.getLogger());
			tmpActSiebelDB.insertSMSOutBound(context);
			
			
			
			context.getLogger().info("End GenActivityLogWorkerProcess.generateSMSBound");
		} catch (Exception e) {
			context.getLogger().info("Error->"+e.getMessage()+": "+e.getCause().toString());
		} finally {
			context.getLogger().info("End GenActivityLogWorkerProcess.generateSMSBound");
		}
	}
	
	public void genFileProcess(String processName,BigDecimal batchTypeId, String jobType) throws Exception{
		/*get batch limit*/
		context.getLogger().info("Start GenActivityLogWorkerProcess.genFileProcess");
		CLBatch batchDB= new CLBatch(context.getLogger());
		GetCLBatchVersionResponse batchVersionResult=batchDB.getCLBatchVersion(batchTypeId);

		if(batchVersionResult!=null &&batchVersionResult.getResponse()!=null){
			int environment=BatchUtil.getEnvionment();
			CLBatchPathResponse batchPath=batchDB.getCLBatchPath(batchTypeId, environment);
			if(batchPath!=null&&batchPath.getResponse()!=null){
				BigDecimal maxRecord=batchVersionResult.getResponse().getLimitPerFile();
				BigDecimal maxFile=batchVersionResult.getResponse().getLimitPerDay();
				BigDecimal batchVersion=batchVersionResult.getResponse().getBatchVersionNo();
				String formatFileName=batchVersionResult.getResponse().getBatchNameFormat();
				String username=Utility.getusername(jobType);
				String outBoundPath=batchPath.getResponse().getPathOutbound();
				
				for(int i=0;i<maxRecord.intValue();i++){
					String fileName=GenFileUtil.genFileName("PLUGIN_ACTV_yyyymmdd_hh24miss.dat");
					/*Insert Batch*/
					CLBatch.CLBatchInfo batchInfo = batchDB.buildCLBatchInfo();
					batchInfo.setBatchTypeId(batchTypeId);
					batchInfo.setBatchVersionNo(batchVersion);
					batchInfo.setBatchStartDtm(new Date());
					batchInfo.setBatchFileName(GenFileUtil.genFileName(formatFileName));
					batchInfo.setOutboundStatus(ConstantsDB.OutboundStatus.Generating);
					batchInfo.setOutboundStatusDtm(new Date());
					batchInfo.setInboundStatus(ConstantsDB.InboundStatus.NoInboundResponse);
					batchInfo.setCreated(new Date());
					batchInfo.setCreatedBy(username);
					batchInfo.setLastUpd(new Date());
					batchInfo.setLastUpdBy(username);
					ExecuteResponse insertResult=batchDB.insertCLBatch(batchInfo);
					
					 BigDecimal batchID = insertResult.getIdentity();
					/*Get Data Top*/
					CLTmpActSiebel tmpActSiebelDB=new CLTmpActSiebel(context.getLogger());
					CLTmpActSiebelResponse result =tmpActSiebelDB.getTmpActSiebelInfo(processName, maxRecord, context);
					if(result!=null&&result.getResponse()!=null&&result.getResponse().size()>0){
						String [] genData=new String[result.getResponse().size()];
						for(int j=0;j<result.getResponse().size();j++){
							CLTmpActSiebelInfo info=result.getResponse().get(j);
							StringBuffer tmp=new StringBuffer();
							tmp.append(ConstantsBatchActivity.body).append(ConstantsBatchActivity.delimiter);
							tmp.append(info.getCaNo()).append(ConstantsBatchActivity.delimiter);
							tmp.append(info.getBaNo()).append(ConstantsBatchActivity.delimiter);
							tmp.append(info.getMobileNo()).append(ConstantsBatchActivity.delimiter);
							tmp.append(info.getJobType()).append(ConstantsBatchActivity.delimiter);
							tmp.append(info.getCategory()).append(ConstantsBatchActivity.delimiter);
							tmp.append(info.getSubcateory()).append(ConstantsBatchActivity.delimiter);
							tmp.append("").append(ConstantsBatchActivity.delimiter);//Planned Start
							tmp.append("").append(ConstantsBatchActivity.delimiter);//Planned Completion
							tmp.append("").append(ConstantsBatchActivity.delimiter);//No Sooner Than
							tmp.append("").append(ConstantsBatchActivity.delimiter);//Actual Start
							tmp.append(info.getActionStatusDtm()).append(ConstantsBatchActivity.delimiter);
							tmp.append("").append(ConstantsBatchActivity.delimiter);//Due
							tmp.append("").append(ConstantsBatchActivity.delimiter);//Priority
							tmp.append("").append(ConstantsBatchActivity.delimiter);//Description
							tmp.append("").append(ConstantsBatchActivity.delimiter);//More Info
							tmp.append("").append(ConstantsBatchActivity.delimiter);//Status
							tmp.append(info.getOwner()).append(ConstantsBatchActivity.delimiter);
							tmp.append("").append(ConstantsBatchActivity.delimiter);//Document#
							tmp.append("").append(ConstantsBatchActivity.delimiter);//Sub Status
							tmp.append("").append(ConstantsBatchActivity.delimiter);//Reason
							tmp.append("").append(ConstantsBatchActivity.delimiter);//Order#
							tmp.append("");//SR#
							GenFileUtil.genFile(genData, fileName,outBoundPath);
						}
						
					}else{
						context.getLogger().info("Cannot Find Data to Gent");
					}
				}
			}else{
				context.getLogger().info("Cannot Get Batch Path");
			}
		}else{
			context.getLogger().info("Cannot Get Batch Version");
		}
		context.getLogger().info("End GenActivityLogWorkerProcess.genFileProcess");
	}
	
	

}
