package fcatrin.pop.views;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import xtvapps.core.swt.CustomWidget;
import xtvapps.core.swt.SWTUtils;

public class ScreenView extends CustomWidget {
	public static final int WIDTH = 160;
	public static final int HEIGHT = 192;
	
	public static final int VIEW_WIDTH = 1280;
	public static final int VIEW_HEIGHT = 768;
	
	private static final String COLOR_DEFAULT[] = {
		"#000000", "#0c2880", "#5478bc", "#bcbcbc"
		
	};
	
	RGB colors[] = new RGB[] {
		SWTUtils.buildColor(COLOR_DEFAULT[0]).getRGB(),
		SWTUtils.buildColor(COLOR_DEFAULT[1]).getRGB(),
		SWTUtils.buildColor(COLOR_DEFAULT[2]).getRGB(),
		SWTUtils.buildColor(COLOR_DEFAULT[3]).getRGB()
	};
	PaletteData palette = new PaletteData(colors);
	private ImageData sourceData;
	private Image image = null;

	public ScreenView(Composite parent) {
		super(parent);
		sourceData = new ImageData(WIDTH, HEIGHT, 2, palette);
	}
	
	public void setColor(int i, String rgb) {
		colors[i] = SWTUtils.buildColor(rgb).getRGB();
		sourceData.palette = new PaletteData(colors);
	}
	
	public void setPixel(int x, int y, int value) {
		//System.out.println("set pixel " + x + "," + y);
		sourceData.setPixel(x, y, value);
	}
	
	public void finishFrame() {
		synchronized (this) {
			if (image!=null) image.dispose();
			image = new Image(Display.getCurrent(), sourceData);
		}
	}
	
	public void clear() {
		for(int y=0; y<HEIGHT; y++) {
			for(int x=0; x<WIDTH; x++) {
				setPixel(x, y, 0);
			}
		}
	}

	@Override
	protected void onPaint(PaintEvent e) {
		if (image == null) return;
		synchronized (this) {
			GC gc = e.gc;
			gc.drawImage(image, 0, 0, WIDTH, HEIGHT, 0, 0, VIEW_WIDTH, VIEW_HEIGHT);
			
		}
	}

}
