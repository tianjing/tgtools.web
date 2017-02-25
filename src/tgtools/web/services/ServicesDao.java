package tgtools.web.services;

import java.util.ArrayList;
import java.util.List;

import tgtools.data.DataTable;
import tgtools.exceptions.APPErrorException;
import tgtools.util.StringUtil;

/**
 * 
 * @author TianJing
 *
 */
public class ServicesDao {
	
	/**
	 * 获取服务
	 * @return
	 * @throws APPErrorException
	 */
	public static DataTable getServices() throws APPErrorException
	{
		String sql="select * from SERVICES";
		return tgtools.db.DataBaseFactory.getDefault().Query(sql);
	}
	
	/**
	 * 获取所有服务
	 * @return
	 * @throws APPErrorException
	 */
	public static List<ServicesEntity> getAllServices() throws APPErrorException
	{
		List<ServicesEntity> result =new ArrayList<ServicesEntity>();
		DataTable dt= getServices();
		for(int i=0;i<dt.getRows().size();i++)
		{
			ServicesEntity entity=ServicesEntity.paser(dt.getRow(i));
			if(null!=entity)
			{
				result.add(entity);
			}
		}
		
		return result;
	}
	
	/**
	 * 通过id获取服务
	 * @param p_ID
	 * @return
	 * @throws APPErrorException
	 */
	public static ServicesEntity getServiceByID(String p_ID) throws APPErrorException
	{
		String sql="select * from SERVICES where ID_='"+p_ID+"'";
		DataTable dt= tgtools.db.DataBaseFactory.getDefault().Query(sql);
		return ServicesEntity.paser(DataTable.getFirstRow(dt));
	}
	/**
	 * 将ENDTIME改为当前时间
	 * @param p_ID
	 * @throws APPErrorException
	 */
	public static void updateRunTime(String p_ID) throws APPErrorException
	{
		String startsql="update SERVICES set ENDTIME=SYSDATE WHERE ID_='"+p_ID+"'";
		tgtools.db.DataBaseFactory.getDefault().executeUpdate(startsql);
	}
	/**
	 * 将STARTTIME改为当前时间
	 * @param p_ID
	 * @throws APPErrorException
	 */
	public static void updateStartTime(String p_ID) throws APPErrorException
	{
		String startsql="update SERVICES set STARTTIME= SYSDATE  WHERE ID_='"+p_ID+"'";
		tgtools.db.DataBaseFactory.getDefault().executeUpdate(startsql);
	}
	/**
	 * 更改服务状态
	 * @param p_ID
	 * @param state
	 * @throws APPErrorException
	 */
	public static void updateState(String p_ID,String state) throws APPErrorException
	{
		if(StringUtil.isNullOrEmpty(p_ID))
		{
			throw new APPErrorException("无效的服务ID");
		}
		
		if(!StringUtil.equals(ServicesEntity.State_Start, state)&&
				!StringUtil.equals(ServicesEntity.State_Ready, state)&&
				!StringUtil.equals(ServicesEntity.State_Stop, state)&&
				!StringUtil.equals(ServicesEntity.State_Stoping, state)&&
				!StringUtil.equals(ServicesEntity.State_Unload, state)&&
				!StringUtil.equals(ServicesEntity.State_Pause, state)
				)
		{
			throw new APPErrorException("无效的服务状态");
			
		}

		String startsql="update SERVICES set STATE='"+state+"'  WHERE ID_='"+p_ID+"'";
		tgtools.db.DataBaseFactory.getDefault().executeUpdate(startsql);
	}
	
	public static int lock(String p_ID) throws APPErrorException
	{
		String ip=tgtools.util.NetHelper.getIP();
		String startsql="update SERVICES set RUNSERVER='"+ip+"'  WHERE ID_='"+p_ID+"' and (RUNSERVER is null or RUNSERVER='' or RUNSERVER ='"+ip+"')";
		return  tgtools.db.DataBaseFactory.getDefault().executeUpdate(startsql);
	}
	public static int unlock(String p_ID) throws APPErrorException
	{
		String ip=tgtools.util.NetHelper.getIP();
		String startsql="update SERVICES set RUNSERVER=''  WHERE ID_='"+p_ID+"' and (RUNSERVER is null or RUNSERVER='' or RUNSERVER ='"+ip+"')";
		return  tgtools.db.DataBaseFactory.getDefault().executeUpdate(startsql);
	}
}
