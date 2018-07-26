package org.activiti.cloud.runtime.bundle.example;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class MortgageQAProcessTest 
{

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();
    
    private Deployment deployment;
    
    @Before
    public void setUp() {
        RepositoryService repositoryService = activitiRule.getRepositoryService();

        this.deployment = repositoryService.createDeployment()
        	.name("Mortgate QA Process Workflow")
        	.addClasspathResource("mortgageQA/documentQAProcess.bpmn")
            .addClasspathResource("mortgageQA/documentQAProcess.drl")
        	.addClasspathResource("mortgageQA/loanQAProcess.bpmn")
            .addClasspathResource("mortgageQA/loanQAProcess.drl")
            .enableDuplicateFiltering()
            .deploy();
    }
    
    @After
    public void tearDown() {
    	 activitiRule.getRepositoryService().deleteDeployment(deployment.getId(), true);
    }
    
    @Test
    public void testItern() {
		String s1="Test";
		String s2="Test";
				
		assertThat(s1.intern()).isEqualTo(s2.intern());
		assert s1.intern() == s2.intern();
	}

    @Test
    public void startValidDocumentQAProcess() throws Exception
    {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        String loanId = "123456789";
        String documentId = "1234567890";
        String documentCategory = "Loan Application";

    	// given
        Map<String, Object> variableMap = new HashMap<String, Object>();
        variableMap.put("documentId", documentId);
        variableMap.put("documentCategory", documentCategory);
        variableMap.put("loanId", loanId);
        variableMap.put("loanApplicationDate", new Date());
        
        // when
        ProcessInstance documentProcess = runtimeService.startProcessInstanceByKey("documentQAProcess", variableMap);
        assertNotNull(documentProcess.getId());
        String documentProcessId = documentProcess.getId();
        
        // then
        System.out.println("document process id: " + documentProcess.getId() + " " + documentProcess.getProcessDefinitionId());
        Document document = (Document) runtimeService.getVariable(documentProcess.getId(), "document");
        assertThat(document.isValid()).isTrue();
        
        ProcessInstance loanProcess = runtimeService.createProcessInstanceQuery()
        		.processInstanceBusinessKey(loanId)
        		.singleResult();
        
        assertThat(loanProcess).isNotNull();

        while(documentProcess != null) {
        	documentProcess = runtimeService.createProcessInstanceQuery()
            		.processInstanceBusinessKey(documentProcessId)
            		.singleResult();

        	Thread.sleep(1000);
        }
        
        assertThat(runtimeService.createExecutionQuery().processInstanceId(documentProcessId).list()).isEmpty();
        assertThat(runtimeService.createExecutionQuery().processInstanceId(loanProcess.getId()).activityId("receiveDocument").singleResult()).isNotNull();

        Loan loan = (Loan) runtimeService.getVariable(loanProcess.getId(), "loan");
        assertThat(loan.getDocuments()).hasSize(1);
        assertThat(loan.getStatus()).isEqualTo(Loan.STATUS_INCOMPLETE);
        assertThat(loan.isValid()).isFalse();
    }


    @Test(timeout=80000)
    public void startValidLoanQAProcessSync() throws Exception
    {
        RuntimeService runtimeService = activitiRule.getRuntimeService();

        // given
        Date loanApplicationDate = new Date();
        String loanIds[] = new String[5];
        for(int i=0; i<loanIds.length; i++) {
        	loanIds[i] = String.format("loan%05d", i+1);
        }
        
        // when
        for(int i=0, j=0; i<loanIds.length; i++, j++) {
        	String documentId = String.format("doc%07d", j+1);
        	String loanId = loanIds[i];
        	
            startDocumentQAProcessSync(documentId, loanId, "Loan Application", loanApplicationDate);
            startDocumentQAProcessSync(documentId, loanId, "Loan Signature", loanApplicationDate);
            startDocumentQAProcessSync(documentId, loanId, "Loan Approval", loanApplicationDate);
            startDocumentQAProcessSync(documentId, loanId, "Loan Completion", loanApplicationDate);
        }
        
        // then
        List<Execution> executions = null;
        do {
        	Thread.sleep(1000);
        	// there should be no timers to retry async jobs
            assertThat(activitiRule.getManagementService().createTimerJobQuery().list()).isEmpty();
        	
        	executions = runtimeService.createExecutionQuery().list();        	
        	System.out.println("----- Async="+ activitiRule.getManagementService().createJobQuery().list().size()+
        					   ", Timers="+ activitiRule.getManagementService().createTimerJobQuery().list().size()+
        	                   ", Suspended="+ activitiRule.getManagementService().createSuspendedJobQuery().list().size()+
        	                   ", Processes="+ runtimeService.createProcessInstanceQuery().list().size()+
        	                   ", Executions="+ runtimeService.createExecutionQuery().list());
        } while(!executions.isEmpty());
        
        for(String loanId: loanIds) 
        	assertLoanProcessCompleted(loanId);

        assertThat(runtimeService.createExecutionQuery().list()).isEmpty();
        
    }
    
    @Test(timeout=80000)
    public void startValidLoanQAProcessAsync() throws Exception
    {
        RuntimeService runtimeService = activitiRule.getRuntimeService();

        // given
        String documentId = "1234567890";
        Date loanApplicationDate = new Date();
        
        // when
        String loanId = "loan00001";
        startDocumentQAProcessAsync(documentId, loanId, "Loan Application", loanApplicationDate);
        startDocumentQAProcessAsync(documentId, loanId, "Loan Signature", loanApplicationDate);
        startDocumentQAProcessAsync(documentId, loanId, "Loan Approval", loanApplicationDate);
        startDocumentQAProcessAsync(documentId, loanId, "Loan Completion", loanApplicationDate);

        String loanId2 = "loan00002";
        startDocumentQAProcessAsync(documentId, loanId2, "Loan Application", loanApplicationDate);
        startDocumentQAProcessAsync(documentId, loanId2, "Loan Signature", loanApplicationDate);
        startDocumentQAProcessAsync(documentId, loanId2, "Loan Approval", loanApplicationDate);
        startDocumentQAProcessAsync(documentId, loanId2, "Loan Completion", loanApplicationDate);

        // then
        do {
        	Thread.sleep(1000);
            assertThat(activitiRule.getManagementService().createTimerJobQuery().list()).isEmpty();
        	
        	System.out.println("----- Async="+ activitiRule.getManagementService().createJobQuery().list().size()+
					   ", Timers="+ activitiRule.getManagementService().createTimerJobQuery().list().size()+
	                   ", Suspended="+ activitiRule.getManagementService().createSuspendedJobQuery().list().size()+
	                   ", Processes="+ runtimeService.createProcessInstanceQuery().list().size()+
	                   ", Executions="+ runtimeService.createExecutionQuery().list());
        } while(!runtimeService.createExecutionQuery().list().isEmpty());
        
        assertLoanProcessCompleted(loanId);
        assertLoanProcessCompleted(loanId2);

        assertThat(runtimeService.createExecutionQuery().list()).isEmpty();
        
    }
    
    private void assertLoanProcessCompleted(String loanId) {
        HistoricProcessInstance processInstance = activitiRule.getHistoryService().createHistoricProcessInstanceQuery()
            	.processInstanceBusinessKey(loanId)
            	.singleResult();
        
        assertThat(processInstance).isNotNull();

        HistoricVariableInstance variableInstance = activitiRule.getHistoryService().createHistoricVariableInstanceQuery()
            	.processInstanceId(processInstance.getId())
            	.variableName("loan")
            	.singleResult();
        
        Loan loan = (Loan) variableInstance.getValue();
        assertThat(loan.getDocuments()).hasSize(4);
        assertThat(loan.getStatus()).isEqualTo(Loan.STATUS_COMPLETE);
        assertThat(loan.isValid()).isTrue();
    	
    }
    
    private void startDocumentQAProcessAsync(String documentId, String loanId, String documentCategory, Date loanApplicationDate) {
        new Thread(new Runnable() {
        	
			@Override
			public void run() {
				startDocumentQAProcessSync(documentId, loanId, documentCategory, loanApplicationDate);
			}
        	
        }).start();
    	
    }

    private void startDocumentQAProcessSync(String documentId, String loanId, String documentCategory, Date loanApplicationDate) {
        RuntimeService runtimeService = activitiRule.getRuntimeService();

        Map<String, Object> variableMap = new HashMap<String, Object>();
        variableMap.put("documentId", documentId);
        variableMap.put("documentCategory", documentCategory);
        variableMap.put("loanId", loanId);
        variableMap.put("loanApplicationDate", loanApplicationDate);

        runtimeService.startProcessInstanceByKey("documentQAProcess", variableMap);
    }
    
    @Test
    public void startInvalidDocumentQAProcess() throws Exception
    {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        String loanId = "invalid";
        String documentId = "invalid";
        String documentCategory = "Loan Something";

    	// given
        Map<String, Object> variableMap = new HashMap<String, Object>();
        variableMap.put("documentId", documentId);
        variableMap.put("documentCategory", documentCategory);
        variableMap.put("loanId", loanId);
        variableMap.put("loanApplicationDate", new Date());
        
        // when
        ProcessInstance documentProcess = runtimeService.startProcessInstanceByKey("documentQAProcess", variableMap);
        
        // then
        assertNotNull(documentProcess.getId());
        HistoricVariableInstance variableInstance = activitiRule.getHistoryService().createHistoricVariableInstanceQuery()
        	.processInstanceId(documentProcess.getId())
        	.variableName("document")
        	.singleResult();        
        
        Document document = (Document) variableInstance.getValue();
        assertThat(document.getStatus()).isEqualTo(Document.STATUS_ERROR);
        
        assertThat(runtimeService.createExecutionQuery().list()).isEmpty();
    }    
}
