package net.namekdev.theconsole.desktop;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.win32.StdCallLibrary;

public interface User32Ext extends StdCallLibrary, WinUser, WinNT {
	User32Ext INSTANCE = (User32Ext) Native.loadLibrary("user32", User32Ext.class);

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
}
