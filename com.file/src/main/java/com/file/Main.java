package com.file;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.jaxb.internal.FileXmlSource;
import org.hibernate.cfg.Configuration;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;




public class Main extends Application{
	
	public static Configuration configuration ;	
		 
	public static SessionFactory sessionFactory ;
	
	
	public static void main(String[] args) {
		
		
		configuration = new Configuration().configure();		
		configuration.addAnnotatedClass(com.file.model.FileInfo.class);
		sessionFactory = configuration.buildSessionFactory();
		
		
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		// TODO Auto-generated method stub
		 
		    Parent root = FXMLLoader.load(getClass().getResource("ui/app.fxml"));
		   
		    
	        Scene scene = new Scene(root, 900, 600);
	    
	        stage.setTitle("FileFinder");
	        
	        stage.setScene(scene);
	        stage.show();
	}

}
