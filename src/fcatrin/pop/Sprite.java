package fcatrin.pop;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Sprite {
	Map<Integer, Image> frames = new HashMap<Integer, Image>();
	
	public void addFrame(int position, File file) {
		try {
			frames.put(position, Image.loadBMP(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
