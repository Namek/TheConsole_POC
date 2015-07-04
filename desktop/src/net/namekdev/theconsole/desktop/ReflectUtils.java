package net.namekdev.theconsole.desktop;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectUtils {
	public static Field getField(Class<?> cls, Object obj, String fieldName) {
		try {
			Field field = cls.getDeclaredField(fieldName);
			field.setAccessible(true);
			return field;
		}
		catch (Exception exc) {
			return null;
		}
	}
	
	public static Field getField(Class<?> cls, String fieldName) {
		return getField(cls, null, fieldName);
	}
	
	public static Field getField(Object obj, String fieldName) {
		return getField(obj.getClass(), obj, fieldName);
	}
	
	public static Object getFieldAsObject(Class<?> cls, Object obj, String fieldName) {
		try {
			Field field = getField(cls, obj, fieldName);
			return field.get(obj);
		}
		catch (Exception exc) {
			return null;
		}
	}
	
	public static Object getFieldAsObject(Object obj, String fieldName) {
		return getFieldAsObject(obj.getClass(), fieldName);
	}
	
	public static Object getFieldAsObject(Class<?> cls, String fieldName) {
		return getFieldAsObject(cls, null, fieldName);
	}
	
	public static Method getMethod(Class<?> cls, String methodName) {
		for (Method method : cls.getDeclaredMethods()) {
			if (method.getName().equals(methodName)) {
				method.setAccessible(true);
				return method;
			}
		}
		
		return null;
	}
}
