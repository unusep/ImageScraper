package com.deshun.imagescraper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class ImageSearch implements IImageDownloader {
	protected String accountKey;
	protected String dirPath;
	protected String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";
	
	public ImageSearch(){
		this(null, null);
	}

	public ImageSearch(String accountKey){
		this(accountKey, null);
	}
	
	/**
	 * Initialises ImageSearch with an account key and save directory
	 * @param accountKey
	 * @param savePath
	 */
	public ImageSearch(String accountKey, String savePath){
		setAccountKey(accountKey);
		setSavePath(savePath);
	}

	@Override
	public void setUserAgent(String userAgent){
		this.userAgent = userAgent;
	}
	
	/**
	 * sets the save directory for the images
	 */
	@Override
	public void setSavePath(String dirPath) {
		this.dirPath = dirPath;
	} 
	
	/**
	 * downloads images from several queries
	 * leverages downloadImages for single query to work
	 */
	@Override
	public void downloadImages(String[] queries, int num){
		for (String query: queries){
			downloadImages(query, num);
		}
	}

	/**
	 * save image takes in a url and a filepath and downloads the image at the url into the directory given by filepath
	 * @param url URL object of the image
	 * @param filepath  (including file name and extension)
	 * @throws IOException
	 */
	protected void saveImage(URL url, String filepath){
		final int BUFFER_SIZE = 2048;
		
		try {
			// try to open a connection to the url
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			httpConn.addRequestProperty("User-Agent", userAgent);
	        int responseCode = httpConn.getResponseCode();
	        
	        if (responseCode == HttpURLConnection.HTTP_OK){
	    		// reads from inputstream and saves into filepath
	    		InputStream is = httpConn.getInputStream();
	    		FileOutputStream os = new FileOutputStream(filepath);
	    		
	    		byte[] b = new byte[BUFFER_SIZE];
	    		int length = -1;
	    		while ((length = is.read(b)) != -1){
	    			os.write(b, 0, length);
	    		}
	    		
	    		is.close();
	    		os.close();
	        } else {
	        	System.out.println(url.toString() + " is not available. HTTP code: " + responseCode);
	        }
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	
}
