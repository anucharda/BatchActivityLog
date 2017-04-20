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
import th.co.ais.cpac.cl.batch.db.CLTreatment;
import th.co.ais.cpac.cl.batch.template.ProcessTemplate;
import th.co.ais.cpac.cl.batch.util.BatchUtil;
import th.co.ais.cpac.cl.batch.util.FileUtil;
import th.co.ais.cpac.cl.batch.util.GenFileUtil;
import th.co.ais.cpac.cl.batch.util.PropertiesReader;
import th.co.ais.cpac.cl.batch.util.Utility;
import th.co.ais.cpac.cl.common.Context;

public class GenActivityLogProcess extends ProcessTemplate {
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
		if(ConstantsBatchActivity.smsActivityLog.equals(processName)){
			generateSMSBound(context,jobType,batchTypeId);
		}else if(ConstantsBatchActivity.letterActivityLog.equals(processName)){
			generateLetterOutBound(context,jobType,batchTypeId);
		}else if(ConstantsBatchActivity.debtActivityLog.equals(processName)){
			generateDebtOutBound(context,jobType,batchTypeId);
		}
		context.getLogger().info("End GenActivityLogWorkerProcess.executeProcess");
	}

	public void generateSMSBound(Context context, String jobType,BigDecimal batchTypeId) {

		try {
			context.getLogger().info("Start GenActivityLogWorkerProcess.generateSMSBound");
			CLTmpActSiebel tmpActSiebelDB= new CLTmpActSiebel(context.getLogger());
			tmpActSiebelDB.insertSMSOutBound(context);
			genFileProcess(ConstantsBatchActivity.smsActivityLog,batchTypeId,jobType);
			context.getLogger().info("End GenActivityLogWorkerProcess.generateSMSBound");
		} catch (Exception e) {
			context.getLogger().info("Error->"+e.getMessage()+": "+e.getCause().toString());
		} finally {
			context.getLogger().info("End GenActivityLogWorkerProcess.generateSMSBound");
		}
	}
	public void generateLetterOutBound(Context context, String jobType,BigDecimal batchTypeId) {

		try {
			context.getLogger().info("Start GenActivityLogWorkerProcess.generateLetterOutBound");
			CLTmpActSiebel tmpActSiebelDB= new CLTmpActSiebel(context.getLogger());
			tmpActSiebelDB.insertLetterOutBound(context);
			genFileProcess(ConstantsBatchActivity.letterActivityLog,batchTypeId,jobType);
			context.getLogger().info("End GenActivityLogWorkerProcess.generateLetterOutBound");
		} catch (Exception e) {
			context.getLogger().info("Error->"+e.getMessage()+": "+e.getCause().toString());
		} finally {
			context.getLogger().info("End GenActivityLogWorkerProcess.generateLetterOutBound");
		}
	}
	public void generateDebtOutBound(Context context, String jobType,BigDecimal batchTypeId) {

		try {
			context.getLogger().info("Start GenActivityLogWorkerProcess.generateDebtOutBound");
			CLTmpActSiebel tmpActSiebelDB= new CLTmpActSiebel(context.getLogger());
			tmpActSiebelDB.insertDebtOutBound(context);
			genFileProcess(ConstantsBatchActivity.debtActivityLog,batchTypeId,jobType);
			context.getLogger().info("End GenActivityLogWorkerProcess.generateDebtOutBound");
		} catch (Exception e) {
			context.getLogger().info("Error->"+e.getMessage()+": "+e.getCause().toString());
		} finally {
			context.getLogger().info("End GenActivityLogWorkerProcess.generateDebtOutBound");
		}
	}
	public void genFileProcess(String processName,BigDecimal batchTypeId, String jobType) throws Exception{
		/*get batch limit*/
		context.getLogger().info("Start GenActivityLogWorkerProcess.genFileProcess:"+processName);
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
				String batchFileType=batchVersionResult.getResponse().getBatchFileType();
				String batchDelimit=batchVersionResult.getResponse().getBatchDelimiter().replace("'", "");
				String batchEnCoding=batchVersionResult.getResponse().getBatchEncoding();
				
				String username=Utility.getusername(jobType);
				String outBoundPath=batchPath.getResponse().getPathOutbound();
				CLTmpActSiebel tmpActSiebelDB=new CLTmpActSiebel(context.getLogger());
				
				int totalDataRecord=tmpActSiebelDB.countTmpActSiebel(processName, context);
				BigDecimal calMaxFile=GenFileUtil.getMaxFile(totalDataRecord,maxRecord);
				
				if(calMaxFile.intValue()<maxFile.intValue()){
					maxFile=calMaxFile;
				}
				
				for(int i=0;i<maxFile.intValue();i++){
					String fileName=GenFileUtil.genFileName(formatFileName+batchFileType);
					StringBuffer header=new StringBuffer();
					header.append(ConstantsBatchActivity.header).append(batchDelimit).append(fileName.replaceAll("PLUGIN_ACTV_", "").replaceAll(batchFileType, ""));				
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
					
					CLTmpActSiebelResponse result =tmpActSiebelDB.getTmpActSiebelInfo(processName, maxRecord, context);
					if(result!=null&&result.getResponse()!=null&&result.getResponse().size()>0){
						String [] genData=new String[result.getResponse().size()];
						BigDecimal [] treatmentArr =new BigDecimal[result.getResponse().size()];
						int totalRecord=0;
						for(int j=0;j<result.getResponse().size();j++){
							CLTmpActSiebelInfo info=result.getResponse().get(j);
							StringBuffer tmp=new StringBuffer();
							tmp.append(ConstantsBatchActivity.body).append(batchDelimit);
							tmp.append(info.getCaNo()).append(batchDelimit);
							tmp.append(info.getBaNo()).append(batchDelimit);
							tmp.append(info.getMobileNo().replaceAll(" ", "")).append(batchDelimit);
							tmp.append(info.getJobType()).append(batchDelimit);
							//tmp.append(info.getCategory()).append(batchDelimit);
							//tmp.append(info.getSubcateory()).append(batchDelimit);
							tmp.append("018").append(batchDelimit);
							tmp.append("21").append(batchDelimit);
							tmp.append("").append(batchDelimit);//Planned Start
							tmp.append("").append(batchDelimit);//Planned Completion
							tmp.append("").append(batchDelimit);//No Sooner Than
							tmp.append("").append(batchDelimit);//Actual Start
							tmp.append(info.getActionStatusDtm()).append(batchDelimit);
							tmp.append("").append(batchDelimit);//Due
							tmp.append("").append(batchDelimit);//Priority
							tmp.append("").append(batchDelimit);//Description
							tmp.append("").append(batchDelimit);//More Info
							tmp.append("").append(batchDelimit);//Status
							tmp.append(info.getOwner()).append(batchDelimit);
							tmp.append("").append(batchDelimit);//Document#
							tmp.append("").append(batchDelimit);//Sub Status
							tmp.append("").append(batchDelimit);//Reason
							tmp.append("").append(batchDelimit);//Order#
							tmp.append("");//SR		
							genData[j]=tmp.toString();
							treatmentArr[j]=info.getTreatmentId();
							totalRecord=j+1;
						}
						StringBuffer footer=new StringBuffer();
						footer.append(ConstantsBatchActivity.footer).append(batchDelimit).append(String.valueOf(totalRecord));
						PropertiesReader reader = new PropertiesReader("th.co.ais.cpac.cl.batch.properties.resource","SystemConfigPath");
						String processPath=reader.get("sb.activity.log.processPath");
						String syncFileName=fileName.replace(".dat", ".sync");
						GenFileUtil.genFile(genData, fileName,outBoundPath,batchEnCoding,header.toString(),footer.toString(),environment,processPath,syncFileName,context);
						//Update batch to complete
						batchDB.updateOutboundCompleteStatus(batchID, username, context);
						//Update gen flag
						tmpActSiebelDB.updateGenFileResultComplete(maxRecord, processName, context);
						//Update Treatment -- Tuning เป็น Update Top น่าจะดีกว่า
						CLTreatment treatDB=new CLTreatment(context.getLogger());
						for(int k=0;k<treatmentArr.length;k++){
							treatDB.updateGenActivityLogResult(treatmentArr[k], username, context);
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
		context.getLogger().info("End GenActivityLogWorkerProcess.genFileProcess:"+processName);
	}
	
	

}
