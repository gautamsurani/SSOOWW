package com.serviceonwheel.model;

import java.io.Serializable;

public class JobList implements Serializable {
    String jobID, image, title, description, date, shre_msg;

    public JobList() {
    }

    public String getJobID() {

        return jobID;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getShre_msg() {
        return shre_msg;
    }

    public void setShre_msg(String shre_msg) {
        this.shre_msg = shre_msg;
    }
}
