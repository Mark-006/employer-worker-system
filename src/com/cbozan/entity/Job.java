package com.cbozan.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

import com.cbozan.dao.EmployerDAO;
import com.cbozan.dao.PriceDAO;
import com.cbozan.exception.EntityException;

public class Job implements Serializable, Cloneable {

    private static final long serialVersionUID = 9178806163995887915L;

    private int id;
    private Employer employer;
    private Price price;
    private String title;
    private String description;
    private Timestamp date;

    // Default constructor
    public Job(int i, String jobText) {
        this.id = 0;
        this.employer = null;
        this.price = null;
        this.title = null;
        this.description = null;
        this.date = null;
    }

    // Constructor with builder
    private Job(JobBuilder builder) throws EntityException {
        this(builder.id,
             builder.employer,
             builder.price,
             builder.title,
             builder.description,
             builder.date);
    }

    // Constructor with parameters
    public Job(int id, Employer employer, Price price, String title, String description, Timestamp date) throws EntityException {
        setId(id);
        setEmployer(employer);
        setPrice(price);
        setTitle(title);
        setDescription(description);
        setDate(date);
    }

    // Builder pattern for Job
    public static class JobBuilder {
        private int id;
        private String title;
        private String description;
        private Timestamp date;
        private Employer employer;
        private Price price;

        public JobBuilder() {
        }

        public JobBuilder(int id, int employerId, int priceId, String title, String description, Timestamp date) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.date = date;
            this.employer = EmployerDAO.getInstance().findById(employerId);
            this.price = PriceDAO.getInstance().findById(priceId);
        }

        public JobBuilder(int id, Employer employer, Price price, String title, String description, Timestamp date) {
            this.id = id;
            this.employer = employer;
            this.price = price;
            this.title = title;
            this.description = description;
            this.date = date;
        }

        public JobBuilder setId(int id) {
            this.id = id;
            return this;
        }

        public JobBuilder setEmployer(Employer employer) {
            this.employer = employer;
            return this;
        }

        public JobBuilder setEmployerId(int employerId) {
            this.employer = EmployerDAO.getInstance().findById(employerId);
            return this;
        }

        public JobBuilder setPrice(Price price) {
            this.price = price;
            return this;
        }

        public JobBuilder setPriceId(int priceId) {
            this.price = PriceDAO.getInstance().findById(priceId);
            return this;
        }

        public JobBuilder setTitle(String title) {
            this.title = title;
            return this;
        }

        public JobBuilder setDescription(String description) {
            this.description = description;
            return this;
        }

        public JobBuilder setDate(Timestamp date) {
            this.date = date;
            return this;
        }

        public Job build() throws EntityException {
            if (this.employer == null) {
                throw new EntityException("Employer cannot be null");
            }
            if (this.price == null) {
                throw new EntityException("Price cannot be null");
            }
            return new Job(this);
        }
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) throws EntityException {
        if (id <= 0)
            throw new EntityException("Job ID must be positive");
        this.id = id;
    }

    public Employer getEmployer() {
        return employer;
    }

    public void setEmployer(Employer employer) throws EntityException {
        if (employer == null)
            throw new EntityException("Employer cannot be null");
        this.employer = employer;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) throws EntityException {
        if (price == null)
            throw new EntityException("Price cannot be null");
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) throws EntityException {
        if (title == null || title.isEmpty())
            throw new EntityException("Title cannot be null or empty");
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    // DAO getters
    public EmployerDAO getEmployerDAO() {
        return EmployerDAO.getInstance();
    }

    public PriceDAO getPriceDAO() {
        return PriceDAO.getInstance();
    }

    // Additional methods as needed

    // Assuming Job has a name attribute
    public String getName() {
        return this.title; // Adjust this according to your Job class's name attribute
    }

    @Override
    public String toString() {
        return getTitle();
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, description, employer, id, price, title);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Job other = (Job) obj;
        return Objects.equals(date, other.date) && Objects.equals(description, other.description)
                && Objects.equals(employer, other.employer) && id == other.id && Objects.equals(price, other.price)
                && Objects.equals(title, other.title);
    }

    @Override
    public Job clone() {
        try {
            return (Job) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getJobTitle() {
        
        throw new UnsupportedOperationException("Unimplemented method 'getJobTitle'");
    }
}
