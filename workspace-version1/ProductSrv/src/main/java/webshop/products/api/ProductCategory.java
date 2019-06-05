package webshop.products.api;

import java.util.List;

import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductCategory {
	private long id;
	@NotEmpty
	private String name;
	@Min(0)
	private long parentCategoryId;
	private List<String> tags;

	public ProductCategory() {
		// Jackson deserialization
	}

	public ProductCategory(long id, String name, long parentCategoryId, List<String> tags) {
		this.id = id;
		this.name = name;
		this.parentCategoryId = parentCategoryId;
		this.tags = tags;
	}

	@JsonProperty
	public long getId() {
		return id;
	}

	@JsonProperty
	public String getName() {
		return name;
	}

	@JsonProperty
	public long getParentCategoryId() {
		return parentCategoryId;
	}

	@JsonProperty
	public List<String> getTags() {
		return tags;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setParentCategoryId(long parentCategoryId) {
		this.parentCategoryId = parentCategoryId;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

}