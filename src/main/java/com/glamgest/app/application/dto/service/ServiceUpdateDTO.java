
package com.glamgest.app.application.dto.service;

import jakarta.validation.constraints.Min;

public class ServiceUpdateDTO {

    private String name;

    private String description;

    @Min(value = 0, message = "Price must be greater than or equal to 0")
    private Integer price;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer durationMinutes;

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
}
