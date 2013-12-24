package com.synload.videoConverter.converter;

import java.util.Hashtable;

public class Converter{
	private Thread cThread;
	public static Hashtable<String,Video> queue = new Hashtable<String,Video>();
	public static Hashtable<String,Video> threadqueue = new Hashtable<String,Video>();
	public Converter(){
		cThread = new Thread(new ConverterThread(this));
	}
	public void start(){
		cThread.start();
	}
}