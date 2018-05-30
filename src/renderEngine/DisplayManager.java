package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.*;
import org.lwjgl.opengl.DisplayMode;

import java.awt.*;

public class DisplayManager {

	private static int width = 1366;
	private static int height = 768;
	private static final int FPS_CAP = 120;

	private static long lastFrameTime;
	private static float delta;

	public static void createDisplay(String title, boolean fullScreen){
		ContextAttribs attribs = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true);			
		
		try {
			if (!fullScreen)
				Display.setDisplayMode(new DisplayMode(width, height));
			else
				Display.setFullscreen(fullScreen);
			Display.create(new PixelFormat().withSamples(8).withDepthBits(24), attribs);
			Display.setTitle(title);
			GL11.glEnable(GL13.GL_MULTISAMPLE);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		if (!fullScreen)
			GL11.glViewport(0, 0, width, height);
		lastFrameTime = getCurrentTime();
	}
	
	public static void updateDisplay(){		
		Display.sync(FPS_CAP);
		Display.update();
		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime) / 1000f;
		lastFrameTime = currentFrameTime;
	}

	public static float getFrameTimeSeconds() {
		return delta;
	}

	public static void closeDisplay(){
		Display.destroy();		
	}

	private static long getCurrentTime(){
		return Sys.getTime() * 1000 / Sys.getTimerResolution();
	}

	public static int getWidth() {
		return width;
	}

	public static void setWidth(int width) {
		DisplayManager.width = width;
	}

	public static int getHeight() {
		return height;
	}

	public static void setHeight(int height) {
		DisplayManager.height = height;
	}
}
