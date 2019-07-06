import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Comparator;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.xmlbeans.impl.xb.xmlschema.SpaceAttribute;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFtnEdn;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyle;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STStyleType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STVerticalAlignRun;
import com.aylien.textapi.TextAPIClient;
import com.aylien.textapi.parameters.ExtractParams;
import com.aylien.textapi.responses.Article;
import javafx.application.Application;
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

public class News extends Application {
	
	Stage window;
	Scene scene;
	String inspectText = "";
	public static void main(String[] args) {
		launch(args);
	}

	@SuppressWarnings("restriction")
	@Override
	public void start(Stage arg0) throws Exception {
		window = arg0;
		window.setTitle("CreditCheck Partners Pvt. Ltd ©");

		GridPane grid = new GridPane();
		grid.setAlignment(Pos.TOP_CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Text scenetitle = new Text("News Extractor");
		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		Tooltip.install(scenetitle, new Tooltip("Powered by Ayush"));
		grid.add(scenetitle, 0, 0, 2, 1);

		Label NameLabel = new Label("Name of the file :   ");
		javafx.scene.control.TextField nameOfFile = new javafx.scene.control.TextField();
		HBox NameHBox = new HBox();
		NameHBox.getChildren().addAll(NameLabel, nameOfFile);
		grid.add(NameHBox, 1, 2);

		Label nameLabel = new Label("Enter Link:                 ");
		HBox hBox = new HBox();
		hBox.getChildren().add(nameLabel);
		// grid.add(nameLabel, 1, 2);
		grid.add(hBox, 1, 3);

		TextArea textArea = new TextArea();
		textArea.setMaxWidth(1200);
		textArea.setMinWidth(1000);
		grid.add(textArea, 1, 4);

		Button getButton = new Button("Get Word Document");
		HBox searchButtonBox = new HBox();
		searchButtonBox.getChildren().add(getButton);
		// searchButtonBox.setAlignment(Pos.BOTTOM_RIGHT);
		grid.add(getButton, 1, 8);

		//CONNECTION TO AYLIEN API 
		TextAPIClient client = new TextAPIClient("82ff819d", "4cfcda02bfab0042218b8bcafcec7841");
		ExtractParams.Builder builder = ExtractParams.newBuilder();
		getButton.setOnAction(e -> {
			try {
				Article extract = null;
				XWPFDocument documentObject = new XWPFDocument(this.getClass().getClassLoader().getResourceAsStream("BaseCase.docx"));
				int length = 0;
				XWPFParagraph para = documentObject.getParagraphs().get(length);
				XWPFRun run = para.createRun();
				String links = textArea.getText();
				String individualLinks[] = links.split("\n");
				String newsFileName = nameOfFile.getText();
				int i = 0;
				Date[] allLinks=new Date[individualLinks.length];
				//initialisation
				for (int count = 0; count < individualLinks.length; count++) {
					java.net.URL url = new java.net.URL(individualLinks[count]);
					builder.setUrl(url);
					extract = client.extract(builder.build());
					System.out.println("Null or not: "+extract.getPublishDate()+" "+extract.getArticle());
					if(extract.getPublishDate()==null) {
//						System.out.println("Entered-1 "+extract.getPublishDate());
						allLinks[i]=new Date(individualLinks[count],extract.getArticle(), Integer.MAX_VALUE, Integer.MAX_VALUE, "Z");
					}else {
//						System.out.println("Entered-2");
						String temp[]=extract.getPublishDate().toString().split(" ");
//						System.out.println(temp[2]+" "+temp[1]+" "+temp[temp.length-1]);
						allLinks[i]=new Date(individualLinks[count],extract.getArticle(), Integer.parseInt(temp[temp.length-1]), Integer.parseInt(temp[2]), getMonth(temp[1]));
					}
					i++;
				}	
//				System.out.println();
				Arrays.sort(allLinks, new SoringDate());
//				System.out.println("After Sorting: ");				
//				for(Date d:allLinks) {
//					System.out.println(" "+d.getDay()+" "+d.getMonth().substring(1)+" "+d.getYear());
//					System.out.println();
//				}
				int paragraphCount=0;
				for(int k=0;k<allLinks.length;k++) {
					run = para.createRun();
					String text = "";
					text=getStartingStatement(allLinks[k])+" "+allLinks[k].getText();
					System.out.println("Text: "+text);
					run.setText(text);
					// check to add footnotes in case of empty
					if (documentObject.getFootnotes().isEmpty()) {
						documentObject.createFootnotes();
					}
					// add footnote
					CTFtnEdn ctfInstance = CTFtnEdn.Factory.newInstance();
					BigInteger id = new BigInteger("1");
					ctfInstance.setId(id);
					CTP ctp = ctfInstance.addNewP();
					ctp.addNewPPr().addNewPStyle().setVal("FootnoteText");
					CTR ctr = ctp.addNewR();
					ctr.addNewRPr().addNewRStyle().setVal("FootnoteReference");
					ctr.addNewFootnoteRef();
					CTText cttext = ctp.addNewR().addNewT();
					cttext.setStringValue(allLinks[k].getLink() + "\n");
					cttext.setSpace(SpaceAttribute.Space.PRESERVE);
					// add footnote to document
					documentObject.addFootnote(ctfInstance);
					// add reference to footnote at end of first paragraph
					ctr = documentObject.getParagraphArray(paragraphCount).getCTP().addNewR();
					ctr.addNewRPr().addNewRStyle().setVal("FootnoteReference");
					ctr.addNewFootnoteReference().setId(id);
					CTStyle style = CTStyle.Factory.newInstance();
					style.setStyleId("FootnoteReference");
					style.setType(STStyleType.CHARACTER);
					style.addNewName().setVal("footnote reference");
					style.addNewBasedOn().setVal("DefaultParagraphFont");
					style.addNewUiPriority().setVal(new BigInteger("99"));
					style.addNewSemiHidden();
					style.addNewUnhideWhenUsed();
					style.addNewRPr().addNewVertAlign().setVal(STVerticalAlignRun.SUPERSCRIPT);
					// add style
					documentObject.getStyles().addStyle(new XWPFStyle(style));
					style = CTStyle.Factory.newInstance();
					style.setType(STStyleType.PARAGRAPH);
					style.setStyleId("FootnoteText");
					style.addNewName().setVal("footnote text");
					style.addNewBasedOn().setVal("Normal");
					style.addNewLink().setVal("FootnoteTextChar");
					style.addNewUiPriority().setVal(new BigInteger("99"));
					style.addNewSemiHidden();
					style.addNewUnhideWhenUsed();
					CTRPr rpr = style.addNewRPr();
					rpr.addNewSz().setVal(new BigInteger("20"));
					rpr.addNewSzCs().setVal(new BigInteger("20"));
					// add style
					documentObject.getStyles().addStyle(new XWPFStyle(style));
					style = CTStyle.Factory.newInstance();
					style.setCustomStyle(STOnOff.X_1);
					style.setStyleId("FootnoteTextChar");
					style.setType(STStyleType.CHARACTER);
					style.addNewName().setVal("Footnote Text Char");
					style.addNewBasedOn().setVal("DefaultParagraphFont");
					style.addNewLink().setVal("FootnoteText");
					style.addNewUiPriority().setVal(new BigInteger("99"));
					style.addNewSemiHidden();
					rpr = style.addNewRPr();
					rpr.addNewSz().setVal(new BigInteger("20"));
					rpr.addNewSzCs().setVal(new BigInteger("20"));
					// add style
					documentObject.getStyles().addStyle(new XWPFStyle(style));
					para = documentObject.createParagraph();
					paragraphCount++;
					length++;
				}
				if (length == individualLinks.length) {
					length = 0;
					writeFile(newsFileName, documentObject);
				}
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		});
		scene = new Scene(grid, 1100, 350);
		window.setScene(scene);
		window.setTitle("CreditCheck Partners Pvt. Ltd ©");
		window.show();
	}

	public static String getMonth(String s) {
		if(s.startsWith("Jan")) {
			return "A"+s.substring(0);
		}
		if(s.startsWith("Feb")) {
			return "B"+s.substring(0);
		}
		if(s.startsWith("Mar")) {
			return "C"+s.substring(0);
		}
		if(s.startsWith("Apr")) {
			return "D"+s.substring(0);
		}
		if(s.startsWith("May")) {
			return "E"+s.substring(0);
		}
		if(s.startsWith("Jun")) {
			return "F"+s.substring(0);
		}
		if(s.startsWith("Jul")) {
			return "G"+s.substring(0);
		}
		if(s.startsWith("Aug")) {
			return "H"+s.substring(0);
		}
		if(s.startsWith("Sep")) {
			return "I"+s.substring(0);
		}
		if(s.startsWith("Oct")) {
			return "J"+s.substring(0);
		}
		if(s.startsWith("Nov")) {
			return "K"+s.substring(0);
		}
		if(s.startsWith("Dec")) {
			return "L"+s.substring(0);
		}
		return "";
	}
	
	public static String getStartingStatement(Date dateObject) {
		if(dateObject.getDay()==Integer.MAX_VALUE||dateObject.getYear()==Integer.MAX_VALUE||dateObject.month==null||dateObject.month.equals("")) {
			return "An article dated "+" DATE NOT FOUND ";
		}
		return "An article dated "+dateObject.getDay()+" "+dateObject.getMonth().substring(1)+" "+dateObject.getYear();
	}

	private static void writeFile(String individualName, XWPFDocument doc) throws IOException {
		String outFileName = "NewsExtractor_Case_Output/";
		File file = new File(outFileName);
		if (!file.exists())
			file.mkdirs();
		FileOutputStream outputStream;
		String finalOutputFileName = "";
		finalOutputFileName += outFileName + individualName;
		if (finalOutputFileName.endsWith(".")) {
			finalOutputFileName = finalOutputFileName.substring(0, finalOutputFileName.length() - 2);
		}
		finalOutputFileName += ".docx";
		try {
			outputStream = new FileOutputStream(new File(finalOutputFileName));
			doc.write(outputStream);
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		popWordDoc(finalOutputFileName);
	}

	public static void popWordDoc(String Name) throws IOException {
		Desktop.getDesktop().open(new File(Name));
	}

}

class Date{
	String link;
	String text;
	int year;
	int day;
	String month;

	
	public Date(String link, String text, int year, int day, String month) {
		super();
		this.link = link;
		this.text = text;
		this.year = year;
		this.day = day;
		this.month = month;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	
	
}

class SoringDate implements Comparator<Date>{

	@Override
	public int compare(Date o1, Date o2) {
		int yearFlag=o1.getYear()-o2.getYear();
		if(yearFlag==0) {
			int monthFlag=o1.getMonth().compareTo(o2.getMonth());
			if(monthFlag==0) {
				int dayFlag=compareDay(o1, o2);
				return dayFlag;
			}
			return monthFlag;
		}else if(yearFlag<0) {
			return -1;
		}
		return 1;
	}
	
	public int compareDay(Date object1,Date object2) {
		if(object1.getDay()==object2.getDay()) {
			return 0;
		}else if(object1.getDay()>object2.getDay()) {
			return 1;
		}else {
			return -1;
		}
	}
	
}

