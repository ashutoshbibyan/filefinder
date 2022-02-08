package com.file.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.apache.derby.tools.sysinfo;
import org.hibernate.internal.build.AllowSysOut;

import com.file.model.FileInfo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ShowDuplicateController {
	
	@FXML
	TableView<FileInfo> tblDuplicateFiles;
	
	
	@FXML
	TableColumn<FileInfo,String> tblcolFileName;
	@FXML
    TableColumn<FileInfo, String> tblcolSize;
	@FXML
	TableColumn<FileInfo,String> tblcolPath;
	
	@FXML
	ImageView imgvPreview;
	
	
	
	
	
	



	public void fileSeleted(MouseEvent event) {
		FileInfo selectedFile = tblDuplicateFiles.getSelectionModel().getSelectedItem();
		
		try {
			String type = Files.probeContentType(Paths.get(selectedFile.getPath()));
			
			if(type.contains("image")) {
				File f = new File(selectedFile.getPath());
		        Image image = new Image(f.toURI().toString());
		        
		        
				imgvPreview.setImage(image);
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	


   @FXML
   public void deleteSelected(ActionEvent event) {
	   
	   ExecutorService exService = Executors.newSingleThreadExecutor();
	   
	   
	   System.out.println("delete files ...");
	   
	   exService.submit(new Runnable() {
		
		@Override
		public void run() {
			
			tblDuplicateFiles.getItems().iterator().forEachRemaining((f)->{
				try {			  
					System.out.println("delete");
					boolean isDeleted = Files.deleteIfExists(Paths.get(f.getPath()));
					System.out.println("file deleted " + isDeleted + f.getPath());
					if(isDeleted) {
						tblDuplicateFiles.getItems().remove(f);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					
					e.printStackTrace();
				}
			});
			
		}
	});
	   
	   exService.shutdown();
   }
   
   @FXML
   public void cancel(ActionEvent event) {
	   
	   Node node = (Node) event.getSource();
	   Scene scene = node.getScene();
	   Stage stage = (Stage) scene.getWindow();
	   stage.close();
   }
	
	public void printTable(List<FileInfo> filesToBeDeleted) {
		

		 tblcolFileName.setCellValueFactory(new PropertyValueFactory<FileInfo, String>("fileName"));
		 tblcolPath.setCellValueFactory(new PropertyValueFactory<FileInfo, String>("path"));
		 tblcolSize.setCellValueFactory(new PropertyValueFactory<FileInfo, String>("size"));
		 
		 tblDuplicateFiles.getItems().addAll(filesToBeDeleted);
		
		
	}




	
		
		
		
		 
		 
	




	
}
