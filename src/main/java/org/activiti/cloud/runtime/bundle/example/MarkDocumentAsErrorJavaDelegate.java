package org.activiti.cloud.runtime.bundle.example;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class MarkDocumentAsErrorJavaDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) {
		Document document = (Document) execution.getVariable("document");
		
		document.setStatus(Document.STATUS_ERROR);
		
		execution.setVariable("document", document);
	}

}
