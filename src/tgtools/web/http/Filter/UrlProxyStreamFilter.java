package tgtools.web.http.Filter;

import tgtools.exceptions.APPErrorException;
import tgtools.json.JSONObject;
import tgtools.net.WebClient;
import tgtools.util.LogHelper;
import tgtools.util.StringUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
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
 * &lt;filter-class&gt;tgtools.web.http.Filter.UrlProxyStreamFilter&lt;/filter-class&gt; <br/>
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
public class UrlProxyStreamFilter extends UrlProxyFilter {

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

                doProxyByStream(method, targetHost + ":" + targetPort, newPath, servletRequest, servletResponse);
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
    protected void doProxyByStream(String p_Method, String p_NewHost, String p_NewPath, ServletRequest p_ServletRequest, ServletResponse p_ServletResponse) throws APPErrorException {
        WebClient client = new WebClient();
        String host = p_NewHost;
        client.setMethod(p_Method);
        String url = p_NewPath;
        client.setUrl("http://" + host + url);
        LogHelper.info("", "代理开始，代理请求地址：" + "http://" + host + url, "UrlProxyFilter");
        HttpServletResponse hsr=(HttpServletResponse) p_ServletResponse;
        InputStream is = null;
        try {
            setHeader(p_ServletRequest, client);
            is = p_ServletRequest.getInputStream();

            InputStream resis = client.doInvokeAsStream(is);
            if (client.getResponseHeader().containsKey("Set-Cookie")) {
                List<String> cookies = client.getResponseHeader().get("Set-Cookie");
                if (cookies.size() > 0) {
                    for (int i = 0; i < cookies.size(); i++) {
                        hsr.addHeader("Set-Cookie", cookies.get(i));
                    }
                }
            }

            if (client.getResponseHeader().containsKey("Content-Type")) {
                List<String> ContentType = client.getResponseHeader().get("Content-Type");

                if (ContentType.size() > 0) {
                    hsr.setContentType(ContentType.get(0));

                }
            }

            if (client.getResponseHeader().containsKey("Content-Length")) {
                List<String> lengths = client.getResponseHeader().get("Content-Length");

                if (lengths.size() > 0) {
                    hsr.setContentLength(Integer.parseInt(lengths.get(0)));

                }
            }

            hsr.setStatus(client.getResponseCode());

            copyStream(resis,p_ServletResponse.getOutputStream());
        } catch (Exception ex) {
            throw new APPErrorException("代理数据出错；Url:" + client.getUrl() + ";;原因：" + ex.getMessage(), ex);
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {

                }
            }
        }
    }

    private void copyStream(InputStream pInputStream,OutputStream pOutputStream) throws APPErrorException {
        byte[] data=new byte[1024];
        int len=0;
        try {
            while ((len = (pInputStream.read(data))) > 0) {
                pOutputStream.write(data,0,len);
            }
        }
        catch (Exception e)
        {
            throw new APPErrorException("copyStream出错；原因："+e.getMessage(),e);
        }
        finally {
            try {
                pInputStream.close();
            } catch (IOException e) {

            }
            try {
                pOutputStream.close();
            } catch (IOException e) {

            }
        }
    }
    public static void main(String[] args)
    {
        WebClient client =new WebClient();
        client.setMethod("GET");
        client.setUrl("http://172.17.3.1/dfd/dd.htm");
        try {
           String ss= client.doInvokeAsString("");
           System.out.println(ss);
        } catch (APPErrorException e) {
            e.printStackTrace();
        }
    }
}
