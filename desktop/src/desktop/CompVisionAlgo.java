package desktop;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;




import boofcv.abst.denoise.FactoryImageDenoise;
import boofcv.abst.denoise.WaveletDenoiseFilter;
import boofcv.abst.feature.detect.interest.ConfigGeneralDetector;
import boofcv.abst.feature.tracker.PointTrack;
import boofcv.abst.feature.tracker.PointTracker;
import boofcv.alg.tracker.klt.PkltConfig;
import boofcv.factory.feature.tracker.FactoryPointTracker;
import boofcv.gui.feature.VisualizeFeatures;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayF32;

public class CompVisionAlgo extends Thread {

	private CompCameraProvider cam;
	private float turnVector;
	private int maxFeatures;
	private BufferedImage visualized;
	private boolean shouldStop = false;
	private boolean autoStarted = false;
	
	private static float turn_treshold = 0.2f;
	
	private Object frameMonitor = new Object();

	public CompVisionAlgo(CompCameraProvider camera, int maxFeatures) {
		this.cam = camera;
	}
	

	public float getTurnVector() {
		aliveCheck();
		
		return turnVector;
	}
	
	public BufferedImage GetVisualizedImage(){
		aliveCheck();
		
		if(visualized==null){
			try{
				synchronized (frameMonitor) {
					frameMonitor.wait(5000);
				}
			}catch(InterruptedException ex){}
		}

		return visualized;
	}
	
	public BufferedImage WaitNextVisualizedImage(){
		aliveCheck();
		
		try{
			synchronized (frameMonitor) {
				frameMonitor.wait(5000);
			}
		}catch(InterruptedException ex){}
		
		return visualized;
	}

