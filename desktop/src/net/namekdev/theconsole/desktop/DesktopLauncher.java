package net.namekdev.theconsole.desktop;

import static net.namekdev.theconsole.desktop.ReflectUtils.getFieldAsObject;
import static net.namekdev.theconsole.desktop.User32Ext.*;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.namekdev.theconsole.TheConsole;
import net.namekdev.theconsole.view.INativeWindowController;

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
import com.sun.jna.platform.win32.WinDef.POINT;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinUser.INPUT;

public class DesktopLauncher implements NativeKeyListener {
	public static void main (String[] args) {
		new DesktopLauncher(args.length > 0 && args[0].equals("--show"));
	}

	LwjglApplicationConfiguration config;
	LwjglApplication app;
	boolean isHidden = false;
	Timer timer = new Timer();

	Object display_impl;   // src.java.org.lwjgl.opengl.WindowsDisplay
	Window window;


	public DesktopLauncher(boolean setVisibleOnStart) {
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
		config.backgroundFPS = 45;
		config.foregroundFPS = 45;
		config.allowSoftwareMode = true;
		config.title = "The Console";
		config.width = 1;
		config.height = 1;

		app = new LwjglApplication(new TheConsole(new NativeWindowController()), config);
		display_impl = getFieldAsObject(Display.class, "display_impl");
		window = new Window(display_impl);

		timer.schedule(new TimerTask() {
			public void run() {
				window.showWindow(SW_HIDE); // hide the window

				// set as visible and then hide again to hide app from task bar
				window.setWindowLong(GWL_STYLE, User32.WS_POPUP | WS_VISIBLE);
				window.showWindow(SW_HIDE);

				window.setWindowLong(GWL_EXSTYLE, window.getWindowLong(GWL_EXSTYLE) | WS_EX_LAYERED | WS_EX_TOOLWINDOW | WS_EX_TOPMOST);
				window.setOpacity(255);

				isHidden = true;

				// Find the biggest resolution (assuming it's currently active)
				// TODO: do it before creating window, Gdx.graphics will not be available, so use JNA.
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

		if (setVisibleOnStart) {
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					setVisible(true);
				}
			}, 1000);
		}
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

						// TODO get current position
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


	class NativeWindowController implements INativeWindowController {
		POINT point = new POINT();

		@Override
		public void setVisible(boolean visible) {
			DesktopLauncher.this.setVisible(visible);
		}

		@Override
		public boolean isVisible() {
			return !isHidden;
		}

		@Override
		public void setPosition(int x, int y) {
			window.setPosition(x, y);
		}

		@Override
		public int getX() {
			window.getPosition(point);
			return point.x;
		}

		@Override
		public int getY() {
			window.getPosition(point);
			return point.y;
		}

		@Override
		public void setSize(int width, int height) {
			window.setSize(width, height);
		}

		@Override
		public int getWidth() {
			window.getSize(point);
			return point.x;
		}

		@Override
		public int getHeight() {
			window.getSize(point);
			return point.y;
		}

		@Override
		public int getScreenWidth() {
			return Gdx.graphics.getDesktopDisplayMode().width;
		}

		@Override
		public int getScreenHeight() {
			return Gdx.graphics.getDesktopDisplayMode().height;
		}

		@Override
		public short getOpacity() {
			return (short) window.getOpacity();
		}

		@Override
		public void setOpacity(short opacity) {
			window.setOpacity(opacity);
		}

	}
}
