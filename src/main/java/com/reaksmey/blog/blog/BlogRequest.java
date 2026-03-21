package com.reaksmey.blog.blog;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record BlogRequest(

	@NotEmpty
	String title,
	@NotEmpty
	String content,
	String excerpt,
	String featuredImageUrl,
	@NotNull
	BlogStatus status
) {
}
