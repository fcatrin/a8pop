package fcatrin.pop;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.eclipse.swt.widgets.Display;

import xtvapps.core.swt.AsyncProcessor;
import xtvapps.core.swt.AsyncTask;
import xtvapps.core.swt.SWTUtils;
import fcatrin.pop.Image.Mode;
import fcatrin.pop.data.Level;

public class Main {
	public static Display display;

	public static void main(String[] args) throws IOException {
		display = new Display();
		SWTUtils.display = display;
		
		Image[] graphics = dumpGraphics(new File("images/IMG.BGTAB1.DUN"), 0x6000);
		/*
		graphics = new Image[] {
				Image.loadBMP(new File("images/dungeon/tile_07.bmp"))		
		};
		*/
		
		File baseDir = new File("images/dungeon");
		Level.addTile(1,  new File(baseDir, "tile_01.bmp"));
		Level.addTile(2,  new File(baseDir, "tile_02.bmp"));
		Level.addTile(3,  new File(baseDir, "tile_03.bmp"));
		Level.addTile(4,  new File(baseDir, "tile_04.bmp"));
		Level.addTile(5,  new File(baseDir, "tile_05.bmp"));
		Level.addTile(6,  new File(baseDir, "tile_06.bmp"));
		Level.addTile(7,  new File(baseDir, "tile_07.bmp"));
		Level.addTile(8,  new File(baseDir, "tile_08.bmp"));
		Level.addTile(9,  new File(baseDir, "tile_09.bmp"));
		Level.addTile(0xa,  new File(baseDir, "tile_0A.bmp"));
		Level.addTile(0xb,  new File(baseDir, "tile_0B.bmp"));
		Level.addTile(0xc,  new File(baseDir, "tile_0C.bmp"));
		Level.addTile(0x13,  new File(baseDir, "tile_13.bmp"));
		Level.addTile(0x15,  new File(baseDir, "tile_15.bmp"));
		Level.addTile(0x19,  new File(baseDir, "tile_19.bmp"));
		Level.addTile(0x1B,  new File(baseDir, "tile_1B.bmp"));
		Level.addTile(0x1E,  new File(baseDir, "tile_1E.bmp"));
		Level.addTile(0x1F,  new File(baseDir, "tile_1F.bmp"));
		Level.addTile(0x20,  new File(baseDir, "tile_20.bmp"));
		Level.addTile(0x21,  new File(baseDir, "tile_21.bmp"));
		Level.addTile(0x2C,  new File(baseDir, "tile_2C.bmp"));
		Level.addTile(0x2D,  new File(baseDir, "tile_2D.bmp"));
		Level.addTile(0x45,  new File(baseDir, "tile_45.bmp"));
		Level.addTile(0x46,  new File(baseDir, "tile_46.bmp"));
		Level.addTile(0x4B,  new File(baseDir, "tile_4B.bmp"));
		Level.addTile(0x4C,  new File(baseDir, "tile_4C.bmp"));
		Level.addTile(0x51,  new File(baseDir, "tile_51.bmp"));
		Level.addTile(0x83,  new File(baseDir, "tile_83.bmp"));
		Level.addTile(0x84,  new File(baseDir, "tile_84.bmp"));
		Level.addTile(0x85,  new File(baseDir, "tile_85.bmp"));
		Level.addTile(0x86,  new File(baseDir, "tile_86.bmp"));
		Level.addTile(0x87,  new File(baseDir, "tile_87.bmp"));
		
		// extra graphics
		Level.addTile(0xA0,  new File(baseDir, "tile_A0.bmp")); // block front end left
		Level.addTile(0xA1,  new File(baseDir, "tile_A1.bmp")); // block front end right
		Level.addTile(0xA2,  new File(baseDir, "tile_A2.bmp")); // block D end left
		Level.addTile(0xA3,  new File(baseDir, "tile_A3.bmp")); // block D end right
		Level.addTile(0xA4,  new File(baseDir, "tile_A4.bmp")); // Pillar Mask
		Level.addTile(0xA5,  new File(baseDir, "tile_A5.bmp")); // Bottle Mask
		Level.addTile(0xA6,  new File(baseDir, "tile_A6.bmp")); // presplate Mask
		
		
		Level level = Level.load(new File("levels/level1"));
		

		AsyncTask.asyncProcessor = new AsyncProcessor(display);
		AsyncTask.asyncProcessor.start();
		
		MainWindow mainWindow = new MainWindow(display);
		mainWindow.setImages(graphics);
		mainWindow.setLevel(level);
		mainWindow.open();
		AsyncTask.asyncProcessor.shutdown();
		display.dispose();
	}
	
	private static Image[] dumpGraphics(File file, int address) throws IOException {
		byte data[] = new byte[262144];
		FileInputStream fis = new FileInputStream(file);
		fis.read(data);
		fis.close();
		
		int nImages = data[0];
		int index = 1;
		Image images[] = new Image[nImages];
		int offsets[] = new int[nImages];
		for(int i=0; i<nImages; i++) {
			Image image = new Image();
			image.mode = Mode.AppleColor;
			byte l = data[index++];
			byte h = data[index++];
			String format = "[%d] l:%x, h:%x";
			System.out.println(String.format(format, i+1, l, h));
			offsets[i] = Utils.word(l, h) - address; 
			images[i] = image;
		}
		
		for(int i=0; i<nImages; i++) {
			Image image = images[i];
			int offset = offsets[i];
			image.width = Utils.b2i(data[offset++]);
			image.height = Utils.b2i(data[offset++]);
			image.data = new byte[image.width * image.height];
			for(int p=0; p<image.data.length; p++) {
				image.data[p] = data[offset+p];
			}
			String format = "image[%d].offset = %x  %dx%d";
			System.out.println(String.format(format, i+1, offset, image.width, image.height));
		}
		return images;
	}
	

}
