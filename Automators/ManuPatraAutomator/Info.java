import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Info {
	
	public static void PerformAction(XWPFDocument document,String html,XWPFTable table,int iterationNumber,int total,String nameOfTheFile) throws Exception {	
		html = removeOccurences(html);
//		System.out.println(html);
		int info = 1;
		Document doc = Jsoup.parse(html);
		HashMap<Integer,String> firstRow = new HashMap<Integer,String>();//first,second,third column 
		Elements centralAlignText = doc.getElementsByClass("centeralign");
 		for(Element e : centralAlignText) {
// 			System.out.println(info+" "+e.text());
 			firstRow.put(info , e.text());
			info++;
		}
 		info--;
 		String date="";
 		//This is whole for the date
 		for(int i=1;i<=info;i++) {
 			if(firstRow.get(i).startsWith("Decided On:")) {
				int colonInd = firstRow.get(i).indexOf(":");
				date = firstRow.get(i).substring(colonInd+1).trim(); //whole date
//				System.out.println(date);
				int firstDot = date.indexOf(".");
				String mid = date.substring(firstDot+1,5);
				String correctNewMidDate = getCorrectFormatOfDate(mid);
				date = date.substring(0,2)+" "+correctNewMidDate+" "+date.substring(6,10);
 			}
 		}
 		
 		int k=-1;
 		String name="";
 		for(int i=1;i<=info;i++) {
 			if(firstRow.get(i).contains("Vs. ")) {
 				k=i;
// 				int index = firstRow.get(i).indexOf("Vs.");
// 				name = firstRow.get(i).substring(11, index).trim().replace(" ", "_");
 			}
 		}		
		int colonForwardIndex = firstRow.get(k).indexOf(":");
		int colonBackwardIndex = firstRow.get(k).lastIndexOf(":");
		int vs_index = firstRow.get(k).indexOf(" Vs");
		String Appellants = firstRow.get(k).substring(colonForwardIndex+1, vs_index).trim();
		String Respondent = firstRow.get(k).substring(colonBackwardIndex+1).trim();
		Appellants=changeInAppellants(Appellants);
		Respondent=changeInRespondent(Respondent);
		//Fisrt Column-->Case_no./Court/Date
		XWPFParagraph para0 = table.getRow(1).getCell(0).getParagraphs().get(0);
		XWPFRun run = para0.createRun();
		run.setText(firstRow.get(2)+" /");
		run.addBreak();
		run.setText(toOnlyFirstLetterCapital(firstRow.get(1))+" /");
		run.addBreak();
		run.setText(date);
		//Second Column-->Appellants
		XWPFParagraph para1 = table.getRow(1).getCell(1).getParagraphs().get(0);
		XWPFRun run1 = para1.createRun();
		run1.setText(Appellants);
		//Third Column-->Respondent
		XWPFParagraph para2 = table.getRow(1).getCell(2).getParagraphs().get(0);
		XWPFRun run2 = para2.createRun();
		run2.setText(Respondent);
		//Start with Background
		int i = 1 , temp = 0 ;
		XWPFParagraph para20 = table.getRow(2).getCell(0).getParagraphs().get(1);
		XWPFRun run20 = para20.createRun();
		run20.addBreak();
		run20.addBreak();
		
		Elements paragraphs = doc.select("p");
		String text="";
		boolean entry=false;
		
		for(Element p : paragraphs) {				
			try {
				boolean truth = p.text().startsWith(i+". ");
				if(p.text().equals("JUDGMENT")||p.text().equals("JUDGEMENT")) {
					entry=true;
				}else if(truth) {
					System.out.println(p.text());
					text = p.text().substring(p.text().indexOf(" ")+1);
					text = correctingDate(text);	
					text = identifyingManu(text);
					text = replaceRemaining(text);
					text = getCorrectAmount(text);
					run20.setText(text);
					run20.addBreak();
					run20.setText("");
					run20.addBreak();
					i+=1;
					temp = i;	
				} else if((!truth&&temp>=1)||entry) {	
					System.out.println(p.text());
					text = p.text();
					if(text.length()>0) {
						text=removeNumbersWithDot(text);
						text=removeLetters(text);
						text=removeNumbersEnclosedInParanthesis(text);
						text=removeRomanNumerals(text);
						try{
							if(text.length()>0) {
								int flag=1;
								int getIndexOfDot=text.indexOf(".");
								if(getIndexOfDot!=-1) {
									String inBetween=text.substring(0,getIndexOfDot);
									for(int start=0;start<inBetween.length();start++) {
										if(!Character.isDigit(inBetween.charAt(start))) {
											flag=0;
											break;
										}
									}
									if(flag==1) {
										int indexOfSpace=text.indexOf(" ");
										text=text.substring(indexOfSpace+1);
									}
								}
							}
							if(text.length()>=3) {
								if(text.substring(0, 3).equals("•")) {
									text = p.text().substring(p.text().indexOf(" ")+1);
								}
							}
						}catch(Exception e){
							//e.printStackTrace();
							break;
						}
						text=toFirstLetterCapital(text);
						text = correctingDate(text);
						text = identifyingManu(text);
						text = replaceRemaining(text);
						text = getCorrectAmount(text);
						if(text.equals("� Manupatra Information Solutions Private Limited")||text.equals("© Manupatra Information Solutions Private Limited")) {
							System.out.println("last line");
							text="";
							run20.setText(text);
						}else{
							run20.setText(text);
							run20.addBreak();
							run20.setText("");
							run20.addBreak();
						}
					}
				}	
			}catch(Exception e) {
				e.printStackTrace();
				break;
			}
		}
			if(iterationNumber+1==total) {
//				document.write(out);
//				out.close();
//				System.out.println("Final:  "+(iterationNumber+1)+" "+total);
				writeFile(nameOfTheFile,document);
			}	
	}
	
	private static void writeFile(String individualName,XWPFDocument doc) throws IOException {
		String outFileName = "ManuPatra_Case_Output/";
		File file = new File(outFileName);
		if(!file.exists())
			file.mkdirs();
		FileOutputStream outputStream;
		String finalOutputFileName = "";
		finalOutputFileName+=outFileName+individualName;
		if(finalOutputFileName.endsWith(".")) {
			finalOutputFileName=finalOutputFileName.substring(0,finalOutputFileName.length()-2);
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
		popWordDoc(finalOutputFileName);
	}
		
	public static void popWordDoc(String Name) throws IOException {
		Desktop.getDesktop().open(new File(Name));
	}
	 
	public static String correctingDate(String t) {	
		String a[]=t.split(" ");
		String returnString="";
		System.out.println("Entered Date: "+t);
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
						String tempYear=a[i].substring(second+1);
						if(tempYear.length()>=4) {
							if(!a[i].substring(second+5).equals("")) {
								a[i] = a[i].substring(0, first)+" "+inBetween+" "+a[i].substring(second+1,second+5)+a[i].substring(second+5);
							}else {
								a[i] = a[i].substring(0, first)+" "+inBetween+" "+a[i].substring(second+1,second+5); 
							}
						}else if(tempYear.length()>=2) {
							if(!a[i].substring(second+3).equals("")) {
								System.out.println("After Text: "+a[i].substring(second+3));
								a[i] = a[i].substring(0, first)+" "+inBetween+" "+"19"+tempYear; 
								System.out.println("Temp New Date With Extra: "+a[i]);
							}else {
								a[i] = a[i].substring(0, first)+" "+inBetween+" "+"19"+tempYear;
								System.out.println("Temp New Date: "+a[i]);
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
	
	
	public static String identifyingManu(String s) {	
		int i=0;
		String returnString="";
		String a[]=s.split(" ");
		for(i=0;i<a.length-1;i++) {
			if(a[i].startsWith("MANU/") && a[i+1].equals(":")) {
				a[i]="*****";
				a[i+1]="";
			}
			else if(a[i].startsWith("MANU/")&&(!a[i+1].equals(":"))) {
				a[i]="*****";
			}
			else if(a[i].contains("MANU/")) {
				int index=a[i].indexOf("MANU/");
				String temp=a[i].substring(index, index+17);
				a[i]=a[i].replace(temp, "*****");
			}
		}
		
		if(a[i].startsWith("MANU/")) {
			a[i]="*****";
		}
		
		i=0;
		for(i=0;i<a.length;i++) {
			returnString+=a[i]+" ";
		}
		
		returnString=returnString.trim();
		
		return returnString;		
	}
	
	public static String replaceRemaining(String t) {
		t = t.replace(" has "," had ");
		t = t.replace(" has been "," had ");
//		t = t.replace(" us "," the judges ");
		//t = t.replace("Us "," the judges ");
//		t = t.replace(" Us "," The judges ");
		t = t.replace(" is ", " was ");
		t = t.replace(" is,"," was,");
		t = t.replace(" are "," were ");
//		t = t.replace(" I "," The Judge ");
//		if(t.startsWith("I ")) {
//			t = t.replace("I ","The Judge ");
//		}
//		t = t.replace(" We "," The Judges ");
//		t = t.replace("We,","The Judges,");
//		t = t.replace("We ","The Judges ");
//		t = t.replace(" we "," the Judges ");
		t = t.replace(" Shri "," ");
		t = t.replace("Shri "," ");
		t = t.replace(" Mr.","");
		t = t.replace("Mr. ","");
		t = t.replace(" M/s.","");
		t = t.replace("M/s ","");
		t = t.replace("M/s. ","");
		t = t.replace(" Mrs.","");
		t = t.replace("Mrs. ","");
		t = t.replace("/-", "");
		t = t.replace(" Rs. ", " INR ");
		t = t.replace("Rs.", "INR ");
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
		t = t.replace(" Ltd.", " Limited");
		t = t.replace(" Ltd. ", " Limited ");
		t = t.replace("Anr.", "Another");
		t = t.replace("Ors.", "Others");
		t = t.replace("© Manupatra Information Solutions Pvt. Ltd.", "");
		return t;
	}
	
	public static String changeInAppellants(String s) {
		s = s.replace(" M/S. ","");
		s=s.replace("P. Ltd.", "Private Limited");
		s=s.replace("Dy.", "Deputy");
		s=s.replace("Pvt.Ltd.", "Private Limited");
		s=s.replace("Pvt. Ltd.", "Private Limited");
		s=s.replace("Pvt.", "Private");
		s=s.replace("Ltd.", "Limited");
		s=s.replace("ltd.", "limited");
		s=s.replace("Cus. & C. Ex.", "Custom and Central Excise");
		s=s.replace("&", "and");
		s=s.replace("ITAT", "Income Tax Appellate Tribunal");
		s=s.replace("Anr.", "Another");
		s=s.replace("Ors.", "Others");
		return s;
	}
		
	public static String changeInRespondent(String s) {
		s = s.replace(" M/S. ","");
		s=s.replace(" Ors. ", " Others ");
		s=s.replace(" Ors.", " Others");
		s=s.replace("P. Ltd.", "Private Limited");
		s=s.replace("Dy.", "Deputy");
		s=s.replace("Pvt.Ltd. ", "Private Limited ");
		s=s.replace("Pvt. Ltd.", "Private Limited");
		s=s.replace("Pvt.", "Private");
		s=s.replace("Pvt", "Private");
		s=s.replace("Ltd","Limited");
		s=s.replace("Ltd.", "Limited");
		s=s.replace("ltd.", "limited");
		s=s.replace("Cus. & C. Ex.", "Custom and Central Excise");
		s=s.replace("&", "and");
		s=s.replace("ITAT", "Income Tax Appellate Tribunal");
		s=s.replace("Itat", "Income Tax Appellate Tribunal");
		s=s.replace("Anr.", "Another");
		s=s.replace("Ors.", "Others");
		return s;
	}
	
	public static String removeOccurences(String s) {	
		s=s.replaceAll("</p>\\s+<blockquote>\\s+(<p>)?","</p><blockquote><p>");
		s=s.replaceAll("</p>\\s+<blockquote>","</p><blockquote>");
		s=s.replaceAll("</p>\\s+</blockquote>\\s+<p","</p></blockquote><p");
		s=s.replaceAll("</blockquote>\\s+<p","</blockquote><p");
		s=s.replace("</p><blockquote><p>", "</p><p>");
		//s=s.replace("</p> <blockquote><p>", "</p><p>");
		s=s.replace("</p></blockquote><p>", "</p><p>");
		s=s.replace("</p></blockquote><p", "</p><p");
		//s=s.replace("</p></blockquote> <p>", "</p><p>");
		s=s.replace("</p><blockquote>","</p><p>");
		s=s.replace("</blockquote></blockquote>", "</p>");
		s=s.replace("</p></p><p>", "</p><p>");
		s=s.replace("</p><p><p", "</p><p");   
		s=s.replace("<blockquote>", "<p>");
		s=s.replace("</blockquote>", "<p>");
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
					if( !Character.isDigit(inBetweenNumber.charAt(len)) ) {
						inBetween = false;
						break;
					}
				}
				if(inBetween) {
					int indexOfSpace=s.indexOf(" ");
					s=s.substring(indexOfSpace+1);
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
	
	public static String toOnlyFirstLetterCapital(String s) {
		String returnString="";
		if(s.equals("")||s.length()<=2) {
			return s;
		}else {
			returnString="";
			String a[]=s.split(" ");
			for(int i=0;i<a.length;i++) {
//				System.out.println(a[i].charAt(0)+" "+a[i].substring(1).toLowerCase());
				String temp=Character.toUpperCase(a[i].charAt(0))+a[i].substring(1).toLowerCase();
				a[i]=temp;
			}
			for(String e:a) {
				returnString+=e+" ";
			}
		}
		return returnString.trim();
	}
	
	public static String getCorrectAmount(String s) {	
		String a[]=s.split(" ");
		String amount="";
		for(int i=0;i<=a.length-2;i++) {
//			System.out.println(a[i].contains("INR")+" is "+a[i]);
//			System.out.println(isNumber(a[i])+"---"+check(a[i+1]));
			if((isNumber(a[i])&&check(a[i+1]))) {
//				System.out.println(a[i]+" "+check(a[i+2]));
				amount=correctAmount(a[i+1]);				
				if((amount.startsWith("C") && getCount(a[i])<3)||(amount.startsWith("c") && getCount(a[i])<3)) {
//					System.out.println("Temp Money in crores(<3): "+a[i]);
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
//		System.out.println("Amount Correction: "+correctDatePara);
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
//		System.out.println("New in Amount "+amount);
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
