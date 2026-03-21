package com.reaksmey.blog.blog;

import org.springframework.stereotype.Component;

@Component
public class BlogMapper {

	public BlogResponse toDto(Blog blog) {
		return new BlogResponse(
			blog.getId(),
			blog.getTitle(),
			blog.getSlug(),
			blog.getContent(),
			blog.getExcerpt(),
			blog.getAuthor().getId(),
			blog.getFeaturedImageUrl(),
			blog.getStatus().name()
		);
	}
}
