package com.org.Triplens.DTO;

import java.util.List;

public class FestivalDTO {

    private String name;
    private String description;
    private String month;
    private List<String> categories;
    private String wikipediaUrl;

    public FestivalDTO() {}

    public FestivalDTO(String name, String description, String month,
                       List<String> categories, String wikipediaUrl) {
        this.name = name;
        this.description = description;
        this.month = month;
        this.categories = categories;
        this.wikipediaUrl = wikipediaUrl;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public List<String> getCategories() {
		return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public String getWikipediaUrl() {
		return wikipediaUrl;
	}

	public void setWikipediaUrl(String wikipediaUrl) {
		this.wikipediaUrl = wikipediaUrl;
	}

    
}
