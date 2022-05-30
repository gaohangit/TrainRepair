package com.ydzbinfo.emis.utils.mybatisplus.wrapper;

import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.toolkit.SqlUtils;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.ydzbinfo.emis.utils.MybatisOgnlUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 额外处理like的通配符
 *
 * @author 张天可
 * @since 2021/11/30
 */
public class CustomWrapper<T> extends EntityWrapper<T> {

    private static final String AND_NOT = " AND NOT ";
    private static final String OR_NOT = " OR NOT ";
    private static final String NOT = " NOT ";
    // private static final String AND_NOT_NEW = ") \nAND NOT (";
    // private static final String OR_NOT_NEW = ") \nOR NOT (";

    public static final String DEFAULT_PARAM_KEY = "VAL";

    private String paramKey = DEFAULT_PARAM_KEY;

    private final WrapperInnerOperationUtil innerOperationUtil = new WrapperInnerOperationUtil(this);

    private void registExtraAndOrPart() {
        innerOperationUtil.registerAndOrPart(AND_NOT);
        innerOperationUtil.registerAndOrPart(OR_NOT);
        // innerOperationUtil.registerAndOrPart(NOT);
        // registAndOrPart(AND_NOT_NEW);
        // registAndOrPart(OR_NOT_NEW);
    }

    public CustomWrapper() {
        registExtraAndOrPart();
    }

    public CustomWrapper(String paramKey) {
        registExtraAndOrPart();
        this.paramKey = paramKey;
    }

    public String getParamKey() {
        return paramKey;
    }

    /**
     * 设置and not连接符
     */
    public void andNot() {
        innerOperationUtil.getLastList().add(AND_NOT);
    }

    /**
     * 设置or not连接符
     */
    public void orNot() {
        innerOperationUtil.getLastList().add(OR_NOT);
    }

    /**
     * 设置not描述符
     */
    public void not() {
        innerOperationUtil.getLastList().add(NOT);
    }

    private void handleLike(String column, String value, SqlLike type, boolean isNot, boolean ignoreCase) {
        if (StringUtils.isNotEmpty(column) && StringUtils.isNotEmpty(value)) {
            StringBuilder inSql = new StringBuilder();
            if (ignoreCase) {
                inSql.append("UPPER(");
                inSql.append(column);
                inSql.append(")");
            } else {
                inSql.append(column);
            }
            if (isNot) {
                inSql.append(" NOT");
            }

            inSql.append(" LIKE {0} escape '\\'");
            this.sql.WHERE(this.formatSql(
                inSql.toString(),
                SqlUtils.concatLike(MybatisOgnlUtils.replaceWildcardChars(
                    ignoreCase ? value.toUpperCase() : value,
                    '\\'
                ), type)
            ));
        }
    }

    public void likeIgnoreCase(String column, String value) {
        this.handleLike(column, value, SqlLike.DEFAULT, false, true);
    }

    @Override
    public CustomWrapper<T> like(String column, String value) {
        this.like(true, column, value);
        return null;
    }

    @Override
    public CustomWrapper<T> like(String column, String value, SqlLike type) {
        return this.like(true, column, value, type);
    }

    @Override
    public CustomWrapper<T> like(boolean condition, String column, String value) {
        return this.like(condition, column, value, SqlLike.DEFAULT);
    }

    @Override
    public CustomWrapper<T> like(boolean condition, String column, String value, SqlLike type) {
        if (condition) {
            this.handleLike(column, value, type, false, false);
        }
        return this;
    }

    @Override
    public CustomWrapper<T> notLike(String column, String value) {
        return this.notLike(true, column, value);
    }

    @Override
    public CustomWrapper<T> notLike(String column, String value, SqlLike type) {
        return this.notLike(true, column, value, type);
    }

    @Override
    public CustomWrapper<T> notLike(boolean condition, String column, String value) {
        return this.notLike(condition, column, value, SqlLike.DEFAULT);
    }

    @Override
    public CustomWrapper<T> notLike(boolean condition, String column, String value, SqlLike type) {
        if (condition) {
            this.handleLike(column, value, type, true, false);
        }
        return this;
    }

    private void handleIn(boolean condition, String column, Collection<?> values, boolean isNot) {
        if (condition && CollectionUtils.isNotEmpty(values)) {
            List<? extends List<?>> splitGroups = MybatisOgnlUtils.splitValuesForHugeSizeIn(values);
            if (splitGroups.size() > 1) {
                for (int i = 0; i < splitGroups.size(); i++) {
                    this.sql.WHERE((i == 0 ? "(" : "") +
                        this.inExpressionParsed(column, splitGroups.get(i), isNot) +
                        (i == splitGroups.size() - 1 ? ")" : "")
                    );
                    if (i != splitGroups.size() - 1) {
                        if (isNot) {
                            this.sql.AND();
                        } else {
                            this.sql.OR();
                        }
                    }
                }
            } else {
                this.sql.WHERE(this.inExpressionParsed(column, values, isNot));
            }
        }
    }

    @Override
    public CustomWrapper<T> in(boolean condition, String column, Collection<?> values) {
        this.handleIn(condition, column, values, false);
        return this;
    }

    @Override
    public CustomWrapper<T> in(boolean condition, String column, Object[] value) {
        return this.in(condition, column, Arrays.asList(value));
    }

    @Override
    public CustomWrapper<T> notIn(boolean condition, String column, Collection<?> values) {
        this.handleIn(condition, column, values, true);
        return this;
    }

    @Override
    public CustomWrapper<T> notIn(boolean condition, String column, Object... value) {
        return this.notIn(condition, column, Arrays.asList(value));
    }

