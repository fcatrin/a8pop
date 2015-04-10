package fcatrin.pop;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.eclipse.swt.widgets.Display;

import xtvapps.core.swt.AsyncProcessor;
import xtvapps.core.swt.AsyncTask;
import xtvapps.core.swt.SWTUtils;

public class Main {
	private static final String LOGTAG = Main.class.getSimpleName();
	
	public static Display display;

	public static void main(String[] args) throws IOException {
		display = new Display();
		SWTUtils.display = display;
		
		ImageData[] graphics = dumpGraphics(new File("images/IMG.BGTAB2.DUN"), 0x6000);

		AsyncTask.asyncProcessor = new AsyncProcessor(display);
		AsyncTask.asyncProcessor.start();
		
		MainWindow mainWindow = new MainWindow(display);
		mainWindow.setImages(graphics);
		mainWindow.open();
		AsyncTask.asyncProcessor.shutdown();
		display.dispose();
	}
	
	private static ImageData[] dumpGraphics(File file, int address) throws IOException {
		byte data[] = new byte[262144];
		FileInputStream fis = new FileInputStream(file);
		fis.read(data);
		fis.close();
		
		int nImages = data[0];
		int index = 1;
		ImageData images[] = new ImageData[nImages];
		for(int i=0; i<nImages; i++) {
			ImageData image = new ImageData();
			byte l = data[index++];
			byte h = data[index++];
			String format = "[%d] l:%x, h:%x";
			System.out.println(String.format(format, i+1, l, h));
			image.offset = word(l, h) - address; 
			images[i] = image;
		}
		
		for(int i=0; i<nImages; i++) {
			ImageData image = images[i];
			int offset = image.offset;
			image.width = b2i(data[offset++]);
			image.height = b2i(data[offset++]);
			image.data = new byte[image.width * image.height];
			for(int p=0; p<image.data.length; p++) {
				image.data[p] = data[offset+p];
			}
			String format = "image[%d].offset = %x  %dx%d";
			System.out.println(String.format(format, i+1, images[i].offset, image.width, image.height));
		}
		return images;
	}
	
	private static int word(byte l, byte h) {
		return b2i(l) + 256 * b2i(h);
	}
	
	private static int b2i(byte b) {
		if (b>=0) return b;
		return b+256;
	}
}
