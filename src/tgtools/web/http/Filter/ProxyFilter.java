package tgtools.web.http.Filter;

import tgtools.exceptions.APPErrorException;
import tgtools.json.JSONObject;
import tgtools.net.IWebClient;
import tgtools.net.WebClient;
import tgtools.util.LogHelper;
import tgtools.util.StringUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * web.xml 增加过滤器 <br/>
 * &lt;filter&gt; <br/>
 * &lt;filter-name&gt;myFilter&lt;/filter-name&gt; <br/>
 * &lt;filter-class&gt;tgtools.web.http.Filter.ProxyFilter&lt;/filter-class&gt; <br/>
 * &lt;init-param&gt;  <br/>
 * &lt;param-name&gt;ipmap&lt;/param-name&gt; <br/>
 * &lt;param-value&gt;{"nf":"192.168.1.245:8091"}&lt;/param-value&gt; <br/>
 * &lt;/init-param&gt; <br/>
 * &lt;/filter&gt; <br/>
 * &lt;filter-mapping&gt; <br/>
 * &lt;filter-name&gt;myFilter&lt;/filter-name&gt; <br/>
 * &lt;url-pattern&gt;*&lt;/url-pattern&gt; <br/>
 * &lt;/filter-mapping&gt; <br/>
 * <p>
 * <p>
 * 名  称：项目路径转发器
 * 编写者：田径
 * 功  能：将指定的项目路径转向新服务器 如上配置意思:/nf/* 转向 192.168.1.245:8091/nf/*
 * 时  间：16:03
 */
public class ProxyFilter implements Filter {

    protected JSONObject m_IpMap;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            String dd = filterConfig.getInitParameter("ipmap");
            m_IpMap = new JSONObject(dd);

        } catch (Exception e) {

        }
    }

    protected String getFirstPath(String P_Url) {
        if (!StringUtil.isNullOrEmpty(P_Url) && P_Url.indexOf("/") == 0) {
            P_Url = P_Url.substring(1);
        }
        if (!StringUtil.isNullOrEmpty(P_Url) && P_Url.indexOf("/") >= 0) {
            return P_Url.substring(0, P_Url.indexOf("/"));
        }
        return P_Url;


    }


    protected String getSourceUrl(ServletRequest p_ServletRequest)
    {
        String method=((HttpServletRequest) p_ServletRequest).getMethod();
        if("GET".equals(method.toUpperCase())){
            String queryString =((HttpServletRequest) p_ServletRequest).getQueryString();
            return ((HttpServletRequest) p_ServletRequest).getRequestURI()+ (StringUtil.isNullOrEmpty(queryString)?"":"?"+queryString);
        }
        else {
            return ((HttpServletRequest) p_ServletRequest).getRequestURI();
        }
    }


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String url = "";
        url = getSourceUrl(servletRequest);
        String method = ((HttpServletRequest) servletRequest).getMethod();
        System.out.println("method:" + method);

        String path = getFirstPath(url);
        System.out.println("path:" + path);
        if (!m_IpMap.has(path)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        try {
            String host = m_IpMap.getString(path);

            byte[] data=doProxy(method,host,path,servletRequest,servletResponse);
            OutputStream os = servletResponse.getOutputStream();
            os.write(data);
            os.close();
        } catch (APPErrorException e) {
            e.printStackTrace();
        } finally {

        }
    }

    protected byte[] doProxy(String p_Method, String p_NewHost, String p_NewPath,ServletRequest p_ServletRequest, ServletResponse p_ServletResponse) throws APPErrorException {
        WebClient client = new WebClient();
        String host = p_NewHost;
        client.setMethod(p_Method);
        String url = p_NewPath;
        client.setUrl("http://" + host + url);
        LogHelper.info("", "代理开始，代理请求地址：" + "http://" + host + url, "UrlProxyFilter");

        InputStream is =null;
        try {
            setHeader(p_ServletRequest, client);
            is = p_ServletRequest.getInputStream();

            byte[] data = client.doInvokeAsByte(is);
            if (client.getResponseHeader().containsKey("Set-Cookie")) {
                List<String> cookies = client.getResponseHeader().get("Set-Cookie");
                if (cookies.size() > 0) {
                    for (int i = 0; i < cookies.size(); i++) {
                        ((HttpServletResponse) p_ServletResponse).addHeader("Set-Cookie", cookies.get(i));
                    }

                }
            }

            if (client.getResponseHeader().containsKey("Content-Type")) {
                List<String> ContentType = client.getResponseHeader().get("Content-Type");

                if (ContentType.size() > 0) {
                    p_ServletResponse.setContentType(ContentType.get(0));

                }
            }

            return data;
        } catch (Exception ex) {
            throw new APPErrorException("代理数据出错；Url:"+client.getUrl()+";;原因："+ex.getMessage(),ex);
        }
        finally {
            if(null!=is) {
                try {
                    is.close();
                } catch (IOException e) {

                }
            }
        }


}

    protected void setHeader(ServletRequest servletRequest, IWebClient p_Client) {
        String cookies = ((HttpServletRequest) servletRequest).getHeader("Cookie");
        String ContentType = ((HttpServletRequest) servletRequest).getHeader("Content-Type");
        p_Client.getHead().put("Cookie", cookies);
        p_Client.getHead().put("Content-Type", ContentType);
    }

    @Override
    public void destroy() {

    }
}
