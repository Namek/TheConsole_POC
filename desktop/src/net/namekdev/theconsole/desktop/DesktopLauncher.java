package net.namekdev.theconsole.desktop;

import static net.namekdev.theconsole.desktop.ReflectUtils.getFieldAsObject;
import static net.namekdev.theconsole.desktop.Window.GWL_EXSTYLE;
import static net.namekdev.theconsole.desktop.Window.GWL_STYLE;
import static net.namekdev.theconsole.desktop.Window.SW_HIDE;
import static net.namekdev.theconsole.desktop.Window.WS_EX_LAYERED;
import static net.namekdev.theconsole.desktop.Window.WS_EX_TOOLWINDOW;
import static net.namekdev.theconsole.desktop.Window.WS_EX_TOPMOST;
import static net.namekdev.theconsole.desktop.Window.WS_VISIBLE;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.namekdev.theconsole.TheConsole;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.NativeInputEvent;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.lwjgl.opengl.Display;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinUser.INPUT;

public class DesktopLauncher implements NativeKeyListener {
	public static void main (String[] arg) {
		new DesktopLauncher();
	}

	LwjglApplicationConfiguration config;
	LwjglApplication app;
	boolean isHidden = false;
	Timer timer = new Timer();

	Object display_impl;   // src.java.org.lwjgl.opengl.WindowsDisplay
	Window window;


	public DesktopLauncher() {
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.WARNING);

		try {
			GlobalScreen.registerNativeHook();
		}
		catch (NativeHookException ex) {
			System.err
					.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());

			System.exit(1);
		}

		GlobalScreen.addNativeKeyListener(this);


		System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");

		config = new LwjglApplicationConfiguration();
		config.x = 0;
		config.y = 0;
		config.resizable = false;
		config.backgroundFPS = 120;
		config.foregroundFPS = 120;
//		config.allowSoftwareMode = true;
		config.title = "The Console";
		config.width = 1920;
		config.height = 600;

		app = new LwjglApplication(new TheConsole(), config);
		display_impl = getFieldAsObject(Display.class, "display_impl");
		window = new Window(display_impl);

		timer.schedule(new TimerTask() {
			public void run() {
				window.showWindow(SW_HIDE); // hide the window
				window.setWindowLong(GWL_STYLE, User32.WS_POPUP | WS_VISIBLE);
				window.setWindowLong(GWL_EXSTYLE, WS_EX_LAYERED | WS_EX_TOOLWINDOW | WS_EX_TOPMOST);
				window.showWindow(SW_HIDE);
				isHidden = true;

				DisplayMode[] modes = Gdx.graphics.getDisplayModes();
				DisplayMode biggestMode = modes[0];
				for (DisplayMode mode : modes) {
					if (mode.width > biggestMode.width && mode.height > biggestMode.height) {
						biggestMode = mode;
					}
				}

				window.setSize(biggestMode.width, biggestMode.height/2);
			}
		}, 100);

		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				setVisible(true);
			}
		}, 1000);
	}

	private void setVisible(boolean show) {
		if (show) {
			timer.schedule(new TimerTask() {
				public void run() {
					window.bringWindowToTop();
				}
			}, 50);

			timer.schedule(new TimerTask() {
				INPUT input = null;

				public void run() {
					if (input == null) {
						int INPUT_MOUSE = 0;
						int MOUSEEVENTF_MOVE = 0x1;
						int MOUSEEVENTF_ABSOLUTE = 0x08000;
						int MOUSEEVENTF_LEFTDOWN = 0x2;
						int MOUSEEVENTF_LEFTUP = 0x4;
						input = new INPUT();
						input.type = new DWORD(INPUT_MOUSE);
						input.input.mi.dx = new LONG(config.x + 10);
						input.input.mi.dy = new LONG(config.y + 10);
						input.input.mi.dwFlags = new DWORD(MOUSEEVENTF_MOVE | MOUSEEVENTF_ABSOLUTE | MOUSEEVENTF_LEFTDOWN | MOUSEEVENTF_LEFTUP);
					}

					// Put window on the top
					window.setForegroundWindow();

					// Simulate click to set focus
					User32Ext.INSTANCE.SendInput(new UINT(1), input, input.size());
				}
			}, 100);
		}
		else {
			timer.schedule(new TimerTask() {
				public void run() {
					window.showWindow(SW_HIDE);
				}
			}, 50);
		}

		isHidden = !show;
	}

	private boolean isConsoleToggleEvent(NativeKeyEvent evt) {
		return evt.getKeyCode() == NativeKeyEvent.VC_BACKQUOTE && (evt.getModifiers() == NativeKeyEvent.CTRL_L_MASK);
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent evt) {
		if (isConsoleToggleEvent(evt)) {
			boolean show = isHidden;

			setVisible(show);
			consumeEvent(evt);
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent evt) {
		if (isConsoleToggleEvent(evt)) {
			consumeEvent(evt);
		}
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent evt) {
	}

	private void consumeEvent(NativeKeyEvent evt) {
		try {
			Field f = NativeInputEvent.class.getDeclaredField("reserved");
			f.setAccessible(true);
			f.setShort(evt, (short) 0x01);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
