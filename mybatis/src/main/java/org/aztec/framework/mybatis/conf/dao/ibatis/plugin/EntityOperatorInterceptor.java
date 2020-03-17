package org.aztec.framework.mybatis.conf.dao.ibatis.plugin;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

import javax.persistence.Table;

import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.defaults.DefaultSqlSession.StrictMap;
import org.aztec.framework.mybatis.conf.dao.ibatis.ShardingThreadLocalContext;
import org.aztec.framework.mybatis.conf.dao.ibatis.ShardingThreadLocalContext.ShardingColumnMatchType;
import org.aztec.framework.mybatis.utils.CustomTimeOperatingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import io.shardingsphere.core.keygen.DefaultKeyGenerator;

/**
 * 实体操作拦截器。用于自动注入生成的主键ID
 * @author liming
 *
 */
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
            Class objClass = object.getClass();
            System.out.println(object);
            System.out.println(objClass.getName());
            if(StrictMap.class.equals(objClass)){
                StrictMap sm = (StrictMap) object;
                List objList = (List) sm.get("list");
                doInjectID(objList);
            }
            else if (object instanceof ParamMap){
                ParamMap paramMap = (ParamMap) object;
                Object insertEntity = paramMap.get("entity");
                doInjectID(insertEntity);
            }
            else {
                doInjectID(object);
            }
            ShardingThreadLocalContext.put(ShardingThreadLocalContext.ContextKeys.ColumnMatchType,ShardingColumnMatchType.FULL);
        }
        else if(SqlCommandType.SELECT.equals(sqlCommandType)
//                || SqlCommandType.UPDATE.equals(sqlCommandType)
//                || sqlCommandType.DELETE.equals(sqlCommandType)
                ){
            ShardingThreadLocalContext.put(ShardingThreadLocalContext.ContextKeys.ColumnMatchType,ShardingColumnMatchType.PART);
        }
    }
    
    private void doInjectID(Object object) throws ArrayIndexOutOfBoundsException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException{
        Class objClass = object.getClass();
        if(objClass.isArray()){
            injectArrayID(object);
        }
        else if(List.class.isAssignableFrom(objClass)){
            injectListID(object);
        }
        else {
            injectData(object);
        }
    }
    
    
    
    private void injectArrayID(Object obj) throws ArrayIndexOutOfBoundsException,
            IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        int arrayLength = Array.getLength(obj);
        for (int i = 0; i < arrayLength; i++) {
            injectData(Array.get(obj, i));
        }
    }
    
    private void injectListID(Object obj) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException{
        List objList = (List) obj;
        for(Object object : objList){
            injectData(object);
        }
    }
    
    public void injectData(Object obj) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException{
        injectID(obj);
        fillForEntityTime(obj);
    }
    
    private void injectID(Object entity) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException{
        Field idField = null;
        try {
            idField = entity.getClass().getDeclaredField("id");
        } catch (java.lang.NoSuchFieldException e) {
            if(entity.getClass().getSuperclass() != null){
                System.out.println(entity.getClass().getSuperclass());
                idField = entity.getClass().getSuperclass().getDeclaredField("id");
            }
        }
        Table tableAnnotation = entity.getClass().getAnnotation(Table.class);
        if (tableAnnotation == null || idField == null)
            return;
        idField.setAccessible(true);
        Long id =  defaultKeyGenerator.generateKey().longValue();
        //idField.set(object, dailySequenceManager.incr(tableAnnotation.name()));
        idField.set(entity, id);
    }

    private void fillForEntityTime(Object  object) {
        try {
            long currentTimeInMills = CustomTimeOperatingUtils.currentTimeInMills();
            Field fieldCreateTime = null;
            try {
                fieldCreateTime = object.getClass().getDeclaredField("createTime");
            } catch (Exception e) {
                if(object.getClass().getSuperclass() != null){
                    fieldCreateTime = object.getClass().getSuperclass().getDeclaredField("createTime");
                }
            }
            fieldCreateTime.setAccessible(true);
            fieldCreateTime.set(object, new Timestamp(currentTimeInMills));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        fillIdForInsertEntity(invocation);
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

