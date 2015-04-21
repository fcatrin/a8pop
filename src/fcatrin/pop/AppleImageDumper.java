package fcatrin.pop;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class AppleImageDumper {
	static Color colors[] = new Color[] {
		new Color(0, 0, 0),
		new Color(200, 200, 200),
		new Color(100, 100, 200),
		new Color(0, 0, 200)
	};
	
	public static void dump(Image image, int index, String dir) {
		BufferedImage offscreenImage = new BufferedImage(image.width*7, image.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = offscreenImage.createGraphics();
		
		for(int y=0; y<image.height; y++) {
			int pixel[] = new int[image.width * 7];
			int mask = 1;
			int offset = 0;
			int pixelIndex = 0;
			int base = image.width*y;
			do {
				byte b = image.data[base+ offset];
				int value = (b & mask)!=0?1:0;
				pixel[pixelIndex++] = value;
				mask *= 2;
				if (mask == 128) {
					offset++;
					mask = 1;
				}
			} while (offset < image.width);
			
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
				g2.setColor(colors[value]);
				g2.drawRect(i, image.height-y-1, 1, 1);
			}

		}
		
		String fileName = String.format("tile_%03d.png", (index+1));
		
		File outputFile = new File("apple/" + dir + "/" + fileName);
		outputFile.getParentFile().mkdirs();
		try {
			ImageIO.write(offscreenImage, "bmp", outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
