package com.pictures.projectoxford.bing.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Instrumentation
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Instrumentation {
	public Instrumentation() {
	}

	public String pageLoadPingUrl;

	@Override
	public String toString() {
		return "Instrumentation [pageLoadPingUrl=" + pageLoadPingUrl + "]";
	}

}