package tgtools.web.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import tgtools.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 树节点的实体对象
 * 注意 type  1:表格，2：编辑，3：多视图
 * Created by tian_ on 2016-09-13.
 */
public class TreeNode {
    /**
     * 表格视图
     */
    public static final String TYPE_GRID="1";
    /**
     * 编辑视图
     */
    public static final String TYPE_EDIT="2";
    /**
     * 多视图
     */
    public static final String TYPE_MULTI="3";
    /**
     * 无视图
     */
    public static final String TYPE_NONE="-1";


    /**
     * 表名称
     */
    public static final String MODEL_TABLE="1";
    /**
     * 过滤条件
     */
    public static final String MODEL_FILTER="2";
    /**
     * 对象
     */
    public static final String MODEL_OBJECT="3";
    /**
     * 关联表名称
     */
    public static final String MODEL_TABLELINK="4";


    public TreeNode()
    {
        children=new ArrayList<TreeNode>();
    }

    private  String m_Id;
    private String m_Name;
    private boolean m_IsLeaf;
    private String m_Img;
    private String m_Type;
    private String m_Pid;
    private String m_Filter;
    private String m_Order;
    private String m_Isgroup;
    private String m_Clsid;
    private boolean m_Expanded;
    private List<TreeNode> children;
    private String m_ExtData;
    private String m_Model;

    public String getModel() {
        return m_Model;
    }

    public void setModel(String p_Model) {
        m_Model = p_Model;
    }

    public String getExtData() {
        return m_ExtData;
    }

    public void setExtData(String p_ExtData) {
        m_ExtData = p_ExtData;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

    public String getId() {
        return m_Id;
    }

    public void setId(String p_Id) {
        m_Id = p_Id;
    }

    public String getName() {
        return m_Name;
    }

    public void setName(String p_Name) {
        m_Name = p_Name;
    }

    public boolean getIsLeaf() {
        return m_IsLeaf;
    }

    public void setIsLeaf(boolean p_IsLeaf) {
        m_IsLeaf = p_IsLeaf;
    }

    public String getImg() {
        return m_Img;
    }

    public void setImg(String p_Img) {
        m_Img = StringUtil.isNullOrEmpty(p_Img) ? "" : p_Img;
    }

    /**
     * type 1:表格，2：编辑，3：多视图
     * @return
     */
    public String getType() {
        return m_Type;
    }

    /**
     * type 1:表格，2：编辑，3：多视图
     * @param p_Type
     */
    public void setType(String p_Type) {
        m_Type = p_Type;
    }

    public String getPid() {
        return m_Pid;
    }

    public void setPid(String p_Pid) {
        m_Pid = p_Pid;
    }

    public String getFilter() {
        return m_Filter;
    }

    public void setFilter(String p_Filter) {
        m_Filter = StringUtil.isNullOrEmpty(p_Filter) ? "" : StringUtil.replace(p_Filter, "'", "#;");
    }

    public String getOrder() {
        return m_Order;
    }

    public void setOrder(String p_Order) {
        m_Order = p_Order;
    }

    public String getIsgroup() {
        return m_Isgroup;
    }

    public void setIsgroup(String p_Isgroup) {

        m_Isgroup =  "true".equals(p_Isgroup) ? "true" : "false";
    }

    public String getClsid() {
        return m_Clsid;
    }

    public void setClsid(String p_Clsid) {
        m_Clsid = p_Clsid;
    }

    public boolean getExpanded() {
        return m_Expanded;
    }

    public void setExpanded(boolean p_Expanded) {
        m_Expanded = p_Expanded;
    }


}
