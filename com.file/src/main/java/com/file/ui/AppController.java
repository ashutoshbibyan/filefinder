package com.file.ui;


import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.derby.tools.sysinfo;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.file.DbConnection;
import com.file.Main;
import com.file.model.FileInfo;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

public class AppController {
	
	@FXML
    ListView<String> lvFolderList;
	
	@FXML
	TableView<FileInfo> tvDuplicateTable;
	
	@FXML
	TableColumn<FileInfo, CheckBox> tbcolSelect;
	
	@FXML
	TableColumn<FileInfo, String> tbcolFileName;
	
	@FXML
	TableColumn<FileInfo,String> tbcolSize;
	
	@FXML
	TableColumn<FileInfo, String> tbcolPath;
	
	@FXML
	Label lblFIleName;
	
	@FXML
	Label lblSize;
	
	@FXML
	Label lblPath;
	
	@FXML
	ProgressIndicator progress;
	
	@FXML
	Label lblLeftStatus;
	
	private long totalNoOfFiles = 0 ;
	private long noOfFileVisited = 0;
	
	private List<FileInfo> filesToBeDeleted = new ArrayList<FileInfo>();
	
	
	@FXML
	public void folderSelected(MouseEvent event) {
		
	}
	
	@FXML
	public void fileTableClicked(MouseEvent event) {
		
		System.out.println("file is clicked ");
		FileInfo fInfo = tvDuplicateTable.getSelectionModel().getSelectedItem();
		
		try {
			lblFIleName.setText(fInfo.getFileName());
			lblPath.setText(fInfo.getPath());
			
			// bytes to mb conversion 
			double size = fInfo.getSize()/1e+6;
			
			size = Math.round(size*100.0)/100.0;
			
			lblSize.setText(Double.toString(size)+"mb");
		} catch (NullPointerException e) {
			System.out.println("No row  is selected");
		}
	
		
		
	}
	
	
	
	
	@FXML
	public void selectFolder(ActionEvent event ) {
		DirectoryChooser dirChooser = new DirectoryChooser();
	    
		
		File f = dirChooser.showDialog(new Stage());
		
		
				
		lvFolderList.getItems().add(f.getPath());
		
		
		ExecutorService exService = Executors.newFixedThreadPool(2);
		
	    Future<Long> noOfFiles =	exService.submit(new Callable<Long>() {

			@Override
			public Long call() throws Exception {
				return Files.walk(Paths.get(f.getPath())).count();
			}
		});
	    
		   try {
			System.out.println("no of files " + noOfFiles.get());
			totalNoOfFiles=noOfFiles.get();
			
		} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
		   
		   catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   
		   
		   
		   System.out.println("search folders");
			
		   
			
			
			
			exService.submit(new Runnable() {
				
				@Override
				public void run() {
					
				
					Session session = Main.sessionFactory.openSession();
						
						try {
							Files.walkFileTree(Paths.get(f.getPath()),new FileVisitor<Path>() {

								@Override
								public FileVisitResult postVisitDirectory(Path arg0, IOException arg1) throws IOException {
									 
									++noOfFileVisited;
									double progressPercent = (float)noOfFileVisited/totalNoOfFiles;
									progress.setProgress(progressPercent);


									return FileVisitResult.CONTINUE;
								}

								@Override
								public FileVisitResult preVisitDirectory(Path arg0, BasicFileAttributes arg1)
										throws IOException {
									// TODO Auto-generated method stub
									return FileVisitResult.CONTINUE;
								}

								@Override
								public FileVisitResult visitFile(Path arg0, BasicFileAttributes arg1) throws IOException {
									// TODO Auto-generated method stub
									if(!arg1.isDirectory()) {
										
										File f = arg0.toFile();
										
										FileInfo fInfo = new FileInfo();
										fInfo.setFileName(f.getName());
										fInfo.setPath(f.getPath());
										fInfo.setSize(arg1.size());
										
										
										
										
										
										
										Transaction transaction = session.beginTransaction();
										
										System.out.println(fInfo);
										session.save(fInfo);
										
										transaction.commit();
										
										
							
										lvFolderList.getItems().add(fInfo.getPath());
										
										
										
										++noOfFileVisited;
										
										//System.out.println("file visited"+noOfFileVisited);
										
										double progressPercent = (float)noOfFileVisited/totalNoOfFiles;
										
										//System.out.println("progress " + progressPercent);
											
										progress.setProgress(progressPercent);

										//System.out.println("total files "+totalNoOfFiles);
										
										
									}
									
									
									return FileVisitResult.CONTINUE;
								}

								@Override
								public FileVisitResult visitFileFailed(Path arg0, IOException arg1) throws IOException {
									// TODO Auto-generated method stub
									
									return FileVisitResult.CONTINUE;
								}
							});
							
							session.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					exService.shutdown();	
					
				}
				
				
			});
			
			noOfFileVisited=0;
		
	}
	
	
	
	@FXML
	public void searchFolders(ActionEvent event) {
		
		
		
	}
	
	@FXML
	public void showDuplicate(ActionEvent event) {
		
		ExecutorService executorService  = Executors.newSingleThreadExecutor();
		
		executorService.submit(new Runnable() {
			
			@Override
			public void run() {
				System.out.println("show Duplicate is called ");
				
				Session session = Main.sessionFactory.openSession();
				Transaction transaction = session.beginTransaction();
				
				SQLQuery<FileInfo> query = session.createSQLQuery("SELECT a.*\n"
						+ "FROM FileInfo a\n"
						+ "JOIN (SELECT fileName, size, COUNT(*)\n"
						+ "FROM FileInfo \n"
						+ "GROUP BY fileName, size\n"
						+ "HAVING count(*) > 1 ) b\n"
						+ "ON a.fileName = b.fileName\n"
						+ "AND a.size = b.size\n"
						+ "ORDER BY a.fileName");
				
				
				query.addEntity(FileInfo.class);
				
				List<FileInfo> listOfFiles = query.list();
				
				transaction.commit();
				
				session.close();
				
				
				tbcolSelect.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<FileInfo,CheckBox>, ObservableValue<CheckBox>>() {
					
					@Override
					public ObservableValue<CheckBox> call(CellDataFeatures<FileInfo, CheckBox> param) {
						// TODO Auto-generated method stub
						CheckBox checkBox = new CheckBox();
						
						checkBox.selectedProperty().set(false);
						
						checkBox.selectedProperty().addListener(new ChangeListener<>() {

							@Override
							public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
									Boolean newValue) {
								System.out.println("old value " + oldValue);
								System.out.println("new Value "+ newValue);
								if(newValue) {
									filesToBeDeleted.add(param.getValue());
								}
								else {
									filesToBeDeleted.remove(param.getValue());
								}
								System.out.println(param.getValue().getPath());
								
							}
						});
						
						return new SimpleObjectProperty<CheckBox>(checkBox);
					}
				});	
					
				
				 tbcolFileName.setCellValueFactory(new PropertyValueFactory<FileInfo, String>("fileName"));
				 tbcolPath.setCellValueFactory(new PropertyValueFactory<FileInfo, String>("path"));
				 tbcolSize.setCellValueFactory(new PropertyValueFactory<FileInfo, String>("size"));
				
				listOfFiles.forEach((fInfo)->{
					 
					tvDuplicateTable.getItems().add(fInfo);
				});
			}
		});
		
		executorService.shutdown();
		
		
	}
	
	@FXML
	public void deleteSelected(ActionEvent event)  {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("show_result.fxml"));
			
			Parent root = loader.load();
			
			ShowDuplicateController showDuplicateController =  loader.getController();
			
			if(filesToBeDeleted.size()>0) {
				showDuplicateController.printTable(filesToBeDeleted);
			}
			
			
			
			Stage stage = new Stage();
			
			Scene scene = new Scene(root);	
			
			
			stage.setScene(scene);
			stage.show();
			
			
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
}
