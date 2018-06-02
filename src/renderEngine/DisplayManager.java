package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.*;
import org.lwjgl.opengl.DisplayMode;
import settings.DisplaySettings;

public class DisplayManager {

	private static long lastFrameTime;
	private static float delta;

	public static void createDisplay(String title, boolean fullScreen){
		ContextAttribs attribs = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true);			
		
		try {
			if (!fullScreen)
				Display.setDisplayMode(new DisplayMode(DisplaySettings.WIDTH, DisplaySettings.HEIGHT));
			else
				Display.setFullscreen(fullScreen);
			Display.create(new PixelFormat().withSamples(8).withDepthBits(24), attribs);
			Display.setTitle(title);
			GL11.glEnable(GL13.GL_MULTISAMPLE);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		if (!fullScreen)
			GL11.glViewport(0, 0, DisplaySettings.WIDTH, DisplaySettings.HEIGHT);
		lastFrameTime = getCurrentTime();
	}
	
	public static void updateDisplay(){		
		Display.sync(DisplaySettings.FPS_CAP);
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
}
