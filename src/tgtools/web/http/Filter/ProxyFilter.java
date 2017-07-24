package tgtools.web.http.Filter;

import tgtools.exceptions.APPErrorException;
import tgtools.json.JSONObject;
import tgtools.net.IWebClient;
import tgtools.net.WebClient;
import tgtools.util.StringUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * web.xml 增加过滤器
 *<filter>
 *<filter-name>myFilter</filter-name>
 *<filter-class>tgtools.web.http.Filter.ProxyFilter</filter-class>
 *<init-param>
 *<param-name>ipmap</param-name>
 *<param-value>{"nf":"192.168.1.245:8091"}</param-value>
 *</init-param>
 *</filter>
 *<filter-mapping>
 *<filter-name>myFilter</filter-name>
 *<url-pattern>*</url-pattern>
 *</filter-mapping>
 *
 *
 * 名  称：
 * 编写者：田径
 * 功  能：
 * 时  间：16:03
 */
public class ProxyFilter implements Filter {

    private JSONObject m_IpMap ;
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            String dd= filterConfig.getInitParameter("ipmap");
            m_IpMap=new JSONObject(dd);

        }
        catch (Exception e)
        {

        }
    }
    private String getFirstPath(String P_Url)
    {
        if(!StringUtil.isNullOrEmpty(P_Url)&&P_Url.indexOf("/")==0)
        {
            P_Url=P_Url.substring(1);
        }
        if(!StringUtil.isNullOrEmpty(P_Url)&&P_Url.indexOf("/")>=0){
            return P_Url.substring(0,P_Url.indexOf("/"));
        }
        return P_Url;


    }
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String url ="";
        url=((HttpServletRequest)servletRequest).getRequestURI();
        String method= ((HttpServletRequest) servletRequest).getMethod();
        System.out.println("method:"+method);
        WebClient client =new WebClient();
        String path=getFirstPath(url);
        System.out.println("path:"+path);
        if(!m_IpMap.has(path))
        {
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }
        try {
        String host=m_IpMap.getString(path);
        client.setMethod(method);

        client.setUrl("http://"+host+url);

        setHeader(servletRequest,client);
        InputStream is= servletRequest.getInputStream();

            byte[] data= client.doInvokeAsByte(is);
            if(client.getResponseHeader().containsKey("Set-Cookie")) {
                List<String> cookies = client.getResponseHeader().get("Set-Cookie");
                if (cookies.size() > 0) {
                    for (int i = 0; i < cookies.size(); i++) {
                        ((HttpServletResponse) servletResponse).addHeader("Set-Cookie", cookies.get(i));
                    }

                }
            }

            if(client.getResponseHeader().containsKey("Content-Type")) {
                List<String> ContentType = client.getResponseHeader().get("Content-Type");

                if (ContentType.size() > 0) {
                    servletResponse.setContentType(ContentType.get(0));

                }
            }
            OutputStream os= servletResponse.getOutputStream();
            os.write(data);
            os.close();
            is.close();
        } catch (APPErrorException e) {
            e.printStackTrace();
        }
        finally {

        }
    }
    private void setHeader(ServletRequest servletRequest, IWebClient p_Client)
    {
        String cookies= ((HttpServletRequest)servletRequest).getHeader("Cookie");
        String ContentType=((HttpServletRequest)servletRequest).getHeader("Content-Type");
        p_Client.getHead().put("Cookie",cookies);
        p_Client.getHead().put("Content-Type",ContentType);
    }
    @Override
    public void destroy() {

    }
}
