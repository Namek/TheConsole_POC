package net.namekdev.theconsole.desktop;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.win32.StdCallLibrary;

public interface User32Ext extends StdCallLibrary, WinUser, WinNT {
	User32Ext INSTANCE = (User32Ext) Native.loadLibrary("user32", User32Ext.class);

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
	public static final int LWA_COLORKEY = 0x00000001;
	public static final int LWA_ALPHA = 0x00000002;

	HWND SetActiveWindow(HWND hwnd);

	BOOL SendNotifyMessageW(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam);

	UINT SendInput(UINT nInputs, INPUT pInputs, int cbSize);

	BOOL BringWindowToTop(HWND hwnd);

	void SwitchToThisWindow(HWND hwnd, BOOL fAltTab);

	BOOL SetForegroundWindow(HWND _hwnd);

	BOOL LockSetForegroundWindow(UINT uLockCode);

	LRESULT SendMessageW(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam);

	LRESULT PostMessageW(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam);

	BOOL GetWindowRect(HWND hWnd, RECT lpRect);

	BOOL SetLayeredWindowAttributes(HWND hwnd, DWORD/*COLORREF*/ crKey, BYTE bAlpha, DWORD dwFlags);

	BOOL GetLayeredWindowAttributes(HWND hwnd, DWORD/*COLORREF*/ crKey, BYTE bAlpha, DWORD dwFlags);
}
