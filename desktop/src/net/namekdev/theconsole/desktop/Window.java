package net.namekdev.theconsole.desktop;

import static net.namekdev.theconsole.desktop.ReflectUtils.getField;
import static net.namekdev.theconsole.desktop.ReflectUtils.getMethod;

import java.lang.reflect.Method;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.UINT;

public class Window {
	private Object src;
	private Class<?> srcClass;
	private long hwnd;
	private HWND _hwnd;

	private Method m_showWindow, m_setWindowLongPtr, m_getWindowLongPtr;
	private Method m_setFocus, m_setWindowPos;

	public static final int SW_HIDE = 0;
	public static final int SW_SHOW = 5;
	public static final int SW_SHOWNORMAL = 1;
	public static final int SW_RESTORE = 9;
	public static final int SWP_SHOWWINDOW = 0x0040;
	public static final int SWP_NOSIZE = 0x0001;
	public static final int SWP_NOMOVE = 0x0002;
	public static final int SWP_NOOWNERZORDER = 0x0200;
	public static final int HWND_TOP = 0;
	public static final int HWND_TOPMOST = -1;
	public static final int GWL_STYLE = -16;
	public static final int GWL_EXSTYLE = -20;
	public static final int WS_VISIBLE = 0x10000000;
	public static final int WS_EX_TOOLWINDOW = 0x00000080;
	public static final int WS_EX_APPWINDOW = 0x40000;
	public static final long WS_EX_NOACTIVATE = 0x08000000L;
	public static final long WS_EX_TOPMOST = 0x00000008L;
	public static final long WS_EX_CLIENTEDGE = 0x00000200;
	public static final long WS_EX_LAYERED = 0x00080000;
	public static final int BM_CLICK = 0x00F5;
	
	
	public Window(Object methodsSourceObject) {
		src = methodsSourceObject;
		srcClass = src.getClass();
		m_showWindow = getMethod(srcClass, "showWindow");
		m_setWindowLongPtr = getMethod(srcClass, "setWindowLongPtr");
		m_getWindowLongPtr = getMethod(srcClass, "getWindowLongPtr");
		m_setFocus = getMethod(srcClass, "setFocus");
		m_setWindowPos = getMethod(srcClass, "setWindowPos");
	}
	
	long getHwnd() {
		if (hwnd == 0) {
			try {
				hwnd = getField(src, "hwnd").getLong(src);
				_hwnd = new HWND(new Pointer(hwnd));
			}
			catch (Exception exc) { 
				exc.printStackTrace();
			}
		}

		return hwnd;
	}
	
	HWND getHwndObj() {
		return _hwnd;
	}

	public void showWindow(int mode) {
		try {
			m_showWindow.invoke(src, getHwnd(), mode);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public long setWindowLong(int nindex, long longPtr) {
		try {
			return (Long) m_setWindowLongPtr.invoke(src, getHwnd(), nindex, longPtr);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return -1;
	}

	public long getWindowLong(int nindex) {
		try {
			return (Long) m_getWindowLongPtr.invoke(src, getHwnd(), nindex);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public boolean setForegroundWindow() {
		return User32Ext.INSTANCE.SetForegroundWindow(_hwnd).booleanValue();
	}
	
	public void setFocus() {
		try {
			m_setFocus.invoke(src, getHwnd());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean setWindowPos(long hwnd_after, int x, int y, int cx, int cy, long uflags) {
		try {
			return (Boolean) m_setWindowPos.invoke(src, getHwnd(), hwnd_after, x, y, cx, cy, uflags);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public void bringWindowToTop() {
		setWindowPos(HWND_TOPMOST, -1, -1, -1, -1,
			SWP_SHOWWINDOW | SWP_NOMOVE | SWP_NOSIZE
		);
	}

	public void setActiveWindow() {
		User32Ext.INSTANCE.SetActiveWindow(_hwnd);
	}

	public void LockSetForegroundWindow(int uLockCode) {
		User32Ext.INSTANCE.LockSetForegroundWindow(new UINT(uLockCode));
	}

	public void setSize(int width, int height) {
		setWindowPos(0, -1, -1, width, height, SWP_NOOWNERZORDER | SWP_NOMOVE);
	}
	
}
