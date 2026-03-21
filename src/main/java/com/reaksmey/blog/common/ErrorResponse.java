package com.reaksmey.blog.common;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime timestamp,
	int status,
	String error,
	String message,
	Map<String, String> validationErrors
) {

	public ErrorResponse(LocalDateTime timestamp, int status, String error, String message) {
		this(timestamp, status, error, message, null);
	}
}
