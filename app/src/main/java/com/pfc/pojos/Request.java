package com.pfc.pojos;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

public class Request {

    private String requestor;
    private boolean status;
    private GeoPoint location;
    private String title;
    private String description;
    private String idDoc;
    private Timestamp creationDate;
    private String mail;

    public Request() {
    }

    public String getRequestor() {
        return requestor;
    }

    public void setRequestor(String requestor) {
        this.requestor = requestor;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
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

    public String getIdDoc() {return idDoc;}

    public void setIdDoc(String idDoc) {
        this.idDoc = idDoc;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    @NonNull
    @Override
    public String toString() {
        return "Request{" +
                "requestor='" + requestor + '\'' +
                ", status=" + status +
                ", location=" + location +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", idDoc='" + idDoc + '\'' +
                ", creationDate=" + creationDate +
                '}';
    }

}//End
