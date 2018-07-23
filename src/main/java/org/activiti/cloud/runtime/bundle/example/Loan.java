package org.activiti.cloud.runtime.bundle.example;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Loan implements Serializable {
	
	public static final String STATUS_COMPLETE = "COMPLETE";
	public static final String STATUS_INCOMPLETE = "INCOMPLETE";

	private String id;
	private Date date;
	private List<Document> documents = new ArrayList<>();
	private String status = STATUS_INCOMPLETE;
	
	Loan() {}
	
	public Loan(String id, Date date) {
		super();
		this.id = id;
		this.date = date;
	}
	
	public String getId() {
		return id;
	}

	public Date getDate() {
		return date;
	}

	public List<Document> getDocuments() {
		return documents;
	}

	public String getStatus() {
		return status;
	}
	
	public boolean isValid() {
		return STATUS_COMPLETE.equals(status);
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((documents == null) ? 0 : documents.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Loan other = (Loan) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (documents == null) {
			if (other.documents != null)
				return false;
		} else if (!documents.equals(other.documents))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Loan [id=" + id + ", date=" + date + ", documents=" + documents + ", status=" + status + "]";
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
