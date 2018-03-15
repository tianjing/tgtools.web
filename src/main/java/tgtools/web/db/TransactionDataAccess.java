package tgtools.web.db;

import com.sun.rowset.CachedRowSetImpl;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import tgtools.data.DataTable;
import tgtools.db.IDataAccess;
import tgtools.exceptions.APPErrorException;
import tgtools.util.JsonParseHelper;
import tgtools.util.LogHelper;
import tgtools.util.ReflectionUtil;
import tgtools.util.StringUtil;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 名  称：
 * 编写者：田径
 * 功  能：
 * 时  间：13:36
 */
public class TransactionDataAccess implements IDataAccess {
    private String m_DataBaseType="";
    public TransactionDataAccess(DataSource p_DataSource)
    {
        LogHelper.info("","初始化数据源:"+p_DataSource,"TransactionDataAccess");
        setDataSource(p_DataSource);
        try {
            init(m_DataSource);
        } catch (APPErrorException e) {
            LogHelper.error("","初始化失败","TransactionDataAccess",e);
        }
    }
    public TransactionDataAccess()
    {
        this(null);
    }
    private JdbcTemplate m_JdbcTemplate;
    private DataSource m_DataSource;


    public void setDataSource(DataSource p_DataSource) {
        m_DataSource=p_DataSource;
    }

    @Override
    public void setDataBaseType(String p_DataBaseType) {

            m_DataBaseType=p_DataBaseType;
    }

    @Override
    public String getDataBaseType() {
        if (!StringUtil.isNullOrEmpty(m_DataBaseType)) {
            return m_DataBaseType;
        }
        String url = getUrl();
        if (!StringUtil.isNullOrEmpty(url)) {
            m_DataBaseType=url.substring(url.indexOf("jdbc:") + 5, url.indexOf(":", url.indexOf("jdbc:") + 5));
        }
        return m_DataBaseType;
    }

    @Override
    public String getUrl() {
        if (null != m_DataSource) {
            try {
                Method method = ReflectionUtil.findMethod(m_DataSource.getClass(),"getUrl", new Class[]{});
                if (null == method) {
                    LogHelper.info("", "无法获取getUrl方法。", "DMDataAccess.getUrl");
                }
                Object obj = method.invoke(m_DataSource, new Object[]{});
                return null == obj ? StringUtil.EMPTY_STRING : obj.toString();
            } catch (Exception e) {
                LogHelper.error("", "获取数据库连接出错。", "DMDataAccess.getUrl", e);
            }
        }
        return StringUtil.EMPTY_STRING;
    }

    @Override
    public DataSource getDataSource() {
        return null;
    }
    @Override
    public ResultSet executeQuery(String p_Sql) throws APPErrorException {
       return m_JdbcTemplate.query(p_Sql,new ResultSetExtractor<ResultSet>(){
            @Override
            public ResultSet extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                CachedRowSetImpl rowset = new CachedRowSetImpl();
                rowset.populate(resultSet);
                return rowset;
            }
        });
    }

    @Override
    public DataTable Query(String p_Sql) throws APPErrorException {
        return Query(p_Sql,false);
    }

    @Override
    public DataTable Query(String p_Sql,boolean p_BlobUseStream) throws APPErrorException {
        final String sql=p_Sql;
        final boolean blobUseStream=p_BlobUseStream;
        LogHelper.info("",sql,"TransactionDataAccess.Query");
        return m_JdbcTemplate.query(p_Sql,new ResultSetExtractor<DataTable>(){
            @Override
            public DataTable extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                return new DataTable(resultSet,sql,blobUseStream);
            }
        });
    }

    @Override
    public <T> T Query(String sql, Class<T> p_Class) throws APPErrorException {
        return (T) JsonParseHelper.parseToObject(Query(sql),p_Class,true);
    }
    @Override
    public int executeUpdate(String p_Sql) throws APPErrorException {
        return m_JdbcTemplate.update(p_Sql);
    }

    @Override
    public int[] executeBatch(String[] p_Sqls) throws APPErrorException {
        return m_JdbcTemplate.batchUpdate(p_Sqls);
    }

    @Override
    public boolean init(Object... objects) throws APPErrorException {
        if(null!=m_JdbcTemplate&&null!=m_DataSource)
        {return true;}

        if(null!=objects&&objects.length==1&&objects[0] instanceof  DataSource)
        {
            m_JdbcTemplate=new JdbcTemplate((DataSource)objects[0]);
            return true;
        }
        return false;
    }

    @Override
    public void close() {
        m_DataSource=null;
        m_JdbcTemplate=null;
    }

    @Override
    public Connection createConnection() throws APPErrorException {
        try {
            return m_DataSource.getConnection();
        } catch (SQLException e) {
           throw new APPErrorException("获取数据库连接失败",e);
        }
    }

    @Override
    public DataTable Query(String p_Sql, Object[] objects) throws APPErrorException {
        final String sql=p_Sql;
        return m_JdbcTemplate.query(p_Sql,objects,new ResultSetExtractor<DataTable>(){
            @Override
            public DataTable extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                return new DataTable(resultSet,sql);
            }
        });
    }

    @Override
    public int executeUpdate(String p_Sql, Object[] objects) throws APPErrorException {
        return m_JdbcTemplate.update(p_Sql,objects);
    }

    @Override
    public int executeBlob(String p_Sql, byte[] bytes) throws APPErrorException {
        return m_JdbcTemplate.update(p_Sql,bytes);
    }

    @Override
    public int[] executeSqlFile(String s) throws APPErrorException {
        throw new APPErrorException("未实现改方法");
    }

    @Override
    public boolean executeBatchByTransaction(final String[] strings, int i) throws APPErrorException {
        final int level=i;
        final String[] sqls=strings;
       return m_JdbcTemplate.execute(new ConnectionCallback<Boolean>() {
            @Override
            public Boolean doInConnection(Connection connection) throws SQLException, DataAccessException {

                try {
                    connection.setAutoCommit(false);
                    if(level>-1) {
                        connection.setTransactionIsolation(level);
                    }
                    Statement statment = connection.createStatement();

                    for (String sql : sqls)
                        if (!StringUtil.isNullOrEmpty(sql)) {
                            statment.addBatch(sql);
                        }

                    statment.executeBatch();
                    connection.commit();
                    return true;
                }
                catch (Exception e) {
                    try {
                        connection.rollback();
                    } catch (SQLException e1) {
                        LogHelper.error("","事物批量回滚失败","TransactionDataAccess.executeBatchByTransaction",e);
                    }
                    StringBuilder sb =new StringBuilder();
                    for(int i=0;i<strings.length;i++)
                    {
                        sb.append(strings[i]);
                        sb.append("\r\n");
                    }
                    LogHelper.error("","事物批量执行失败;sqls: \r\n"+sb.toString(),"TransactionDataAccess.executeBatchByTransaction",e);
                    return false;
                } finally {
                    try{connection.close();}
                    catch (Exception e)
                    {

                    }
                }
            }
        });

    }
}
