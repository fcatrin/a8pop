package fcatrin.pop;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import xtvapps.core.swt.SWTUtils;
import fcatrin.pop.data.Level;
import fcatrin.pop.views.ScreenView;

public class MainWindow {

	private Shell shell;
	private ScreenView screenView;
	private String colors[] = {"#000000", "#0c2880", "#5478bc", "#bcbcbc"};
	private float RENDER_PERIOD = 1000 / 15.0f;
	private Image[] graphics;

	public MainWindow(Display display) {
		shell = new Shell(display);
	    shell.setText("Prince of Persia Testing");
	    shell.setLayout(new GridLayout(1, true));
	    
	    screenView = new ScreenView(shell);
	    screenView.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) {
				onKeyReleased(e);
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
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

	protected void onKeyReleased(KeyEvent e) {
		if (e.keyCode == SWT.ARROW_LEFT) {
			graphicsIndex --;
		} else if (e.keyCode == SWT.ARROW_RIGHT) {
			graphicsIndex++;
		}
		if (graphicsIndex < 0) graphicsIndex = graphics.length-1;
		if (graphicsIndex+1 > graphics.length) graphicsIndex = 0;
		System.out.println("graphics " + (graphicsIndex+1));
		screenView.postInvalidate();
	}

	public void open() {
		shell.pack();
	    //shell.setSize(1024, 640);
	    SWTUtils.centerOnScreen(shell);
	    shell.open();
	    
	    SWTUtils.mainLoop(shell);
	}
	
	int graphicsIndex = 0;
	private Level level;
	
	private void render() {
		synchronized (screenView) {
		    screenView.clear();
		    
		    Image image = graphics[graphicsIndex];
		    image.render(screenView, 0, 0);
		    
		    level.render(screenView, 0);
		    
		}
	}
	

	public void setImages(Image[] graphics) {
		this.graphics = graphics;
	}
	
	public void setLevel(Level level) {
		this.level = level;
	}

}
