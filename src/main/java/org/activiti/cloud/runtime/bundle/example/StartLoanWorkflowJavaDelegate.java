package org.activiti.cloud.runtime.bundle.example;

import java.util.Date;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.cfg.TransactionPropagation;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandConfig;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.activiti.engine.runtime.ProcessInstance;

public class StartLoanWorkflowJavaDelegate implements JavaDelegate {

	private static Object lock = new Object();

	@Override
	public void execute(DelegateExecution execution) {
		String loanId = (String) execution.getVariable("loanId");
		Date date = (Date) execution.getVariable("loanApplicationDate");

		CommandExecutor commandExecutor = Context.getProcessEngineConfiguration().getCommandExecutor();
		CommandConfig commandConfig = new CommandConfig(false, TransactionPropagation.REQUIRES_NEW);

		ProcessInstance loanProcess = null;

		// make sure synchronized atomic execution 
		synchronized (lock) {
			loanProcess = commandExecutor.execute(commandConfig, new Command<ProcessInstance>() {
				public ProcessInstance execute(CommandContext commandContext) {

					Loan loan = new Loan(loanId, date);

					ProcessInstance loanProcess = Context.getProcessEngineConfiguration().getRuntimeService()
							.createProcessInstanceQuery().processInstanceBusinessKey(loanId).singleResult();

					if (loanProcess == null) {
						loanProcess = Context.getProcessEngineConfiguration().getRuntimeService()
								.createProcessInstanceBuilder().businessKey(loanId)
								.processDefinitionKey("loanQAProcess").variable("loan", loan).start();
					}

					return loanProcess;
				}
			});
		}

		execution.setVariable("loanProcessId", loanProcess.getId());

	}
	
	private String intern(String id) {
		return new String(this.getClass() + "_" + id).intern();
	}
	
}
