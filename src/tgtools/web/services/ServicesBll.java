package tgtools.web.services;

import java.util.List;

import tgtools.exceptions.APPErrorException;
import tgtools.service.BaseService;


public class ServicesBll {
/**
 * 获取task
 * @param p_ID
 * @return
 * @throws APPErrorException
 */
	private static TableServiceTask getTask(String p_ID)
			throws APPErrorException {
		ServicesEntity entity = ServicesDao.getServiceByID(p_ID);
		if (!tgtools.service.ServiceFactory.hasService(entity.getNAME())) {
			TableServiceTask task = createService(entity);
			tgtools.service.ServiceFactory.register(task);
		}
		BaseService service = tgtools.service.ServiceFactory.getService(entity
				.getNAME());
		if (service instanceof TableServiceTask) {
			return (TableServiceTask) service;
		}
		return null;
	}
/**
 * 创建服务
 * @param p_TaskInfo
 * @return
 * @throws APPErrorException
 */
	private static TableServiceTask createService(ServicesEntity p_TaskInfo)
			throws APPErrorException {
		TableServiceTask task = new TableServiceTask(p_TaskInfo);
		if (!task.init()) {
			return null;
		}
		return task;
	}

	/**
	 * 加载所有服务
	 * @throws APPErrorException
	 */
	public static void laodAllServices() throws APPErrorException {
		List<ServicesEntity> services = ServicesDao.getAllServices();
		for (int i = 0; i < services.size(); i++) {
			TableServiceTask task = createService(services.get(i));
			if (null != task) {
				if (ServicesEntity.State_Start
						.equals(services.get(i).getSTATE())||ServicesEntity.State_Stop.equals(services.get(i).getSTATE())) {
					task.setState(ServicesEntity.State_Start);
				}
				tgtools.service.ServiceFactory.register(task);
			}
		}

	}

	/**
	 * 更新服务状态
	 * @param p_ID
	 * @throws APPErrorException
	 */
	public static void run(String p_ID) throws APPErrorException {
		TableServiceTask task = getTask(p_ID);
//		try {
//			ServicesDao.updateState(p_ID, ServicesEntity.State_Start);
//		} catch (Exception e) {
//			LogHelper.error("", "更新服务状态出错", "ServicesBll.run", e);
//		}
		task.setState(ServicesEntity.State_Start);
	}


/**
 * 将服务状态改为停止
 * @param p_ID
 * @throws APPErrorException
 */
	public static void stop(String p_ID) throws APPErrorException {
		TableServiceTask task = getTask(p_ID);
//		try {
//			ServicesBll.changeState(p_ID, ServicesEntity.State_Stop);
//		} catch (Exception e) {
//			LogHelper.error("", "修改服务状态出错", "TableServiceTask", e);
//		}
		task.setState(ServicesEntity.State_Pause);
	}

	/**
	 * 删除服务
	 * @param p_ID
	 * @throws APPErrorException
	 */
	public static void del(String p_ID) throws APPErrorException {
		TableServiceTask task = getTask(p_ID);
		tgtools.service.ServiceFactory.stopService(task.getName());
		tgtools.service.ServiceFactory.unRegister(task.getName());
	}
	/**
	 * 注销服务
	 * @param p_ID
	 * @throws APPErrorException
	 */
	public static void unRegister(String p_ID) throws APPErrorException {
		TableServiceTask task = getTask(p_ID);
		task.setState(ServicesEntity.State_Stoping);
		tgtools.service.ServiceFactory.stopService(task.getName());
		tgtools.service.ServiceFactory.unRegister(task.getName());
		task.setState(ServicesEntity.State_Unload);

	}
	/**
	 * 更改runtime
	 * @param p_ID
	 * @throws APPErrorException
	 */
	public static void updateRunTime(String p_ID)
			throws APPErrorException {
		ServicesDao.updateRunTime(p_ID);
	}
	/**
	 * 更改Startime
	 * @param p_ID
	 * @throws APPErrorException
	 */
	public static void updateStartTime(String p_ID)
			throws APPErrorException {
		ServicesDao.updateStartTime(p_ID);
	}
	/**
	 * 更改服务状态
	 * @param p_ID
	 * @param p_State
	 * @throws APPErrorException
	 */
	public static void changeState(String p_ID, String p_State)
			throws APPErrorException {
		ServicesDao.updateState(p_ID, p_State);
	}
	/**
	 * 给运行的服务加锁
	 * @author tian.jing
	 * @date 2015年12月30日
	 * @param p_ID
	 * @return
	 * @throws APPErrorException 
	 */
	public static boolean lockService(String p_ID) 
	{
		int resul=-1;
		try {
			resul = ServicesDao.lock(p_ID);
		} catch (APPErrorException e) {

		}
		return resul>0;
	}

	/**
	 * 给运行的服务解锁
	 * @author tian.jing
	 * @date 2015年12月30日
	 * @param p_ID
	 * @return
	 * @throws APPErrorException
	 */
	public static boolean unlockService(String p_ID) 
	{
		int resul=-1;
		try {
			resul = ServicesDao.unlock(p_ID);
		} catch (APPErrorException e) {

		}
		return resul>0;
	}
}
