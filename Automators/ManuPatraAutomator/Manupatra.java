import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import javafx.application.Application;
//import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Manupatra extends Application {
	
	Stage window;
	Scene scene;
	String inspectText = "";
	
	public static void main(String[] args)  {
		launch(args);
	}

	@Override
	public void start(Stage arg0) throws Exception {
		window = arg0;
		window.setTitle("CreditCheck Partners Pvt. Ltd ©");
	
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.TOP_CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
		
		Text scenetitle = new Text("ManuPatra Automator");
		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		Tooltip.install(scenetitle, new Tooltip("Powered by Ayush"));
		grid.add(scenetitle, 0, 0, 2, 1);
		
		Label nameLabel = new Label("Enter HTML:                         \n(Second Line of Inspect Element)");
		HBox hBox = new HBox();
		hBox.getChildren().add(nameLabel);
		//grid.add(nameLabel, 1, 2);
		grid.add(hBox, 1, 2);
		
		TextArea textAreas[]=new TextArea[5];
	
		for(int i=0,rowNo=3;i<textAreas.length;i++,rowNo++) {
			textAreas[i]=new TextArea();
			textAreas[i].setMaxWidth(1200);
			textAreas[i].setMinWidth(1000);
			grid.add(textAreas[i], 1, rowNo);
		}
		
		Button getButton = new Button("Get Word Document");
		HBox searchButtonBox = new HBox();
		searchButtonBox.getChildren().add(getButton);
//		searchButtonBox.setAlignment(Pos.BOTTOM_RIGHT);
		grid.add(getButton, 1, 8);

		getButton.setOnAction(e -> {
			try {
				String fileName="";
				for(int start=0;!textAreas[start].getText().equals("");start++) {
					Document doc = Jsoup.parse(textAreas[start].getText());
					HashMap<Integer,String> firstRow = new HashMap<Integer,String>();//first,second,third column 
					Elements centralAlignText = doc.getElementsByClass("centeralign");	
					int info=0;
			 		for(Element ele : centralAlignText) {
//			 			System.out.println(info+" "+ele.text());
			 			firstRow.put(info , ele.text());
						info++;
					}
			 		info--;
					String testName="";
					for(int k=1;k<=info;k++) {
			 			if(firstRow.get(k).contains("Vs. ")) {
			 				int index = firstRow.get(k).indexOf("Vs.");
			 				testName = firstRow.get(k).substring(11, index).trim().replace(" ", "_");
			 			}	
					}
					if((start+1)<5&&!textAreas[start+1].getText().equals("")) {
						fileName+=testName+"_AND_";
					}else {
						fileName+=testName;
						break;
					}	
				}		
//				System.out.println("FinalName: "+fileName);
				XWPFDocument d = new XWPFDocument(this.getClass().getClassLoader().getResourceAsStream("BaseCase.docx")); 
				int totalNoOfTextAreas=getNoOfTextAreaFilled(textAreas),textAreaNumber;
//				System.out.println("TextAreas in all: "+totalNoOfTextAreas);
				for(textAreaNumber=0;textAreaNumber<textAreas.length;textAreaNumber++) {
					if(textAreas[textAreaNumber].getText().equals("")) {
						break;
					} else {	
						if(textAreaNumber==0) {
							XWPFTable changingTable = d.getTables().get(textAreaNumber);
							System.out.println("Table "+textAreaNumber+" "+changingTable);
							Info.PerformAction(d,textAreas[textAreaNumber].getText(),changingTable,textAreaNumber,totalNoOfTextAreas,fileName);					
							textAreas[textAreaNumber].setText("");
						} else {
							XWPFTable changingTable = d.getTables().get(textAreaNumber);
							System.out.println("Table "+textAreaNumber+" "+changingTable);
							Info.PerformAction(d,textAreas[textAreaNumber].getText(),changingTable,textAreaNumber,totalNoOfTextAreas,fileName);					
							textAreas[textAreaNumber].setText("");
						}
					}
				}
			}catch (Exception exc) {
				exc.printStackTrace();
			}});
		scene = new Scene(grid,1100,550);
		window.setScene(scene);
		window.setTitle("CreditCheck Partners Pvt. Ltd ©") ;
		window.show();
	}
	
	public static int getNoOfTextAreaFilled(TextArea textBoxes[]) {
		int count=0;
		for(int i=0;i<textBoxes.length;i++) {
			if(textBoxes[i].getText().equals("")) {
				break;
			}
			else {
				count++;
			}
		}
		return count;
	}
}