package com.pictures.projectoxford.bing.http;

@SuppressWarnings("serial")
public class LiteHttpClientException extends Exception {

	public LiteHttpClientException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);

	}

	public LiteHttpClientException(Throwable throwable) {
		super(throwable);

	}

}