    private String inExpressionParsed(String column, Collection<?> value, boolean isNot) {
        if (StringUtils.isNotEmpty(column) && CollectionUtils.isNotEmpty(value)) {
            StringBuilder inSql = new StringBuilder();
            inSql.append(column);
            if (isNot) {
                inSql.append(" NOT");
            }

            inSql.append(" IN ");
            inSql.append("(");
            int size = value.size();
            int i = 0;
            for (Object v : value) {
                String genParamName = this.genParamName();
                inSql.append(this.genParamSlot(genParamName));
                this.paramPairs.put(genParamName, v);
                if (i + 1 < size) {
                    inSql.append(",");
                }
                i++;
            }

            inSql.append(")");
            return inSql.toString();
        } else {
            return null;
        }
    }

    private final Map<String, Object> paramPairs = new HashMap<>();
    private final AtomicInteger paramNameSeq = new AtomicInteger(0);

    private String genParamName() {
        return getParamKey() + "_" + this.paramNameSeq.incrementAndGet();
    }

    private String genParamSlot(String genParamName) {
        return "#{" + this.getParamAlias() + ".paramPairs." + genParamName + "}";
    }

    @Override
    protected String formatSqlIfNeed(boolean need, String sqlStr, Object... params) {
        if (need && !StringUtils.isEmpty(sqlStr)) {
            if (ArrayUtils.isNotEmpty(params)) {
                for (int i = 0; i < params.length; ++i) {
                    String genParamName = this.genParamName();
                    sqlStr = sqlStr.replace(String.format("{%s}", i), this.genParamSlot(genParamName));
                    this.paramPairs.put(genParamName, params[i]);
                }
            }
            return sqlStr;
        } else {
            return null;
        }
    }

    @Override
    public Map<String, Object> getParamNameValuePairs() {
        return this.paramPairs;
    }

    public Map<String, Object> getParamPairs() {
        return this.paramPairs;
    }

    private String replacePlaceholder(String sqlSegment) {
        if (StringUtils.isEmpty(sqlSegment)) {
            return "";
        } else {
            return sqlSegment.replaceAll("#\\{" + this.getParamAlias() + ".paramPairs." + getParamKey() + "_[0-9]+}", "\\?");
        }
    }

    @Override
    public String originalSql() {
        return this.replacePlaceholder(this.getSqlSegment());
    }

    private boolean isHavingSet = false;

    @Override
    public CustomWrapper<T> having(boolean condition, String sqlHaving, Object... params) {
        if (condition) {
            this.sql.HAVING(this.formatSql(sqlHaving, params));
            isHavingSet = true;
        }
        return this;
    }

    private boolean isGroupBySet = false;

    @Override
    public CustomWrapper<T> groupBy(boolean condition, String columns) {
        if (condition) {
            this.sql.GROUP_BY(columns);
            isGroupBySet = true;
        }
        return this;
    }

    private boolean isOrderBySet = false;

    @Override
    public CustomWrapper<T> orderBy(boolean condition, String columns) {
        if (condition) {
            this.sql.ORDER_BY(columns);
            isOrderBySet = true;
        }
        return this;
    }

    @Override
    public CustomWrapper<T> orderBy(boolean condition, String columns, boolean isAsc) {
        if (condition && StringUtils.isNotEmpty(columns)) {
            this.sql.ORDER_BY(columns + (isAsc ? " ASC" : " DESC"));
            isOrderBySet = true;
        }
        return this;
    }

    public String getSqlWhereSegment() {
        if (isHavingSet) {
            throw new RuntimeException("已设置过having，不可获得where语句");
        }
        if (isGroupBySet) {
            throw new RuntimeException("已设置过groupBy，不可获得where语句");
        }
        if (isOrderBySet) {
            throw new RuntimeException("已设置过orderBy，不可获得where语句");
        }
        repairAllFirstNot();
        String sqlWhere = this.sql.toString();
        if (StringUtils.isEmpty(sqlWhere)) {
            return null;
        } else {
            return sqlWhere.replaceFirst("WHERE", "");
        }
    }

    private void repairFirstNot(List<String> parts) {
        if (parts.size() > 1 && parts.get(0).equals(NOT)) {
            parts.remove(0);
            parts.set(0, NOT + parts.get(0));
        }
    }

    private void repairAllFirstNot() {
        // 如果开始是NOT将其融入第一个条件，防止出现bug
        repairFirstNot(this.innerOperationUtil.getWhere());
        repairFirstNot(this.innerOperationUtil.getHaving());
    }

    @Override
    public String getSqlSegment() {
        repairAllFirstNot();
        String sqlWhere = this.sql.toString();
        if (StringUtils.isEmpty(sqlWhere)) {
            return null;
        } else {
            if (this.isWhere != null && this.isWhere) {
                return sqlWhere;
            } else {
                return sqlWhere.replaceFirst("WHERE", this.AND_OR);
            }
        }
    }

    public void and(CustomWrapper<T> wrapper) {
        this.sql.AND();
        this.addWrapperCondition(wrapper);
    }

    public void or(CustomWrapper<T> wrapper) {
        this.sql.OR();
        this.addWrapperCondition(wrapper);
    }

    public void addWrapperCondition(CustomWrapper<T> wrapper) {
        this.sql.WHERE(wrapper.getSqlWhereSegment());
        wrapper.getParamNameValuePairs().forEach((key, value) -> {
            if (!this.paramPairs.containsKey(key)) {
                this.paramPairs.put(key, value);
            } else {
                throw new WrapperException("名称重复");
            }
        });
    }

    public void toWhere() {
        innerOperationUtil.toWhere();
    }

    public void toHaving() {
        innerOperationUtil.toHaving();
    }

}
