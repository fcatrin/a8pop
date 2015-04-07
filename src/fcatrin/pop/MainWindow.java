package fcatrin.pop;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import fcatrin.pop.views.ScreenView;
import xtvapps.core.swt.SWTUtils;

public class MainWindow {

	private Shell shell;
	private ScreenView screenView;
	private String colors[] = {"#000000", "#404040", "#A0A0A0", "#F0F0F0"};

	public MainWindow(Display display) {
		shell = new Shell(display);
	    shell.setText("Prince of Persia Testing");
	    shell.setLayout(new GridLayout(1, true));
	    
	    screenView = new ScreenView(shell);
	    SWTUtils.setSize(screenView, ScreenView.WIDTH, ScreenView.HEIGHT);
	    
	    for(int i=0; i<colors.length; i++) {
	    	screenView.setColor(i, colors[i]);
	    }
	}

	public void open() {
		shell.pack();
	    //shell.setSize(1024, 640);
	    SWTUtils.centerOnScreen(shell);
	    shell.open();
	    
	    SWTUtils.mainLoop(shell);
		
	}

}
