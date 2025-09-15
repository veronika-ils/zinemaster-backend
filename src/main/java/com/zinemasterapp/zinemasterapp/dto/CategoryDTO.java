package com.zinemasterapp.zinemasterapp.dto;

public class CategoryDTO {

    private String id;
    private String name;
    private boolean accessible;
    public CategoryDTO(String id, String name, boolean accessible) {
        this.id = id;
        this.name = name;
        this.accessible = accessible;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAccessible() {
        return accessible;
    }

    public void setAccessible(boolean accessible) {
        this.accessible = accessible;
    }
}
