package tgtools.web.entity;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author 田径
 * @Title
 * @Description
 * @date 9:28
 */
public class GridData implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean mSuccess;
    private int mCurPage;
    private int mPageSize;
    private String mSortField;
    private String mSortOrder;
    private int mTotalRows;
    private Collection mData;

    public boolean getSuccess() {
        return mSuccess;
    }

    public void setSuccess(boolean pSuccess) {
        mSuccess = pSuccess;
    }

    public int getCurPage() {
        return mCurPage;
    }

    public void setCurPage(int pCurPage) {
        mCurPage = pCurPage;
    }

    public int getPageSize() {
        return mPageSize;
    }

    public void setPageSize(int pPageSize) {
        mPageSize = pPageSize;
    }

    public String getSortField() {
        return mSortField;
    }

    public void setSortField(String pSortField) {
        mSortField = pSortField;
    }

    public String getSortOrder() {
        return mSortOrder;
    }

    public void setSortOrder(String pSortOrder) {
        mSortOrder = pSortOrder;
    }

    public int getTotalRows() {
        return mTotalRows;
    }

    public void setTotalRows(int pTotalRows) {
        mTotalRows = pTotalRows;
    }

    public Collection getData() {
        return mData;
    }

    public void setData(Collection pData) {
        mData = pData;
    }
}
