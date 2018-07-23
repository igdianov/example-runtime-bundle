package org.activiti.cloud.runtime.bundle.example;

import java.io.Serializable;

public class Document implements Serializable {
	
	public static final String STATUS_NEW = "NEW";
	public static final String STATUS_VALID = "VALID";
	public static final String STATUS_ERROR = "ERROR";
	
	private String id;
	private String loanId;
	private String category;
	private String status = STATUS_NEW;
	
	Document() {}
	
	public Document(String id, String loanId, String category, String status) {
		this.id = id;
		this.loanId = loanId;
		this.category = category;
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public String getCategory() {
		return category;
	}

	public String getStatus() {
		return status;
	}

	public String getLoanId() {
		return loanId;
	}

	public boolean isValid() {
		return Document.STATUS_VALID.equals(this.status);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((loanId == null) ? 0 : loanId.hashCode());
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
		Document other = (Document) obj;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (loanId == null) {
			if (other.loanId != null)
				return false;
		} else if (!loanId.equals(other.loanId))
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
		return "Document [id=" + id + ", loanId=" + loanId + ", category=" + category + ", status=" + status + "]";
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
