package tgtools.web.rests.data;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import tgtools.data.DataTable;
import tgtools.exceptions.APPErrorException;
import tgtools.util.GUID;
import tgtools.web.rests.entity.RequestEntity;
import tgtools.web.rests.entity.ResposeEntity;

@Controller
@RequestMapping("/data")
public class DataAccessRest {
    /**
     * 数据库查询
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @ResponseBody
    public ResposeEntity query(@RequestBody RequestEntity user) {
        ResposeEntity dd = new ResposeEntity();
        dd.Success = false;
        if (null != user.Data && user.Data instanceof String) {
            String sql = (String) user.Data;
            if (sql.toLowerCase().indexOf("select") > -1) {
                try {
                    DataTable dt = tgtools.db.DataBaseFactory.getDefault()
                            .Query(sql);
                    dd.Success = true;
                    dd.Data = dt.toJson();
                } catch (Exception e) {
                    dd.Error = e.getMessage() + e.toString();
                }
            } else {
                dd.Error = "无效的sql语句";
            }
        } else {
            dd.Error = "无效的sql语句";
        }
        return dd;
    }

    /**
     * 数据库增删改
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "/execute", method = RequestMethod.POST)
    @ResponseBody
    public ResposeEntity execute(@RequestBody RequestEntity user) {

        ResposeEntity dd = new ResposeEntity();
        dd.Success = false;
        if (null != user.Data && user.Data instanceof String) {
            String sql = (String) user.Data;
            String sqllower = sql.toLowerCase();
            if ((sqllower.indexOf("create") > -1)||(sqllower.indexOf("alter") > -1)||(sqllower.indexOf("drop") > -1)||(sqllower.indexOf("insert") > -1) || (sqllower.indexOf("update") > -1) || (sqllower.indexOf("delete") > -1)) {
                try {
                    int res = tgtools.db.DataBaseFactory.getDefault()
                            .executeUpdate(sql);
                    dd.Success = true;
                    dd.Data = res;
                } catch (Exception e) {
                    dd.Error = e.getMessage() + e.toString();
                }
            } else {
                dd.Error = "无效的sql语句";
            }
        } else {
            dd.Error = "无效的sql语句";
        }
        return dd;
    }

    /**
     * 获取GUID
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "/getGUID", method = RequestMethod.POST)
    @ResponseBody
    public ResposeEntity getGUID(@RequestBody RequestEntity user) {
        ResposeEntity dd = new ResposeEntity();

        dd.Success = true;
        dd.Data = GUID.newGUID();

        return dd;
    }
    /**
     * 获取 数据库生成GUID
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "/getGUIDByDB", method = RequestMethod.POST)
    @ResponseBody
    public ResposeEntity getGUIDByDB(@RequestBody RequestEntity user) {
        ResposeEntity dd = new ResposeEntity();
        try {
            String sql = "select substr(guid,1,8)||'-'||substr(guid,9,4)||'-'||substr(guid,13,4)||'-'||substr(guid,17,4)||'-'||substr(guid,21,12) as guid from( " +
                    " select guid() as guid from dual";
            dd.Data = tgtools.db.DataBaseFactory.getDefault()
                    .Query(sql).getRow(0).getValue("GUID").toString();
            dd.Success = true;
        } catch (APPErrorException e) {
            dd.Success = false;
            dd.Error = e.getMessage();
        }
        return dd;
    }
}
