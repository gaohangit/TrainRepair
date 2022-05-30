package com.ydzbinfo.emis.handlers;

import com.baomidou.mybatisplus.MybatisDefaultParameterHandler;
import com.baomidou.mybatisplus.enums.DBType;
import com.baomidou.mybatisplus.plugins.SqlParserHandler;
import com.baomidou.mybatisplus.plugins.pagination.DialectFactory;
import com.baomidou.mybatisplus.plugins.pagination.PageHelper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.baomidou.mybatisplus.plugins.parser.ISqlParser;
import com.baomidou.mybatisplus.plugins.parser.SqlInfo;
import com.baomidou.mybatisplus.toolkit.JdbcUtils;
import com.baomidou.mybatisplus.toolkit.PluginUtils;
import com.baomidou.mybatisplus.toolkit.SqlUtils;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.ydzbinfo.emis.utils.CommonUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

/**
 * 自定义分页拦截器，修复无法合理化页码的bug
 * 基于com.baomidou.mybatisplus.plugins.PaginationInterceptor修改
 *
 * @author 张天可
 * @date 2022年3月24日
 */
@Intercepts({
    @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class AppPaginationInterceptor extends SqlParserHandler implements Interceptor {

    public AppPaginationInterceptor() {
    }

    /**
     * 日志
     */
    private static final Log logger = LogFactory.getLog(AppPaginationInterceptor.class);
    /**
     * COUNT SQL 解析
     */
    private ISqlParser sqlParser;
    /**
     * 溢出总页数，设置第一页
     */
    private boolean overflowCurrent = false;
    /**
     * 方言类型
     */
    private String dialectType;
    /**
     * 方言实现类
     */
    private String dialectClazz;
    /**
     * 是否开启 PageHelper localPage 模式
     */
    private boolean localPage = false;

    /**
     * Physical Pagination Interceptor for all the queries with parameter {@link org.apache.ibatis.session.RowBounds}
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        this.sqlParser(metaObject);
        // 先判断是不是SELECT操作
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        if (!SqlCommandType.SELECT.equals(mappedStatement.getSqlCommandType())) {
            return invocation.proceed();
        }
        RowBounds rowBounds = (RowBounds) metaObject.getValue("delegate.rowBounds");
        /* 不需要分页的场合 */
        if (rowBounds == null || rowBounds == RowBounds.DEFAULT) {
            // 本地线程分页
            if (localPage) {
                // 采用ThreadLocal变量处理的分页
                rowBounds = PageHelper.getPagination();
                if (rowBounds == null) {
                    return invocation.proceed();
                }
            } else {
                // 无需分页
                return invocation.proceed();
            }
        }
        // 针对定义了rowBounds，做为mapper接口方法的参数
        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
        String originalSql = boundSql.getSql();
        Connection connection = (Connection) invocation.getArgs()[0];
        DBType dbType = StringUtils.isNotEmpty(dialectType) ? DBType.getDBType(dialectType) : JdbcUtils.getDbType(connection.getMetaData().getURL());
        if (rowBounds instanceof Pagination) {
            Pagination page = (Pagination) rowBounds;
            boolean orderBy = true;
            if (page.isSearchCount()) {
                SqlInfo sqlInfo = SqlUtils.getOptimizeCountSql(page.isOptimizeCountSql(), sqlParser, originalSql);
                orderBy = sqlInfo.isOrderBy();
                this.queryTotal(overflowCurrent, sqlInfo.getSql(), mappedStatement, boundSql, page, connection);
                int total = page.getTotal();
                if (total <= 0) {
                    return invocation.proceed();
                } else {
                    // 合理化查询页码
                    int pageSize = page.getSize();
                    int lastPageSize = CommonUtils.getLastPageSize(total, pageSize);
                    int pageCount = total / pageSize + (lastPageSize == pageSize ? 0 : 1);
                    int pageNum = page.getCurrent();
                    if (pageNum > pageCount) {
                        page.setCurrent(pageCount);
                    } else if (pageNum <= 0) {
                        page.setCurrent(1);
                    }
                }
            }
            String buildSql = SqlUtils.concatOrderBy(originalSql, page, orderBy);
            originalSql = DialectFactory.buildPaginationSql(page, buildSql, dbType, dialectClazz);
        } else {
            // support physical Pagination for RowBounds
            originalSql = DialectFactory.buildPaginationSql(rowBounds, originalSql, dbType, dialectClazz);
        }

        /*
         * <p> 禁用内存分页 </p>
         * <p> 内存分页会查询所有结果出来处理（这个很吓人的），如果结果变化频繁这个数据还会不准。</p>
         */
        metaObject.setValue("delegate.boundSql.sql", originalSql);
        metaObject.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET);
        metaObject.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);
        return invocation.proceed();
    }

    /**
     * 查询总记录条数
     *
     * @param sql
     * @param mappedStatement
     * @param boundSql
     * @param page
     */
    protected void queryTotal(boolean overflowCurrent, String sql, MappedStatement mappedStatement, BoundSql boundSql, Pagination page, Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            DefaultParameterHandler parameterHandler = new MybatisDefaultParameterHandler(mappedStatement, boundSql.getParameterObject(), boundSql);
            parameterHandler.setParameters(statement);
            int total = 0;
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    total = resultSet.getInt(1);
                }
            }
            page.setTotal(total);
            /*
             * 溢出总页数，设置第一页
             */
            int pages = page.getPages();
            if (overflowCurrent && (page.getCurrent() > pages)) {
                // 设置为第一条
                page.setCurrent(1);
            }
        } catch (Exception e) {
            logger.error("Error: Method queryTotal execution error !", e);
        }
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties prop) {
        String dialectType = prop.getProperty("dialectType");
        String dialectClazz = prop.getProperty("dialectClazz");
        String localPage = prop.getProperty("localPage");

        if (StringUtils.isNotEmpty(dialectType)) {
            this.dialectType = dialectType;
        }
        if (StringUtils.isNotEmpty(dialectClazz)) {
            this.dialectClazz = dialectClazz;
        }
        if (StringUtils.isNotEmpty(localPage)) {
            this.localPage = Boolean.valueOf(localPage);
        }
    }

    public AppPaginationInterceptor setDialectType(String dialectType) {
        this.dialectType = dialectType;
        return this;
    }

    public AppPaginationInterceptor setDialectClazz(String dialectClazz) {
        this.dialectClazz = dialectClazz;
        return this;
    }

    public AppPaginationInterceptor setOverflowCurrent(boolean overflowCurrent) {
        this.overflowCurrent = overflowCurrent;
        return this;
    }

    public AppPaginationInterceptor setSqlParser(ISqlParser sqlParser) {
        this.sqlParser = sqlParser;
        return this;
    }

    public AppPaginationInterceptor setLocalPage(boolean localPage) {
        this.localPage = localPage;
        return this;
    }
}
