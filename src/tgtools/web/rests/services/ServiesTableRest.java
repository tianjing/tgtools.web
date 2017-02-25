package tgtools.web.rests.services;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import tgtools.exceptions.APPErrorException;
import tgtools.util.StringUtil;
import tgtools.web.rests.entity.RequestEntity;
import tgtools.web.rests.entity.ResposeEntity;
import tgtools.web.services.ServicesBll;
@Controller
@RequestMapping("/servicestable")
public class ServiesTableRest {

	/**
	 * 启动服务
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/run", method = RequestMethod.POST)
	@ResponseBody
	public ResposeEntity run(@RequestBody RequestEntity request) {
		ResposeEntity entity =new ResposeEntity();
		String id=null!=request.Data?request.Data.toString():StringUtil.EMPTY_STRING;
		try {
			ServicesBll.run(id);
			entity.Success=true;
			entity.Data=true;
		} catch (Exception e) {
			entity.Success=false;
			entity.Error=e.getMessage();
		}
		return entity;
	}
	
/**
 * 停止服务
 * @param request
 * @return
 */
	@RequestMapping(value = "/stop", method = RequestMethod.POST)
	@ResponseBody
	public ResposeEntity stop(@RequestBody RequestEntity request) {
		ResposeEntity entity =new ResposeEntity();
		String id=null!=request.Data?request.Data.toString():StringUtil.EMPTY_STRING;
		try {
			ServicesBll.stop(id);
			entity.Success=true;
			entity.Data=true;
		} catch (Exception e) {
			entity.Success=false;
			entity.Error=e.getMessage();
		}
		return entity;
	}
	/**
	 * 删除服务
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/del", method = RequestMethod.POST)
	@ResponseBody
	public ResposeEntity del(@RequestBody RequestEntity request) {
		ResposeEntity entity =new ResposeEntity();
		String id=null!=request.Data?request.Data.toString():StringUtil.EMPTY_STRING;
		try {
			ServicesBll.del(id);
			entity.Success=true;
			entity.Data=true;
		} catch (Exception e) {
			entity.Success=false;
			entity.Error=e.getMessage();
		}
		return entity;
	}
	
	/**
	 * 卸载服务
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/unload", method = RequestMethod.POST)
	@ResponseBody
	public ResposeEntity unload(@RequestBody RequestEntity request) {
		ResposeEntity entity =new ResposeEntity();
		String id=null!=request.Data?request.Data.toString():StringUtil.EMPTY_STRING;
		try {
			ServicesBll.unRegister(id);
			entity.Success=true;
			entity.Data=true;
		} catch (Exception e) {
			entity.Success=false;
			entity.Error=e.getMessage();
		}
		return entity;
	}
}
