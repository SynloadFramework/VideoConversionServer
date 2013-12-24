package com.synload.videoConverter.http;

import java.io.IOException;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ContextHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.synload.videoConverter.SynloadConverter;
import com.synload.videoConverter.converter.Converter;
import com.synload.videoConverter.converter.Video;

public class HTTPHandler extends ContextHandler {
	private static final MultipartConfigElement MULTI_PART_CONFIG = 
		new MultipartConfigElement(
			"./uploads/", 
			943718400, 
			948718400, 
			948718400
		);
	

	@Override
	public void doHandle(String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException 
	{
		if(target.equalsIgnoreCase("/upload")){
			if (request.getContentType() != null && request.getContentType().startsWith("multipart/form-data")) {
    			baseRequest.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, MULTI_PART_CONFIG);
    		}
			response.setContentType("text/html;charset=utf-8");
	        response.setStatus(HttpServletResponse.SC_OK);
	        baseRequest.setHandled(true);
	        try{
				if(request.getParameter("user").equalsIgnoreCase(SynloadConverter.prop.getProperty("userName")) && 
						request.getParameter("key").equalsIgnoreCase(SynloadConverter.prop.getProperty("userPassword")) &&
						baseRequest.getParameterMap().containsKey("vid")){
					for(Part part :request.getParts()){
						if(part.getSubmittedFileName()!=null){
							if(part.getSize()>0){
								Video d = new Video (
									part.getSubmittedFileName(),
									part.getSize(),
									request.getParameter("target"),
									part,
									request.getParameterMap()
								);
								d.buildVideo();
								response.getWriter().println("Recieved "+part.getSubmittedFileName());
							}
						}
					}
				}else{
					response.getWriter().println("Authentication failed!");
					for(Part part :request.getParts()){
						part.delete();
					}
				}
	        } catch (NullPointerException e) {
	        	response.getWriter().println("Authentication failed!");
				for(Part part :request.getParts()){
					part.delete();
				}
			}
		}else if(target.equalsIgnoreCase("/uq")){
			response.setContentType("text/html;charset=utf-8");
	        response.setStatus(HttpServletResponse.SC_OK);
	        baseRequest.setHandled(true);
			if(SynloadConverter.uploadQueue.size()==0){
				response.getWriter().println(buildData("none"));
				return;
			}
			/*JSONArray tmpArray = new JSONArray();
			
			for(Entry<String, Video> entry:SynloadConverter.uploadQueue.entrySet()){
				JSONObject tmp = new JSONObject();
				tmp.put("videoID", entry.getValue().getParams().get("vid")[0]);
				tmp.put("originFilename", entry.getValue().getName());
				tmp.put("format", entry.getValue().getFormat());
				tmp.put("uploadURL", entry.getValue().getTarget());
				tmp.put("size", (new File(entry.getValue().getVideoFile())).length());
				tmp.put("Filename", entry.getValue().getVideoFile());
				tmpArray.add(tmp);
			}
			response.getWriter().println(buildData(tmpArray));*/
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String json = ow.writeValueAsString(SynloadConverter.uploadQueue);
			response.getWriter().println(json);
		}else if(target.equalsIgnoreCase("/history")){
			response.setContentType("text/html;charset=utf-8");
	        response.setStatus(HttpServletResponse.SC_OK);
	        baseRequest.setHandled(true);
			if(SynloadConverter.history.size()==0){
				response.getWriter().println(buildData("none"));
				return;
			}
			/*JSONArray tmpArray = new JSONArray();
			for(Entry<String, Video> entry:SynloadConverter.history.entrySet()){
				JSONObject tmp = new JSONObject();
				tmp.put("videoID", entry.getValue().getParams().get("vid")[0]);
				tmp.put("originFilename", entry.getValue().getName());
				tmp.put("format", entry.getValue().getFormat());
				tmp.put("uploadURL", entry.getValue().getTarget());
				tmp.put("size", (new File(entry.getValue().getVideoFile())).length());
				tmp.put("Filename", entry.getValue().getVideoFile());
				tmpArray.add(tmp);
			}
			response.getWriter().println(buildData(tmpArray));*/
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String json = ow.writeValueAsString(SynloadConverter.history);
			response.getWriter().println(json);
		}else if(target.equalsIgnoreCase("/cq")){
			response.setContentType("text/html;charset=utf-8");
	        response.setStatus(HttpServletResponse.SC_OK);
	        baseRequest.setHandled(true);
			if(Converter.queue.size()==0){
				response.getWriter().println(buildData("none"));
				return;
			}
			/*JSONArray tmpArray = new JSONArray();
			for(Entry<String, Video> entry:Converter.queue.entrySet()){
				JSONObject tmp = new JSONObject();
				tmp.put("videoID", entry.getValue().getParams().get("vid")[0]);
				tmp.put("originFilename", entry.getValue().getName());
				tmp.put("format", entry.getValue().getFormat());
				tmp.put("uploadURL", entry.getValue().getTarget());
				tmpArray.add(tmp);
			}
			response.getWriter().println(buildData(tmpArray));*/
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String json = ow.writeValueAsString(Converter.queue);
			response.getWriter().println(json);
		}else if(target.equalsIgnoreCase("/status")){
			response.setContentType("text/html;charset=utf-8");
	        response.setStatus(HttpServletResponse.SC_OK);
	        baseRequest.setHandled(true);
			if(SynloadConverter.current.size()!=0){
				/*Video video = (Video) SynloadConverter.current.get("video");
				
				JSONObject tmp = new JSONObject();
				tmp.put("videoID", video.getParams().get("vid")[0]);
				tmp.put("originFilename", video.getName());
				tmp.put("tempName", video.getVideo());
				tmp.put("fps", SynloadConverter.current.get("FPS"));
				tmp.put("bitrate", SynloadConverter.current.get("Bitrate"));
				tmp.put("time", timeLeft);
				tmp.put("format", video.getFormat());
				tmp.put("uploadURL", video.getTarget());
				tmp.put("percent", (((float)SynloadConverter.current.get("Position"))/video.getDuration()*100));
				if(tmp.containsKey("Action")){
					tmp.put("action", tmp.get("Action"));
				}
				response.getWriter().println(buildData(tmp));*/
				ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
				String json = ow.writeValueAsString(SynloadConverter.current);
				response.getWriter().println(json);
			}else{
				response.getWriter().println(buildData("none"));
			}
		}
	}
	public String buildData(Object obj){
		//JSONObject tmp = new JSONObject();
		//tmp.put("data", obj);
		//return tmp.toJSONString();
		return obj.toString();
	}
}