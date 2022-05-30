package com.ydzbinfo.emis.utils.mybatisplus.method;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.utils.entity.ReflectUtil;
import com.ydzbinfo.emis.utils.mybatisplus.param.ParamOperator;
import com.ydzbinfo.emis.utils.mybatisplus.param.*;
import com.ydzbinfo.emis.utils.mybatisplus.wrapper.CustomWrapper;

import java.lang.reflect.Array;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 张天可
 * @since 2021/12/29
 */
public class MethodUtil {

    @SuppressWarnings("unchecked")
    static <T> LogicalLinkable<? super T>[] getLogicalLinkableArray(List<? extends LogicalLinkable<? super T>> logicalLinkableList) {
        if (logicalLinkableList == null) {
            return null;
        } else {
            return logicalLinkableList.toArray((LogicalLinkable<? super T>[]) Array.newInstance(LogicalLinkable.class, 0));
        }
    }

    @SuppressWarnings("unchecked")
    static <T> OrderBy<? super T>[] getOrderByArray(List<? extends OrderBy<? super T>> orderByList) {
        if (orderByList == null) {
            return null;
        } else {
            return orderByList.toArray((OrderBy<? super T>[]) Array.newInstance(OrderBy.class, 0));
        }
    }

    // static <T> void addParams(CustomWrapper<T> wrapper, LogicalLinkable<T>[] logicalLinkableArray) {
    //     if (logicalLinkableArray.length == 1 && logicalLinkableArray[0] instanceof Criteria) {
    //         setCriteria(wrapper, (Criteria<T>) logicalLinkableArray[0]);
    //     } else {
    //         setCriteria(wrapper, ParamUtil.allMatch(logicalLinkableArray));
    //     }
    // }

    @SuppressWarnings("unchecked")
    static <T> void addParams(CustomWrapper<T> wrapper, Class<T> mainClass, LogicalLinkable<? super T>[] logicalLinkableArray) {
        if (logicalLinkableArray.length == 1 && logicalLinkableArray[0] instanceof Criteria) {
            if (logicalLinkableArray[0].getMainClass().equals(mainClass)) {
                setCriteria(wrapper, (Criteria<T>) logicalLinkableArray[0]);
            } else {
                Criteria<? super T> criteria_ = (Criteria<? super T>) logicalLinkableArray[0];
                Criteria<T> criteria;
                LogicalLinkable<? super T>[] children = MethodUtil.getLogicalLinkableArray(criteria_.getChildren());
                switch (criteria_.getLinkOperator()) {
                    case AND:
                        criteria = ParamUtil.allMatch(mainClass, children);
                        break;
                    case OR:
                        criteria = ParamUtil.anyMatch(mainClass, children);
                        break;
                    case AND_NOT:
                        criteria = ParamUtil.allNotMatch(mainClass, children);
                        break;
                    case OR_NOT:
                        criteria = ParamUtil.anyNotMatch(mainClass, children);
                        break;
                    default:
                        throw new RuntimeException("Unexpected value: " + criteria_.getLinkOperator());
                }
                setCriteria(wrapper, criteria);
            }
        } else {
            setCriteria(wrapper, ParamUtil.allMatch(mainClass, logicalLinkableArray));
        }
    }

    static <T> void orderBy(CustomWrapper<T> wrapper, OrderBy<? super T>[] orderBys) {
        if (orderBys != null) {
            for (OrderBy<? super T> orderBy : orderBys) {
                wrapper.orderBy(orderBy.getColumn(), orderBy.isAsc());
            }
        }
    }

    /**
     * 过滤掉空项
     */
    private static void filterEmptyCriteria(Criteria<?> criteria) {
        criteria.getChildren().removeIf(child -> {
            if (child == null) {
                return true;
            } else if (child instanceof Criteria) {
                Criteria<?> childCriteria = (Criteria<?>) child;
                filterEmptyCriteria(childCriteria);
                return childCriteria.getChildren().size() == 0;
            }
            return false;
        });
    }

