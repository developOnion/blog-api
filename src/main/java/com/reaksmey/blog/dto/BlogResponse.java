package com.reaksmey.blog.dto;

import java.util.UUID;

public record BlogResponse(

	UUID id,
	String title,
	String slug,
	String content,
	String excerpt,
	UUID authorId,
	String featuredImageUrl,
	String status
) {
}
