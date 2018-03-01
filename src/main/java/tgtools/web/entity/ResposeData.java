package tgtools.web.entity;

import java.io.Serializable;

/**
 * @author 田径
 * @Title
 * @Description
 * @date 9:28
 */
public class ResposeData implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean mSuccess;
    private Object mData;
    private String mError;

    public boolean getSuccess() {
        return mSuccess;
    }

    public void setSuccess(boolean pSuccess) {
        mSuccess = pSuccess;
    }

    public Object getData() {
        return mData;
    }

    public void setData(Object pData) {
        mData = pData;
    }

    public String getError() {
        return mError;
    }

    public void setError(String pError) {
        mError = pError;
    }
}
