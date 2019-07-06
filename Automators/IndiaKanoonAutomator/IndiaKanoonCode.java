import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
//import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

@SuppressWarnings("restriction")
public class IndiaKanoonData extends Application {

	int noOfRecords=0;
	Stage window;
	Scene scene;
	String inspectText = "";
	
	String caseData[]=new String[50];
	String petitionerNames[]=new String[50];
	String respondentNames[]=new String[50];
	String backgroundText[]=new String[50];	
	
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
		
		Text scenetitle = new Text("IndiaKanoon Automator");
		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		grid.add(scenetitle, 0, 0, 2, 1);

		Label NameLabel = new Label("Name of the file :   ");
		javafx.scene.control.TextField nameOfFile=new javafx.scene.control.TextField();
		HBox NameHBox=new HBox();
		NameHBox.getChildren().addAll(NameLabel,nameOfFile);
		grid.add(NameHBox, 1, 5);
		
		Label caseLabel = new Label("Case//Tribunal//Date :   ");
		grid.add(caseLabel, 1, 6);
		
		TextArea metaData=new TextArea();
		metaData.setPrefHeight(50);
		grid.add(metaData, 1, 7);
		
		Label petitionerLabel = new Label("PETITIONER :     ");
		TextArea petitionerTextField=new TextArea();
		grid.add(petitionerLabel,1,8);
		grid.add(petitionerTextField, 1, 9);
		petitionerTextField.setPrefHeight(60);
		
		Label nameLabel = new Label("RESPONDENT :    ");
		TextArea respondentTextField=new TextArea();
		grid.add(nameLabel,1,10);
		grid.add(respondentTextField, 1, 11);
		respondentTextField.setPrefHeight(70);
		
		Label nameLabel3 = new Label("Enter Text");
		HBox hBox3 = new HBox();
		hBox3.getChildren().add(nameLabel3);
		grid.add(hBox3, 1, 12);
		
		TextArea textArea=new TextArea();
		grid.add(textArea, 1, 13);
		textArea.setPrefHeight(100);
		
		Button getButton = new Button("Get Word Document");
		HBox searchButtonBox = new HBox();
		searchButtonBox.setAlignment(Pos.CENTER);
		searchButtonBox.getChildren().add(getButton);
		grid.add(getButton, 1, 16);
		
		HBox hbox = new HBox(15);
		Button submitButton = new Button("Submit Data");		
//		Button nextButton = new Button("Next");
		hbox.getChildren().addAll(submitButton);
//		grid.add(hbox,2,15);
		
		HBox hBox = new HBox(15);
		Button nextButton = new Button("Next");
		hBox.getChildren().addAll(nextButton);
		grid.add(hBox,2,16);
		
		submitButton.setOnAction(e ->{
			caseData[noOfRecords]=metaData.getText();
//			System.out.println("CaseData: "+metaData.getText());			
			
			petitionerNames[noOfRecords]=petitionerTextField.getText();
//			System.out.println("PName "+petitionerTextField.getText());
			
			respondentNames[noOfRecords]=respondentTextField.getText();
//			System.out.println("RName "+respondentTextField.getText());
			
			backgroundText[noOfRecords]=textArea.getText();
//			System.out.println("Btext "+textArea.getText());	
		});
		
		nextButton.setOnAction(e ->{
			submitButton.fire();
			metaData.setText("");
			petitionerTextField.setText("");
			respondentTextField.setText("");
			textArea.setText("");
			noOfRecords++;
		});
						
