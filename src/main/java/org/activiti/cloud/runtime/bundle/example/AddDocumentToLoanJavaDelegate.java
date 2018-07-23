package org.activiti.cloud.runtime.bundle.example;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class AddDocumentToLoanJavaDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) {
		Loan loan = (Loan) execution.getVariable("loan");
		Document document = (Document) execution.getVariable("document");
		
		loan.getDocuments().add(document);
		
		execution.setVariable("loan", loan);
	}
}
