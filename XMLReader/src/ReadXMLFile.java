import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.*;
import java.util.* ;
import java.util.regex.* ;

class Shloka {
	
	public String title ;
	public String sanskrit_verse ;
	public String english_verse ;
	public String commentary;
	public String word_meanings ;
	public String footnotes ;
	public String ending ;
	
	String content ;
	int startOfCommentary = 0 ;
	
	Shloka(String thetitle, String thecontent) {
		title = thetitle ;
		sanskrit_verse = "" ;
		english_verse = "" ;
		commentary = thecontent ;
		content = thecontent ;
		word_meanings = "" ;
		footnotes = "" ;
		ending = "" ;
		
		System.out.println("added " + title) ;
		
		extractSanskritVerse() ;
		extractEnglishVerse() ;
		extractCommentary() ;
		extractFootnotes() ;
		extractVerseEnding() ;
	}
	
	void extractSanskritVerse() {
		Pattern p = Pattern.compile("<i>([a-zA-Z ]+[|])") ;
		Matcher m = p.matcher(content) ;
		
		String htmlVerse = "" ;
		
		if (m.find()) {
			htmlVerse = m.group(1) ;
			//System.out.println(">>" + m.group(1) + "<<") ;
			//System.out.println(m.start() + " " + m.end() + " " + m.group());
		}
		
		p = Pattern.compile("([a-zA-Z ]+[|][|][ ]*[0-9]+[ ]*[|][|])") ;
		m = p.matcher(content) ;
		
		if (m.find()) {
			htmlVerse = htmlVerse +  "\\\\\\" + "noindent \n" + m.group(1) ;
			//System.out.println(">>" + m.group(1) + "<<") ;
			//System.out.println(m.start() + " " + m.end() + " " + m.group());
		}
		
		
		htmlVerse = htmlVerse.replaceAll("<i>", "") ;
		htmlVerse = htmlVerse.replaceAll("</i>", "") ;
		htmlVerse = htmlVerse.replaceAll("<br[ ]*/>", " ") ;
		
		//System.out.println(htmlVerse) ;
		
		sanskrit_verse = htmlVerse ;
	}
	
	void extractEnglishVerse() {
		Pattern p = Pattern.compile("<b>(.*)</b>") ;
		Matcher m = p.matcher(content) ;
		
		String htmlVerse = "" ;
		
		if (m.find()) {
			startOfCommentary = m.end() ;
			htmlVerse = m.group(1) ;
			//System.out.println(">>" + m.group(1) + "<<") ;
			//System.out.println(m.start() + " " + m.end() + " " + m.group());
		}
		
		htmlVerse = htmlVerse.replaceAll("<b>", "") ;
		htmlVerse = htmlVerse.replaceAll("</b>", "") ;
		htmlVerse = htmlVerse.replaceAll("<br[ ]*/>", "") ;
		htmlVerse = htmlVerse.replaceAll("[&]nbsp;", "") ;
		
		//System.out.println(htmlVerse) ;
		
		
		
		english_verse = htmlVerse ;
	}
	
	void extractCommentary() {
		
		int endOfMeanings = 0 ;
		
		Pattern p = Pattern.compile("([A-Za-z- ]+:[A-Za-z- /]+)<br[ ]*/>") ;
		Matcher m = p.matcher(content) ;
		
		while (m.find()) {
			word_meanings = word_meanings + m.group(1) + "\n" ;
			endOfMeanings = m.end() ;
			//System.out.println(">>" + word_meanings + "<<") ;
			//System.out.println(m.start() + " " + m.end());
		}

		String rawCommentary = content.substring(endOfMeanings, content.length()).trim() ;

		rawCommentary = rawCommentary.replaceAll("<br[ ]*/>", "\n") ;
		rawCommentary = rawCommentary.replaceAll("[&]nbsp;", "") ;
		
		//System.out.println(rawCommentary) ;
		
		commentary = rawCommentary.trim() ;

	}
	
	void extractFootnotes() {
		
		if (!commentary.contains("Footnote")) {
			return ;
		}
		
		String [] result = commentary.split("Footnote") ;
		
		footnotes = "Notes" + result[1].substring(1, result[1].length()) ;
		
		commentary = result[0] ;
		//System.out.println(footnotes) ;
		
	}

	void extractVerseEnding() {
		Pattern p = Pattern.compile("<i>(om tatsatiti [a-z ]+[|][|][ ][0-9]+[ ][|][|])</i>") ;
		Matcher m = p.matcher(commentary) ;
		
		String htmlVerse = "" ;
		int index = 0 ;
		
		if (m.find()) {
			index = m.start() ;
			htmlVerse = m.group(1) ;
			htmlVerse = htmlVerse.replaceAll("<i>", "") ;
			htmlVerse = htmlVerse.replaceAll("</i>", "") ;
			htmlVerse = htmlVerse.replaceAll("<br[ ]*/>", " ") ;
			
			ending = htmlVerse ;
			commentary = commentary.substring(0, index-1) ;
			
			//	System.out.println(">>" + m.group(1) + "<<") ;
			//System.out.println(m.start() + " " + m.end() + " " + m.group());
		}
		
		
		//System.out.println(htmlVerse) ;
		
		//sanskrit_verse = htmlVerse ;
	}
	
}

class XMLParser {
	
	static List<Shloka> shlokas = new Vector<Shloka>() ;
	
	static String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
	 
	        Node nValue = (Node) nlList.item(0);
	 