    /**
     * 将直觉上可以感受到的不必要的层拍平
     * and嵌套and (考虑)
     * or嵌套or (考虑)
     * and not 嵌套 or (不考虑)
     * or not 嵌套 and (不考虑)
     */
    private static <T> void flatUnnecessaryLayers(Criteria<T> criteria) {
        List<LogicalLinkable<? super T>> children = criteria.getChildren();
        for (int index = 0; index < children.size(); index++) {
            LogicalLinkable<? super T> child = children.get(index);
            if (child instanceof Criteria) {
                //noinspection unchecked
                Criteria<? super T> childLinkable = (Criteria<? super T>) child;
                // 先拍平子条件的子条件
                flatUnnecessaryLayers(childLinkable);
                // 如果当前子条件满足拍平条件，则对当前子条件拍平
                if (needFlat(criteria, childLinkable)) {
                    // 把当前子条件替换为子条件的子条件列表
                    children.remove(index);
                    children.addAll(index, childLinkable.getChildren());
                    index += childLinkable.getChildren().size() - 1;
                }
            }
        }
    }

    private static boolean needFlat(Criteria<?> parent, Criteria<?> child) {
        LogicalLinkOperator parentLinkOperator = parent.getLinkOperator();
        LogicalLinkOperator childLinkOperator = child.getLinkOperator();
        if (parentLinkOperator == LogicalLinkOperator.AND || parentLinkOperator == LogicalLinkOperator.OR) {
            return childLinkOperator == parentLinkOperator;
        } else {
            return false;
        }
    }

    static <T> void setCriteria(CustomWrapper<T> wrapper, Criteria<? super T> criteria) {
        filterEmptyCriteria(criteria);
        flatUnnecessaryLayers(criteria);
        setCriteria(wrapper, criteria, new AtomicInteger(0));
    }

    private static <T> void setCriteria(CustomWrapper<T> wrapper, Criteria<? super T> criteria, AtomicInteger paramNameSeq) {
        if (criteria != null) {
            LogicalLinkOperator linkOperator = criteria.getLinkOperator();
            boolean first = true;
            for (LogicalLinkable<? super T> linkable : criteria.getChildren()) {
                if (first) {
                    first = false;
                    // and not \ or not 第一个需要省略 and \ or
                    // 如：
                    // not A and not B and not C
                    // not A or not B or not C
                    if (linkOperator == LogicalLinkOperator.AND_NOT || linkOperator == LogicalLinkOperator.OR_NOT) {
                        wrapper.not();
                    }
                } else {
                    if (linkOperator == LogicalLinkOperator.AND) {
                        wrapper.and();
                    } else if (linkOperator == LogicalLinkOperator.OR) {
                        wrapper.or();
                    } else if (linkOperator == LogicalLinkOperator.AND_NOT) {
                        wrapper.andNot();
                    } else if (linkOperator == LogicalLinkOperator.OR_NOT) {
                        wrapper.orNot();
                    }
                }

                if (linkable instanceof ColumnParam) {
                    @SuppressWarnings("unchecked")
                    ColumnParam<? super T> columnParam = (ColumnParam<? super T>) linkable;
                    setColumnParam(wrapper, columnParam);
                } else if (linkable instanceof Criteria) {
                    @SuppressWarnings("unchecked")
                    Criteria<? super T> childCriteria = (Criteria<? super T>) linkable;
                    CustomWrapper<T> childWrapper = new CustomWrapper<>(CustomWrapper.DEFAULT_PARAM_KEY + paramNameSeq.getAndIncrement());
                    setCriteria(childWrapper, childCriteria, paramNameSeq);
                    wrapper.addWrapperCondition(childWrapper);
                }
            }
        }
    }

    private static <T extends U, U> void setColumnParam(CustomWrapper<T> wrapper, ColumnParam<U> columnParam) {
        String column = columnParam.getColumnName();
        ParamOperator operator = columnParam.getOperator();
        Object value = columnParam.getParamValue();
        operator.setValueToWrapper(wrapper, column, value);
    }

    static <T> Class<T> getMainClassFromService(IService<T> service) {
        return ReflectUtil.getFirstTypeArgument(service, true);
    }

    static <T> Class<T> getMainClassFromBaseMapper(BaseMapper<T> baseMapper) {
        return ReflectUtil.getFirstTypeArgument(baseMapper, true);
    }

}