		getButton.setOnAction(e -> {
			submitButton.fire();
//			System.out.println("Entered Get Word");
			XWPFDocument doc=null;
		
			try {
				doc = new XWPFDocument(this.getClass().getClassLoader().getResourceAsStream("BaseCase.docx"));
			} catch (IOException e1) {
				e1.printStackTrace();
			} 
			
			for(int i=0;i<=noOfRecords;i++) {
//				System.out.println("Iteration No: "+i);
				XWPFTable table=null;
				if(i<10) {
					table = doc.getTables().get(i);
				}else {
					break;
				}
				if(caseData[i] != null && !caseData[i].isEmpty()) {
					String data[]=caseData[i].split("//");
					XWPFParagraph para0=table.getRow(1).getCell(0).getParagraphs().get(0);
					para0.setAlignment(ParagraphAlignment.BOTH);
					XWPFRun run0=para0.createRun();
					if(data.length>0) {
						if(!data[0].equals("")) {
							run0.setText(data[0]);
							run0.addBreak();
//							System.out.println("Case: "+data[0]);
						}
						if(data.length>1&&!data[1].equals("")) {
							run0.setText("/ "+toOnlyFirstLetterCapital(data[1]));
							run0.addBreak();
//							System.out.println("Tribunal: "+toOnlyFirstLetterCapital(data[1]));
						}
						if(data.length>2&&!data[2].equals("")) {
							run0.setText("/"+correctingDate(data[2]));
//							System.out.println("Date: "+correctingDate(data[2]));
						}
					}
				}
				
				//Second Column-->Petitioner
				String Petitioner=petitionerNames[i];
				String Respondent=respondentNames[i];
				if(Petitioner != null && !Petitioner.isEmpty()) {
					XWPFParagraph para1 = table.getRow(1).getCell(1).getParagraphs().get(0);
				//	para1.setAlignment(ParagraphAlignment.BOTH);
					XWPFRun run1 = para1.createRun();
					Petitioner=changeInAppellants(Petitioner);
					run1.setText(Petitioner);
				}
				
				//Third Column-->Respondent
				if(Respondent!= null && !Respondent.isEmpty()) {
					XWPFParagraph para2 = table.getRow(1).getCell(2).getParagraphs().get(0);
		//			para2.setAlignment(ParagraphAlignment.BOTH);
					XWPFRun run2 = para2.createRun();
					Respondent=changeInRespondent(Respondent);
					run2.setText(Respondent);
				}
				
				XWPFParagraph para20 = table.getRow(2).getCell(0).getParagraphs().get(1);
				para20.setAlignment(ParagraphAlignment.BOTH);
				XWPFRun run20 = para20.createRun();
				run20.addBreak();
				run20.addBreak();
				
				String a=backgroundText[i];
				
				if(a!= null && !a.isEmpty()) {	
					String s[]=a.split("\n");
					outer:for(int start=0;start<s.length;start++) {
						System.out.println("Before Enter");
						getProperText(s[start]);
						s[start]=s[start].trim();
						if(start<s.length-1) {
							//System.out.println((start+1)+" "+s[start+1].equals(""));
							if(s[start+1].equals("")==false) {
								run20.setText("");
								run20.addBreak();
							}
						}	
						if(s[start].equals("")) {
							continue outer;
						}	
						if(s[start].matches("^((\\d)+\\.).*$")) {
							int indexOfDot=s[start].indexOf(".");
							if(s[start].charAt(indexOfDot+1)!=' ') {
								s[start]=s[start].substring(indexOfDot+1);						
							}else {
								s[start]=s[start].substring(s[start].indexOf(" ")+1);
							}
						}
						if(start>=s.length-3) {
							s[start] = s[start].replace(" I "," The Judge ");
							if(s[start].startsWith("I ")) {
								s[start] = s[start].replace("I ","The Judge ");
							}
						}
						if(s[start].length()>0) {
							s[start]=removeLetters(s[start]);
							s[start]=removeRomanNumerals(s[start]);
							s[start]=removeNumbersEnclosedInParanthesis(s[start]);
							s[start]=removeNumbersWithDot(s[start]);
							s[start]=replaceRemaining(s[start]);
							s[start]=correctingDateDifferent(s[start]);
							s[start]=correctingDate(s[start]);
							s[start]=getCorrectAmount(s[start]);
							s[start]=toFirstLetterCapital(s[start]);
							run20.setText(s[start]);
							run20.addBreak();	
						}	
					}	
				}
				}
			try {
				writeFile(nameOfFile.getText(), doc);
				metaData.setText("");
				petitionerTextField.setText("");
				respondentTextField.setText("");
				textArea.setText("");
				nameOfFile.setText("");
				noOfRecords=0;
			} catch (IOException e1) {
				e1.printStackTrace();
			}
				
		});
		scene = new Scene(grid,750,650);
		window.setScene(scene);
		window.show();
	}
	
	public static void getProperText(String f) {
		/*
		 Brief facts of the case are that the assessee is a company engaged
		  in the business of construction and sale of flats filed its return of income on 30.09.2009 admitting 
		  a total income of Rs. 59,68,667. The return was processed u/s 143(1) of IT Act, and the case was selected 
		  for scrutiny u/s 143(2) and the ITA No. 959/Hyd/2013 M/s. Aditya Housing & Infrastructure Development Corporation
		   Pvt. Ltd. */
//		System.out.println("After Entering...");
		String a[]=f.split(" ");
		for(int i=0;i<a.length;i++) {
			System.out.println(i+" "+a[i]);
		}
		System.out.println();
	}
	
	private static void writeFile(String individualName,XWPFDocument doc) throws IOException {
		if(individualName.contains(" ")) {
			String s[]=individualName.split(" ");
			individualName="";
			for(int len=0;len<s.length;len++) {
				if(len==s.length-1) {
					individualName+=s[len];
				}else {
					individualName+=s[len]+"_";
				}
			}
		}
		String outFileName = "IndiaKanoon_Case_Output/";
		File file = new File(outFileName);
		if(!file.exists())
			file.mkdirs();
		FileOutputStream outputStream;
		String finalOutputFileName = "";
		finalOutputFileName+=outFileName+individualName;
		if(finalOutputFileName.endsWith(".")) {
			finalOutputFileName=finalOutputFileName.substring(0,finalOutputFileName.length()-1);
		}
		finalOutputFileName+=".docx";
		try {
			outputStream = new FileOutputStream(new File(finalOutputFileName));
			doc.write(outputStream);
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		System.out.println("FileName: "+finalOutputFileName);
		popUpWordDoc(finalOutputFileName);
	}
	
	public static void popUpWordDoc(String outputFile) throws IOException {	
		Desktop.getDesktop().open(new File(outputFile));
	}
	
	public static String toOnlyFirstLetterCapital(String s) {
		String returnString="";
		String a[]=s.split(" ");
		for(int i=0;i<a.length;i++) {
			String temp=Character.toUpperCase(a[i].charAt(0))+a[i].substring(1).toLowerCase();
			a[i]=temp;
		}
		for(String e:a) {
			returnString+=e+" ";
		}
		return returnString.trim();
	}
	
	public static String correctingDate(String t) {	
		
		String returnString="";
		String a[]=t.split(" ");
		
		outer: for(int i=0;i<a.length;i++) {
					if(a[i].contains(".")&&countDot(a[i])>=2) {
						int first=a[i].indexOf(".");
						int second=a[i].indexOf(".", first+1);
						String inBetween=a[i].substring(first+1, second);
						for(int j=0;j<inBetween.length();j++) {
							if(!Character.isDigit(inBetween.charAt(j))) {
								continue outer;
							}
						}
						inBetween=getCorrectFormatOfDate(inBetween);
						if(countYear(a[i].substring(second+1))){
							if(!a[i].substring(second+5).equals("")) {
								a[i] = a[i].substring(0, first)+" "+inBetween+" "+a[i].substring(second+1,second+5)+a[i].substring(second+5); 								
							}else {
							a[i] = a[i].substring(0, first)+" "+inBetween+" "+a[i].substring(second+1,second+5); 
							}
						}
					}else if(a[i].contains("-")&&countDash(a[i])>=2) {
						int first=a[i].indexOf("-");
						int second=a[i].indexOf("-", first+1);
						String inBetween=a[i].substring(first+1, second);
						for(int j=0;j<inBetween.length();j++) {
							if(!Character.isDigit(inBetween.charAt(j))) {
								continue outer;
							}
						}
						inBetween=getCorrectFormatOfDate(inBetween);
						if(countYear(a[i].substring(second+1))){
							if(!a[i].substring(second+5).equals("")) {
								a[i] = a[i].substring(0, first)+" "+inBetween+" "+a[i].substring(second+1,second+5)+a[i].substring(second+5); 								
							}else {
							a[i] = a[i].substring(0, first)+" "+inBetween+" "+a[i].substring(second+1,second+5); 
							}
						}
					}
				}
			for(int i=0;i<a.length;i++) {
				returnString+=a[i]+" ";
			}
			returnString=returnString.trim();
			return returnString;
	}
	
	public static boolean countYear(String s) {
		
		if(s.length()>4) {
			for(int i=0;i<=3;i++) {
				if(Character.isDigit(s.charAt(i))) {
					continue;
				}
				else {
					return false;
				}
			}
		}else if(s.length()==4) {
			for(int i=0;i<=3;i++) {
				if(Character.isDigit(s.charAt(i))) {
					continue;
				}
				else {
					return false;
				}
			}
		}else {
			return false;
		}
		return true;
	}
	
	public static int countDot(String s) {
		int count=0;
		for(int i=0;i<s.length();i++) {
			if(s.charAt(i)=='.') {
				count++;
			}
		}
		return count;
	}
	
	public static int countDash(String s) {
		int count=0;
		for(int i=0;i<s.length();i++) {
			if(s.charAt(i)=='-') {
				count++;
			}
		}
		return count;
	}

	public static String getCorrectFormatOfDate(String midDate) {
		midDate=midDate.trim();
		if(midDate.startsWith("0")) {
			midDate = midDate.replace("0", "");
		}
		if( midDate.equals("1")) {
			midDate = midDate.replace("1", "January");
		}
		else if(  midDate.equals("2")) {
			midDate = midDate.replace("2", "February");
		}
		else if( midDate.equals("3")) {
			midDate = midDate.replace("3", "March");
		}
		else if(  midDate.equals("4")) {
			
			midDate = midDate.replace("4", "April");
		}
		else if( midDate.equals("5")) {
		
			midDate = midDate.replace("5", "May");
		}
		else if( midDate.equals("6")) {
			
			midDate = midDate.replace("6", "June");
		}
		else if( midDate.equals("7")) {
			midDate = midDate.replace("7", "July");
		}
		else if( midDate.equals("8")) {
			midDate = midDate.replace("8", "August");
		}
		else if( midDate.equals("9")) {
			midDate = midDate.replace("9", "September");
		}
		else if(midDate.equals("10")) {
			midDate = midDate.replace("10", "October");
		}
		else if(midDate.equals("11")) {
			midDate = midDate.replace("11", "November");
		}
		else if(midDate.equals("12")) {
			midDate = midDate.replace("12", "December");
		}
		return midDate;			
	}
		
	public static String correctingDateDifferent(String t) {
		String returnString="";
		String a[]=t.split(" ");
		outer: for(int i=0;i<a.length;i++) {
					if(a[i].contains(".")&&countDot(a[i])>=2) {
						int first=a[i].indexOf(".");
						int second=a[i].indexOf(".", first+1);
						String inBetween=a[i].substring(first+1, second);
						if(inBetween.length()>0) {
							for(int j=0;j<inBetween.length();j++) {
								if(!Character.isDigit(inBetween.charAt(j))) {
									continue outer;
								}
							}
							inBetween=getCorrectFormatOfDate(inBetween);
							int myValue=differentFormat(a[i].substring(second+1));
							if(myValue==2){
								a[i] = a[i].substring(0, first)+" "+inBetween+" "+"20"+a[i].substring(second+1);
							}
						}
					}
					else if(a[i].contains("-")&&countDash(a[i])>=2) {
						int first=a[i].indexOf("-");
						int second=a[i].indexOf("-", first+1);
						String inBetween=a[i].substring(first+1, second);
						for(int j=0;j<inBetween.length();j++) {
							if(!Character.isDigit(inBetween.charAt(j))) {
								continue outer;
							}
						}
					}
				}
			for(int i=0;i<a.length;i++) {
				returnString+=a[i]+" ";
			}
			returnString=returnString.trim();
			return returnString;
	}
	
	public static int differentFormat(String s) {
		int count=0;
		for(int i=0;i<s.length();i++) {
			if(Character.isDigit(s.charAt(i))) {
				count++;
			}else {
				break;
			}
		}
		return count;
	}

	public static String replaceRemaining(String t) {
		t = t.replace(" has "," had ");
		t = t.replace(" has been "," had ");
		t = t.replace(" is ", " was ");
		t = t.replace(" is,"," was,");
		t = t.replace(" are "," were ");
		t = t.replace(" Shri "," ");
		t = t.replace("Shri "," ");
		t = t.replace("Shri","");
		t = t.replace(" Mr. "," ");
		t = t.replace(" Mr."," ");
		t = t.replace("Mr. "," ");
		t = t.replace("Mr.","");
		t = t.replace("M/S.","");
		t = t.replace(" M/s.","");
		t = t.replace("M/s ","");
		t = t.replace("M/s. ","");
		t = t.replace(" Mrs.","");
		t = t.replace("Mrs. ","");
		t = t.replace("Mrs.","");
		t = t.replace("/-", "");
		t = t.replace(" Rs. ", " INR ");
		t = t.replace("Rs.", "INR ");
		t = t.replace("Rs", "INR");
		t = t.replace(" Rupees ", " INR ");
		t = t.replace("Rupees ", "INR ");
		t = t.replace("Rupees", " INR ");
		t = t.replace(" rupees ", " INR ");
		t = t.replace("rupees ", "INR ");
		t = t.replace("rupees", " INR ");
		t = t.replace(" sq.ft. ", " square feet ");
		t = t.replace(" sq.mt ", " sqare metre ");
		t = t.replace(" sq.mt.", " sqare metre");
		t = t.replace("(P) Ltd.", "Private Limited");
		t = t.replace(" P. Ltd. ", " Private Limited ");
		t = t.replace(" Pvt Ltd ", "Private Limited");
		t = t.replace(" Pvt. Ltd.", " Private Limited");
		t = t.replace(" Pvt.Ltd. ", "Private Limited");
		t = t.replace("Pvt.", "Private");
		t = t.replace("Pvt", "Private");
		t = t.replace(" Ltd.", " Limited");
		t = t.replace(" Ltd. ", " Limited ");
		t = t.replace("Ltd.", "Limited");
		t = t.replace("Ltd", " Limited ");
		t = t.replace("PVT.", "PRIVATE");
		t = t.replace("PVT", "PRIVATE");
		t = t.replace("Anr.", "Another");
		t = t.replace("Ors.", "Others");
		t = t.replace("&", "and");
		t = t.replace("Â© Manupatra Information Solutions Pvt. Ltd.", "");
		return t;
	}
	
	public static String changeInAppellants(String s) {
		s=s.trim();
		s = s.replace(" M/S. ","");
		s = s.replace("M/S ","");
		s = s.replace("M/S. ","");
		s = s.replace("M/S.","");
		s = s.replace("M/s.","");
		s=s.replace("P. Ltd.", "Private Limited");
		s=s.replace("Dy.", "Deputy");
		s=s.replace("Pvt.Ltd.", "Private Limited");
		s=s.replace("Pvt. Ltd.", "Private Limited");
		s=s.replace("Pvt.", "Private");
		s=s.replace("Pvt", "Private");
		s=s.replace("PVT.", "PRIVATE");
		s=s.replace("PVT", "PRIVATE");
		s=s.replace("Ltd.", "Limited");
		s=s.replace("Ltd", " Limited ");
		s=s.replace("ltd.", "limited");
		s=s.replace("LTD.", "LIMITED");
		s=s.replace("LTD", "LIMITED");
		s=s.replace("Cus. & C. Ex.", "Custom and Central Excise");
		s=s.replace("&", "and");
		s=s.replace("ITAT", "Income Tax Appellate Tribunal");
		s=s.replace("Anr.", "Another");
		s=s.replace("Ors.", "Others");
		return s;
	}
		
	public static String changeInRespondent(String s) {
		s=s.trim();
		if(s.matches("^((\\d)+\\.).*$")) {
			int indexOfDot=s.indexOf(".");
			if(s.charAt(indexOfDot+1)!=' ') {
				s=s.substring(indexOfDot+1);						
			}else {
				s=s.substring(s.indexOf(" ")+1);
			}
		}
		s = s.replace(" M/S. ","");
		s = s.replace("M/S ","");
		s = s.replace("M/S. ","");
		s = s.replace("M/S.","");
		s = s.replace("M/s.","");
		s=s.replace(" Ors. ", " Others ");
		s=s.replace(" Ors.", " Others");
		s=s.replace("P. Ltd.", "Private Limited");
		s=s.replace("Dy.", "Deputy");
		s=s.replace("Pvt.Ltd. ", "Private Limited ");
		s=s.replace("Pvt. Ltd.", "Private Limited");
		s=s.replace("Pvt.", "Private");
		s=s.replace("Pvt", "Private");
		s=s.replace("PVT.", "Private");
		s=s.replace("PVT", "Private");
		s=s.replace("Ltd","Limited");
		s=s.replace("Ltd.", "Limited");
		s=s.replace("ltd.", "limited");
		s=s.replace("LTD.", "LIMITED");
		s=s.replace("LTD", "LIMITED");
		s=s.replace("Cus. & C. Ex.", "Custom and Central Excise");
		s=s.replace("&", "and");
		s=s.replace("ITAT", "Income Tax Appellate Tribunal");
		s=s.replace("Itat", "Income Tax Appellate Tribunal");
		s=s.replace("Anr.", "Another");
		s=s.replace("Ors.", "Others");
		return s;
	}

	public static String toFirstLetterCapital(String s) {
		if(s.length()>0) {
			if(s.charAt(0)>='a'&&s.charAt(0)<='z') {
				s=Character.toUpperCase(s.charAt(0))+s.substring(1);
			}
		}
		return s;
	}
	
	public static String removeNumbersWithDot(String s) {
		//Of the form "1. <text>..
		boolean inBetween = true;
		if(s.startsWith("\"")) {						
			int getIndexOfDot = s.indexOf(".");
			if(getIndexOfDot!=-1) {
				String inBetweenNumber = s.substring(1, getIndexOfDot);
				for(int len = 0;len<inBetweenNumber.length();len++) {
					if(!Character.isDigit(inBetweenNumber.charAt(len))) {
						inBetween = false;
						break;
					}
				}
				if(inBetween) {
					if(s.charAt(getIndexOfDot+1)!=' ') {
						s=s.substring(getIndexOfDot+1);
					}else {
						int indexOfSpace=s.indexOf(" ");
						s=s.substring(indexOfSpace+1);
					}
				}	
			}
		}
		return s;
	}
	
	public static String removeNumbersEnclosedInParanthesis(String s) {
		//Of the form "(1) <text>...
		int flag=1;
		if(s.startsWith("\"")) {
			if(s.charAt(1)=='(') {
				//"(1)" ...
				int end=s.indexOf(")");
				String inBetween=s.substring(2,end);
				for(int i=0;i<inBetween.length();i++) {
					if(!Character.isDigit(inBetween.charAt(i))) {
						flag=0;
						break;
					}
				}
				if(flag==1) {
					int indexOfSpace=s.indexOf(" ");
					s=s.substring(indexOfSpace+1);
				}
			}else {
				//"1) ...
				int end=s.indexOf(")");
				if(end==-1) {
					return s;
				}
				String inBetween=s.substring(1,end);
				for(int i=0;i<inBetween.length();i++) {
					if(!Character.isDigit(inBetween.charAt(i))) {
						flag=0;
						break;
					}
				}
				if(flag==1) {
					int indexOfSpace=s.indexOf(" ");
					s=s.substring(indexOfSpace+1);
				}
			}
		}else if(s.startsWith("(")) {
			//(1) <text>..
			int getIndexOfClosing=s.indexOf(")");
			String inBetween=s.substring(1,getIndexOfClosing);
			for(int i=0;i<inBetween.length();i++) {
				if(!Character.isDigit(inBetween.charAt(i))) {
					flag=0;
					break;
				}
			}
			if(flag==1) {
				int indexOfSpace=s.indexOf(" ");
				s=s.substring(indexOfSpace+1);
			}
		}else {
			//1)
			int indexOfClosing=s.indexOf(")");
			if(indexOfClosing==-1) {
				return s;
			}
			String inBetween=s.substring(0,indexOfClosing);
			for(int i=0;i<inBetween.length();i++) {
				if(!Character.isDigit(inBetween.charAt(i))) {
					flag=0;
					break;
				}
			}
			if(flag==1) {
				int indexOfSpace=s.indexOf(" ");
				s=s.substring(indexOfSpace+1);
			}
		}
		return s;
	}
	
	public static String removeLetters(String s) {
		int flag=1;
		if(s.startsWith("\"")) {
			if(s.charAt(1)=='(') {
				//Of the form "(a) <text>
				int end=s.indexOf(")");
				//MARK
				String inBetween=s.substring(2,end);
				if(inBetween.length()<=2) {
					for(int i=0;i<inBetween.length();i++) {
						if(!Character.isLetter(inBetween.charAt(i))) {
							flag=0;
							break;
						}
					}
					if(flag==1) {
						int indexOfSpace=s.indexOf(" ");
						s=s.substring(indexOfSpace+1);
					}
				}
			}else{
				//Of the form--->"a) <text>..
				//System.out.println("Entered");
				int getIndexOfClosing=s.indexOf(")");
				if(getIndexOfClosing==-1) {
					return s;
				}
				String betweenText=s.substring(1, getIndexOfClosing);
//				System.out.println(betweenText);
				if(betweenText.length()<=2) {
					for(int i=0;i<betweenText.length();i++) {
						if(!Character.isLetter(betweenText.charAt(i))) {
							flag=0;
							break;
						}
					}
					if(flag==1) {
						int indexOfSpace=s.indexOf(" ");
						s=s.substring(indexOfSpace+1);
					}
				}
			}
		}else if(s.startsWith("(")) {
			//(a) <text>..
			int getIndexOfClosing=s.indexOf(")");
			if(getIndexOfClosing==-1) {
				return s;
			}
			String betweenText=s.substring(1, getIndexOfClosing);
			if(betweenText.length()<=2) {
				for(int i=0;i<betweenText.length();i++) {
					if(!Character.isLetter(betweenText.charAt(i))) {
						flag=0;
						break;
					}
				}
				if(flag==1) {
					int indexOfSpace=s.indexOf(" ");
					s=s.substring(indexOfSpace+1);
				}
			}
		}else{
			//a) <text>..
			int getIndexOfClosing=s.indexOf(")");
			if(getIndexOfClosing==-1) {
				return s;
			}
			String inBetween=s.substring(0,getIndexOfClosing);
			if(inBetween.length()<=2) {
				for(int i=0;i<inBetween.length();i++) {
					if(!Character.isLetter(inBetween.charAt(i))) {
						flag=0;
						break;
					}
				}
				if(flag==1) {
					int indexOfSpace=s.indexOf(" ");
					s=s.substring(indexOfSpace+1);
				}
			}
		}
		return s;
	}
		
	public static String removeRomanNumerals(String s) {
		if(s.startsWith("\"")) {
			//"(i) <text>..."
			int indexOfOpening=s.indexOf("(");	
			if(indexOfOpening==-1) {
				//"i) <text>..."
				int indexOfClosing=s.indexOf(")");
				if(indexOfClosing==-1) {
					return s;
				}
				String inBetween=s.substring(1,indexOfClosing);
//				System.out.println("In Between RomanNumerals: "+inBetween);
				if(inBetween.toUpperCase().matches("[IVXLCDM]+")) {
					int indexOfSpace=s.indexOf(" ");
					s=s.substring(indexOfSpace+1);
					return s;
				}
			}
			if(indexOfOpening==1) {
				int indexOfClosing=s.indexOf(")");
				if(indexOfClosing==-1) {
					return s;
				}
				String inBetween=s.substring(2,indexOfClosing);
//				System.out.println("In Between RomanNumerals: "+inBetween);
				if(inBetween.toUpperCase().matches("[IVXLCDM]+")) {
					int indexOfSpace=s.indexOf(" ");
					s=s.substring(indexOfSpace+1);
					return s;
				}
			}
		}else if(!s.startsWith("\"")){
			//(i) <text>...
			int indexOfOpening=s.indexOf("(");	
			if(indexOfOpening==-1) {
				//ii) <text>...
				int indexOfClosing=s.indexOf(")");
				if(indexOfClosing==-1) {
					return s;
				}
				String inBetween=s.substring(0,indexOfClosing);
//				System.out.println("In Between RomanNumerals: "+inBetween);
				if(inBetween.toUpperCase().matches("[IVXLCDM]+")) {
					int indexOfSpace=s.indexOf(" ");
					s=s.substring(indexOfSpace+1);
					return s;
				}
			}
			if(indexOfOpening==0) {
				int indexOfClosing=s.indexOf(")");
				if(indexOfClosing==-1) {
					return s;
				}
				String inBetween=s.substring(1,indexOfClosing);
//				System.out.println("In Between RomanNumerals: "+inBetween);
				if(inBetween.toUpperCase().matches("[IVXLCDM]+")) {
					int indexOfSpace=s.indexOf(" ");

					
					s=s.substring(indexOfSpace+1);
					return s;
				}
			}
		}
		return s;
	}
	
	public static String getCorrectAmount(String s) {	
		String a[]=s.split(" ");
		String amount="";
		for(int i=0;i<=a.length-2;i++) {
			System.out.println(a[i].contains("INR")+" is "+a[i]);
			System.out.println(isNumber(a[i])+"---"+check(a[i+1]));
			if((isNumber(a[i])&&check(a[i+1]))) {
//				System.out.println(a[i]+" "+check(a[i+2]));
				amount=correctAmount(a[i+1]);				
				if((amount.startsWith("C") && getCount(a[i])<3)||(amount.startsWith("c") && getCount(a[i])<3)) {
					System.out.println("Temp Money in crores(<3): "+a[i]);
					if(checkAmount(a[i])) {
						double newRupee=Double.parseDouble(a[i]);
						newRupee=(newRupee*10);
						double roundOff = Math.round(newRupee * 100.0) / 100.0;
						String actual=Double.toString(roundOff);
						a[i]=a[i].replaceFirst(a[i], actual);
						a[i+1]=a[i+1].replaceFirst(amount, "Million");
					}
				}else if((amount.startsWith("L") && getCount(a[i])<3)||(amount.startsWith("l") && getCount(a[i])<3)) {
					if(checkAmount(a[i])) {
						Float newRupee=Float.parseFloat(a[i]);
						newRupee=(newRupee/10);
						double roundOff = Math.round(newRupee * 100.0) / 100.0;
						String actual=Double.toString(roundOff);
						a[i]=a[i].replaceFirst(a[i], actual);
						a[i+1]=a[i+1].replaceFirst(amount, "Million");
					}
				}else if((amount.startsWith("C") && getCount(a[i])>=3)||(amount.startsWith("c") && getCount(a[i])>=3)) {	
					String temp="";
					for(int k=0;k<a[i].length();k++) {
						if(a[i].charAt(k)==',') {
							continue;
						}
						else {
							temp=temp+a[i].charAt(k);
						}
					}		
					if(checkAmount(temp)) {
						Float newRupee=Float.parseFloat(temp);
						newRupee=(newRupee/100);
						double roundOff = Math.round(newRupee * 100.0) / 100.0;
						String actual=Double.toString(roundOff);
						a[i]=a[i].replaceFirst(a[i], actual);
						a[i+1]=a[i+1].replaceFirst(amount, "Billion");
					}
				}else if((amount.startsWith("L") && getCount(a[i+1])>=3)||(amount.startsWith("l") && getCount(a[i+1])>=3)) {					
					String temp="";
					for(int k=0;k<a[i].length();k++) {
						if(a[i].charAt(k)==',') {
							continue;
						}
						else {
							temp=temp+a[i].charAt(k);
						}
					}
					if(checkAmount(temp)) {
						Float newRupee=Float.parseFloat(temp);
						newRupee=(newRupee/10000);
						double roundOff = Math.round(newRupee * 100.0) / 100.0;
						String actual=Double.toString(roundOff);
						a[i]=a[i].replaceFirst(a[i], actual);
						a[i+1]=a[i+1].replaceFirst(amount, "Billion");			
					}				
				}
			}
		}
		String correctDatePara="";
		for(int k=0;k<a.length;k++) {
			correctDatePara+=a[k]+" ";
		}
		System.out.println("Amount Correction: "+correctDatePara);
		return correctDatePara;
	}
	
	public static boolean isNumber(String s) {
		String amount=" ";
		for(int i=0;i<s.length();i++) {
			if(Character.isDigit(s.charAt(i))) {
				amount+=s.charAt(i);
			}else {
				continue;
			}
		}
		amount=amount.trim();
		System.out.println("New in Amount "+amount);
		return (amount.length()>0);
	}
	
	public static boolean checkAmount(String s) {
		for(int i=0;i<s.length();i++) {
			if(Character.isDigit(s.charAt(i))||s.charAt(i)==','||s.charAt(i)=='.') {
				continue;
			}else {
				return false;
			}
		}
		return true;
	}
	
	public static String correctAmount(String s) {
		int k=0,flag=1;
		for(k=0;k<s.length();k++) {
			if(!Character.isLetter(s.charAt(k))) {
				flag=0;
				break;
			}
		}
		if(flag==0) {
			s=s.substring(0, k);
		}
		return s;
	}
	
	public static boolean check(String amount) {
		if(amount.startsWith("Crores")||amount.startsWith("crores")||amount.startsWith("Crores")||amount.startsWith("crore")||amount.startsWith("Crore")||amount.startsWith("Lakhs")||amount.startsWith("lakhs")||amount.startsWith("Lakh")||amount.startsWith("lakh")||amount.startsWith("Lac")||amount.startsWith("lac")||amount.startsWith("lacs")||amount.startsWith("Lacs")) {
			if(amount.endsWith(".")) {
				return true;
			}
			return true;
		}else {
			return false;
		}
	}

	public static int getCount(String s) {
		int count=0;
		if(s.contains(",")&&s.contains(".")) {
			for(int i=0;i<s.length();i++) {
				if(s.charAt(i)==',') {
					continue;
				}else if(s.charAt(i)=='.'){
					return count;
				}else {
					count++;
				}
			}
		}else if(s.contains(".")) {
			for(int i=0;i<s.length();i++) {
				if(s.charAt(i)=='.') {
					return count;
				}
				else {
					count++;
				}
			}
		}else if(s.contains(",")) {
			for(int i=0;i<s.length();i++) {
				if(s.charAt(i)==',') {
					continue;
				}
				else {
					count++;
				}
			}
		}else {
			count=s.length();
		}
//		System.out.println("Count: "+count);
		return count;
	}
	
	public static boolean checkForOnlyNumber(String s) {
		System.out.println("S: "+s); 
		for(int i=0;i<s.length();i++) {
			if(!Character.isDigit(s.charAt(i))) {
				System.out.println("Char: "+s.charAt(i));
				return false;
			}
		}
		return true;
	}
	
	
}