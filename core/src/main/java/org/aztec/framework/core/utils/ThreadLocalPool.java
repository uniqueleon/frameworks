package org.aztec.framework.core.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * ThreadLocal������
 * 
 * @author tanson lam
 * @create 2016��8��18��
 * 
 */
public class ThreadLocalPool {
	private final static Set<ThreadLocal<?>> threadLocalPool = Collections
			.synchronizedSet(new HashSet<ThreadLocal<?>>());

	public static <T> ThreadLocal<T> createThreadLocal() {
		ThreadLocal<T> threadLocal = new ThreadLocal<T>();
		threadLocalPool.add(threadLocal);
		return threadLocal;
	}

	public static void clean() {
		try {
			for (ThreadLocal<?> theadLocal : threadLocalPool) {
				theadLocal.remove();
			}
		} catch (Exception e) { 
			 
		}
	}
}