		return nValue.getNodeValue();
	  }
	  
	  static void writeFile(String path) throws Exception {
		  BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path)));
		  
		  for (int i = 0; i < shlokas.size(); ++i) {
			  writer.write(shlokas.get(i).title) ;
			  writer.write("\n") ;
			  writer.write(shlokas.get(i).sanskrit_verse) ;
			  writer.write("\n") ;
			  writer.write(shlokas.get(i).english_verse) ;
			  writer.write("\n") ;
			  writer.write(shlokas.get(i).word_meanings) ;
			  writer.write("\n") ;
			  writer.write(shlokas.get(i).commentary) ;
			  writer.write("\n") ;

		  }
		  
		  writer.close() ;
	  }

	
	static List<Shloka> doParse(String inFileName, String outFileName) throws Exception {
		  
		 	File fXmlFile = new File(inFileName);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
	 
			//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			NodeList nList = doc.getElementsByTagName("entry");
			//System.out.println("-----------------------");
	 
			for (int temp = 0; temp < nList.getLength(); temp++) {
	 
			   Node nNode = nList.item(temp);
			   if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	 
			      Element eElement = (Element) nNode;
			      
			      String title = getTagValue("title", eElement);
			      
			      if (title.contains("hapter") 
			    		  && !(title.contains("ummary") || title.contains("ntroduction"))) {
			    	  
			    	  String content = getTagValue("content", eElement);
			    	  //String transformed_content = transformContent(content) ;
			    	  //String transformed_title = transformTitle(title) ;
			    	  //System.out.println("Title : " + getTagValue("title", eElement));
			    	  //System.out.println("Content : " + content);
		              //System.out.println("Nick Name : " + getTagValue("nickname", eElement));
			      //System.out.println("Salary : " + getTagValue("salary", eElement));
			    	  
			    	  shlokas.add(new Shloka(title, content)) ;
			      }
	 
			   }
			}
			
			//writeFile(outFileName) ;
			
			//Collections.reverse(shlokas) ;
			
			LatexTransform.writeLatexFile(shlokas, outFileName) ;
			
			return shlokas ;
	}
		 	
}

class LatexTransform {
	
	
	

	static String html2latexTitle(String str) {
		
		String newChapter = "" ;
		
		if (str.contains("erse 1,")) {
			Pattern p = Pattern.compile("Chapter ([0-9]+)") ;
			Matcher m = p.matcher(str) ;
			
			if (m.find()) {
				String chapterNumber = m.group(1) ;
				//System.out.println("found chapter " + chapterNumber) ;
				newChapter = "\\chapter{Chapter " + chapterNumber + "} " ;
			}
		}
		
		String result = newChapter + "\\" + "newpage\n" + "\\section{" + str + "}" ; 
		return result ;
		
	}
	
	static String html2latexSanskrit(String str) {
		
		String result = "\\textit{" + str + "}" ; 
		return result ;
		
	}

	static String html2latexEnglish(String str) {
		String result = "\\" + "noindent" + "\\textbf{" + str + "}" ; 
		return result ;
		
	}
	
	static String html2latexWordMeaning(String str) {
		str = str.replaceAll("\n", "~\\\\\\\\\n") ;
		String result = "\\marginnote{" + str + "}" ; 
		return result ;
		
	}



	static String html2latexCommentary(String str) {
		
		String result = str.replaceAll("\n\n", "\n") ;
		result = result.replaceAll("\n", "\n~\\\\\\\\") ; 
		result = result.replaceAll("&gt;", ">") ;
		result = result.replaceAll("&rsquo;", "'") ;
		result = result.replaceAll("“", "\"") ;
		result = result.replaceAll("”", "\"") ;
		result = result.replaceAll("%", " pct") ;
		return result ;
		
	}

	static String finalLatex(Shloka s) {
		String finaltext = "\n" ;
		
		finaltext +=  html2latexTitle(s.title) + "\n" ;
		finaltext +=  html2latexWordMeaning(s.word_meanings) + "\n";
		finaltext +=  html2latexSanskrit(s.sanskrit_verse) +  "\\\\~\\\\\n";
		finaltext +=  html2latexEnglish(s.english_verse) + "\\\\\n\\" + "bigskip \n";
		finaltext +=  html2latexCommentary(s.commentary) ;				
		
		if(!s.footnotes.equals("")) {
			int seventylen = finaltext.length() * 70 / 100 ;
			String x1 = finaltext.substring(0, seventylen) ;
			String x2 = finaltext.substring(seventylen, finaltext.length()) ;
			finaltext =  x1 + " " + html2latexWordMeaning(s.footnotes) + x2 ;
			//finaltext +=  html2latexWordMeaning(s.footnotes) + "\n\\" + "bigskip \n";
		}
		
		if(!s.ending.equals("")) {
			finaltext += "\\" + "bigskip" + html2latexSanskrit(s.ending) + "\n" ; 
		}
		return finaltext ;
	}
	
	static void writeLatexFile(List <Shloka> shlokas, String path) throws Exception {
		  BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path)));
		  
		  for (int i = shlokas.size()-1 ; i >= 0; --i) {
			  writer.write(finalLatex(shlokas.get(i))) ;
		  }
		  
		  writer.close() ;
	  }

	
}

public class ReadXMLFile {
	

 
	public static void main(String argv[]) {
		
		try {
		//doMain() ;
		XMLParser.doParse("c:\\users\\gkm\\downloads\\blog-07-25-2012.xml", "c:/users/gkm/downloads/gita-out.txt") ;
		  
		
		} catch (Exception e) {
				e.printStackTrace();
			  }
	}
	


}