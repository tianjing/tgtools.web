package tgtools.web.core;



import tgtools.web.sqls.BaseViewSqls;
import tgtools.web.sqls.SqlsFactory;

/**
 * Created by tian_ on 2016-08-19.
 */
public class Constants {

static{
    SQLs= SqlsFactory.getSQLs(new BaseViewSqls());
}
public static BaseViewSqls SQLs;



}
