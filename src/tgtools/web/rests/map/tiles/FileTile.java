package tgtools.web.rests.map.tiles;

import tgtools.exceptions.APPErrorException;
import tgtools.util.FileUtil;
import tgtools.util.LogHelper;
import tgtools.web.util.ValidateHelper;

/**
 * @author 田径
 * @Title
 * @Description
 * @date 11:17
 */
public class FileTile  {

    public byte[] getTile(String p_MapPath,String p_X,String p_Y,String p_Level) throws APPErrorException {
        ValidateHelper.validEmptyParam(p_MapPath,"p_MapPath");
        ValidateHelper.validEmptyParam(p_X,"p_X");
        ValidateHelper.validEmptyParam(p_Y,"p_Y");
        ValidateHelper.validEmptyParam(p_Level,"p_Level");

        String path="";
        path+="L"+String.format("%02d",Integer.parseInt(p_Level))+"/";
        path+="R"+ String.format("%08x",Integer.parseInt(p_Y))+"/";
        path+="C"+String.format("%08x",Integer.parseInt(p_X));
        path+=".png";
        LogHelper.info("","获取切片："+p_MapPath+path,"GISMapBll.getTile");
        return FileUtil.readFileToByte(p_MapPath+path);
    }
}
