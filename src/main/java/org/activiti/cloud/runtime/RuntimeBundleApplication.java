package org.activiti.cloud.runtime;

import org.activiti.cloud.starter.rb.configuration.ActivitiRuntimeBundle;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.stagemonitor.core.Stagemonitor;

@SpringBootApplication
@ActivitiRuntimeBundle
public class RuntimeBundleApplication {

    public static void main(String[] args) {
        Stagemonitor.init();
        
        SpringApplication.run(RuntimeBundleApplication.class, args);
    }
       
} 