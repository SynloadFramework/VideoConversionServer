package com.synload.videoConverter.uploader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;


import com.synload.videoConverter.SynloadConverter;
import com.synload.videoConverter.converter.Presets;
import com.synload.videoConverter.converter.Video;


public class Uploader implements Runnable{
	@Override
	public void run() {
		while(true){
			Hashtable<String,Video> queueTemp = new Hashtable<String,Video>(SynloadConverter.uploadQueue);
			for(Entry<String, Video> entry: queueTemp.entrySet()){
				System.out.println("Uploading converted video!");
				Video video = entry.getValue();
				Hashtable<String,String> tmp = new Hashtable<String,String>();
				String confirmData = "";
				try {
					tmp.put("upload", "send");
					confirmData = postFile(video.getTarget(),null,tmp);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				if(confirmData.equalsIgnoreCase("ok")){
					tmp = Presets.requestBuild(video);
					String returnData="";
					try {
						returnData = postFile(video.getTarget(),video,tmp);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if(returnData.equalsIgnoreCase("ok")){
						SynloadConverter.uploadQueue.remove(entry.getKey());
					}
				}
			}
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public static String postFile(String url, Video video, Hashtable<String,String> paramData) throws Exception {

	    HttpClient client = new DefaultHttpClient();
	    HttpPost post = new HttpPost(url);
	    MultipartEntityBuilder builder = MultipartEntityBuilder.create();        
	    builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
	    
	    if(video!=null){
		    File file = new File(video.getVideoFile());
		    FileBody fb = new FileBody(file);
		    builder.addPart("movie", fb);  
	    }
	    for(Entry<String, String> entry :paramData.entrySet()){
	    	if(entry.getValue().startsWith("@")){
	    		File file = new File(entry.getValue().replace("@", ""));
			    FileBody fb = new FileBody(file);
			    builder.addPart(entry.getKey(), fb);  
	    	}else{
	    		builder.addTextBody( entry.getKey(), entry.getValue() );
	    	}
	    }
	    final HttpEntity yourEntity = builder.build();

	    class ProgressiveEntity implements HttpEntity {
	        @SuppressWarnings("deprecation")
			@Override
	        public void consumeContent() throws IOException {
	            yourEntity.consumeContent();                
	        }
	        @Override
	        public InputStream getContent() throws IOException,
	                IllegalStateException {
	            return yourEntity.getContent();
	        }
	        @Override
	        public Header getContentEncoding() {             
	            return yourEntity.getContentEncoding();
	        }
	        @Override
	        public long getContentLength() {
	            return yourEntity.getContentLength();
	        }
	        @Override
	        public Header getContentType() {
	            return yourEntity.getContentType();
	        }
	        @Override
	        public boolean isChunked() {             
	            return yourEntity.isChunked();
	        }
	        @Override
	        public boolean isRepeatable() {
	            return yourEntity.isRepeatable();
	        }
	        @Override
	        public boolean isStreaming() {             
	            return yourEntity.isStreaming();
	        } // CONSIDER put a _real_ delegator into here!

	        @Override
	        public void writeTo(OutputStream outstream) throws IOException {

	            class ProxyOutputStream extends FilterOutputStream {
	                /**
	                 * @author Stephen Colebourne
	                 */

	                public ProxyOutputStream(OutputStream proxy) {
	                    super(proxy);    
	                }
	                public void write(int idx) throws IOException {
	                    out.write(idx);
	                }
	                public void write(byte[] bts) throws IOException {
	                    out.write(bts);
	                }
	                public void write(byte[] bts, int st, int end) throws IOException {
	                    out.write(bts, st, end);
	                }
	                public void flush() throws IOException {
	                    out.flush();
	                }
	                public void close() throws IOException {
	                    out.close();
	                }
	            } // CONSIDER import this class (and risk more Jar File Hell)

	            class ProgressiveOutputStream extends ProxyOutputStream {
	                public ProgressiveOutputStream(OutputStream proxy) {
	                    super(proxy);
	                }
	                public void write(byte[] bts, int st, int end) throws IOException {

	                    // FIXME  Put your progress bar stuff here!

	                    out.write(bts, st, end);
	                }
	            }

	            yourEntity.writeTo(new ProgressiveOutputStream(outstream));
	        }

	    };
	    ProgressiveEntity myEntity = new ProgressiveEntity();

	    post.setEntity(myEntity);
	    HttpResponse response = client.execute(post);        

	    return getContent(response);

	} 

	public static String getContent(HttpResponse response) throws IOException {
	    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	    String body = "";
	    String content = "";

	    while ((body = rd.readLine()) != null) 
	    {
	        content += body;
	    }
	    return content.trim();
	}
}