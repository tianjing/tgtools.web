package tgtools.web.util;

import tgtools.exceptions.APPErrorException;
import tgtools.util.StringUtil;

/**
 * 名  称：
 * 编写者：田径
 * 功  能：
 * 时  间：9:09
 */
public final class ValidateHelper {
    public static void validEmptyParam(String p_Param,String p_ParamName)throws APPErrorException
    {
        if(StringUtil.isNullOrEmpty(p_Param))
        {
            throw new APPErrorException(p_ParamName+"不能为空");
        }
    }

}
