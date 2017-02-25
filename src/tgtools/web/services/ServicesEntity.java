package tgtools.web.services;

import java.util.Date;

import tgtools.data.DataRow;
import tgtools.exceptions.APPErrorException;
import tgtools.util.StringUtil;
/**
 * 服务实体类
 * @author marjuce
 *
 */
public class ServicesEntity {
	public static final String State_Start="运行";
	public static final String State_Ready="已加载";
	public static final String State_Pause="暂停";
	public static final String State_Stop="已停止";
	public static final String State_Stoping="正在停止中";
	public static final String State_Unload="已卸载";
	
	private String ID_;
	private long  REV_;
	private String NAME;
	private String PATH;
	private String CLASSNAME;
	private Date STARTTIME;
	private Date ENDTIME;
	private String STATE;
	public String getID_() {
		return ID_;
	}
	public void setID_(String iD_) {
		ID_ = iD_;
	}
	public long getREV_() {
		return REV_;
	}
	public void setREV_(long rEV_) {
		REV_ = rEV_;
	}
	public String getNAME() {
		return NAME;
	}
	public void setNAME(String nAME) {
		NAME = nAME;
	}
	public String getPATH() {
		return PATH;
	}
	public void setPATH(String pATH) {
		PATH = pATH;
	}
	public String getCLASSNAME() {
		return CLASSNAME;
	}
	public void setCLASSNAME(String cLASSNAME) {
		CLASSNAME = cLASSNAME;
	}
	public Date getSTARTTIME() {
		return STARTTIME;
	}
	public void setSTARTTIME(Date sTARTTIME) {
		STARTTIME = sTARTTIME;
	}
	public Date getENDTIME() {
		return ENDTIME;
	}
	public void setENDTIME(Date eNDTIME) {
		ENDTIME = eNDTIME;
	}
	public String getSTATE() {
		return STATE;
	}
	public void setSTATE(String sTATE) {
		STATE = sTATE;
	}
	public static ServicesEntity paser(DataRow p_Row)throws APPErrorException
	{
		if(null==p_Row||StringUtil.isNullOrEmpty(p_Row.getValue("ID_").toString()))
		{
			throw new APPErrorException("无效的服务数据行");
		}
		ServicesEntity entity=new ServicesEntity();
		entity.setID_(p_Row.getValue("ID_").toString());
		if(p_Row.getValue("REV_") instanceof Long){
		entity.setREV_((Long)p_Row.getValue("REV_"));
		}
		entity.setNAME(p_Row.getValue("NAME").toString());
		entity.setCLASSNAME(p_Row.getValue("CLASSNAME").toString());
		entity.setPATH(p_Row.getValue("PATH").toString());
		if(p_Row.getValue("STARTTIME") instanceof Date){
		entity.setSTARTTIME((Date)p_Row.getValue("STARTTIME"));
		}
		if(p_Row.getValue("ENDTIME") instanceof Date){
		entity.setENDTIME((Date)p_Row.getValue("ENDTIME"));
		}
		entity.setSTATE(p_Row.getValue("STATE").toString());
		
		return entity;
	}
}
