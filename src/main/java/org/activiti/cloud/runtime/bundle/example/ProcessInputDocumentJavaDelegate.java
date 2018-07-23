package org.activiti.cloud.runtime.bundle.example;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class ProcessInputDocumentJavaDelegate implements JavaDelegate {
	@Override
	public void execute(DelegateExecution execution) {
	    Document document =  new Document(
	    		(String) execution.getVariable("documentId"),
	    		(String) execution.getVariable("loanId"),
	    		(String) execution.getVariable("documentCategory"),
	    		Document.STATUS_NEW
	    );
	    
		execution.setVariable("document", document);
	}
}
