package com.yww.api.config.mybatis;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;

import java.util.List;

/**
 * <p>
 *      批量插入SQL注入器
 * </P>
 *
 * @author yww
 * @since 2023/11/26
 */
public class InsertBatchSqlInjector extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        // 获取MybatisPlus的自带方法
        List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);
        // 添加自定义批量插入方法，名称为insertBatchSomeColumn
        methodList.add(new InsertBatchSomeColumn());
        return methodList;
    }

}