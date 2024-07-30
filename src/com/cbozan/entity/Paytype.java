package com.cbozan.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;
import com.cbozan.exception.EntityException;

public class Paytype implements Serializable {

    private static final long serialVersionUID = 240445534493303939L;

    private int id;
    private String title;
    private Timestamp date;

    // Private constructor for use by builder
    private Paytype() {
        this.id = 0;
        this.title = null;
        this.date = null;
    }

    // Constructor using the builder pattern
    private Paytype(PaytypeBuilder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.date = builder.date;
    }

    // Builder class
    public static class PaytypeBuilder {
        private int id;
        private String title;
        private Timestamp date;

        public PaytypeBuilder() {}

        public PaytypeBuilder setId(int id) {
            if (id <= 0) {
                throw new IllegalArgumentException("Paytype ID cannot be negative or zero");
            }
            this.id = id;
            return this;
        }

        public PaytypeBuilder setTitle(String title) {
            if (title == null || title.isEmpty()) {
                throw new IllegalArgumentException("Paytype title cannot be null or empty");
            }
            this.title = title;
            return this;
        }

        public PaytypeBuilder setDate(Timestamp date) {
            this.date = date;
            return this;
        }

        public Paytype build() {
            if (this.id <= 0) {
                throw new IllegalArgumentException("Paytype ID cannot be negative or zero");
            }
            if (this.title == null || this.title.isEmpty()) {
                throw new IllegalArgumentException("Paytype title cannot be null or empty");
            }
            return new Paytype(this);
        }
    }

    // Singleton instance of an empty Paytype
    private static class EmptyInstanceSingleton {
        private static final Paytype instance = new Paytype();
    }

    public static final Paytype getEmptyInstance() {
        return EmptyInstanceSingleton.instance;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) throws EntityException {
        if (id <= 0)
            throw new EntityException("Paytype ID cannot be negative or zero");
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) throws EntityException {
        if (title == null || title.isEmpty())
            throw new EntityException("Paytype title cannot be null or empty");
        this.title = title;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    // Method to get the name (assuming name is based on title)
    public String getName() {
        return getTitle(); // Assuming title represents the name of the Paytype
    }

    // toString, hashCode, equals methods
    @Override
    public String toString() {
        return getTitle();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, date);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Paytype other = (Paytype) obj;
        return id == other.id && Objects.equals(title, other.title) && Objects.equals(date, other.date);
    }
}
