package org.aztec.framework.mybatis.conf.dao.ibatis.transformer;

import java.text.DateFormat.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.aztec.framework.mybatis.conf.dao.ibatis.ShardValueTransformer;
import org.springframework.stereotype.Component;

@Component("ybt")
public class YearBasedTransformer extends DatetimeTransformer implements ShardValueTransformer{
    

    public final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");

    @Override
    public SimpleDateFormat getTransformDataFormat() {
        return dateFormat;
    }


    public static void main(String[] args) {
        YearBasedTransformer mbt = new YearBasedTransformer();
        System.out.println(mbt.transformToLong(0, "2019-01-11 16:36:36"));
    }




    @Override
    public Object nextPoint(Object point) {
        Date datePoint = (Date) point;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(datePoint);
        calendar.set(Field.YEAR.getCalendarField(),calendar.get(Field.YEAR.getCalendarField()) + 1);
        return calendar.getTime();
    }


    @Override
    public Object previousPoint(Object point) {
        Date datePoint = (Date) point;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(datePoint);
        calendar.set(Field.YEAR.getCalendarField(),calendar.get(Field.YEAR.getCalendarField()) - 1);
        return calendar.getTime();
    }

}
