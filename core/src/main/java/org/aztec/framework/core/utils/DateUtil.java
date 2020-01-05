package org.aztec.framework.core.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

public class DateUtil {

	public static final String DATE_FORMAT_HHMMSS = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_FORMAT_YMD = "yyyy-MM-dd";

	public static final ThreadLocal<SimpleDateFormat> df = new ThreadLocal<SimpleDateFormat>() {
		protected synchronized SimpleDateFormat initialValue() {
			return new SimpleDateFormat();
		}
	};

	/**
	 * ʱ��ת�ַ���
	 * 
	 * @param format
	 *            ʱ���ʽ
	 * @param date
	 *            ����
	 * @return
	 */
	public static String getDate2Str(String format, Date date) {

		if (date == null)
			return "";
		df.get().applyPattern(format);
		return df.get().format(date);
	}
    public static Date add(Date date,int type, int number) {  
        try {
        	Calendar k = Calendar.getInstance();
        	k.setTime(date);
        	k.add(type, number);
        	return k.getTime();
        } catch (Exception e) {  
           throw new RuntimeException(); 
        }  
    }
	/**
	 * ���ض���ʽ��ʱ���ַ���ת��ΪDate����
	 * 
	 * @param format
	 *            ʱ���ʽ
	 * @param str
	 *            ĳ���ڵ��ַ���
	 * @return ĳ���ڣ�Date��
	 */
	public static Date getStrToDate(String format, String str) {

		if (StringUtils.isBlank(str))
			return null;

		df.get().applyPattern(format);
		ParsePosition parseposition = new ParsePosition(0);
		return df.get().parse(str, parseposition);
	}

	/**
	 * ������ת��Ϊ���ַ�������������-��-�� ʱ:��:�룩
	 * 
	 * @param date
	 *            ����
	 * @return �������磺yyyy-MM-dd HH:mm:ss ���ַ���
	 */
	public static String getDate2Str(Date date) {
		return getDate2Str(DATE_FORMAT_HHMMSS, date);
	}

	/**
	 * ��ĳָ�����ַ���ת��Ϊ���磺yyyy-MM-dd HH:mm:ss��ʱ��
	 * 
	 * @param str
	 *            ����ת��ΪDate���ַ���
	 * @return ����date
	 */
	public static Date getStr2Date(String str) {
		return getStrToDate(DATE_FORMAT_HHMMSS, str);
	}
	/**
	 * �Կ�ֵ�����жϣ�Ȼ��ʹ��getStr2Dateת������
	 * @see com.taobao.wmpbasic.client.util.DateUtil.getStr2Date(String)
	 * @param str
	 * @return
	 */
	public static Date getStr2DateBlankable(String str) {
		if(isBlank(str)){
			return null;
		}
		try {
			DateFormat df = DateFormat.getDateInstance();
			return df.parse(str);
		} catch (ParseException e) {
			return getStr2Date(str);
		}
	}
    /**
     * copy from apache common lang
     * @param cs
     * @return
     */
    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

	/**
	 * 
	 * @describe: ƴ���ַ�����Ϊ�ַ���
	 * @param: String[]
	 * @return:ƴ���ַ���
	 */
	public static String getStringByArray(String[] strs) {
		StringBuilder sb = new StringBuilder(strs.length * 3);
		for (int i = 0; i < strs.length; i++) {
			if (strs[i].length() == 0) {
				strs[i] = " ";
			}
			sb.append(strs[i]).append(";");
		}
		return sb.toString();
	}

	/**
	 * �Ƚ����ڴ�С ����0��ʱ��1 > ʱ��2
	 */
	public static long compareDate(Date date1, Date date2) {
		return date1.getTime() - date2.getTime();
	}
	
    /**
     * ��ȡ����d��days����һ��Date
     * @param d
     * @param days
     * @return
     */
    public static Date getInternalDateByDay(Date d, int days)
    {
        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        now.setTime(d);
        now.add(Calendar.DATE, days);
        return now.getTime();
    }
     
    public static Date getInternalDateByMon(Date d, int months)
    {
        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        now.setTime(d);
        now.add(Calendar.MONTH, months);
        return now.getTime();
    }
     
    public static Date getInternalDateByYear(Date d, int years)
    {
        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        now.setTime(d);
        now.add(Calendar.YEAR, years);
        return now.getTime();
    }
     
    public static Date getInternalDateBySec(Date d, int sec)
    {
        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        now.setTime(d);
        now.add(Calendar.SECOND, sec);
        return now.getTime();
    }
     
