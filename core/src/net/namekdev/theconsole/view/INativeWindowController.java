package net.namekdev.theconsole.view;

public interface INativeWindowController {
	public void setVisible(boolean visible);
	public boolean isVisible();

	public void setPosition(int x, int y);
	public int getX();
	public int getY();

	public void setSize(int width, int height);
	public int getWidth();
	public int getHeight();

	public int getScreenWidth();
	public int getScreenHeight();
}
