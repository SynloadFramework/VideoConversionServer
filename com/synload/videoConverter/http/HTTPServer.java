package com.synload.videoConverter.http;

import javax.servlet.MultipartConfigElement;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;

public class HTTPServer extends Server{
	public HTTPServer(int port) throws Exception{
		super(port);
		this.setHandler(new HTTPHandler());
		this.start();
		this.join();
	}
}