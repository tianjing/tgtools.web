package tgtools.web.http.Filter;

import tgtools.exceptions.APPErrorException;
import tgtools.json.JSONArray;
import tgtools.json.JSONException;
import tgtools.json.JSONObject;
import tgtools.util.LogHelper;

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
 * &lt;filter-class&gt;tgtools.web.http.Filter.ProxyFilter&lt;/filter-class&gt; <br/>
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
        try {
            String sourceurl = getSourceUrl(servletRequest);
            String path = mathPath(sourceurl);
            JSONObject setting = m_IpMap.getJSONObject(path);
            if (null != setting) {
                String method = ((HttpServletRequest) servletRequest).getMethod();
                String targetPort = setting.getString("TargetPort");
                String targetHost = setting.getString("TargetHost");
                String targetUrl = setting.getString("TargetUrl");
                String newPath = targetUrl + sourceurl.substring(path.length());

                LogHelper.info("", "代理开始，请求地址：" + sourceurl + ";;;代理地址：" + newPath, "UrlProxyFilter");

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
            String error="代理出错；原因：" + e.getMessage();
            LogHelper.error("", error, "UrlProxyFilter", e);
            servletResponse.getWriter().write(error);
            servletResponse.getWriter().close();
            return;
        }


    }

    protected String mathPath(String p_Url) throws JSONException {
        JSONArray names = m_IpMap.names();
        for (int i = 0; i < names.length(); i++) {
            if (p_Url.startsWith(names.getString(i))) {
                return names.getString(i);
            }
        }
        return null;
    }
}
