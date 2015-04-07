package fcatrin.pop.views;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;

import xtvapps.core.swt.CustomWidget;
import xtvapps.core.swt.SWTUtils;

public class ScreenView extends CustomWidget {
	public static final int WIDTH = 320;
	public static final int HEIGHT = 192;
	
	Color colors[] = new Color[4];
	

	public ScreenView(Composite parent) {
		super(parent);
	}
	
	public void setColor(int i, String rgb) {
		colors[i] = SWTUtils.buildColor(rgb);
	}

	@Override
	protected void onPaint(PaintEvent e) {
		
		GC gc = e.gc;
		gc.setBackground(colors[0]);
		gc.fillRectangle(0, 0, WIDTH, HEIGHT);
		
		gc.setForeground(colors[3]);
		gc.drawLine(0, 0, WIDTH, HEIGHT);

	}

}
