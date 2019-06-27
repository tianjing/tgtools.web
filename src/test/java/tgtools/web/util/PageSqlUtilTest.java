package tgtools.web.util;

public class PageSqlUtilTest {

    @org.junit.Test
    public void getPageDataLimitSQL() {
        String dm7 = PageSqlUtil.getPageDataLimitSQL("dm", "select * from MW_APP.MWT_UD_YCJX_JXSB", "1", "100");
        String dm6 = PageSqlUtil.getPageDataLimitSQL("dm6", "select * from HISDB.ALARM.YX_BW", "1", "100");
        System.out.println("getPageDataLimitSQL dm7:"+dm7);
        System.out.println("getPageDataLimitSQL dm6:"+dm6);
    }

    @org.junit.Test
    public void getPageDataSQL() {
        String dm7 = PageSqlUtil.getPageDataSQL("dm", "select * from MW_APP.MWT_UD_YCJX_JXSB",  "1", "100");
        String dm6 = PageSqlUtil.getPageDataSQL("dm6", "select * from HISDB.ALARM.YX_BW",  "1", "100");
        System.out.println("getPageDataSQL dm7:"+dm7);
        System.out.println("getPageDataSQL dm6:"+dm6);
    }
}