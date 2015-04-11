package fcatrin.pop;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import fcatrin.pop.views.ScreenView;

public class Image {
	public enum Mode {AppleMono, AppleColor, Atari}
	public int width;
	public int height;
	public byte data[];
	public Mode mode;
	
	public static Image loadBMP(File file) throws IOException {
		byte header[] = new byte[0x7a];
		FileInputStream is = null;
		try {
			is = new FileInputStream(file);
			is.read(header);
			
			int width = Utils.b2i(header[0x12]);
			int height = Utils.b2i(header[0x16]);
			System.out.println(String.format("bmp %dx%d", width, height));
			byte scan[] = new byte[((width*3 + 3)/4)*4];

			Image image = new Image();
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
					if (hex.equals("ffbc7854")) color = 2;
					else if (hex.equals("ffbbbbbc")) color = 3;
					else if (hex.equals("ff80280c")) color = 1;
					else if (!hex.equals("000000"))	color = 3;
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
	
	public void render(ScreenView screenView, int top, int left) {
		if (mode == Mode.Atari) {
			for(int y=0; y<height; y++) {
				for(int x=0; x<width; x++) {
					screenView.setPixel(left+x, top+y, (int)data[width*y + x]);
				}
			}
		} else {
			for(int y=0; y<height; y++) {
				renderScanApple(screenView, left, top+y, width, data, (height-y-1)*width);
			}
		}
	}
	
	public void renderBottom(ScreenView screenView, int bottom, int left, Image mask) {
		if (mode == Mode.Atari) {
			if (mask == null) {
				for(int y=0; y<height; y++) {
					int basey = bottom - height + y;
					for(int x=0; x<width; x++) {
						screenView.setPixel(left+x, basey, (int)data[width*y + x]);
					}
				}
			} else {
				for(int y=0; y<height; y++) {
					int basey = bottom - height + y;
					int masky = mask.height - height + y;
					for(int x=0; x<width; x++) {
						if (masky>=0 && x<mask.width && (int)mask.data[mask.width*masky + x] != 0) {
							
						} else {
							screenView.setPixel(left+x, basey, (int)data[width*y + x]);
						}
					}
				}
			}
		}
	}

	
	private void renderScanApple(ScreenView screenView, int x, int y, int width, byte[] data, int base) {
		// ignore MSB
		// width in bytes
		
		int pixel[] = new int[width * 7];
		int mask = 1;
		int offset = 0;
		int pixelIndex = 0;
		do {
			byte b = data[base+ offset];
			int value = (b & mask)!=0?1:0;
			pixel[pixelIndex++] = value;
			mask *= 2;
			if (mask == 128) {
				offset++;
				mask = 1;
			}
		} while (offset < width);
		for(int i=0; i<pixel.length; i++) {
			boolean even = i % 2 == 0;
			int v0 = i>0?pixel[i-1]:0;
			int v1 = pixel[i];
			int v2 = (i<pixel.length-1)?pixel[i+1]:0;
			int value = 0;
			if (v1!=0) {
				if (v0!=0 || v2!=0) {
					value = 3; 
				} else {
					value = even?1:2;
				}
			} else {
				if (v0 == 0 || v2 == 0) {
					value = 0;
				} else {
					value = even?2:1;
				}
			}
			screenView.setPixel(x++, y, value);
		}
	}


}
