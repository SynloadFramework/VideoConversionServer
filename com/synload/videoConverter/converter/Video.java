package com.synload.videoConverter.converter;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.Part;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.synload.videoConverter.SynloadConverter;


@JsonTypeInfo(
	use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "class"
)
public class Video{
	public String fileName,pathToVideo,video,videofile,targetServer,data,mData,fps = null;
	public Long size = (long) 0;
	public Float duration = (float) 0;
	public Map<String, String[]> params = null;
	public Boolean h264 = false;
	public ArrayList<String> subs = new ArrayList<String>();
	@JsonIgnore public Part part = null;
	public Video(String fileName,  Long size, String targetServer, Part part, Map<String, String[]> params){
		this.fileName = fileName;
		this.targetServer = targetServer;
		this.part = part;
		this.params = params;
		this.size = size;
	}
	
	@JsonIgnore
	public String getName(){
		return fileName;
	}
	
	@JsonIgnore
	public Map<String, String[]> getParams(){
		return params;
	}
	
	@JsonIgnore
	public String getVideo(){
		return video;
	}
	
	@JsonIgnore
	public String getVideoFile(){
		return videofile;
	}
	
	@JsonIgnore
	public void setVideoFile(String videoFile){
		this.videofile = videoFile;
	}
	
	@JsonIgnore
	public String getFormat(){
		if(getParams().containsKey("size")){
			if(getParams().get("size")[0].equalsIgnoreCase("custom")){
				if(getParams().containsKey("quality")){
					return getParams().get("quality")[0];
				}else{
					return "vp8";
				}
			}else{
				return getParams().get("size")[0];
			}
		}else{
			return "vp8";
		}
	}
	
	@JsonIgnore
	public String getTemp(){
		return pathToVideo;
	}
	
	@JsonIgnore
	public String getTarget(){
		return targetServer;
	}
	
	@JsonIgnore
	public long getSize(){
		return size;
	}
	
	@JsonIgnore
	public String getData(){
		return data;
	}
	
	@JsonIgnore
	public float getDuration(){
		return duration;
	}
	
	@JsonIgnore
	public String getMData(){
		return mData;
	}
	
	@JsonIgnore
	public void setH264(boolean isH264){
		h264 = isH264;
	}
	
	@JsonIgnore
	public boolean isH264(){
		return h264;
	}
	
	@JsonIgnore
	public void setMData(String mkvMergeData){
		mData = mkvMergeData;
	}
	
	@JsonIgnore
	public void setName(String fileName){
		this.fileName = fileName;
	}
	
	@JsonIgnore
	public void setTemp(String pathToVideo){
		this.pathToVideo = pathToVideo;
	}
	
	@JsonIgnore
	public void setTarget(String targetServer){
		this.targetServer = targetServer;
	}
	
	@JsonIgnore
	public void setFPS(String framesPerSecond){
		fps = framesPerSecond;
	}
	
	@JsonIgnore
	public String getFPS(){
		return fps;
	}
	
	@JsonIgnore
	public void setSubtitles(ArrayList<String> subtitles){
		subs = subtitles;
	}
	
	@JsonIgnore
	public ArrayList<String> getSubtitles(){
		return subs;
	}
	
	@JsonIgnore
	public void delete(){
		(new File(SynloadConverter.prop.getProperty("videoPath")+this.getTemp())).delete();
	}
	
	@JsonIgnore
	public String randomString(){
		SecureRandom random = new SecureRandom();
	    return new BigInteger(130, random).toString(32);
	}
	
	@JsonIgnore
	public void buildVideo() throws IOException{
		this.setTemp(randomString()+".video");
		DataInputStream is = new java.io.DataInputStream(part.getInputStream());
		OutputStream out = new FileOutputStream(SynloadConverter.prop.getProperty("videoPath")+this.getTemp());
		int bytesRead;
		byte[] buffer = new byte[8 * 1024];
		while ((bytesRead = is.read(buffer)) != -1) {
			out.write(buffer, 0, bytesRead);
		}
		out.close();
		is.close();
		part.getInputStream().close();
		try {
			part.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.data = ConverterProcessing.cmdExec(SynloadConverter.prop.getProperty("ffmpeg")+" -i "+SynloadConverter.prop.getProperty("videoPath")+this.getTemp());
		this.video = this.randomString();
		this.duration = ConverterProcessing.getDuration(this);
		Converter.queue.put(this.randomString(),this);
	}
	
	@JsonIgnore
	public void prepVideo(){
		fps = ConverterProcessing.getFPS(this);
		try{
			Process p = Runtime.getRuntime().exec(SynloadConverter.prop.getProperty("mkvmerge")+" -i "+SynloadConverter.prop.getProperty("videoPath")+this.getTemp());
	        InputStream is = p.getInputStream();
	        InputStreamReader isr = new InputStreamReader(is);
	        BufferedReader br = new BufferedReader(isr);
			String line;
			while ((line = br.readLine()) != null) {
				ConverterProcessing.getH264(this,line);
				ConverterProcessing.extractSubs(this,line);
			}
			br.close();
			isr.close();
			is.close();
			if(this.subs.size()>0){
				ConverterProcessing.removeSubs(this);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}