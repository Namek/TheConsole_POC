package net.namekdev.theconsole.desktop;

import static net.namekdev.theconsole.desktop.ReflectUtils.getField;
import static net.namekdev.theconsole.desktop.ReflectUtils.getMethod;
import static net.namekdev.theconsole.desktop.User32Ext.*;

import java.lang.reflect.Method;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.POINT;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinDef.UINT;

public class Window {
	private Object src;
	private Class<?> srcClass;
	private long hwnd;
	private HWND _hwnd;

	private Method m_showWindow, m_setWindowLongPtr, m_getWindowLongPtr;
	private Method m_setFocus, m_setWindowPos;

	private RECT tmpRect = new RECT();
	private DWORD tmpDWord = new DWORD(0);
	private DWORD tmpDWord2 = new DWORD(0);
	private BYTE tmpByte = new BYTE(0);


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

	public void setPosition(int x, int y) {
		setWindowPos(0, x, y, -1, -1, SWP_NOOWNERZORDER | SWP_NOSIZE);
	}

	public void getPosition(POINT outPosition) {
		User32Ext.INSTANCE.GetWindowRect(_hwnd, tmpRect);
		outPosition.x = tmpRect.left;
		outPosition.y = tmpRect.top;
	}

	public void getSize(POINT outSize) {
		User32Ext.INSTANCE.GetWindowRect(_hwnd, tmpRect);
		outSize.x = tmpRect.right - tmpRect.left;
		outSize.y = tmpRect.bottom - tmpRect.top;
	}

	/**
	 * @param opacity value in range: 0 to 255
	 */
	public void setOpacity(int opacity) {
		tmpDWord2.setValue(LWA_ALPHA);
		tmpByte.setValue(opacity);
		User32Ext.INSTANCE.SetLayeredWindowAttributes(_hwnd, tmpDWord, tmpByte, tmpDWord2);
	}

	public short getOpacity() {
		tmpDWord2.setValue(LWA_ALPHA);
		User32Ext.INSTANCE.GetLayeredWindowAttributes(_hwnd, tmpDWord, tmpByte, tmpDWord2);
		return tmpByte.shortValue();
	}
}
