package com.reaksmey.blog.service;

import com.github.slugify.Slugify;
import com.reaksmey.blog.dto.BlogRequest;
import com.reaksmey.blog.dto.BlogResponse;
import com.reaksmey.blog.mapper.BlogMapper;
import com.reaksmey.blog.model.Blog;
import com.reaksmey.blog.model.User;
import com.reaksmey.blog.repository.BlogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PostService {

	private final BlogRepository blogRepository;
	private final BlogMapper blogMapper;
	private final Slugify slugify;
	private static final int EXCERPT_LENGTH = 100;

	public PostService(
		BlogRepository blogRepository,
		BlogMapper blogMapper,
		Slugify slugify
	) {

		this.blogRepository = blogRepository;
		this.blogMapper = blogMapper;
		this.slugify = slugify;
	}

	public Page<BlogResponse> getAllPosts(Pageable pageable) {

		log.info("Fetching all blog posts with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
		Page<Blog> blogs = blogRepository.findAll(pageable);
		return blogs.map(blogMapper::toDto);
	}

	public BlogResponse createPost(BlogRequest blogRequest, User user) {

		log.info("Creating new blog post with title: {}", blogRequest.title());

		String slug = slugify.slugify(blogRequest.title());
		if (blogRepository.existsBySlug(slug)) {
			slug += "-" + System.currentTimeMillis();
		}

		String excerpt = blogRequest.excerpt() != null
			? blogRequest.excerpt()
			: blogRequest.content().substring(0, Math.min(EXCERPT_LENGTH, blogRequest.content().length()));

		Blog blog = Blog.builder()
			.title(blogRequest.title())
			.slug(slug)
			.content(blogRequest.content())
			.excerpt(excerpt)
			.author(user)
			.featuredImageUrl(blogRequest.featuredImageUrl())
			.status(blogRequest.status())
			.build();
		Blog savedBlog = blogRepository.save(blog);

		return blogMapper.toDto(savedBlog);
	}
}
