package tgtools.web.util;

import tgtools.exceptions.APPErrorException;
import tgtools.json.JSONObject;
import tgtools.net.WebClient;
import tgtools.util.StringUtil;
import tgtools.util.ZipStringUtil;

import java.util.HashMap;
import java.util.Map;


public class WebRestClient {

	/**
	 * 请求通用接口的
	 * @author tian.jing
	 * @date 2016年5月6日
	 * @param p_Url
	 *            如：http://localhost:8080/EmptyProject/myrest/CommRest/exec"
	 * @param p_Name
	 *            插件名称 如：PluginDemo
	 * @param p_Data
	 *            传输的数据 p_Data会在传输时压缩
	 * @return
	 * @throws APPErrorException
	 */
	public static String doPost(String p_Url, String p_Name, String p_Data)
			throws APPErrorException {
		if (StringUtil.isNullOrEmpty(p_Name)) {
			throw new APPErrorException("无效的接口名称");

		}
		if (StringUtil.isNullOrEmpty(p_Url)) {
			throw new APPErrorException("无效的请求地址");
		}

		try {
			String data = ZipStringUtil.gzip(null == p_Data ? "" : p_Data);
			WebClient client =new WebClient();
			client.addHead("Content-Type","application/x-www-form-urlencoded");
			client.setUrl(p_Url);
			Map<String ,String> param =new HashMap<String, String>();
			param.put("name",p_Name);
			param.put("data",data);

			String res = client.doInvokeAsString(param);
//			String res = WebClient.doPost(p_Url, new NameValuePair[] {
//					new NameValuePair("name", p_Name),
//					new NameValuePair("data", data) });
			tgtools.json.JSONObject json = new JSONObject(res);
			Boolean sucess = (Boolean) json.get("Success");
			if (sucess) {
				return null == json.get("Data") ? "" : ZipStringUtil.gunzip(json
						.get("Data").toString());
			} else {
				String error = null == json.get("Error") ? "" : json.get(
						"Error").toString();
				throw new APPErrorException("处理失败：" + error);
			}
		} catch (APPErrorException e) {
			throw new APPErrorException("交互失败 :" + e.getMessage(), e);
		}

	}
	public static void main(String []args)
	{
		try {
			String url ="http://121.199.8.69:8081/EmptyProjectMini/myrest/CommRest/exec";
			String dd=tgtools.web.util.WebRestClient.doPost(
					url,
					"DBPlugin", "pageIndex=0&pageSize=10&sortField=id&sortOrder=asc");

			//String dd= WebRestClient.doPost("http://192.168.88.4:8081/WebSun/myrest/CommRest/exec", "PluginDemo", "插件参数");
//			String dd=tgtools.web.util.WebRdestClient.doPost(
//					"http://192.168.88.1:8080/EmptyProject/myrest/CommRest/exec",
//					"PluginDemo", "1tiansen2tiansen,");

			System.out.println(dd);
		} catch (APPErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
