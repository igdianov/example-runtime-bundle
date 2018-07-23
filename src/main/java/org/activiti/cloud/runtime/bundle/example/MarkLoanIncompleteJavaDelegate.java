package org.activiti.cloud.runtime.bundle.example;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class MarkLoanIncompleteJavaDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) {
		Loan loan = (Loan) execution.getVariable("loan");
		
		loan.setStatus(Loan.STATUS_INCOMPLETE);		
		
		execution.setVariable("loan", loan);
	}

}
