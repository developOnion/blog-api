package com.reaksmey.blog.blog;

import com.reaksmey.blog.common.BaseEntity;
import com.reaksmey.blog.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
@Table(name = "blogs")
public class Blog extends BaseEntity {

	@NotEmpty
	@Column(nullable = false)
	private String title;

	@NotEmpty
	@Column(nullable = false, unique = true)
	private String slug;

	@NotEmpty
	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@NotEmpty
	@Column(length = 500)
	private String excerpt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "author_id", nullable = false)
	private User author;

	private String featuredImageUrl;

	@NotNull
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private BlogStatus status;
}
