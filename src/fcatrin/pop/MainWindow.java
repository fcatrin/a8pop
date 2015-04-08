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
	
	int scroll = 0;
	private void render() {
		synchronized (screenView) {
		    screenView.clear();
		    for(int x=0; x<ScreenView.WIDTH; x++) {
		    	for(int y=0; y<ScreenView.HEIGHT; y++) {
		    		screenView.setPixel(x, y, (x+scroll) % 3);
		    	}
		    }
			//scroll++;
		}
	}

}
