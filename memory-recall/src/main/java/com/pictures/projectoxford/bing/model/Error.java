package com.pictures.projectoxford.bing.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Error
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Error {
	public Error() {
	}

	public String code;
	public String message;
	public String parameter;

	@Override
	public String toString() {
		return "Error [code=" + code + ", message=" + message + ", parameter=" + parameter + "]";
	}
}
