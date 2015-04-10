package fcatrin.pop;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ImageData {
	enum Mode {AppleMono, AppleColor, Atari}
	int width;
	int height;
	int offset;
	byte data[];
	Mode mode;
	
	
	public static ImageData loadBMP(File file) throws IOException {
		byte header[] = new byte[0x7a];
		FileInputStream is = null;
		try {
			is = new FileInputStream(file);
			is.read(header);
			
			int width = Utils.b2i(header[0x12]);
			int height = Utils.b2i(header[0x16]);
			System.out.println(String.format("bmp %dx%d", width, height));
			byte scan[] = new byte[((width*3 + 3)/4)*4];

			ImageData image = new ImageData();
			image.mode = Mode.Atari;
			image.width = width;
			image.height = height;
			image.data = new byte[width*height];
			
			for(int row = 0; row<height; row++) {
				is.read(scan);
				int y = height -row -1;
				int x = 0;
				for(int i=0; i<width*3; i+=3) {
					int r = scan[i+0];
					int g = scan[i+1];
					int b = scan[i+2];
					int value = r * 65536 + g*256 + b;
					String hex = String.format("%06x", value);
					int color = 0;
					if (hex.equals("ffbc7854")) color = 1;
					else if (hex.equals("ffbbbbbc")) color = 2;
					else if (hex.equals("ff80280c")) color = 3;
					else if (!hex.equals("000000"))	System.out.println(hex);
					image.data[y*width + x] = (byte)color;
					x++;
				}
				y++;
			}
			return image;
		} finally {
			if (is!=null) is.close();
		}
	}
}
