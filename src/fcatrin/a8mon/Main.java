package fcatrin.a8mon;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.eclipse.swt.widgets.Display;

import xtvapps.core.swt.AsyncProcessor;
import xtvapps.core.swt.AsyncTask;
import xtvapps.core.swt.SWTUtils;

public class Main {
	public static Display display;

	public static void main(String[] args) throws IOException {
		display = new Display();
		SWTUtils.display = display;
		
		AsyncTask.asyncProcessor = new AsyncProcessor(display);
		AsyncTask.asyncProcessor.start();
		
		Process process = Runtime.getRuntime().exec("atari800");
		attachProcessors(process);
		
		
		MainWindow mainWindow = new MainWindow(display);
		mainWindow.open();
		
		AsyncTask.asyncProcessor.shutdown();
		display.dispose();
	}
	
	private static void attachProcessors(final Process process) {
		Runnable outputProcessor = new Runnable() {

			@Override
			public void run() {
				BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
				String line;
				try {
					while ((line = in.readLine()) != null) {
					    System.out.println("XX:" + line);
					    
					    if (line.startsWith("  0  ")) {
					    	out.append("POKEY\n");
					    	out.flush();
					    	System.out.println("sending pokey");
					    }
					}
					process.waitFor();
					System.out.println("ok!");
	
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		};
		
		Thread t = new Thread(outputProcessor);
		t.start();
	}
	
	private static class CommandExecutor implements Runnable {
		
		private OutputStream os;

		public CommandExecutor(OutputStream os) {
			this.os = os;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
	}

}
