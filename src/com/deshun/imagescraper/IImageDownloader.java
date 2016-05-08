package com.deshun.imagescraper;

public interface IImageDownloader {
	/**
	 * optional
	 * sets user agent for downloading images
	 * @param userAgent
	 */
	public void setUserAgent(String userAgent);
	
	/**
	 * setKey will set the API key for use with the interface
	 * @param key
	 */
	public void setAccountKey(String key);
	
	/**
	 * setSaveDir sets the directory to save images to
	 * @param dirPath
	 */
	public void setSavePath(String dirPath);
	
	/**
	 * optional
	 * resets the internal count for the number of times API has been used
	 */
	public void resetSearchCount();
	
	/**
	 * downloadImages will take a query, the number of images to download 
	 * and saves it in directory given by dirPath
	 * or in /images/(searchengine)/(query) if savepath is not set
	 * @param query
	 * @param num
	 */
	public void downloadImages(String query, int num);
	
	/**
	 * downloadImages given an array of queries
	 * @param queries
	 * @param num
	 */
	public void downloadImages(String[] queries, int num);
}
