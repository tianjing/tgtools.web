package tgtools.web.core;



import tgtools.web.sqls.BaseViewSqls;
import tgtools.web.sqls.SqlsFactory;

/**
 * Created by tian_ on 2016-08-19.
 */
public class Constants {

static{
    SQLs= SqlsFactory.getSQLs(new BaseViewSqls());
    System.out.println("11111:"+Constants.SQLs.Page_GetCountData_SQL);
}
public static BaseViewSqls SQLs;



}