	public void run() {
		
		ConfigGeneralDetector configDetector = new ConfigGeneralDetector(
				maxFeatures, 12, 1);
		PkltConfig configKlt = new PkltConfig(4, new int[] { 1, 2, 4, 8 });

		PointTracker<GrayF32> tracker = FactoryPointTracker.klt(configKlt,
				configDetector, GrayF32.class, null);

		HashMap<Long, PointData> pointLife = new HashMap<Long, PointData>();
		ArrayList<PointGroup> pointGroups = new ArrayList<PointGroup>();

		long lastPointUpdate = System.currentTimeMillis();
		long pointUpdateTreshold = 500;

		int minimumTracks = 100;

		// How many levels in wavelet transform
		int numLevels = 4;
		// Create the noise removal algorithm
		WaveletDenoiseFilter<GrayF32> denoiser = FactoryImageDenoise
				.waveletBayes(GrayF32.class, numLevels, 0, 255);

		double trackX = cam.getWidth() / 2;
		double trackY = cam.getHeight() / 2;
		double targetTrackX = 0;
		double targetTrackY = 0;

		while (!shouldStop) {
			try {

				BufferedImage image = cam.getLastFrame();
				if (image == null) {
					turnVector = 0;
					break;
				}

				GrayF32 noise = ConvertBufferedImage.convertFrom(image,
						(GrayF32) null);
				GrayF32 gray = noise.createSameShape();

				denoiser.process(noise, gray);

				tracker.process(gray);

				// remove noise from the image

				List<PointTrack> tracks = tracker.getActiveTracks(null);

				// Spawn tracks if there are too few
				if (tracks.size() < minimumTracks
						|| System.currentTimeMillis() - lastPointUpdate > pointUpdateTreshold) {

					lastPointUpdate = System.currentTimeMillis();

					tracker.spawnTracks();
					tracks = tracker.getActiveTracks(null);
					minimumTracks = tracks.size() / 2;

					HashMap<Long, PointData> newPointLife = new HashMap<Long, PointData>();

					if (pointGroups.size() == 0) {
						PointGroup defaultGroup = new PointGroup();
						pointGroups.add(defaultGroup);

						PointGroup moveGroup = new PointGroup();
						pointGroups.add(moveGroup);
					}

					for (PointGroup pg : pointGroups) {
						pg.size = 0;
					}

					for (PointTrack t : tracks) {
						PointData data = pointLife.get(t.featureId);
						if (data == null) {
							data = new PointData();
							data.lX = t.x;
							data.lY = t.y;
						}
						newPointLife.put(t.featureId, data);

						if (data.group == null) {
							data.group = pointGroups.get(0);
						}

						data.group.size++;
						data.wobblyness /= 2;
					}

					pointLife = newPointLife;
				}

				// Draw the tracks
				Graphics2D g2 = image.createGraphics();

				for (PointGroup pg : pointGroups) {
					pg.vX = 0;
					pg.vY = 0;
				}

				for (PointTrack t : tracks) {
					PointData data = pointLife.get(t.featureId);
					if (data == null) {
						continue;
					}
					data.lifeTime++;

					double nx = t.x;
					double ny = t.y;

					data.lVX = data.vX;
					data.lVY = data.vY;

					data.vX = nx - data.lX;
					data.vY = ny - data.lY;

					data.lX = nx;
					data.lY = ny;

					if (data.group.size != 0) {
						data.group.vX += data.vX / data.group.size;
						data.group.vY += data.vY / data.group.size;
					} else {
						System.out.println("Groupsize 0!!!!");
					}

					if (data.isWobbly()) {
						VisualizeFeatures.drawPoint(g2, (int) t.x, (int) t.y,
								Color.RED);
					} else {
						VisualizeFeatures.drawPoint(g2, (int) t.x, (int) t.y,
								data.group.color);
					}
				}

				ArrayList<PointTrack> moving = new ArrayList<PointTrack>();

				for (PointTrack t : tracks) {
					PointData data = pointLife.get(t.featureId);
					if (data == null) {
						continue;
					}

					double wobblyTreshold = 5;
					int wobblyDropTreshold = 5;

					if (Math.abs(data.vX - data.lVX) > wobblyTreshold
							|| Math.abs(data.vY - data.lVY) > wobblyTreshold) {

						if (Math.abs(data.vX - data.group.vX) > wobblyTreshold
								|| Math.abs(data.vY - data.group.vY) > wobblyTreshold) {

							data.wobblyness++;

							if (data.wobblyness > wobblyDropTreshold) {
								tracker.dropTrack(t);
							}
						}

					} else {
						data.wobblyness--;
					}

					double moveTreshold = 5;
					if (Math.abs(data.vX - data.group.vX) > moveTreshold
							|| Math.abs(data.vY - data.group.vY) > moveTreshold) {
						if (!data.isWobbly()) {
							PointGroup newGroup = pointGroups.get(1);
							if (newGroup != data.group) {
								data.group.size--;
								data.group = newGroup;
								newGroup.size++;
							}

							moving.add(t);
						}
					} else {
						if (!data.isWobbly()) {
							PointGroup newGroup = pointGroups.get(0);
							if (newGroup != data.group) {
								data.group.size--;
								data.group = newGroup;
								newGroup.size++;
							}
						}
					}

				}

				targetTrackX = 0;
				targetTrackY = 0;

				double trackR = 30;

				int highestMovingI = 0;

				for (PointTrack t : moving) {
					double x1 = t.x;
					double y1 = t.y;

					int movingI = 0;
					int otherI = 0;

					for (PointTrack t2 : tracks) {
						double x2 = t2.x;
						double y2 = t2.y;

						double d = distance(x1, y1, x2, y2);

						if (d < trackR) {
							if (moving.contains(t2)) {
								movingI++;
							} else {
								otherI++;
							}
						}
					}

					if (movingI > otherI && movingI > highestMovingI) {
						targetTrackX = x1;
						targetTrackY = y1;
						highestMovingI = movingI;
					}
				}

				if (highestMovingI < 4) {
					targetTrackX = cam.getWidth() / 2;
					targetTrackY = cam.getHeight() / 2;

				} else {
					trackX = slerp(trackX, targetTrackX, 20);
					trackY = slerp(trackY, targetTrackY, 20);
				}

				trackX += pointGroups.get(0).vX;
				trackY += pointGroups.get(0).vY;

				for (PointTrack t : tracker.getDroppedTracks(null)) {
					PointData pd = pointLife.get(t.featureId);
					if (pd != null)
						pd.group.size--;
					pointLife.remove(t.featureId);
				}

				g2.setColor(Color.CYAN);
				VisualizeFeatures
						.drawCircle(g2, targetTrackX, targetTrackY, 20);

				g2.setColor(Color.GREEN);
				g2.fillOval((int) (trackX - 20), (int) (trackY - 20), 40, 40);
												
				turnVector = (float)((trackX/cam.getWidth())*2.0 - 1.0);
				
				float turnMag = Math.abs(turnVector);
				if(turnMag < turn_treshold){
					turnMag = 0f;
				}else{
					turnMag *= 1+ (turn_treshold*2);
					turnMag -= turn_treshold;
					
					if(turnMag > 1){
						turnMag = 1;
					}
					
				}
				turnVector *= turnMag;
				

				visualized = imageDeepCopy(image);
				
				synchronized (frameMonitor) {
					frameMonitor.notifyAll();
				}

			} catch (Exception ex) {
				ex.printStackTrace();
				break;
			}
		}
	}
	
