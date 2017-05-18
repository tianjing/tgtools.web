package tgtools.web.services;

import java.util.Date;

import tgtools.exceptions.APPErrorException;
import tgtools.interfaces.IDispose;
import tgtools.plugin.util.JARLoader;
import tgtools.service.BaseService;
import tgtools.tasks.TaskContext;
import tgtools.util.DateUtil;
import tgtools.util.LogHelper;
import tgtools.web.platform.Platform;

public class TableServiceTask extends BaseService implements IDispose {

	public TableServiceTask(ServicesEntity p_Info) {
		m_Info = p_Info;
		//m_IsStop=true;
	}

	private String m_State;
	private JARLoader m_JARLoader;
	private ServicesEntity m_Info;
	private BaseService m_Service;
	/**
	 * 服务初始化
	 * @return
	 */
	public boolean init() {
		boolean res = load(m_Info);
		try {
			ServicesBll
					.changeState(m_Info.getID_(), ServicesEntity.State_Ready);
		} catch (Exception e) {
			LogHelper.error("", "修改服务状态出错", "TableServiceTask", e);
		}

		return res;
	}

	/**
	 * 加载服务
	 * @param info
	 * @return
	 */
	private boolean load(ServicesEntity info) {
		m_JARLoader = new JARLoader(ClassLoader.getSystemClassLoader());
		LogHelper.info("", "加载服务路径:"+Platform.getServerPath() + info.getPATH(),"TableServiceTask.load");
		m_JARLoader.addPath(Platform.getServerPath() + info.getPATH());
		try {
			Class<?> clazz = m_JARLoader.loadClass(info.getCLASSNAME());
			if (null != clazz) {
				Object obj = clazz.newInstance();
				if (null != obj && obj instanceof BaseService) {
					m_Service = (BaseService) obj;
					return true;
				}
			}
		} catch (Exception e) {
			LogHelper.error("", "服务初始化失败", "TableServiceTask", e);
		}
		return false;
	}

	@Override
	public String getName() {
		return m_Service.getName();
	}

	@Override
	protected int getInterval() {

		return -1;
	}

	@Override
	public boolean canRun() {
		return (null == m_State||ServicesEntity.State_Start.equals(m_State))&&m_Service.canRun();
	}

	@Override
	protected Date getEndTime() {
		return null;
	}

	/**
	 * 运行服务
	 * @param p_Context
	 */
	@Override
	public void run(TaskContext p_Context) {
		//while (true) {
			try {
				if(this.m_IsStop)
				{
					doStop();
					return;
				}
				if (!ServicesEntity.State_Start.equals(this.getState())) {
					wait1();
			//		continue;
				}
				if(this.m_IsStop)
				{
					doStop();
					return;
				}
				if (m_Service.canRun() &&(this.isConcurrency()|| ServicesBll.lockService(m_Info.getID_()))) {
					if(null==p_Context)
					{
						p_Context=new TaskContext();
					}
					p_Context.put("info", m_Info);
					ServicesBll.updateStartTime(m_Info.getID_());
					m_Service.run(p_Context);
					ServicesBll.unlockService(m_Info.getID_());
					m_Service.setLastTime(DateUtil.getCurrentDate());
					ServicesBll.updateRunTime(m_Info.getID_());
					wait1();
				}
				if(this.m_IsStop)
				{
					doStop();
					return;
				}
			} catch (Exception e) {
				LogHelper.error("", "服务运行出错", "TableServiceTask", e);
			}
			if (!ServicesEntity.State_Start.equals(this.getState())) {
				wait1();
			//	continue;
			}
			
	//	}
	}

	private void wait1() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	private void doStop()
	{
		setState(ServicesEntity.State_Stop);
		ServicesBll.unlockService(m_Info.getID_());
	}
	@Override
	public void Dispose() {
		if (null != m_JARLoader) {
			m_JARLoader = null;
		}
		if (null != m_Info) {
			m_Info = null;
		}
		if (null != m_Service) {
			m_Service = null;
		}

	}

	@Override
	public void start() {
//		if(this.m_IsStop) {
//			this.runThread(null);
//		}
//		m_IsStop=false;
		super.start();
	}

	public String getState() {
		return m_State;
	}

	/**
	 * 设置状态
	 * @param p_State
	 */
	public void setState(String p_State) {
		this.m_State = p_State;
		try {
			ServicesDao.updateState(m_Info.getID_(), p_State);
		} catch (APPErrorException e) {
			LogHelper.error("", "更新服务状态错误", "TableServiceTask.updateState", e);
		}

	}
}
