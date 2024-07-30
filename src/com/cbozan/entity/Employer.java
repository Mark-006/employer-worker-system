package com.cbozan.entity;

import java.util.List;

public class Employer {
    private int employer_id; // Primary key
    private String name;
    private String surname;
    private List<String> phoneNumber;
    private String description;

    // Constructor (private to force usage of Builder)
    private Employer(int employer_id, String name, String surname, List<String> phoneNumber, String description) {
        this.employer_id = employer_id;
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.description = description;
    }

    // Getters
    public int getEmployerId() {
        return employer_id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public List<String> getPhoneNumber() {
        return phoneNumber;
    }

    public String getDescription() {
        return description;
    }

    // Builder class
    public static class EmployerBuilder {
        private int employer_id;
        private String name;
        private String surname;
        private List<String> phoneNumber;
        private String description;

        public EmployerBuilder setEmployerId(int employer_id) {
            this.employer_id = employer_id;
            return this;
        }

        public EmployerBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public EmployerBuilder setSurname(String surname) {
            this.surname = surname;
            return this;
        }

        public EmployerBuilder setPhoneNumber(List<String> phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public EmployerBuilder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Employer build() {
            return new Employer(employer_id, name, surname, phoneNumber, description);
        }
    }

    // Setters (needed for update functionality)
    public void setName(String name) {
        this.name = name;
    }

    public void setEmployerId(int employer_id) {
        this.employer_id = employer_id;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setPhoneNumber(List<String> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Additional methods
    public String getFname() {
        return name;
    }

    public String getLname() {
        return surname;
    }

    public void setFname(String fname) {
        this.name = fname;
    }

    public void setLname(String lname) {
        this.surname = lname;
    }

    public List<String> getTel() {
        return phoneNumber;
    }

    public void setTel(List<String> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
