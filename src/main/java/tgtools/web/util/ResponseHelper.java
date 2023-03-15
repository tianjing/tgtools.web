package tgtools.web.util;

import tgtools.util.LogHelper;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

/**
 * @author 田径
 * @date 2020-02-27 14:27
 * @desc
 **/
public class ResponseHelper {

    /**
     * 响应写入字节数据
     *
     * @param pData     数据
     * @param pFileName 文件名
     * @param pResponse 响应
     * @throws Exception
     */
    public static void writeBytes(byte[] pData, String pFileName, HttpServletResponse pResponse)
            throws Exception {

        pFileName = URLEncoder.encode(pFileName, "UTF-8");
        pResponse.addHeader("Content-Disposition", "attachment;filename=" + pFileName);
        ServletOutputStream vOutput = pResponse.getOutputStream();
        vOutput.write(pData);
        vOutput.close();
    }

    /**
     * 响应写入 流数据
     *
     * @param pSource
     * @param pTarget
     * @throws IOException
     */
    public static void writeStream(InputStream pSource, OutputStream pTarget) throws IOException {
        writeStream(pSource, pTarget, -1);
    }

    /**
     * 响应写入 流数据
     *
     * @param pSource
     * @param pTarget
     * @param pLimit  限速 每秒字节数
     * @throws IOException
     */
    public static void writeStream(InputStream pSource, OutputStream pTarget, int pLimit) throws IOException {
        int vSize = 200;
        if (pLimit > 0) {
            vSize = pLimit;
        }
        byte[] vTemp = new byte[vSize * 1024];
        int vLength = 0;
        try {

            while ((vLength = pSource.read(vTemp)) > 0) {

                pTarget.write(vTemp, 0, vLength);
                pTarget.flush();

                if (pLimit > 0) {
                    try {
                        Thread.sleep(900);
                    } catch (InterruptedException e) {
                        LogHelper.error("", "写流延迟出错", "ResponseUtil.writeStream", e);
                    }
                }
            }
        } finally {
            try {
                pSource.close();
            } catch (Exception e) {
            }
        }

    }

    /**
     * 响应写入 流数据
     *
     * @param pFileName
     * @param pSource
     * @param pTarget
     */
    public static void writeFileStream(String pFileName, InputStream pSource, HttpServletResponse pTarget) {
        writeFileStream(pFileName, pSource, pTarget, -1);
    }

    /**
     * 响应写入 流数据
     *
     * @param pFileName
     * @param pSource
     * @param pTarget
     * @param pLimit    限速 每秒字节数
     */
    public static void writeFileStream(String pFileName, InputStream pSource, HttpServletResponse pTarget, int pLimit) {
        try (OutputStream vOs = pTarget.getOutputStream()) {
            pTarget.setContentLength(pSource.available());
            pTarget.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(pFileName, "UTF-8"));
            writeStream(pSource, vOs, pLimit);
        } catch (Exception ex) {
            LogHelper.error("", "下载文件写流出错", "writeFileStream", ex);
        }
    }

    /**
     * 响应写入 流数据
     *
     * @param pFileName
     * @param pSize     响应数据大小
     * @param pSource
     * @param pTarget
     */
    public static void writeFileStream(String pFileName, long pSize, InputStream pSource, HttpServletResponse pTarget) {
        writeFileStream(pFileName, pSize, pSource, pTarget);
    }

    /**
     * 响应写入 流数据
     *
     * @param pFileName
     * @param pSize     响应数据大小
     * @param pSource
     * @param pTarget
     * @param pLimit    限速 每秒字节数
     */
    public static void writeFileStream(String pFileName, long pSize, InputStream pSource, HttpServletResponse pTarget, int pLimit) {
        try (OutputStream vOs = pTarget.getOutputStream()) {
            pTarget.setContentLengthLong(pSize);
            pTarget.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(pFileName, "UTF-8"));
            writeStream(pSource, vOs, pLimit);
        } catch (Exception ex) {
            LogHelper.error("", "下载文件写流出错", "writeFileStream", ex);
        }
    }
}
