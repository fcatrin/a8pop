package fcatrin.pop;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.eclipse.swt.widgets.Display;

import xtvapps.core.swt.AsyncProcessor;
import xtvapps.core.swt.AsyncTask;
import xtvapps.core.swt.SWTUtils;
import fcatrin.pop.Image.Mode;

public class Main {
	public static Display display;

	public static void main(String[] args) throws IOException {
		display = new Display();
		SWTUtils.display = display;
		
		Image[] graphics = dumpGraphics(new File("images/IMG.BGTAB1.DUN"), 0x6000);
		
		
		graphics = new Image[] {
				Image.loadBMP(new File("images/dungeon/tile_07.bmp"))		
		};

		AsyncTask.asyncProcessor = new AsyncProcessor(display);
		AsyncTask.asyncProcessor.start();
		
		MainWindow mainWindow = new MainWindow(display);
		mainWindow.setImages(graphics);
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
