package tgtools.web.rests.map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import tgtools.exceptions.APPErrorException;
import tgtools.util.LogHelper;
import tgtools.util.StringUtil;
import tgtools.web.platform.Platform;
import tgtools.web.rests.map.tiles.DataBaseTile;
import tgtools.web.rests.map.tiles.FileTile;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * 名  称：
 * 编写者：田径
 * 功  能：
 * 时  间：11:41
 */
@Controller
@RequestMapping("/maptiles")
public class MapTiles {
    FileTile mFileTile=new FileTile();
    DataBaseTile mDataBaseTile =new DataBaseTile();
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "{level}@{y}@{x}@{dbname}", method = RequestMethod.GET)
    @ResponseBody
    public void get(@PathVariable("level") String level, @PathVariable("y") String y, @PathVariable("x") String x,
                    @PathVariable("dbname") String dbname, HttpServletResponse response) throws UnsupportedEncodingException {

        byte[] data=null;
        try {
            if(StringUtil.isNullOrEmpty(dbname))
            {
                data=mFileTile.getTile(Platform.getServerPath()+"WEB-INF/MapTile/",x,y,level);
            }
            else
            {
                data=mDataBaseTile.getTile(Platform.getServerPath()+"WEB-INF/MapTile/",x,y,level,dbname);
            }
        } catch (APPErrorException e) {
            e.printStackTrace();
            data=new byte[0];
        }

        try {
            response.reset();
            response.setContentLength(data.length);
            OutputStream ous = response.getOutputStream();
            response.setContentType("image/png");
            ous.write(data);
            ous.flush();
            ous.close();
        }
        catch (Exception ex){
            LogHelper.error("","输出切片出错："+ex.getMessage(),"MapTile.get",ex);
        }
    }

}
