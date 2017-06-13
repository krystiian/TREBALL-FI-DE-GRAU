package com.christian.mavenproject2.analisy_algorithms;

import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.christian.mavenproject2.main.mainMenu;

public class lastAction extends TimerTask {
	public long lastAction = -1;
	public mainMenu menu = null;
	
	public lastAction(mainMenu _menu){
		this.menu = _menu;
		
	};
	@Override
	public void run() {
		if(menu.lastAction == lastAction)
		{
			menu.writeConsole("CRAWLING SESION FINISHED\n\n\nShutting down sesion in 5 seconds");
			try {
				TimeUnit.SECONDS.sleep(1);
				menu.writeConsole(".");
				TimeUnit.SECONDS.sleep(1);
				menu.writeConsole(".");
				TimeUnit.SECONDS.sleep(1);
				menu.writeConsole(".");
				TimeUnit.SECONDS.sleep(1);
				menu.writeConsole(".");
				TimeUnit.SECONDS.sleep(1);
				menu.writeConsole(".");
				System.exit(0);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		else lastAction = menu.lastAction;
	}

}
