package tgtools.web.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import tgtools.exceptions.APPErrorException;
import tgtools.json.JSONArray;
import tgtools.json.JSONObject;
import tgtools.web.entity.TreeNode;

import java.util.List;

/**
 * 名  称：
 * 编写者：田径
 * 功  能：
 * 时  间：14:02
 */
public class TreeNodeParser {



    public static String createNode(TreeNode p_Node) throws APPErrorException {
        if(null==p_Node)
        {
            throw new APPErrorException("无效的TreeNode");
        }
        if(p_Node.getChildren().size()<1)
        {
            p_Node.setChildren(null);
        }
        ObjectMapper mapper=new ObjectMapper();

        try {
            return mapper.writeValueAsString(p_Node);
        } catch (JsonProcessingException e) {
            throw new APPErrorException("TreeNode转换失败",e);
        }
    }
    public static String createNode(List<TreeNode> p_Nodes) throws APPErrorException {
        JSONArray array = new JSONArray();
        if(null!=p_Nodes&&p_Nodes.size()>0)
        {
            for(int i=0;i<p_Nodes.size();i++)
                array.put(new JSONObject(createNode(p_Nodes.get(i))));


        }
        return array.toString();
    }

}
