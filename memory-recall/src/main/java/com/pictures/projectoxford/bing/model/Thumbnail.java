package com.pictures.projectoxford.bing.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Thumbnail
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Thumbnail {
	public Thumbnail() {
	}

	public int width;
	public int height;

	@Override
	public String toString() {
		return "Thumbnail [width=" + width + ", height=" + height + "]";
	}

}