package org.activiti.cloud.runtime.bundle.example;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class MarkLoanCompleteJavaDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) {
		Loan loan = (Loan) execution.getVariable("loan");
		
		loan.setStatus(Loan.STATUS_COMPLETE);
		
		execution.setVariable("loan", loan);
	}

}
