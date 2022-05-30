package com.ydzbinfo.emis.utils.mybatisplus.wrapper;

import com.baomidou.mybatisplus.MybatisAbstractSQL;
import com.baomidou.mybatisplus.mapper.SqlPlus;
import com.baomidou.mybatisplus.mapper.Wrapper;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 用于操作Wrapper内部不可见属性
 */
class WrapperInnerOperationUtil {
    private final Wrapper<?> wrapper;

    public WrapperInnerOperationUtil(Wrapper<?> wrapper) {
        assert wrapper != null;
        this.wrapper = wrapper;
        this.toWhere();
    }

    private List<String> andOr = null;

    @SuppressWarnings("unchecked")
    void registerAndOrPart(String andOrPart) {
        if (andOr == null) {
            try {
                Object SQLCondition = getInnerSQLCondition();
                Field andOrListField = SQLCondition.getClass().getDeclaredField("andOr");
                andOrListField.setAccessible(true);
                andOr = (List<String>) andOrListField.get(SQLCondition);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new WrapperException("注册andOr失败", e);
            }
        }
        andOr.add(andOrPart);
    }

    private Object SQLCondition_ = null;

    private Object getInnerSQLCondition() {
        if (SQLCondition_ == null) {
            try {
                Field sqlField = Wrapper.class.getDeclaredField("sql");
                sqlField.setAccessible(true);
                SqlPlus sqlPlus = (SqlPlus) sqlField.get(wrapper);
                Field sqlConditionField = MybatisAbstractSQL.class.getDeclaredField("sql");
                sqlConditionField.setAccessible(true);
                SQLCondition_ = sqlConditionField.get(sqlPlus);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new WrapperException("获取内部SQLCondition失败", e);
            }
        }
        return SQLCondition_;
    }


    private Field lastListField = null;

    public Field getLastListField() {
        if (lastListField == null) {
            try {
                lastListField = getInnerSQLCondition().getClass().getDeclaredField("lastList");
                lastListField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new WrapperException("获取lastListField失败", e);
            }
        }
        return lastListField;
    }


    @SuppressWarnings("unchecked")
    List<String> getLastList() {
        try {
            return (List<String>) getLastListField().get(getInnerSQLCondition());
        } catch (IllegalAccessException e) {
            throw new WrapperException("获取lastList失败", e);
        }
    }

    void setLastList(List<String> lastList) {
        try {
            getLastListField().set(getInnerSQLCondition(), lastList);
        } catch (IllegalAccessException e) {
            throw new WrapperException("设置lastList失败", e);
        }
    }

    private Field whereField = null;

    public Field getWhereField() {
        if (whereField == null) {
            try {
                whereField = getInnerSQLCondition().getClass().getDeclaredField("where");
                whereField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new WrapperException("获取whereField失败", e);
            }
        }
        return whereField;
    }

    @SuppressWarnings("unchecked")
    List<String> getWhere() {
        try {
            return (List<String>) getWhereField().get(getInnerSQLCondition());
        } catch (IllegalAccessException e) {
            throw new WrapperException("获取where失败", e);
        }
    }

    void toWhere() {
        List<String> where = this.getWhere();
        if (this.getLastList() != where) {
            this.setLastList(where);
        }
    }

    private Field havingField = null;

    public Field getHavingField() {
        if (havingField == null) {
            try {
                havingField = getInnerSQLCondition().getClass().getDeclaredField("having");
                havingField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new WrapperException("获取havingField失败", e);
            }
        }
        return havingField;
    }

    @SuppressWarnings("unchecked")
    List<String> getHaving() {
        try {
            return (List<String>) getHavingField().get(getInnerSQLCondition());
        } catch (IllegalAccessException e) {
            throw new WrapperException("获取having失败", e);
        }
    }

    void toHaving() {
        List<String> having = this.getHaving();
        if (this.getLastList() != having) {
            this.setLastList(having);
        }
    }

}
