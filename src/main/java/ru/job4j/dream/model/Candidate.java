package ru.job4j.dream.model;

import java.util.Objects;

public class Candidate {
    private int id;
    private String name;
    private int city_id;
    private String photoId;

    public Candidate(int id, String name, int city_id, String photoId) {
        this.id = id;
        this.name = name;
        this.city_id = city_id;
        this.photoId = photoId;
    }

    public Candidate(String name, int city_id, String photoId) {
        this.name = name;
        this.city_id = city_id;
        this.photoId = photoId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Candidate candidate = (Candidate) o;
        return id == candidate.id &&
                Objects.equals(name, candidate.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public int getCity() {
        return city_id;
    }

    public void setCity(int city_id) {
        this.city_id = city_id;
    }
}
