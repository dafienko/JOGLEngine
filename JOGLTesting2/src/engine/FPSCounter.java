package engine;

public class FPSCounter {
	private long lastTimeMs;
	private double lastFPS;
	private int tickCount;
	private long timeSinceLastFrameUpdate;
	
	private final int UPDATE_INTERVAL = 1; // how long between framerate updates
	
	public FPSCounter() {
		lastTimeMs = System.currentTimeMillis();
		tickCount = 0;
		lastFPS = 0;
		timeSinceLastFrameUpdate = 0;
	}
	
	public float tick() {
		long thisTimeMs = System.currentTimeMillis();
		long duration = thisTimeMs - lastTimeMs;
		lastTimeMs = thisTimeMs;
		
		timeSinceLastFrameUpdate += duration;
		
		tickCount++;
		
		if (((double)timeSinceLastFrameUpdate / (double)1000) >= UPDATE_INTERVAL) {
			lastFPS = (double)tickCount / ((double)timeSinceLastFrameUpdate / (double)1000);
			timeSinceLastFrameUpdate = 0;
			tickCount = 0;
		}
		
		return (float)duration / 1000.0f;
	}
	
	public double getFPS() {
		return lastFPS;
	}
}
