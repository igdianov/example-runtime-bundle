package org.activiti.cloud.runtime.bundle.example;

import java.util.Collections;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.cfg.TransactionPropagation;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandConfig;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.activiti.engine.runtime.Execution;

public class SendLoanDocumentJavaDelegate implements JavaDelegate {
	
	private static Object lock = new Object();

	@Override
	public void execute(DelegateExecution execution) {
		String loanProcessId = (String) execution.getVariable("loanProcessId");

		synchronized (intern(loanProcessId)) {
			
			CommandExecutor commandExecutor = Context.getProcessEngineConfiguration().getCommandExecutor();
			CommandConfig commandConfig = new CommandConfig(false, TransactionPropagation.REQUIRED);
			
			Execution result = commandExecutor.execute(commandConfig, new Command<Execution>() {
				public Execution execute(CommandContext commandContext) {
					Document document = (Document) execution.getVariable("document");
					
			  		Execution receiveDocument = Context.getProcessEngineConfiguration().getRuntimeService()
							.createExecutionQuery()
							.processInstanceId(loanProcessId)
							.activityId("receiveDocument")
							.singleResult();

					if(receiveDocument != null) {
						Context.getProcessEngineConfiguration()
							.getRuntimeService()
							.trigger(receiveDocument.getId(), Collections.singletonMap("document", document));
					}
					
					return receiveDocument;
				}
			});
				
			if(result==null)
				throw new ActivitiException("Cannot find active loan process execution for id: " + loanProcessId);
		}

	}
	
	private String intern(String id) {
		return new String(this.getClass() + "_" + id).intern();
	}
	
}
