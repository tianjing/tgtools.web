package tgtools.web.rests.plugin;


import java.util.Set;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import tgtools.plugin.PluginInfo;
import tgtools.web.rests.entity.RequestEntity;
import tgtools.web.rests.entity.ResposeEntity;

@Controller
@RequestMapping("/plugins")
public class PluginManageRest {


	/**
	 * 添加插件
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public ResposeEntity add(@RequestBody RequestEntity request) {
		ResposeEntity entity =new ResposeEntity();
		entity.Success= tgtools.plugin.PluginFactory.addPlugin(request.Data.toString());
		return entity;
	}
	/**
	 * 删除插件
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/del", method = RequestMethod.POST)
	@ResponseBody
	public ResposeEntity del(@RequestBody RequestEntity request) {
		ResposeEntity entity =new ResposeEntity();
		tgtools.plugin.PluginFactory.delPlugin(request.Data.toString());
		entity.Success=true;
		return entity;
	}
	/**
	 * 加载插件
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/load", method = RequestMethod.POST)
	@ResponseBody
	public ResposeEntity load(@RequestBody RequestEntity request) {
		ResposeEntity entity =new ResposeEntity();
		entity.Success=tgtools.plugin.PluginFactory.loadPlugin(request.Data.toString());
		entity.Data=entity.Success;
		return entity;
	}
	/**
	 * 卸载插件
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/unload", method = RequestMethod.POST)
	@ResponseBody
	public ResposeEntity unload(@RequestBody RequestEntity request) {
		ResposeEntity entity =new ResposeEntity();
		entity.Success=tgtools.plugin.PluginFactory.unloadPlugin(request.Data.toString());	
		entity.Data=entity.Success;
		return entity;
	}
	/**
	 * 列表插件
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	@ResponseBody
	public ResposeEntity list(@RequestBody RequestEntity request) {
		ResposeEntity entity =new ResposeEntity();
		entity.Success=true;
		ObjectMapper json=new ObjectMapper();
		
		Set<PluginInfo> infos=tgtools.plugin.PluginFactory.getPlugins();
		try {
			String jsonstr= json.writeValueAsString(infos);
			entity.Data=jsonstr;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return entity;
	}
}
