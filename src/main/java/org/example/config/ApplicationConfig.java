package org.example.config;

import javax.faces.annotation.FacesConfig;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@FacesConfig
@WebListener
public class ApplicationConfig implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent event) { }
}
