package tgtools.web.util;

import tgtools.exceptions.APPErrorException;
import tgtools.util.StringUtil;

/**
 * 名  称：Sql字符串辅助类
 * 编写者：田径
 * 功  能：验证参数，替换占位符
 * 时  间：9:59
 */
public final class SqlStrUtil {

    private static String m_SpecialStrReg = "(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|(\\b(frame|<frame|iframe|<iframe|img|<img|JavaScript|<javascript|script|<script|alert|select|update|and|or|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)";

    /**
     * SQL字符串替换，并验证参数是否存在特殊字符
     * @param p_SQL
     * @param p_Mark
     * @param p_Content
     * @return
     * @throws APPErrorException
     */
    public static String replace(String p_SQL, String p_Mark, String p_Content) throws APPErrorException {
        validParam(p_Content);
        return StringUtil.replace(p_SQL, p_Mark, p_Content);
    }

    /**
     * 验证字符串是否含有特殊字符，如果有则抛出错误
     * @param p_Param
     * @throws APPErrorException
     */
    public static void validParam(String p_Param) throws APPErrorException {
        if(hasSpecialStrParam(p_Param))
        {
            throw new APPErrorException("参数中含有字符;请重新输入。"+p_Param);
        }
    }

    /**
     * 是否含有特殊字符
     * @param p_Param
     * @return true含有特殊字符，false不含特殊字符
     */
    public static boolean hasSpecialStrParam(String p_Param) {
        if (StringUtil.isNullOrEmpty(p_Param)) {
            return false;
        }
        return tgtools.util.RegexHelper.isMatch(p_Param, m_SpecialStrReg);
    }


}
