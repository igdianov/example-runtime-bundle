package org.activiti.cloud.runtime;

import java.util.Arrays;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.rules.RulesDeployer;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class RuntimeBundleApplicationConfigurer implements ProcessEngineConfigurationConfigurer {
	
	@Value("classpath*:mortgateQA/*.*")
	private Resource[] deploymentResources;

	@Override
	public void configure(SpringProcessEngineConfiguration configuration) {
		configuration.setCustomPostDeployers(Arrays.asList(new RulesDeployer()));
		configuration.setDatabaseSchemaUpdate("drop-create");
		configuration.setAsyncExecutorActivate(true);
		configuration.setAsyncExecutorDefaultAsyncJobAcquireWaitTime(500); // ms
		configuration.setAsyncExecutorDefaultTimerJobAcquireWaitTime(500); // ms
		configuration.setAsyncExecutorAsyncJobLockTimeInMillis(100); // ms
		configuration.setAsyncExecutorTimerLockTimeInMillis(100); // ms
		configuration.setAsyncExecutorMaxAsyncJobsDuePerAcquisition(10);
		configuration.setAsyncExecutorMaxTimerJobsPerAcquisition(10);
		configuration.setAsyncExecutorThreadPoolQueueSize(1000);
		configuration.setAsyncExecutorDefaultQueueSizeFullWaitTime(100); // ms
	}
	
	@Bean
	public CommandLineRunner deployMortgageQAResources(RepositoryService repositoryService) {
		return (args) -> {
	        DeploymentBuilder deployment = repositoryService.createDeployment()
        	.name("Mortgate QA Process Workflow")
            .enableDuplicateFiltering();
	        
	        for(Resource resource: deploymentResources)
	        	deployment.addInputStream(resource.getFilename(), resource.getInputStream());
	        
            deployment.deploy();			
		};
	}
}