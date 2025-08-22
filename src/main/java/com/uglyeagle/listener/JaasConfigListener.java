package com.uglyeagle.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class JaasConfigListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Get the absolute path of login.config inside WEB-INF
        String loginConfigPath = sce.getServletContext().getRealPath("/WEB-INF/login.config");
        System.setProperty("java.security.auth.login.config", loginConfigPath);
        System.out.println("JAAS login config set to: " + loginConfigPath);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Optional cleanup
        System.clearProperty("java.security.auth.login.config");
    }
}
