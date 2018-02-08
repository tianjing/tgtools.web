package tgtools.web.http.Filter;

import tgtools.exceptions.APPErrorException;
import tgtools.json.JSONArray;
import tgtools.json.JSONException;
import tgtools.json.JSONObject;
import tgtools.util.LogHelper;
import tgtools.util.StringUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.OutputStream;

/**
 * web.xml 增加过滤器 <br/>
 * &lt;filter&gt; <br/>
 * &lt;filter-name&gt;myFilter&lt;/filter-name&gt; <br/>
 * &lt;filter-class&gt;tgtools.web.http.Filter.UrlProxyFilter&lt;/filter-class&gt; <br/>
 * &lt;init-param&gt;  <br/>
 * &lt;param-name&gt;ipmap&lt;/param-name&gt; <br/>
 * &lt;param-value&gt;{"/":{"TargetHost":"192.168.1.245","TargetPort":"8091","TargetUrl":"/nf/im/client/"}}&lt;/param-value&gt; <br/>
 * &lt;/init-param&gt; <br/>
 * &lt;/filter&gt; <br/>
 * &lt;filter-mapping&gt; <br/>
 * &lt;filter-name&gt;myFilter&lt;/filter-name&gt; <br/>
 * &lt;url-pattern&gt;*&lt;/url-pattern&gt; <br/>
 * &lt;/filter-mapping&gt; <br/>
 * <p>
 * 名  称：
 * 编写者：田径
 * 功  能：
 * 时  间：10:12
 */
public class UrlProxyFilter extends ProxyFilter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String sourceurl = getSourceUrl(servletRequest);
        try {
            String path = mathPath(servletRequest, sourceurl);
            if (StringUtil.isNullOrEmpty(path)) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
            JSONObject setting = m_IpMap.getJSONObject(path);
            if (null != setting) {
                String method = ((HttpServletRequest) servletRequest).getMethod();
                String targetPort = setting.getString("TargetPort");
                String targetHost = setting.getString("TargetHost");
                String targetUrl = setting.getString("TargetUrl");
                String newPath = targetUrl + sourceurl.substring(path.length());

                LogHelper.info("", "代理开始，请求原始地址：" + sourceurl, "UrlProxyFilter");

                byte[] data = doProxy(method, targetHost + ":" + targetPort, newPath, servletRequest, servletResponse);
                OutputStream os = servletResponse.getOutputStream();
                os.write(data);
                os.close();
                return;
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
        } catch (APPErrorException e) {
            String error = "代理出错；代理原地址："+sourceurl+"原因：" + e.getMessage();
            LogHelper.error("", error, "UrlProxyFilter", e);
            servletResponse.setCharacterEncoding("UTF-8");
            servletResponse.getWriter().write(error);
            servletResponse.getWriter().close();
            return;
        }


    }

    protected String mathPath(ServletRequest p_ServletRequest, String p_Url) throws JSONException {
        String host = p_ServletRequest.getLocalAddr();
        int port = p_ServletRequest.getLocalPort();
        JSONArray names = m_IpMap.names();
        for (int i = 0; i < names.length(); i++) {
            if (p_Url.startsWith(names.getString(i))) {
                if (String.valueOf(port).equals(m_IpMap.getJSONObject(names.getString(i)).getString("TargetPort")) && host.equals(m_IpMap.getJSONObject(names.getString(i)).getString("TargetHost").equals(host))) {
                    return null;
                } else {
                    return names.getString(i);
                }
            }
        }
        return null;
    }
}
