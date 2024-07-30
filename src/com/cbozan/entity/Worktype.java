package com.cbozan.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

import com.cbozan.exception.EntityException;

public class Worktype implements Serializable {

    private static final long serialVersionUID = 5584866637778593619L;

    private int id;
    private String title;
    private int no;
    private Timestamp date;

    // Default constructor
    public Worktype() {
        this.id = 0;
        this.title = null;
        this.no = 0;
        this.date = null;
    }

    // Constructor with id and title
    public Worktype(int id, String title) {
        this.id = id;
        this.title = title;
        // Initialize other fields as needed
        this.no = 0; // Default value for no, adjust as necessary
        this.date = null; // Default value for date, adjust as necessary
    }

    // Constructor with builder
    private Worktype(WorktypeBuilder builder) throws EntityException {
        super();
        setId(builder.id);
        setTitle(builder.title);
        setNo(builder.no);
        setDate(builder.date);
    }

    // Builder pattern for Worktype
    public static class WorktypeBuilder {
        private int id;
        private String title;
        private int no;
        private Timestamp date;

        public WorktypeBuilder() {
        }

        public WorktypeBuilder(int id, String title, int no, Timestamp date) {
            this.id = id;
            this.title = title;
            this.no = no;
            this.date = date;
        }

        public WorktypeBuilder setId(int id) {
            this.id = id;
            return this;
        }

        public WorktypeBuilder setTitle(String title) {
            this.title = title;
            return this;
        }

        public WorktypeBuilder setNo(int no) {
            this.no = no;
            return this;
        }

        public WorktypeBuilder setDate(Timestamp date) {
            this.date = date;
            return this;
        }

        public Worktype build() throws EntityException {
            return new Worktype(this);
        }
    }

    private static class EmptyInstanceSingleton {
        private static final Worktype instance = new Worktype();
    }

    public static final Worktype getEmptyInstance() {
        return EmptyInstanceSingleton.instance;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) throws EntityException {
        if (id <= 0)
            throw new EntityException("Worktype ID must be positive");
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) throws EntityException {
        if (title == null || title.isEmpty())
            throw new EntityException("Worktype title cannot be null or empty");
        this.title = title;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    // Override methods for toString, hashCode, equals
    @Override
    public String toString() {
        return getTitle();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, no, date);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Worktype worktype = (Worktype) obj;
        return id == worktype.id &&
                no == worktype.no &&
                Objects.equals(title, worktype.title) &&
                Objects.equals(date, worktype.date);
    }

    public String getName() {
       
        throw new UnsupportedOperationException("Unimplemented method 'getName'");
    }

    public String getWorktypeName() {
        
        throw new UnsupportedOperationException("Unimplemented method 'getWorktypeName'");
    }
}

