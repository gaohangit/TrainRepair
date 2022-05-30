package com.ydzbinfo.emis.handlers;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.entity.GlobalConfiguration;
import com.baomidou.mybatisplus.entity.TableFieldInfo;
import com.baomidou.mybatisplus.entity.TableInfo;
import com.baomidou.mybatisplus.enums.FieldFill;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.baomidou.mybatisplus.enums.SqlMethod;
import com.baomidou.mybatisplus.mapper.AutoSqlInjector;
import com.baomidou.mybatisplus.toolkit.GlobalConfigUtils;
import com.ydzbinfo.emis.utils.CommonUtils;
import com.ydzbinfo.emis.utils.entity.ReflectUtil;
import org.apache.ibatis.mapping.SqlSource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * 修复mybatis-plus更新方法不能更新id的问题
 *
 * @author 张天可
 * @since 2022/3/25
 */
public class AppSqlInjector extends AutoSqlInjector {

    private TableFieldInfo getIdFieldInfo(TableInfo tableInfo, Class<?> modelClass) {
        Optional<Field> idFieldOptional = Arrays.stream(ReflectUtil.getEntityFields(modelClass)).filter(
            field -> field.isAnnotationPresent(TableId.class)
        ).findAny();
        if (idFieldOptional.isPresent()) {
            Field idField = idFieldOptional.get();
            GlobalConfiguration globalConfig = GlobalConfigUtils.getGlobalConfig(configuration);
            TableField tableField = new TableField() {
                @Override
                public Class<? extends Annotation> annotationType() {
                    return null;
                }

                @Override
                public String value() {
                    return tableInfo.getKeyColumn();
                }

                @Override
                public String el() {
                    return null;
                }

                @Override
                public boolean exist() {
                    return false;
                }

                @Override
                public String condition() {
                    return null;
                }

                @Override
                public String update() {
                    return null;
                }

                @Override
                public FieldStrategy strategy() {
                    return null;
                }

                @Override
                public FieldFill fill() {
                    return null;
                }
            };

            boolean logicDelete = tableInfo.isLogicDelete();
            TableFieldInfo idFieldInfo = new TableFieldInfo(
                globalConfig,
                tableInfo,
                tableInfo.getKeyColumn(),
                idField.getName(),
                idField,
                tableField
            );
            tableInfo.setLogicDelete(logicDelete);
            return idFieldInfo;
        } else {
            return null;
        }

    }

    private String sqlSet(boolean selective, TableInfo tableInfo, @SuppressWarnings("SameParameterValue") String prefix, Class<?> modelClass) {
        LinkedList<TableFieldInfo> fieldList = new LinkedList<>(tableInfo.getFieldList());
        TableFieldInfo idFieldInfo = getIdFieldInfo(tableInfo, modelClass);
        if (idFieldInfo != null) {
            fieldList.add(0, idFieldInfo);
        }
        TableInfo idFieldAddedTableInfo = new TableInfo(){
            @Override
            public void setFieldList(GlobalConfiguration globalConfig, List<TableFieldInfo> fieldList) {

            }

            @Override
            public List<TableFieldInfo> getFieldList() {
                return fieldList;
            }
        };
        CommonUtils.copyPropertiesToOther(tableInfo, idFieldAddedTableInfo, TableInfo.class);
        return super.sqlSet(selective, idFieldAddedTableInfo, prefix);
    }


    @Override
    protected void injectUpdateSql(Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        SqlMethod sqlMethod = SqlMethod.UPDATE;
        String sql = String.format(sqlMethod.getSql(), table.getTableName(), sqlSet(true, table, "et.", modelClass), sqlWhereEntityWrapper(table));
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        this.addUpdateMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource);
    }

    @Override
    protected void injectUpdateByIdSql(boolean selective, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        SqlMethod sqlMethod = selective ? SqlMethod.UPDATE_BY_ID : SqlMethod.UPDATE_ALL_COLUMN_BY_ID;
        String sql = String.format(sqlMethod.getSql(), table.getTableName(), sqlSet(selective, table, "et.", modelClass), table.getKeyColumn(),
            "et." + table.getKeyProperty(),
            "<if test=\"et instanceof java.util.Map\">"
                + "<if test=\"et.MP_OPTLOCK_VERSION_ORIGINAL!=null\">"
                + "and ${et.MP_OPTLOCK_VERSION_COLUMN}=#{et.MP_OPTLOCK_VERSION_ORIGINAL}"
                + "</if>"
                + "</if>"
        );
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        this.addUpdateMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource);
    }
}
