package com.deshun.imagescraper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * simple bing image search implementation
 * 
 * @author deshun
 * 
 * referenced the following websites for code snippets in the writing of this programme:
 *  	search api:
 *  http://stackoverflow.com/questions/27966272/get-all-the-links-from-bing-search-api-and-add-them-to-array
 * 	http://stackoverflow.com/questions/11485578/how-do-save-image-from-google-images-using-google-api
 * 		file IO:
 * http://stackoverflow.com/questions/4716503/reading-a-plain-text-file-in-java
 * http://www.codejava.net/java-se/networking/use-httpurlconnection-to-download-file-from-an-http-url
 * 
 * the following APIs were used:
 * Bing Search API: https://datamarket.azure.com/dataset/bing/search
 */

public class BingImageSearch extends ImageSearch implements IImageDownloader{
	private int timesSearched; // checks how many times api has been called
	
	private final String searchURL = "https://api.datamarket.azure.com/Bing/Search/v1/Image?Query=%%27%s%%27&$skip=%d&$format=JSON";
	private final short SEARCH_LIMIT = 5000;
	private final short NUM_IMAGES_IN_A_PAGE = 50;
	
	public BingImageSearch(){
		this(null, null);
	}

	public BingImageSearch(String accountKey){
		this(accountKey, null);
	}
	
	/**
	 * Initialises BingImageSearch with an account key and save directory
	 * @param accountKey
	 * @param savePath
	 */
	public BingImageSearch(String accountKey, String savePath){
		super(accountKey, savePath);
		File f = new File("timesSearched.txt");
		if (f.exists() && !f.isDirectory()){
			try {
				BufferedReader in = new BufferedReader(new FileReader(f));
				timesSearched = (Integer.parseInt(in.readLine()));
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
	}
	
	/**
	 * Sets the account key to use with the API
	 * @param accountKey
	 */
	@Override
	public void setAccountKey(String accountKey) {
		// encode and store account key
		this.accountKey = Base64.getEncoder().encodeToString((accountKey + ":" + accountKey).getBytes());
	}
	
	/**
	 * resets the number of times API has been used
	 * resets timesSearched to 0 and timesSearched.txt
	 */
	@Override
	public void resetSearchCount(){
		timesSearched = 0;
		FileWriter wr;
		try {
			wr = new FileWriter("timesSearched.txt");
			wr.write(String.valueOf(0));
			wr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * downloadImages takes a query and the number of images to download,
	 * and downloads the images returned by the search query to a folder
	 * @param query
	 * @param num number of images to download
	 * assumes account key (if provided) is valid
	 */
	@Override
	public void downloadImages(String query, int num){
		if (accountKey == null){
			throw new IllegalAccessError("No account key provided");
		}
		if (num >= SEARCH_LIMIT - timesSearched){
			throw new IllegalAccessError("Cannot search more than API limit");
		}

		int skip = 0;
		
		// create directory if not already created
		dirPath = dirPath == null ? "images/bing" : dirPath;
		String subDir = dirPath + "/" + query;
		new File(subDir).mkdirs();
		
		// assumes response is not empty
		URL query_url = generateSearchQuery(query, skip);
		JSONObject response = getJSONResponse(query_url);
		JSONObject d = response.getJSONObject("d");
		JSONArray results = d.getJSONArray("results");
		
		for (int i = 0; i < num; i++){
			int index = i % NUM_IMAGES_IN_A_PAGE;
			// submit query and consolidate response from Bing
			try {				
				// get each image's data
				JSONObject results_i = results.getJSONObject(index);
				String stringURL = results_i.getString("MediaUrl");
				URL url = new URL(stringURL);
				String fileExtension = stringURL.substring(stringURL.lastIndexOf("."));
				String ID = results_i.getString("ID");
				String filename = ID + fileExtension;
				String filepath = subDir + "/" + filename;

				// save this image
				saveImage(url, filepath);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (index == 49){
				skip += NUM_IMAGES_IN_A_PAGE;
				query_url = generateSearchQuery(query, skip);
				response = getJSONResponse(query_url);
				results = response.getJSONObject("d").getJSONArray("results");
			}
		}
		System.out.println("Download operation completed");
	}
	
	/**
	 * generates a search query url based on a query and number of results to skip
	 * @param query
	 * @param skip
	 * @return URL generated based on search query
	 * @throws IOException
	 */
	private URL generateSearchQuery(String query, int skip){
		String q;
		try {
			q = URLEncoder.encode(query, Charset.defaultCharset().name());
			URL bingURL = new URL(String.format(searchURL, q, skip));
			return bingURL;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * getJSONResponse takes in a url and returns a JSON representation of the results returned by that url
	 * @param url 
	 * @return JSON representation of bing search results
	 * @throws IOException
	 */
	private JSONObject getJSONResponse(URL url) {
		if (timesSearched >= SEARCH_LIMIT){
			throw new IllegalAccessError("Cannot search more than " + SEARCH_LIMIT + "times with current API.");
		}
		try {
			// sets up connection to the url
			URLConnection connection = url.openConnection();
			connection.setRequestProperty("Authorization", "Basic " + accountKey);
			
			// update times searched
			timesSearched++;
			FileWriter wr = new FileWriter("timesSearched.txt");
			wr.write(String.valueOf(timesSearched));
			wr.close();
			
			// build response in a string
			StringBuilder response = new StringBuilder();
			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while ((line = reader.readLine()) != null){
				response.append(line);
			} 
			
					BufferedWriter writer = null;
					try
					{
					    writer = new BufferedWriter( new FileWriter( "output2.txt"));
					    writer.write( response.toString());
		
					}
					catch ( IOException e)
					{
					}
					finally
					{
					    try
					    {
					        if ( writer != null)
					        writer.close( );
					    }
					    catch ( IOException e)
					    {
					    }
					}
			
			// return response as a json object
			return new JSONObject(response.toString());
		} catch (Exception e){
			throw new RuntimeException(e);
		} 
	}
	
	public static void main(String[] args){
		String k = "TCQOFzyoQi9SHsfFC0KlPrxphKcz50NM6XzEEFPUHjg";
		IImageDownloader searcher = new BingImageSearch(k);
		String[] queries = {"helium", "hydrogen"};
		searcher.downloadImages(queries, 10);
	}
}
