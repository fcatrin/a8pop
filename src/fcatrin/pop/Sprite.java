package fcatrin.pop;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fcatrin.pop.data.Bounds;

public class Sprite {
	
	public class Frame {
		public int frameIndex;
		public int dx;
		public int dy;
		public Image image;
		
		public Frame(int frame, int dx, int dy) {
			this.frameIndex = frame;
			this.dx = dx;
			this.dy = dy;
		}
	}
	
	Map<Integer, Image> frameImages = new HashMap<Integer, Image>();
	
	Frame animation_run_jump[] = {
			new Frame(34, 0, 0),
			new Frame(35, 0, 0),
			new Frame(36, 0, 0),
			new Frame(37, 0, 0),
			new Frame(38, 0, 0),
			new Frame(39, 0, 0),
			new Frame(40, 0, 0),
			new Frame(41, 0, 0),
			new Frame(42, 0, 0),
			new Frame(43, 0, 0),
			new Frame(44, 0, 0),
	};
		
	Frame animations[][] = {animation_run_jump};
	int frameIndex = 0;
	int animationIndex = 0;
	
	int positionX = 0;
	int positionY = 100;
	
	Frame animation[];
	Frame currentFrame;
	
	Bounds lastBounds = new Bounds();
	Bounds currentBounds = new Bounds();
	Bounds bounds = new Bounds();
	
	public Sprite() {
	}
	
	public void init() {
		animation = animations[animationIndex];
		updateFrame();
	}
	
	private void updateFrame() {
		currentFrame = animation[frameIndex];
		currentFrame.image = frameImages.get(currentFrame.frameIndex);
		currentBounds.x1 = positionX;
		currentBounds.x2 = positionX + currentFrame.image.width;
		currentBounds.y1 = positionY - currentFrame.image.height;
		currentBounds.y2 = positionY;
	}

	public Frame getFrame() {
		return currentFrame;
	}
	
	private void saveLastBounds() {
		lastBounds.x1 = currentBounds.x1;
		lastBounds.x2 = currentBounds.x2;
		lastBounds.y1 = currentBounds.y1;
		lastBounds.y2 = currentBounds.y2;
	}
	
	public Bounds getBounds() {
		bounds.x1 = Math.min(lastBounds.x1, currentBounds.x1);
		bounds.x2 = Math.max(lastBounds.x2, currentBounds.x2);
		bounds.y1 = Math.min(lastBounds.y1, currentBounds.y1);
		bounds.y2 = Math.max(lastBounds.y2, currentBounds.y2);
		return bounds;
	}
	
	public void advanceFrame() {
		saveLastBounds();

		frameIndex++;
		if (frameIndex>=animation.length) {
			frameIndex = 0;
		}
		updateFrame();
	}
	
	public void addFrame(int position, File file) {
		try {
			frameImages.put(position, Image.loadBMP(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