    public static Date getInternalDateByMin(Date d, int min)
    {
        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        now.setTime(d);
        now.add(Calendar.MINUTE, min);
        return now.getTime();
    }
     
    public static Date getInternalDateByHour(Date d, int hours)
    {
        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        now.setTime(d);
        now.add(Calendar.HOUR_OF_DAY, hours);
        return now.getTime();
    }
    

	/**
	 * ����currentDate ֮ǰ day ��ʱ�䣬�����и�ʽ��<br>
	 * ���ؿ�ʼʱ�䣬�ͽ���ʱ��<br>
	 * һ�����ڲ�ѯ<br>
	 * @param currentDate
	 * @param day
	 */
	public static List<Date> getIntervalDateList(Date currentDate, int day){
	  List<Date> list = new ArrayList<Date>();	
	  Calendar c = Calendar.getInstance();
	  c.setTime(currentDate);
	  c.add(Calendar.DAY_OF_MONTH, day);
	  Date startDate = c.getTime();
	  list.add(startDate);
	  list.add(currentDate);
	  return list;
	}
	/**
	 * ĳһ�µĵ�һ��,��ʽ��ֻ��������
	 * @param currentDate
	 * @param format
	 * @return
	 */
	public static List<Date> getMonthFirstDateList(Date currentDate){
		  List<Date> list = new ArrayList<Date>();
		  Calendar c = Calendar.getInstance();
		  c.setTime(currentDate);
		  c.set(Calendar.DAY_OF_MONTH, 1);
		  c.set(Calendar.HOUR_OF_DAY,0);
		  c.set(Calendar.MINUTE,0);
		  c.set(Calendar.SECOND,0);
		  Date startDate = c.getTime();
		  list.add(startDate);
		  c.setTime(currentDate);
		  c.set(Calendar.HOUR_OF_DAY,0);
		  c.set(Calendar.MINUTE,0);
		  c.set(Calendar.SECOND,0);
		  list.add(c.getTime());
		  return list;
		}
	/**
	 * ����currentDate ֮ǰ day ��ʱ�䣬�����и�ʽ��<br>
	 * ���ؿ�ʼʱ�䣬�ͽ���ʱ��<br>
	 * һ�����ڲ�ѯ<br>
	 * @param currentDate
	 * @param day
	 */
	public static List<String> getIntervalDateStrList(Date currentDate, String format,int day){
	  List<String> list = new ArrayList<String>();	
	  Calendar c = Calendar.getInstance();
	  c.setTime(currentDate);
	  c.add(Calendar.DAY_OF_MONTH, day);
	  Date startDate = c.getTime();
	  list.add(getDate2Str(format, startDate));
	  list.add(getDate2Str(format, currentDate));
	  return list;
	}
	
	/**
	 * ĳһ�µĵ�һ��,��ʽ��ֻ��������
	 * @param currentDate
	 * @param format
	 * @return
	 */
	public static List<String> getMonthFirstDayList(Date currentDate, String format){
		List<String> list = new ArrayList<String>();	
		  Calendar c = Calendar.getInstance();
		  c.setTime(currentDate);
		  c.set(Calendar.DAY_OF_MONTH, 1);
		  Date startDate = c.getTime();
		  list.add(getDate2Str(format, startDate));
		  list.add(getDate2Str(format, currentDate));
		  return list;
	}
	 /**
		 * ����һ����һ��
		 * @param d
		 * @return
		 */
	public static Date addOneDayLess(Date d){
		if( d != null ){
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0 );
			c.set(Calendar.SECOND, 0);
			long endTime = c.getTimeInMillis() + (24 * 60 * 60 * 1000-1000);
			return new Date(endTime);
		}
		return null;
	
	}
	
	/**
     * ����Ŀ�ʼʱ��
     * @return
     */
    public static Date startOfTodDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date date=calendar.getTime();
        return date;
    }
    
    /**
     * ����Ľ���ʱ��
     * @return
     */
    public static Date endOfTodDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date date=calendar.getTime();
        return date;
    }
    

	public static void main(String args[]){
		List<Date> list = getMonthFirstDateList(new Date());
		for(Date d : list){
			System.out.println(getDate2Str(d));
		}
		List<String> dates = getMonthFirstDayList(new Date(), "yyyy-MM-dd");
		for(String s : dates){
			System.out.println(s);
		}
		
		List<String> ss = getIntervalDateStrList(new Date(), "yyyy-MM-dd", -2);
		System.out.println(ss);
		
		Date d = startOfTodDay();
		System.out.println(d);
		
		Date f = endOfTodDay();
		System.out.println(f);
		
		//======================================
		
	}
	
	
}
