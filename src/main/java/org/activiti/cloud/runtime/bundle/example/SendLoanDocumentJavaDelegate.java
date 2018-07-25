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
		Document document = (Document) execution.getVariable("document");

		synchronized (lock) {
		
	  		Execution loanProcess = Context.getProcessEngineConfiguration().getRuntimeService()
					.createExecutionQuery()
					.processInstanceId(loanProcessId)
					.activityId("receiveDocument")
					.singleResult();
				
			if(loanProcess != null) {
				
				CommandExecutor commandExecutor = Context.getProcessEngineConfiguration().getCommandExecutor();
				CommandConfig commandConfig = new CommandConfig(false, TransactionPropagation.REQUIRED);
				
					commandExecutor.execute(commandConfig, new Command<Void>() {
						public Void execute(CommandContext commandContext) {
							Context.getProcessEngineConfiguration()
								.getRuntimeService()
								.trigger(loanProcess.getId(), Collections.singletonMap("document", document));
							
							return null;
						}
					});
			} else { 
				throw new ActivitiException("Cannot find active loan process execution for document: "+document);
			}
		}

	}
	
}
