package tgtools.web.entity;

import tgtools.data.DataTable;

/**
 * Created by tian_ on 2016-08-18.
 */
public class GridDataEntity {
    private boolean m_Sussecc;
    private int m_CurPage;
    private int m_PageSize;
    private String m_SortField;
    private String m_SortOrder;
    private int m_TotalRows;
    private DataTable m_Data;
    private String m_Error;

    public boolean getSussecc() {
        return m_Sussecc;
    }

    public void setSussecc(boolean p_Sussecc) {
        m_Sussecc = p_Sussecc;
    }

    public int getCurPage() {
        return m_CurPage;
    }

    public void setCurPage(int p_CurPage) {
        m_CurPage = p_CurPage;
    }

    public int getPageSize() {
        return m_PageSize;
    }

    public void setPageSize(int p_PageSize) {
        m_PageSize = p_PageSize;
    }

    public String getSortField() {
        return m_SortField;
    }

    public void setSortField(String p_SortField) {
        m_SortField = p_SortField;
    }

    public String getSortOrder() {
        return m_SortOrder;
    }

    public void setSortOrder(String p_SortOrder) {
        m_SortOrder = p_SortOrder;
    }

    public int getTotalRows() {
        return m_TotalRows;
    }

    public void setTotalRows(int p_TotalRows) {
        m_TotalRows = p_TotalRows;
    }

    public DataTable getData() {
        return m_Data;
    }

    public void setData(DataTable p_Data) {
        m_Data = p_Data;
    }

    public String getError() {
        return m_Error;
    }

    public void setError(String p_Error) {
        m_Error = p_Error;
    }
}
