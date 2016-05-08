package com.deshun.imagescraper;

public class ExampleDownloader {
	public static void main(String[] args) {
		String key = "(your key here)"; // setting your access token for bing api
		IImageDownloader searcher = new BingImageSearch(key); // initialising bing image search
		
		// String query = "red"; 
		String[] queries = {"blue", "green"}; // can also input multiple queries in an array of strings
		
		// searcher.setSavePath("imagescraper/images"); // optional save path. defaults to "image/bing/(query)"
		
		searcher.downloadImages(queries, 10); // download 10 images from each search query
	}
}
