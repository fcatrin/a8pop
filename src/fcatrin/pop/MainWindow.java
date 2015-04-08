package fcatrin.pop;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import fcatrin.pop.views.ScreenView;
import xtvapps.core.swt.SWTUtils;

public class MainWindow {

	private Shell shell;
	private ScreenView screenView;
	private String colors[] = {"#000000", "#804040", "#A0A0C0", "#F0F0F0"};
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
			for(int x=0; x<image.width; x++) {
				renderByte(x*7, y, image.data[(image.height-y-1)*image.width + x]);
			}
		}
	}
	
	private void renderByte(int x, int y, byte b) {
		int mask = 1;
		do {
			int value = ((b & mask)!=0)?1:0;
			screenView.setPixel(x, y, value);
			x++;
			mask *= 2;
		} while (mask<128);
	}

	public void setImages(ImageData[] graphics) {
		this.graphics = graphics;
	}

}
