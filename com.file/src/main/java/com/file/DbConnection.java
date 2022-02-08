package com.file;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DbConnection {
	
	Configuration configuration;
	
	
	SessionFactory sessionFactory;
	
	public   void makeConnection() {
		
		
		 configuration = new Configuration().configure();
		
		 configuration.addAnnotatedClass(com.file.model.FileInfo.class);
		 sessionFactory = configuration.buildSessionFactory();
		
		
	}
	
	public  Session getSession() {
		return sessionFactory.openSession();
	}

}
