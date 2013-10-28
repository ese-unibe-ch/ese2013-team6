package com.ese2013.mub.util;

public class MensaDownloadException extends Exception {
	private static final long serialVersionUID = -8309614028626496381L;

	public MensaDownloadException(Throwable throwable) {
		super(throwable);
	}

	public MensaDownloadException(String message) {
		super(message);
	}
}
