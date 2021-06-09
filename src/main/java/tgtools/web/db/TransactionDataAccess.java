package tgtools.web.db;

import com.sun.rowset.CachedRowSetImpl;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import tgtools.data.DataTable;
import tgtools.db.AbstractDataAccess;
import tgtools.exceptions.APPErrorException;
import tgtools.util.JsonParseHelper;
import tgtools.util.LogHelper;
import tgtools.util.StringUtil;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.*;

/**
 * 名  称：
 * 编写者：田径
 * 功  能：
 * 时  间：13:36
 */
public class TransactionDataAccess extends AbstractDataAccess {
    private JdbcTemplate m_JdbcTemplate;

    public TransactionDataAccess(DataSource p_DataSource) {
        LogHelper.info("", "初始化数据源:" + p_DataSource, "TransactionDataAccess");
        setDataSource(p_DataSource);
        try {
            init(m_DataSource);
        } catch (APPErrorException e) {
            LogHelper.error("", "初始化失败", "TransactionDataAccess", e);
        }
    }

    public TransactionDataAccess() {
        this(null);
    }


    @Override
    public ResultSet executeQuery(String p_Sql) throws APPErrorException {
        return m_JdbcTemplate.query(p_Sql, new ResultSetExtractor<ResultSet>() {
            @Override
            public ResultSet extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                CachedRowSetImpl rowset = new CachedRowSetImpl();
                rowset.populate(resultSet);
                return rowset;
            }
        });
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
        if (null != m_JdbcTemplate && null != m_DataSource) {
            return true;
        }

        if (null != objects && objects.length == 1 && objects[0] instanceof DataSource) {
            m_JdbcTemplate = new JdbcTemplate((DataSource) objects[0]);
            return true;
        }
        return false;
    }

    @Override
    public void close() {
        m_DataSource = null;
        m_JdbcTemplate = null;
    }

    @Override
    public Connection createConnection() throws APPErrorException {
        try {
            return m_DataSource.getConnection();
        } catch (SQLException e) {
            throw new APPErrorException("获取数据库连接失败", e);
        }
    }

    @Override
    public int executeUpdate(String p_Sql, Object[] objects) throws APPErrorException {
        return m_JdbcTemplate.update(p_Sql, objects);
    }

    @Override
    public int executeUpdate(String sql, Object[] p_Params, boolean pUseSetInputStream) throws APPErrorException {
        Connection conn = null;

        try {
            conn = createConnection();
            if (conn != null) {
                PreparedStatement statement = conn.prepareStatement(sql);
                setParams(statement, p_Params, pUseSetInputStream);
                return statement.executeUpdate();
            }

        } catch (SQLException e) {
            throw new APPErrorException("sql执行失败：" + sql, e);
        } finally {
            close(conn);
        }
        return -1;
    }

    @Override
    public int executeBlob(String p_Sql, byte[] bytes) throws APPErrorException {
        return m_JdbcTemplate.update(p_Sql, bytes);
    }

    @Override
    public int[] executeSqlFile(String s) throws APPErrorException {
        throw new APPErrorException("未实现改方法");
    }

    @Override
    public boolean executeBatchByTransaction(final String[] strings, int i) throws APPErrorException {
        final int level = i;
        final String[] sqls = strings;
        return m_JdbcTemplate.execute(new ConnectionCallback<Boolean>() {
            @Override
            public Boolean doInConnection(Connection connection) throws SQLException, DataAccessException {

                try {
                    connection.setAutoCommit(false);
                    if (level > -1) {
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
                } catch (Exception e) {
                    try {
                        connection.rollback();
                    } catch (SQLException e1) {
                        LogHelper.error("", "事物批量回滚失败", "TransactionDataAccess.executeBatchByTransaction", e);
                    }
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < strings.length; i++) {
                        sb.append(strings[i]);
                        sb.append("\r\n");
                    }
                    LogHelper.error("", "事物批量执行失败;sqls: \r\n" + sb.toString(), "TransactionDataAccess.executeBatchByTransaction", e);
                    return false;
                } finally {
                    try {
                        connection.close();
                    } catch (Exception e) {

                    }
                }
            }
        });

    }


    @Override
    public DataTable Query(String p_Sql, Object[] objects) throws APPErrorException {
        return query(p_Sql, objects);
    }

    @Override
    public DataTable Query(String p_Sql) throws APPErrorException {
        return query(p_Sql);
    }

    @Override
    public DataTable Query(String p_Sql, boolean p_BlobUseStream) throws APPErrorException {
        return query(p_Sql, p_BlobUseStream);
    }

    @Override
    public <T> T Query(String p_Sql, Class<T> p_Class) throws APPErrorException {
        return query(p_Sql, p_Class);
    }


    @Override
    public DataTable query(String p_Sql, boolean p_BlobUseStream) throws APPErrorException {
        final String sql = p_Sql;
        final boolean blobUseStream = p_BlobUseStream;
        LogHelper.info("", sql, "TransactionDataAccess.Query");
        return m_JdbcTemplate.query(p_Sql, new ResultSetExtractor<DataTable>() {
            @Override
            public DataTable extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                return new DataTable(resultSet, sql, blobUseStream);
            }
        });
    }

    @Override
    public DataTable query(String p_Sql, Object[] p_Params) throws APPErrorException {
        final String sql = p_Sql;
        return m_JdbcTemplate.query(p_Sql, p_Params, new ResultSetExtractor<DataTable>() {
            @Override
            public DataTable extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                return new DataTable(resultSet, sql);
            }
        });
    }

    @Override
    public DataTable query(String sql) throws APPErrorException {
        return query(sql, false);
    }

    @Override
    public <T> T query(String sql, Class<T> p_Class) throws APPErrorException {
        return (T) JsonParseHelper.parseToObject(query(sql), p_Class, true);
    }

    protected void setParams(PreparedStatement p_Statement, Object[] p_Params, boolean pUseSetInputStream)
            throws SQLException {
        if (null != p_Params) {
            for (int i = 0; i < p_Params.length; i++) {
                if (pUseSetInputStream && (p_Params[i] instanceof InputStream)) {
                    try {
                        p_Statement.setBinaryStream(i + 1, (InputStream) p_Params[i], ((InputStream) p_Params[i]).available());
                    } catch (Exception ex) {
                        throw new SQLException("文件流设置错误；原因：" + ex.toString(), ex);
                    }
                } else {
                    p_Statement.setObject(i + 1, p_Params[i]);
                }
            }
        }
    }
    private void close(Connection p_Conn) {
        try {
            if (null != p_Conn) {
                p_Conn.close();
            }
        } catch (Exception e) {
        }
        p_Conn = null;
    }
}
