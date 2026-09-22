
package com.glamgest.app.application.dto.service;

import jakarta.validation.constraints.Min;
import com.glamgest.app.common.validation.DurationMinutes;

public class ServiceUpdateDTO {

    private String name;

    private String description;

    @Min(value = 0, message = "Price must be greater than or equal to 0")
    private Integer price;

    @DurationMinutes
    private Integer durationMinutes;

    private Integer categoryId;
    private Boolean active;
    private boolean categoryIdSet;

    // Getters and Setters
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

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getCategoryId() { return categoryId; }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
        this.categoryIdSet = true;
    }

    public Boolean getActive() { return active; }

    public void setActive(Boolean active) { this.active = active; }

    public boolean isCategoryIdSet() { return categoryIdSet; }
}
