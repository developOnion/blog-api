package com.reaksmey.blog.blog;

import jakarta.validation.constraints.Size;

public record BlogPatchRequest(

	@Size(min = 1)
	String title,
	@Size(min = 1)
	String content,
	String featuredImageUrl,
	BlogStatus status
) {
}
