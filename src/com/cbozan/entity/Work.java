package com.cbozan.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

import com.cbozan.exception.EntityException;

public class Work implements Serializable {

    private static final long serialVersionUID = 1466581631433254437L;

    private int id;
    private Job job;
    private Worker worker;
    private Worktype worktype;
    private String description;
    private Timestamp date;

    // Default constructor
    public Work() {
        this.id = 0;
        this.job = null;
        this.worker = null;
        this.worktype = null;
        this.description = null;
        this.date = null;
    }

    // Constructor with parameters
    public Work(int id, Job job, Worker worker, Worktype worktype, String description) {
        this.id = id;
        this.job = job;
        this.worker = worker;
        this.worktype = worktype;
        this.description = description;
        this.date = new Timestamp(System.currentTimeMillis()); // Assuming current timestamp
    }

    public Work(int i, Job job2, Worker worker2, Worktype worktype2, Date currentDate, String description2) {
        
    }

   
    public static class WorkBuilder {
        private int id;
        private Job job;
        private Worker worker;
        private Worktype worktype;
        private String description;
        public WorkBuilder() {
        }

        public WorkBuilder setId(int id) {
            this.id = id;
            return this;
        }

        public WorkBuilder setJob(Job job) {
            this.job = job;
            return this;
        }

        public WorkBuilder setWorker(Worker worker) {
            this.worker = worker;
            return this;
        }

        public WorkBuilder setWorktype(Worktype worktype) {
            this.worktype = worktype;
            return this;
        }

        public WorkBuilder setDescription(String description) {
            this.description = description;
            return this;
        }

        public WorkBuilder setDate(Timestamp date) {
            return this;
        }

        public Work build() throws EntityException {
            // Validate required fields if necessary
            if (job == null || worker == null || worktype == null) {
                throw new EntityException("Job, Worker, or Worktype cannot be null");
            }
            return new Work(id, job, worker, worktype, description);
        }

		public void setWorker_id(int int1) {
			
			throw new UnsupportedOperationException("Unimplemented method 'setWorker_id'");
		}

        public void setWorktype_id(int int1) {
            
            throw new UnsupportedOperationException("Unimplemented method 'setWorktype_id'");
        }

        public void setWorkgroup_id(int int1) {
           
            throw new UnsupportedOperationException("Unimplemented method 'setWorkgroup_id'");
        }

        public void setJob_id(int int1) {
           
            throw new UnsupportedOperationException("Unimplemented method 'setJob_id'");
        }
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) throws EntityException {
        if (id <= 0)
            throw new EntityException("Work ID must be positive");
        this.id = id;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) throws EntityException {
        if (job == null)
            throw new EntityException("Job in Work cannot be null");
        this.job = job;
    }

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) throws EntityException {
        if (worker == null)
            throw new EntityException("Worker in Work cannot be null");
        this.worker = worker;
    }

    public Worktype getWorktype() {
        return worktype;
    }

    public void setWorktype(Worktype worktype) throws EntityException {
        if (worktype == null)
            throw new EntityException("Worktype in Work cannot be null");
        this.worktype = worktype;
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

    @Override
    public String toString() {
        return "Work [id=" + id + ", job=" + job + ", worker=" + worker + ", worktype=" + worktype + ", description="
                + description + ", date=" + date + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, job, worker, worktype, description, date);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Work other = (Work) obj;
        return id == other.id && Objects.equals(job, other.job) && Objects.equals(worker, other.worker)
                && Objects.equals(worktype, other.worktype) && Objects.equals(description, other.description)
                && Objects.equals(date, other.date);
    }

    public Work getWorkgroup() {
       
        throw new UnsupportedOperationException("Unimplemented method 'getWorkgroup'");
    }
}
