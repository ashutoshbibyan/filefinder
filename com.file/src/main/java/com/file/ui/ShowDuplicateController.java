package com.file.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.derby.tools.sysinfo;

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
	TableColumn<FileInfo, String> tblcolStatus;
	@FXML
	TableColumn<FileInfo,String> tblcolFileName;
	@FXML
    TableColumn<FileInfo, String> tblcolSize;
	@FXML
	TableColumn<FileInfo,String> tblcolPath;
	
	@FXML
	ImageView imgvPreview;
	
	
	private List<FileInfo> filesToBeDeleted ;
	
	
	
	
	public List<FileInfo> getFilesToBeDeleted() {
		return filesToBeDeleted;
	}



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

	public void setFilesToBeDeleted(List<FileInfo> filesToBeDeleted) {
		this.filesToBeDeleted = filesToBeDeleted;
	}


   @FXML
   public void deleteSelected(ActionEvent event) {
	   
	   System.out.println("delete files ...");
	   
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
