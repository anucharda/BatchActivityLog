package th.co.ais.cpac.cl.batch.ssfcc.exemptcreditlimit;

import java.math.BigDecimal;
import java.util.Date;

import th.co.ais.cpac.cl.batch.ConstantsBatchActivity;
import th.co.ais.cpac.cl.batch.ConstantsDB;
import th.co.ais.cpac.cl.batch.db.CLBatch;
import th.co.ais.cpac.cl.batch.db.CLBatch.CLBatchPathResponse;
import th.co.ais.cpac.cl.batch.db.CLBatch.ExecuteResponse;
import th.co.ais.cpac.cl.batch.db.CLBatch.GetCLBatchVersionResponse;
import th.co.ais.cpac.cl.batch.db.CLBatchExempt;
import th.co.ais.cpac.cl.batch.db.CLTmpExemptCreditLimit;
import th.co.ais.cpac.cl.batch.db.CLTmpExemptCreditLimit.CLTmpExemptCreditLimitInfo;
import th.co.ais.cpac.cl.batch.db.CLTmpExemptCreditLimit.CLTmpExemptCreditLimitResponse;
import th.co.ais.cpac.cl.batch.template.ProcessTemplate;
import th.co.ais.cpac.cl.batch.util.BatchUtil;
import th.co.ais.cpac.cl.batch.util.FileUtil;
import th.co.ais.cpac.cl.batch.util.GenFileUtil;
import th.co.ais.cpac.cl.batch.util.Utility;
import th.co.ais.cpac.cl.common.Context;

public class GenExemptCreditLimitProcess extends ProcessTemplate {
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

	public void executeProcess(Context context, String jobType) throws Exception {
		context.getLogger().info("Start GenExemptCreditLimitProcess.executeProcess");
		execute();
		BigDecimal batchTypeId=BatchUtil.getBatchTypeId(jobType);
		generate(context,jobType,batchTypeId);
		context.getLogger().info("End GenExemptCreditLimitProcess.executeProcess");
	}

	public void generate(Context context, String jobType,BigDecimal batchTypeId) {

		try {
			context.getLogger().info("Start GenExemptCreditLimitProcess.generate");
			CLTmpExemptCreditLimit  tmpExemptDB= new CLTmpExemptCreditLimit(context.getLogger());
			tmpExemptDB.insertExempCreditLimit(context,batchTypeId);
			genFileProcess(batchTypeId,jobType);
			context.getLogger().info("End GenExemptCreditLimitProcess.generate");
		} catch (Exception e) {
			context.getLogger().info("Error->"+e.getMessage()+": "+e.getCause().toString());
		} finally {
			context.getLogger().info("End GenExemptCreditLimitProcess.generate");
		}
	}
	
	public void genFileProcess(BigDecimal batchTypeId, String jobType) throws Exception{
		/*get batch limit*/
		context.getLogger().info("Start GenExemptCreditLimitProcess.genFileProcess");
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
				String batchEnCoding=batchVersionResult.getResponse().getBatchEncoding();
				String username=Utility.getusername(jobType);
				String outBoundPath=batchPath.getResponse().getPathOutbound();
				
				for(int i=0;i<maxFile.intValue();i++){
					String fileName=GenFileUtil.genFileName("ExemptUpdate_yyyymmdd_hh24miss.dat");
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
					CLTmpExemptCreditLimit tmpExemptCreditLimitDB=new CLTmpExemptCreditLimit(context.getLogger());
					CLTmpExemptCreditLimitResponse result =tmpExemptCreditLimitDB.getTmpExemptCreditLimitInfo(maxRecord, context);
					if(result!=null&&result.getResponse()!=null&&result.getResponse().size()>0){
						String [] genData=new String[result.getResponse().size()];
						BigDecimal [] exempIdArr=new BigDecimal[result.getResponse().size()];
						for(int j=0;j<result.getResponse().size();j++){
							CLTmpExemptCreditLimitInfo info=result.getResponse().get(j);
							StringBuffer tmp=new StringBuffer();
							tmp.append(ConstantsBatchActivity.body).append(ConstantsBatchActivity.delimiter);
							tmp.append(info.getExemptCustomerId()).append(ConstantsBatchActivity.delimiter);
							tmp.append(info.getCaNo()).append(ConstantsBatchActivity.delimiter);
							tmp.append(info.getBaNo()).append(ConstantsBatchActivity.delimiter);
							tmp.append(info.getMobileNo()).append(ConstantsBatchActivity.delimiter);
							tmp.append(info.getExemptMode()).append(ConstantsBatchActivity.delimiter);
							tmp.append(info.getExemptLevel()).append(ConstantsBatchActivity.delimiter);
							tmp.append(info.getChannel()).append(ConstantsBatchActivity.delimiter);
							tmp.append(info.getEffectiveDate()).append(ConstantsBatchActivity.delimiter);
							tmp.append(info.getEndDate()).append(ConstantsBatchActivity.delimiter);
							tmp.append(info.getExpireDate()).append(ConstantsBatchActivity.delimiter);
							tmp.append(info.getDuration()).append(ConstantsBatchActivity.delimiter);
							tmp.append(info.getLocationCode()).append(ConstantsBatchActivity.delimiter);
							tmp.append(info.getReason()).append(ConstantsBatchActivity.delimiter);
							genData[j]=tmp.toString();		
							exempIdArr[j]=info.getExemptCustomerId();
						}
						GenFileUtil.genFile(genData, fileName,outBoundPath,batchEnCoding);
						//Update batch to complete
						batchDB.updateOutboundCompleteStatus(batchID, username, context);
						//Update gen flag
						tmpExemptCreditLimitDB.updateGenFileResultComplete(maxRecord, context);
						//Update Treatment -- Tuning เป็น Update Top น่าจะดีกว่า
						CLBatchExempt batchExemptDB=new CLBatchExempt(context.getLogger());
						for(int k=0;k<exempIdArr.length;k++){
							CLBatchExempt.CLBatchExemptInfo batchExemptInfo = batchExemptDB.buildCLBatchExemptInfo();
							batchExemptInfo.setBatchId(batchID);
							batchExemptInfo.setExemptCustomerId(exempIdArr[k]);
							batchExemptInfo.setCreatedBy(username);
							batchExemptInfo.setLastUpdBy(username);
							batchExemptDB.insertCLBatchExempt(batchExemptInfo);
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
		context.getLogger().info("End GenExemptCreditLimitProcess.genFileProcess");
	}
	
	

}
