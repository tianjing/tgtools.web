package tgtools.web.rests.map.tiles;

import tgtools.data.DataTable;
import tgtools.db.IDataAccess;
import tgtools.exceptions.APPErrorException;
import tgtools.util.StringUtil;
import tgtools.web.util.ValidateHelper;

/**
 * @author 田径
 * @Title
 * @Description
 * @date 11:18
 */
public class DataBaseTile {

    public byte[] getTile(String p_MapPath,String p_X,String p_Y,String p_Level,String pDbName) throws APPErrorException {
        ValidateHelper.validEmptyParam(p_MapPath,"p_MapPath");
        ValidateHelper.validEmptyParam(p_X,"p_X");
        ValidateHelper.validEmptyParam(p_Y,"p_Y");
        ValidateHelper.validEmptyParam(p_Level,"p_Level");


        return getTile(p_X,p_Y,p_Level,pDbName);
    }
    private byte[] getTile(String p_X,String p_Y,String p_Level,String pDbName) throws APPErrorException {
        if(null==tgtools.db.DataBaseFactory.get(pDbName))
        {throw new APPErrorException("无效的数据源:"+pDbName);}
        IDataAccess db= tgtools.db.DataBaseFactory.get(pDbName);

        String filesql="SELECT ID FROM Tiles where x=${x} and y=${y} and zoom =${zoom}";
        String contentsql="SELECT TILE FROM TilesData where id=${id}";
        String sql= StringUtil.replace(filesql,"${x}",p_X);
        sql= StringUtil.replace(sql,"${y}",p_Y);
        sql= StringUtil.replace(sql,"${zoom}",p_Level);
        DataTable dt =db.Query(sql);
        if(!DataTable.hasData(dt))
        {
            throw new APPErrorException("找不到切片信息");
        }
        String id=DataTable.getFirstRow(dt).getValue("ID").toString();
        if(StringUtil.isNullOrEmpty(id))
        {
            throw new APPErrorException("找不到切片ID");
        }
        sql= StringUtil.replace(contentsql,"${id}",id);
        DataTable data =db.Query(sql);
        if(!DataTable.hasData(data))
        {
            throw new APPErrorException("找不到切片；id:"+id);
        }
        Object obj=DataTable.getFirstRow(data).getValue(0);
        if(null==obj||!(obj instanceof byte[]))
        {
            throw new APPErrorException("无效切片数据或类型:"+null==obj?"null":obj.getClass().toString());
        }
        return (byte[])obj;
    }
}
