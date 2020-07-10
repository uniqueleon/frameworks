/**
 *  
 */
package org.aztec.framework.async.task.util;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

/**
 * �����жϵĹ�����
 * 
 * @author jack_liang 2015��4��28��
 */
public class ParameterUtils {

	/**
	 * �޶���������Ϊ�գ�null��""����Ҳ������ȫ���ַ��ǿո񣬷����׳�IllegalArgumentException
	 * 
	 * @param s
	 *            - �ַ���
	 * @param errorMessage
	 *            - ����ʱ���쳣������Ϣ
	 * @throws IllegalArgumentException
	 *             - ����ʱ�׳��쳣
	 */
	public static void assertNotBlank(String s, String errorMessage) {
		if (StringUtils.isBlank(s)) {
			throw new IllegalArgumentException(errorMessage);
		}
	}

	/**
	 * �޶���������Ϊtrue�������׳�IllegalArgumentException
	 * 
	 * @param b
	 *            - ����ֵ
	 * @param errorMessage
	 *            - ����ʱ���쳣������Ϣ
	 * @throws IllegalArgumentException
	 *             - ����ʱ�׳��쳣
	 * 
	 * @author liuying 2014-08-18
	 */
	public static void assertTrue(boolean b, String errorMessage) {
		if (!b) {
			throw new IllegalArgumentException(errorMessage);
		}
	}

	/**
	 * �޶������ĳ��Ȳ��ܳ���ָ��ֵ�������׳��쳣�� �� s ������null�����ҳ��ȳ���maxLength���׳��쳣��
	 * 
	 * @param s
	 *            - Ҫ�жϵ��ַ���
	 * @param maxLength
	 *            - ָ������
	 * @param errorMessage
	 *            - ����ʱ���쳣������Ϣ
	 * @throws IllegalArgumentException
	 *             - ����ʱ�׳��쳣
	 */
	public static void assertMaxLength(String s, int maxLength, String errorMessage) {
		if (s != null && s.length() > maxLength) {
			throw new IllegalArgumentException(errorMessage);
		}
	}

	/**
	 * �޶�һ�����������Ǹ����������׳�IllegalArgumentException
	 * 
	 * @param n
	 *            - Ҫ�жϵ�����
	 * @param errorMessage
	 *            - ����ʱ���쳣������Ϣ
	 * @throws IllegalArgumentException
	 *             - ����ʱ�׳��쳣
	 */
	public static void assertNonnegativeInt(int n, String errorMessage) {
		if (n < 0) {
			throw new IllegalArgumentException(errorMessage);
		}
	}

	/**
	 * �޶�a������ڵ���b�������׳�IllegalArgumentException
	 * 
	 * @param a
	 * @param b
	 * @param errorMessage
	 *            - ����ʱ���쳣������Ϣ
	 * @throws IllegalArgumentException
	 *             - ����ʱ�׳��쳣
	 */
	public static void assertGreaterThanOrEqual(int a, int b, String errorMessage) {
		if (a < b) {
			throw new IllegalArgumentException(errorMessage);
		}
	}

	/**
	 * �޶�a�������b�������׳�IllegalArgumentException
	 * 
	 * @param a
	 * @param b
	 * @param errorMessage
	 *            - ����ʱ���쳣������Ϣ
	 * @throws IllegalArgumentException
	 *             - ����ʱ�׳��쳣
	 */
	public static void assertEqual(int a, int b, String errorMessage) {
		if (a != b) {
			throw new IllegalArgumentException(errorMessage);
		}
	}

	/**
	 * �޶�a������ڵ���b�������׳�IllegalArgumentException
	 * 
	 * @param a
	 * @param b
	 * @param errorMessage
	 *            - ����ʱ���쳣������Ϣ
	 * @throws IllegalArgumentException
	 *             - ����ʱ�׳��쳣
	 */
	public static void assertGreaterThanOrEqual(long a, long b, String errorMessage) {
		if (a < b) {
			throw new IllegalArgumentException(errorMessage);
		}
	}

	/**
	 * �޶�����ֵ������ָ���б������֮һ�������׳�IllegalArgumentException
	 * 
	 * @param a
	 *            - ����ֵ
	 * @param errorMessage
	 *            - ����ʱ���쳣������Ϣ
	 * @param values
	 *            - ָ���б�
	 * @throws IllegalArgumentException
	 *             - ����ʱ�׳��쳣
	 */
	public static void assertOneOfThem(short a, String errorMessage, short... values) {
		boolean pass = false;
		if (values != null) {
			for (short n : values) {
				if (a == n) {
					pass = true;
					break;
				}
			}
		} else {
			throw new IllegalArgumentException("valuesΪ��");
		}
		if (!pass) {
			throw new IllegalArgumentException(errorMessage);
		}
	}

	/**
	 * �жϲ���������� 0�������׳��쳣
	 * 
	 * @param n
	 *            - Ҫ�жϵ�����
	 * @param errorMessage
	 *            - ����ʱ���쳣������Ϣ
	 * @throws IllegalArgumentException
	 *             - ����ʱ�׳��쳣
	 */
	public static void assertGreaterThanZero(int n, String errorMessage) {
		if (n < 1) {
			throw new IllegalArgumentException(errorMessage);
		}
	}

	/**
	 * �жϲ����б��������һ������0
	 * 
	 * @param values
	 *            - Ҫ�жϵ��������
	 * @param errorMessage
	 *            - ����ʱ���쳣������Ϣ
	 * @throws IllegalArgumentException
	 *             - ����ʱ�׳��쳣
	 */
	public static void assertOneOfThemThanZero(String errorMessage, int... values) {
		boolean exception = true;
		if (values != null) {
			for (int n : values) {
				if (n >= 0) {
					exception = false;
					break;
				}
			}
		}
		if (exception) {
			throw new IllegalArgumentException(errorMessage);
		}

	}

	/**
	 * �жϲ�������Ϊnull�������׳��쳣
	 * 
	 * @param o
	 *            - Ҫ�жϵĲ���
	 * @param errorMessage
	 *            - ����ʱ���쳣������Ϣ
	 * @throws IllegalArgumentException
	 *             - ����ʱ�׳��쳣
	 */
	public static void assertNotNull(Object o, String errorMessage) {
		if (o == null) {
			throw new IllegalArgumentException(errorMessage);
		}
	}
	
	/**
	 * �жϲ���Ϊnull�������׳��쳣
	 * 
	 * @param o
	 *            - Ҫ�жϵĲ���
	 * @param errorMessage
	 *            - ����ʱ���쳣������Ϣ
	 * @throws IllegalArgumentException
	 *             - ����ʱ�׳��쳣
	 */
	public static void assertNull(Object o, String errorMessage) {
		if (o != null) {
			throw new IllegalArgumentException(errorMessage);
		}
	}

	/**
	 * �ж�ʱ�䷶Χ�ĺϷ��ԣ�end>=start�������׳��쳣
	 * 
	 * @param start
	 *            - ��ʼʱ�䣬����Ϊnull
	 * @param end
	 *            - ����ʱ�䣬����Ϊnull
	 * @param errorMessage
	 *            - ����ʱ���쳣������Ϣ
	 * @throws IllegalArgumentException
	 *             - ����ʱ�׳��쳣
	 */
	public static void assertTimeRange(Date start, Date end, String errorMessage) {
		if (start == null || end == null) {
			throw new IllegalArgumentException("start��endΪ��");
		}
		if (start.after(end)) {
			throw new IllegalArgumentException(errorMessage);
		}
	}
}
