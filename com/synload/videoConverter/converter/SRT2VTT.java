package com.synload.videoConverter.converter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.regex.PatternSyntaxException;

public class SRT2VTT{
	public static void convert(String file, OutputStream out) throws IOException{
		out.write("WEBVTT\n".getBytes(Charset.forName("UTF-8")));
		out.write("\n".getBytes(Charset.forName("UTF-8")));
		InputStream ips = new FileInputStream(new File(file));
        InputStreamReader isr = new InputStreamReader(ips);
        BufferedReader br = new BufferedReader(isr);
		String line;
		while ((line = br.readLine()) != null) {
			line = changeTimeStamps(line);
			line = changeChars(line);
			out.write((line+"\n").getBytes(Charset.forName("UTF-8")));
		}
		br.close();
		isr.close();
		out.close();
	}
	public static String changeTimeStamps(String line){
		try {
			return line.replaceAll("(?si)([0-9]+):([0-9]+):([0-9]+),([0-9]+) --> ([0-9]+):([0-9]+):([0-9]+),([0-9]+)", "$1:$2:$3.$4 --> $5:$6:$7.$8");
		} catch (PatternSyntaxException ex) {
		} catch (IllegalArgumentException ex) {
		} catch (IndexOutOfBoundsException ex) {
		}
		return "";
	}
	public static String changeChars(String line){
		try {
			return line.replaceAll("(?si)\\{(.*?)\\}", "");
		} catch (PatternSyntaxException ex) {
		} catch (IllegalArgumentException ex) {
		} catch (IndexOutOfBoundsException ex) {
		}
		return "";
	}
}