	public void safeStop(){
		shouldStop = true;
	}
	
	
	private void aliveCheck(){
		if(!this.isAlive() && !shouldStop && !autoStarted){
			try{
				setDaemon(true);
				start();
				autoStarted = true;
			}catch(IllegalThreadStateException ex){}
		}else if(shouldStop){
			throw new RuntimeException("I'm already dead. - Hanzo main 2017");
		}
	}

	private static int clamp(int value, int min, int max) {
		if (value < min) {
			value = min;
		} else if (value > max) {
			value = max;
		}
		return value;
	}

	private static double clamp(double value, double min, double max) {
		if (value < min) {
			value = min;
		} else if (value > max) {
			value = max;
		}
		return value;
	}

	private static double distance(double x1, double y1, double x2, double y2) {
		double dist;
		double dx = x1 - x2;
		double dy = y1 - y2;
		dist = Math.sqrt(dx * dx + dy * dy);

		return dist;
	}

	private static double slerp(double curr, double target, double maxDelta) {
		double val = curr;
		double err = curr - target;
		if (maxDelta < 0) {
			maxDelta = -maxDelta;
		}

		if (err < 0) {
			err = clamp(err, -maxDelta, 0);
		} else {
			err = clamp(err, 0, maxDelta);
		}

		val -= err;

		return val;
	}

	private static BufferedImage imageDeepCopy(BufferedImage image) {
		ColorModel colorModel = image.getColorModel();
		boolean isAlphaPremultiplied = colorModel.isAlphaPremultiplied();
		WritableRaster raster = image.copyData(null);
		return new BufferedImage(colorModel, raster, isAlphaPremultiplied, null);
	}
	
	
	private static class PointData {
		public long featureID;
		public long lifeTime;

		PointGroup group;

		public double vX, vY;
		public double lX = -1, lY = -1;
		public double lVX, lVY;

		public long wobblyness;

		public boolean isWobbly() {
			return wobblyness > 0;
		}
	}

	private static class PointGroup {
		private static Random r = new Random();
		private static long lastGroupID = 0;

		public long GroupID;

		public int size;
		public Color color;
		public double vX;
		public double vY;

		ArrayList<PointData> members = new ArrayList<PointData>();

		public PointGroup() {
			GroupID = _GetNextGroupID();
			color = new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256));
		}

		static private long _GetNextGroupID() {
			return lastGroupID++;
		}
	}

}


