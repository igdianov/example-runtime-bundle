package org.activiti.cloud.runtime.bundle.example;

import java.util.Collections;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.runtime.Execution;

public class SendLoanDocumentJavaDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) {
		String loanProcessId = (String) execution.getVariable("loanProcessId");
		Document document = (Document) execution.getVariable("document");

  		Execution loanProcess = Context.getProcessEngineConfiguration().getRuntimeService()
				.createExecutionQuery()
				.processInstanceId(loanProcessId)
				.activityId("receiveDocument")
				.singleResult();
			
		if(loanProcess != null)
			Context.getProcessEngineConfiguration().getRuntimeService()
				.trigger(loanProcess.getId(), Collections.singletonMap("document", document));
		else 
			throw new ActivitiException("Cannot find active loan process execution for document: "+document);
	}
	
}
