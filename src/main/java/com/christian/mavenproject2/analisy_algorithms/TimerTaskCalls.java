package com.christian.mavenproject2.analisy_algorithms;

import java.util.Timer;
import java.util.TimerTask;

import com.christian.mavenproject2.main.mainMenu;

public class TimerTaskCalls extends TimerTask {

	public int numberOfCalls = 0;

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (numberOfCalls > 9)
			this.numberOfCalls -= 10;
	}

	public int getNumberOfCalls() {
		return this.numberOfCalls;
	}

	public void setNumberOfCalls(int i) {
		this.numberOfCalls = i;
	}
}