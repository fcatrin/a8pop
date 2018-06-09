package fcatrin.a8mon;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import xtvapps.core.swt.SWTUtils;

public class MainWindow {

	private Shell shell;

	public MainWindow(Display display) {
		shell = new Shell(display);
	    shell.setText("Atari800 Monitor");
	    GridLayout layout = new GridLayout(1, true);
	    layout.marginWidth = 0;
	    layout.marginHeight = 0;
	    shell.setLayout(layout);
	}

	public void open() {
		shell.pack();
	    shell.setSize(1024, 640);
	    SWTUtils.centerOnScreen(shell);
	    shell.open();
	    
	    SWTUtils.mainLoop(shell);
	}
	
}
