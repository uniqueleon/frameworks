package org.aztec.framework.mybatis.conf.dao.ibatis.plugin;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Properties;

import javax.persistence.Table;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.aztec.framework.mybatis.utils.CustomTimeOperatingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import io.shardingsphere.core.keygen.DefaultKeyGenerator;

@Configuration
@Intercepts({ @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }) })
public class EntityOperatorInterceptor implements Interceptor {

    private static Logger logger = LoggerFactory.getLogger(EntityOperatorInterceptor.class);

    private static DefaultKeyGenerator defaultKeyGenerator = new DefaultKeyGenerator(); 

    private void fillIdForInsertEntity(Invocation invocation)
            throws IllegalArgumentException, IllegalAccessException, Exception {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object object = null;
        // sql类型
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        if (SqlCommandType.INSERT.equals(sqlCommandType)) {
            object = invocation.getArgs()[1];
            Field idField = object.getClass().getDeclaredField("id");
            Table tableAnnotation = object.getClass().getAnnotation(Table.class);
            if (tableAnnotation == null || idField == null)
                return;
            idField.setAccessible(true);
            Long id =  defaultKeyGenerator.generateKey().longValue();
            //idField.set(object, dailySequenceManager.incr(tableAnnotation.name()));
            idField.set(object, id);
        }
    }

    private void fillForEntityTime(Invocation invocation) {
        try {
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            Object object = null;
            // sql类型
            SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
            if (SqlCommandType.INSERT.equals(sqlCommandType)) {
                object = invocation.getArgs()[1];
                // 插入操作时，自动插入
                long currentTimeInMills = CustomTimeOperatingUtils.currentTimeInMills();
                Field fieldCreateTime = object.getClass().getDeclaredField("createTime");
                fieldCreateTime.setAccessible(true);
                fieldCreateTime.set(object, new Timestamp(currentTimeInMills));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        fillIdForInsertEntity(invocation);
        fillForEntityTime(invocation);
        Object result = invocation.proceed();
        return result;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

}
