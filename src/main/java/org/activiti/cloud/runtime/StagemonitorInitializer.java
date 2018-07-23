package org.activiti.cloud.runtime;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.stereotype.Component;
import org.stagemonitor.web.servlet.initializer.ServletContainerInitializerUtil;

@Component
public class StagemonitorInitializer implements ServletContextInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        // necessary for spring boot 2.0.0 until stagemonitor supports it natively
        ServletContainerInitializerUtil.registerStagemonitorServletContainerInitializers(servletContext);
    }
}