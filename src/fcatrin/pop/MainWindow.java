package fcatrin.pop;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import fcatrin.pop.views.ScreenView;
import xtvapps.core.swt.SWTUtils;

public class MainWindow {

	private Shell shell;
	private ScreenView screenView;
	private String colors[] = {"#000000", "#4040FF", "#FFFF00", "#F0F0F0"};
	private float RENDER_PERIOD = 1000 / 15.0f;
	private ImageData[] graphics;
	private int frame = 0;

	public MainWindow(Display display) {
		shell = new Shell(display);
	    shell.setText("Prince of Persia Testing");
	    shell.setLayout(new GridLayout(1, true));
	    
	    screenView = new ScreenView(shell);
	    SWTUtils.setSize(screenView, ScreenView.VIEW_WIDTH, ScreenView.VIEW_HEIGHT);
	    
	    for(int i=0; i<colors.length; i++) {
	    	screenView.setColor(i, colors[i]);
	    }
	    
	    Thread t = new Thread("RenderThread") {

			@Override
			public void run() {
				while (!shell.isDisposed()) {
					long t0 = System.currentTimeMillis();
					frame++;
					render();
					screenView.finishFrame();
					screenView.postInvalidate();
					long elapsed = System.currentTimeMillis() - t0;
					if (elapsed < RENDER_PERIOD) {
						try {
							Thread.sleep((long)(RENDER_PERIOD - elapsed));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
	    };
	    t.start();
	}

	public void open() {
		shell.pack();
	    //shell.setSize(1024, 640);
	    SWTUtils.centerOnScreen(shell);
	    shell.open();
	    
	    SWTUtils.mainLoop(shell);
	}
	
	int graphicsIndex = 0;
	
	private void render() {
		synchronized (screenView) {
		    screenView.clear();
		    renderGraphic(graphicsIndex);
		    if (frame % 30 == 0) graphicsIndex++;
		}
	}
	
	private void renderGraphic(int index) {
		ImageData image = graphics[index];
		for(int y=0; y<image.height; y++) {
			renderLine(0, y, image.width, image.data, (image.height-y-1)*image.width);
		}
	}
	
	private void renderLine(int x, int y, int width, byte[] data, int base) {
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

	public void setImages(ImageData[] graphics) {
		this.graphics = graphics;
	}

}
