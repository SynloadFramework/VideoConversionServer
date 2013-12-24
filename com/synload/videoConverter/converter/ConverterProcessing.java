package com.synload.videoConverter.converter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.synload.videoConverter.SynloadConverter;

public class ConverterProcessing{
	public static String getFPS(Video video){
		String ResultString="1";
		Pattern regex = Pattern.compile("([0-9.]+) fps");
		Matcher regexMatcher = regex.matcher(video.getData());
		if (regexMatcher.find()) {
			ResultString = regexMatcher.group(1);
		}
		if(ResultString.equals("1")){
			regex = Pattern.compile("-> ([0-9.]+)");
			regexMatcher = regex.matcher(video.getData());
			if (regexMatcher.find()) {
				ResultString = regexMatcher.group(1);
			}
		}
		if(ResultString.equals("1")){
			regex = Pattern.compile("([0-9.]+) tbr");
			regexMatcher = regex.matcher(video.getData());
			if (regexMatcher.find()) {
				ResultString = regexMatcher.group(1);
			}
		}
		return ResultString;
	}
	public static String getFPS(String line){
		String ResultString="1";
		Pattern regex = Pattern.compile("fps=(|[ ]+)([0-9.]+)");
		Matcher regexMatcher = regex.matcher(line);
		if (regexMatcher.find()) {
			ResultString = regexMatcher.group(2);
		}
		if(ResultString.equals("1")){
			regex = Pattern.compile("([0-9.]+) fps");
			regexMatcher = regex.matcher(line);
			if (regexMatcher.find()) {
				ResultString = regexMatcher.group(1);
			}
		}
		if(ResultString.equals("1")){
			regex = Pattern.compile("-> ([0-9.]+)");
			regexMatcher = regex.matcher(line);
			if (regexMatcher.find()) {
				ResultString = regexMatcher.group(1);
			}
		}
		if(ResultString.equals("1")){
			regex = Pattern.compile("([0-9.]+) tbr");
			regexMatcher = regex.matcher(line);
			if (regexMatcher.find()) {
				ResultString = regexMatcher.group(1);
			}
		}
		return ResultString;
	}
	public static float getDuration(Video video){
		float totalPlay = 0;
		try {
			Pattern regex = Pattern.compile("Duration: ([0-9]{1,2}):([0-9]{1,2}):([0-9]{1,2}).([0-9]{1,2})");
			Matcher regexMatcher = regex.matcher(video.getData());
			while (regexMatcher.find()) {
				totalPlay = ((Integer.valueOf(regexMatcher.group(1))*60)*60)+(Integer.valueOf(regexMatcher.group(2))*60)+(Integer.valueOf(regexMatcher.group(3)));
			}
		} catch (PatternSyntaxException e) {
			e.printStackTrace();
		}
		return totalPlay;
	}
	public static String getOutput(String line){
		try {
			Pattern regex = Pattern.compile("Output #0, ([a-zA-Z0-9.]+), to '(.*?)':");
			Matcher regexMatcher = regex.matcher(line);
			while (regexMatcher.find()) {
				return regexMatcher.group(2);
			}
		} catch (PatternSyntaxException e) {
			e.printStackTrace();
		}
		return "";
	}
	public static float getPosition(String line){
		float totalPlay = 0;
		try {
			Pattern regex = Pattern.compile("time=(|[ ]+)([0-9]{1,2}):([0-9]{1,2}):([0-9]{1,2}).([0-9]{1,2})");
			Matcher regexMatcher = regex.matcher(line);
			while (regexMatcher.find()) {
				totalPlay = ((Integer.valueOf(regexMatcher.group(2))*60)*60)+(Integer.valueOf(regexMatcher.group(3))*60)+(Integer.valueOf(regexMatcher.group(4)));
			}
		} catch (PatternSyntaxException e) {
			e.printStackTrace();
		}
		return totalPlay;
	}
	public static String getCurrentBitrate(String line){
		try {
			Pattern regex = Pattern.compile("bitrate=(|[ ]+)([0-9a-zA-Z/.]+)");
			Matcher regexMatcher = regex.matcher(line);
			while (regexMatcher.find()) {
				return regexMatcher.group(2);
			}
		} catch (PatternSyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String getFrames(String line){
		try {
			Pattern regex = Pattern.compile("frame=(|[ ]+)([0-9]+)");
			Matcher regexMatcher = regex.matcher(line);
			while (regexMatcher.find()) {
				return regexMatcher.group(2);
			}
		} catch (PatternSyntaxException e) {
			e.printStackTrace();
		}
		return "0";
	}
	public static void extractSubs(Video video,String mDataLine){
		ArrayList<String> out = video.getSubtitles();
		try {
			Pattern regex = Pattern.compile("subtitles");
			Matcher regexMatcher = regex.matcher(mDataLine);
			if (regexMatcher.find()) {
				regex = Pattern.compile("Track ID ([0-9]+)");
				regexMatcher = regex.matcher(mDataLine);
				if (regexMatcher.find()) {
					System.out.println("Found Subtitles");
					String filename = video.randomString();
					String cmd = SynloadConverter.prop.getProperty("mkvextract")+" tracks "+SynloadConverter.prop.getProperty("videoPath")+video.getTemp()+" "+regexMatcher.group(1)+":"+SynloadConverter.prop.getProperty("videoPath")+filename+".ass";
					ProcessBuilder builder = new ProcessBuilder(cmd.split(" "));
					builder.redirectErrorStream(true);
					Process pr = builder.start();
					InputStream is = pr.getInputStream();
			        InputStreamReader isr = new InputStreamReader(is);
			        BufferedReader br = new BufferedReader(isr);
					String line;
					while ((line = br.readLine()) != null) {}
					br.close();
					isr.close();
					is.close();
					cmd = SynloadConverter.prop.getProperty("ffmpeg")+" -i "+SynloadConverter.prop.getProperty("videoPath")+filename+".ass "+SynloadConverter.prop.getProperty("videoPath")+filename+".srt";
					builder = new ProcessBuilder(cmd.split(" "));
					builder.redirectErrorStream(true);
					pr = builder.start();
					is = pr.getInputStream();
			        isr = new InputStreamReader(is);
			        br = new BufferedReader(isr);
					while ((line = br.readLine()) != null) {}
					br.close();
					isr.close();
					is.close();
					
					OutputStream outfile = new FileOutputStream(new File(SynloadConverter.prop.getProperty("videoPath")+filename+".vtt"));
					SRT2VTT.convert(SynloadConverter.prop.getProperty("videoPath")+filename+".srt", outfile);
					if((new File(SynloadConverter.prop.getProperty("videoPath")+filename+".srt")).exists()){
						out.add(SynloadConverter.prop.getProperty("videoPath")+filename);
					}
					video.setSubtitles(out);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void getH264(Video video, String mDataLine){
		if(!video.isH264()){
			Pattern regex = Pattern.compile("h264");
			Matcher regexMatcher = regex.matcher(mDataLine);
			if (regexMatcher.find()) {
				video.setH264(true);
			} else {
			}
			regex = Pattern.compile("x264");
			regexMatcher = regex.matcher(mDataLine);
			if (regexMatcher.find()) {
				video.setH264(true);
			} else {
			}
		}
	}
	public static void removeSubs(Video video) throws IOException{
		String cmd = SynloadConverter.prop.getProperty("ffmpeg")+" -i "+SynloadConverter.prop.getProperty("videoPath")+video.getTemp()+" -sn -vcodec copy -acodec copy "+SynloadConverter.prop.getProperty("videoPath")+video.getTemp()+".mkv";
		ProcessBuilder builder = new ProcessBuilder(cmd.split(" "));
		builder.redirectErrorStream(true);
		Process pr = builder.start();
		InputStream is = pr.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
		String line;
		while ((line = br.readLine()) != null) {
			//System.out.println(line);
		}
		br.close();
		isr.close();
		is.close();
		(new File(SynloadConverter.prop.getProperty("videoPath")+video.getTemp())).delete();
		(new File(SynloadConverter.prop.getProperty("videoPath")+video.getTemp()+".mkv")).renameTo(new File(SynloadConverter.prop.getProperty("videoPath")+video.getTemp()));
	}
	public static String cmdExec(String cmdLine) {
	    String output = "";
	    try {
	        Process p = Runtime.getRuntime().exec(cmdLine);
	        InputStream is = p.getErrorStream();
	        InputStreamReader isr = new InputStreamReader(is);
	        BufferedReader br = new BufferedReader(isr);
	        String line;
	        while ((line = br.readLine()) != null) {
	        	output+=line;
	        }
	    }catch (Exception ex) {
	    	ex.printStackTrace();
	    }
	    return output;
	}
}