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
	
	final static int VERSE = 100 ;
	final static int SUMMARY = 200 ;
	
	public String title = "" ;
	public String sanskrit_verse  = "" ;
	public String english_verse  = "" ;
	public String commentary = "" ;
	public String word_meanings  = "" ;
	public String footnotes  = "" ;
	public String ending  = "" ;
	public String firstTwoWordsShloka  = "" ;
	public List <String> terms ;
	
	String content ;
	int startOfCommentary = 0 ;
	
	public int type ;
	
	Shloka(String thetitle, String thecontent, int thetype) {
		type = thetype ;
		commentary = thecontent ;
		footnotes = "" ;
		
		commentary = commentary.replaceAll("<br[ ]*/>", "\n") ;
		commentary = commentary.replaceAll("[&]nbsp;", "") ;
		
		title = thetitle ;
	
		
		extractFootnotes() ;
		System.out.println("added " + title) ;
	}
	
	Shloka(String thetitle, String thecontent,List<String> theTerms) {
		title = thetitle ;
		sanskrit_verse = "" ;
		english_verse = "" ;
		commentary = thecontent ;
		content = thecontent ;
		word_meanings = "" ;
		ending = "" ;
		firstTwoWordsShloka = "" ;
		type = VERSE ;
		footnotes = "" ;
		
		terms = theTerms ;
		
		System.out.println("added " + title) ;
		
		extractSanskritVerse() ;
		extractEnglishVerse() ;
		extractCommentary() ;
		extractFootnotes() ;
		extractVerseEnding() ;
	}
	
	String getRawVerse() {
		String htmlVerse = "" ;
		String splitResult [] ;
		int splitLineCount = 0 ;
		
		splitResult = content.split("</i>") ;
		splitLineCount = splitResult.length ;
				
		//System.out.println(splitLineCount) ;
		
		for (int i = 0; i < splitLineCount-1; ++i) {
			String splitLine = splitResult[i] ;
			//System.out.println(splitLine) ;	
			
			if (splitLine.contains("om tatsatiti")) {
			//	System.out.println("got om, skipping") ;
				continue ;
			}
	
			htmlVerse += splitLine + "\n" ;
		}
		
		return htmlVerse ;
	}
	
	
	
	void extractSanskritVerse() {
		
		/*
		Pattern p = Pattern.compile("<i>([a-zA-Z :<>/]+[|])") ;
		Matcher m = p.matcher(content) ;
		
		String htmlVerse = "" ;
		
		if (m.find()) {
			htmlVerse = m.group(1) ;
		//	System.out.println(">>" + m.group(1) + "<<") ;
		//	System.out.println(m.start() + " " + m.end() + " " + m.group());
		}
		
		p = Pattern.compile("([a-zA-Z ]+[|][|][ ]*[0-9]+[ ]*[|][|])") ;
		m = p.matcher(content) ;
		
		if (m.find()) {
			//htmlVerse = htmlVerse +  "\\\\\\" + "noindent \n" + m.group(1) ;
			htmlVerse = htmlVerse +  "\n" + m.group(1) ;
			//System.out.println(">>" + m.group(1) + "<<") ;
			//System.out.println(m.start() + " " + m.end() + " " + m.group());
		}
		*/
		
		String htmlVerse = getRawVerse() ;
		Pattern p ;
		Matcher m ;
		
		htmlVerse = htmlVerse.replaceAll("<i>", "") ;
		htmlVerse = htmlVerse.replaceAll("</i>", "") ;
		htmlVerse = htmlVerse.replaceAll("<br[ ]*/>", "\n") ;
		htmlVerse = htmlVerse.replaceAll("[&]nbsp;", "") ;
		htmlVerse = htmlVerse.replaceAll("&gt;", ">") ;
		
		//System.out.println(htmlVerse) ;
		
		sanskrit_verse = htmlVerse ;
		
		htmlVerse = htmlVerse.replaceAll("[Uu]vaa?cha", "") ;
		htmlVerse = htmlVerse.replaceAll("[Dd]hritraashtra", "") ;
		htmlVerse = htmlVerse.replaceAll("[Ss]anjaya", "") ;
		htmlVerse = htmlVerse.replaceAll("[Ss]hr[ei]+", "") ;
		htmlVerse = htmlVerse.replaceAll("[Bb]hagavaa?n", "") ;
		
		p = Pattern.compile("([a-zA-Z]+[ ][a-zA-Z]+)") ;
		m = p.matcher(htmlVerse) ;
		
		if (m.find()) {
			firstTwoWordsShloka = m.group(1) ;
			String firstChar = Character.toString(firstTwoWordsShloka.charAt(0)).toUpperCase() ;
			firstTwoWordsShloka = firstChar + firstTwoWordsShloka.substring(1) ;
			
			//System.out.println(">> " + firstTwoWordsShloka ) ;
		}
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
		htmlVerse = htmlVerse.replaceAll("“", "\"") ;
		htmlVerse = htmlVerse.replaceAll("”", "\"") ;
		
		//System.out.println(htmlVerse) ;
		
		
		
		english_verse = htmlVerse ;
	}
	
	void extractCommentary() {
		
		int endOfMeanings = 0 ;
		
		Pattern p = Pattern.compile("([A-Za-z- ]+:[A-Za-z- /]+)<br[ ]*/>") ;
		//Pattern p = Pattern.compile("([A-Za-z- <>/]+):([A-Za-z- <>/]+)") ;
		Matcher m = p.matcher(content) ;
		
		while (m.find()) {
			word_meanings = word_meanings 
					+ m.group(1).replaceAll("<br[ ]*/>", " ") 
					//+ " : "
					//+ m.group(2).replaceAll("<br[ ]*/>", "\n") 
					+ "\n" ;
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
			htmlVerse = htmlVerse.replaceAll("“", "\"") ;
			htmlVerse = htmlVerse.replaceAll("”", "\"") ;
				
			ending = htmlVerse ;
			commentary = commentary.substring(0, index-1) ;
			
			//	System.out.println(">>" + m.group(1) + "<<") ;
			//System.out.println(m.start() + " " + m.end() + " " + m.group());
		}
		
		
		//System.out.println(htmlVerse) ;
		
		//sanskrit_verse = htmlVerse ;
	}
	
	void simpleSerialize(BufferedWriter buf) throws Exception {
			buf.write(title) ;
			buf.newLine() ;
			
			buf.write(sanskrit_verse) ;
			buf.newLine() ;
			
			buf.write(english_verse) ;
			buf.newLine() ;
			
			buf.write(commentary) ;
			buf.newLine() ;
	}
	
}

class XMLParser {
	
	static List<Shloka> shlokas = new Vector<Shloka>() ;
	
	static String blogXmlFile = "c:\\users\\gkm\\downloads\\blog-10-13-2012.xml" ;
	static String outTexFile = "c:/users/gkm/downloads/outg1.tex" ;
	static String texTemplateFile = "c:/users/gkm/workspace/gkm-bookparser/XMLReader/src/gita-tex-template.tex" ;
	
	static Map<String,String> chapterNames = new HashMap<String,String>() ;
	
	static String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
	 
	        Node nValue = (Node) nlList.item(0);
	 
		return nValue.getNodeValue();
	  }
	
	static NodeList getTagValues(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag);
	 
		return nlList ;
	        //Node nValue = (Node) nlList.item(0);
	 
		//return nValue.getNodeValue();
	  }
	
	static void writeFile(String path) throws Exception {
		  System.out.print("Writing to file .. " + path + " , ") ;
		  
		  BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path)));
		  
		  for (int i = shlokas.size() - 1; i >= 0; --i) {
			shlokas.get(i).simpleSerialize(writer) ;
			
			writer.newLine() ;
			writer.newLine() ;
			writer.newLine() ;
			
		  }
		  
		  writer.close() ;
		  
		  System.out.println(" done.") ;
	  }

	
	static List<Shloka> doParse(String inFileName, String outFileName, String texTemplateFile) throws Exception {
		
			chapterNames.put("1", "Arjuna Vishaada Yoga") ;
			chapterNames.put("2", "Saankhya Yoga") ;
			chapterNames.put("3", "Karma Yoga") ;
			chapterNames.put("4", "Jnyana Karma Sanyaasa Yoga") ;
			chapterNames.put("5", "Karma Sanyaasa Yoga") ;
			chapterNames.put("6", "Dhyaana Yoga") ;
			chapterNames.put("7", "Jnyaana Vijnyaana Yoga") ;
			chapterNames.put("8", "Akshara Brahma Yoga") ;
			chapterNames.put("9", "Raja Vidya Raja Guhya Yoga") ;
			chapterNames.put("10", "Vibhooti Yoga") ;
			chapterNames.put("11", "Vishwaroopa Darshana Yoga") ;
			chapterNames.put("12", "Bhakti Yoga") ;
			chapterNames.put("13", "Kshetra Kshetrajnya Vibhaaga Yoga") ;
			chapterNames.put("14", "Gunatraya Vibhaaga Yoga") ;
			chapterNames.put("15", "Purushottam Yoga") ;
			chapterNames.put("16", "Daivaasursampad Vibhaaga Yoga") ;
			chapterNames.put("17", "Shraddhatraya Vibhaaga Yoga") ;
			chapterNames.put("18", "Moksha Sanyaasa Yoga") ;
			
			
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
			    	  
			    	  shlokas.add(new Shloka(title, content, extractTerms(eElement))) ;
			    	  
			    	   
			      } else if (title.contains("ummary")) {
			    	 // System.out.println("found chapter summary") ;
			    	  String content = getTagValue("content", eElement);
			    	//  System.out.println("Content : " + content);
			    	  shlokas.add(new Shloka(title, content, Shloka.SUMMARY)) ;
			    	  
			      }
	 
			   }
			}
			
			//writeFile(outFileName) ;
			
			//Collections.reverse(shlokas) ;
			
			LatexTransform.writeLatexFile(shlokas, outFileName, texTemplateFile) ;
			
			writeFile("c:/users/gkm/downloads/tempout.txt") ;
			
			return shlokas ;
	}
	
	static List<String> extractTerms(Element theElement) {

		List <String>terms = new ArrayList<String> () ;
		
		NodeList categories = getTagValues("category", theElement);
  	  //System.out.println(categories) ;
		
  	  //System.out.println(categories) ;
  		  for (int j = 0; j < categories.getLength(); j++) {
  		
  			Node termNode = categories.item(j);
  		
  			if (termNode.getNodeType() == Node.ELEMENT_NODE) {
	 
  				Element tElem = (Element) termNode;
			      
			      //String term = getTagValue("term", tElem);
			      
				  //System.out.println(tElem.getAttribute("term")) ;
  				  terms.add(tElem.getAttribute("term")) ;
				   }
  		  }
  	  
		return terms ;
	}
			 	
}

class LatexTransform {
	
	
	

	static String html2latexTitle(Shloka s) {
		
		String str = s.title ;
		String newChapter = "" ;
		String versenum = "" ;
		String chapterNumber = "" ;
		
		if (str.contains("erse 1,")) {
			Pattern p = Pattern.compile("Chapter ([0-9]+)") ;
			Matcher m = p.matcher(str) ;
			
			if (m.find()) {
				chapterNumber = m.group(1) ;
				//System.out.println("found chapter " + chapterNumber) ;
				newChapter = "\\chapter"
				+ "{" + XMLParser.chapterNames.get(chapterNumber) + "} " ;
			}
		}
		
		
		Pattern p = Pattern.compile("Verse.? ([0-9-]+)") ;
		Matcher m = p.matcher(str) ;
		if (m.find() ) {
			versenum = m.group(1) ;
		} else if (s.type == Shloka.SUMMARY) {
			versenum = "S " ;
			s.firstTwoWordsShloka = "Summary" ;
		}
		
		p = Pattern.compile("Chapter ([0-9]+)") ;
		m = p.matcher(str) ;
		if (m.find() ) {
			chapterNumber = m.group(1) ;
		}
		
		
		String result = newChapter + "\\" + "newpage\n" + "\\section"
				+ "[" + chapterNumber + "." + versenum + " " + s.firstTwoWordsShloka + "]"
				+"{" + str + "}" ; 
		return result ;
		
	}
	
	static String html2latexSanskrit(String str) {
		
		String result = "\\textit{" + str + "}" ;
		
		result = result.replaceAll("[ ][Uu]vacha[ :]?", " uvaacha:" + "\n") ;
		result = result.replaceAll("[ ][Uu]vaacha[ :]?", " uvaacha:" + "\n") ;
		
		result = result.replaceAll("[\n]+", "\n") ;
		result = result.replaceAll("[\n]", "~\\\\\\\\" + "\n " + "\\\\" + "noindent ") ;
		//result = result.replaceAll("<br[ ][/]>", "~\\\\\\\\" + "\n " + "\\\\" + "noindent ") ;
		
		
		
		//System.out.println(result) ;
		
		return result ;
		
	}

	static String html2latexEnglish(String str) {
		String result = "\\" + "noindent" + "\\textbf{" + str + "}" ; 
		result = result.replaceAll("\u2019", "'") ;
		return result ;
		
	}
	
	static String html2latexWordMeaning(String str) {
		str = str.replaceAll("\n", "~\\\\\\\\\n") ;
		str = str.replaceAll("\u2019", "'") ;
		String result = "\\marginnote{" + str + "}\n" ; 
		return result ;
		
	}

	static String txt2latexTerms(Shloka s) {
		
		List<String> terms = s.terms ;
		String result = "" ;
		Pattern p = Pattern.compile("[0-9]+([.][0-9]+)?") ;
		
		for (int i = 0; i < terms.size(); ++i) {
			String str = terms.get(i) ;
			
			if(str.startsWith("http") || str.contains("chapter") || p.matcher(str).matches()) {
				continue ;
			}
			
			str = str.replaceAll("\n", "~\\\\\\\\\n") ;
			result += "\\index{" + str + "}\n" ; 
		}
		
		//result ;
		
		return result ;
		
	}


	static String html2latexCommentary(String str) {
		
		String result = str.replaceAll("\n\n", "\n") ;
		result = result.replaceAll("\n", "\\\\\\\\\n~\\\\\\\\") ; 
		result = result.replaceAll("&gt;", ">") ;
		result = result.replaceAll("&rsquo;", "'") ;
		result = result.replaceAll("“", "\"") ;
		result = result.replaceAll("”", "\"") ;
		result = result.replaceAll("\u2019", "'") ;
		result = result.replaceAll("%", " pct") ;
		return result ;
		
	}

	static String finalLatex(Shloka s) {
		
		String finaltext = "\n" ;
		
		if (s.type == Shloka.SUMMARY) {
			finaltext +=  html2latexTitle(s) + "\n" ;
			finaltext +=  html2latexCommentary(s.commentary) ;
			//System.out.println(finaltext) ;
			return appendFootNote(s, finaltext) ;
		}
		
		finaltext +=  html2latexTitle(s) + "\n" ;
		finaltext +=  html2latexWordMeaning(s.word_meanings) + "\n";
		finaltext +=  html2latexSanskrit(s.sanskrit_verse) +  "\\\\~\\\\\n";
		finaltext +=  html2latexEnglish(s.english_verse) + "\\\\\n\\" + "\\\\\\\\ \n";
		finaltext +=  html2latexCommentary(s.commentary) ;				
		finaltext +=  txt2latexTerms(s) ;	
		
		/* if(!s.footnotes.equals("")) {
			int seventylen = finaltext.length() * 70 / 100 ;
			
			int offset = 0 ;
			
			while(finaltext.charAt(seventylen + offset) != ' ') {
				++offset ;
			}
			
			String x1 = finaltext.substring(0, seventylen + offset) ;
			String x2 = finaltext.substring(seventylen + offset, finaltext.length()) ;
			
			
			finaltext =  x1 + " " + html2latexWordMeaning(s.footnotes) + " " + x2 ;
			//finaltext +=  html2latexWordMeaning(s.footnotes) + "\n\\" + "\\\\\\\\ \n";
		}*/
		
		finaltext = appendFootNote(s, finaltext) ;
		
		if(!s.ending.equals("")) {
			finaltext += html2latexSanskrit(s.ending) + "\n" ; 
		}
		return finaltext ;
	}

	static String appendFootNote(Shloka s, String finaltext) {
		if(!s.footnotes.equals("")) {
			int seventylen = finaltext.length() * 70 / 100 ;
			int offset = 0 ;
		
			while(finaltext.charAt(seventylen + offset) != ' ') {
				++offset ;
			}
		
			String x1 = finaltext.substring(0, seventylen + offset) ;
			String x2 = finaltext.substring(seventylen + offset, finaltext.length()) ;
		
		
			return  x1 + " " + html2latexWordMeaning(s.footnotes) + " " + x2 ;
		//finaltext +=  html2latexWordMeaning(s.footnotes) + "\n\\" + "\\\\\\\\ \n";
		} else {
			return finaltext ;
		}
	}
	
	static void writeLatexFile(List <Shloka> shlokas, String path, String texTemplateFile) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(texTemplateFile));
		String line = "" ;
		String footer = "\n" + "\\" + "printindex\n" + "\\" + "end{document}" + "\n";
		StringBuffer header = new StringBuffer();
		
		System.out.println("Reading template file " + texTemplateFile) ;
		
		while ((line = br.readLine()) != null) {
			header.append(line);
			header.append("\n") ;
		}  
		
		br.close();
		
		
		System.out.println("Writing output file " + path) ;
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path)));
		  
		writer.write(header.toString()) ;
		writer.write("\n\n") ;
		  for (int i = shlokas.size()-1 ; i >= 0; --i) {
			  writer.write(finalLatex(shlokas.get(i))) ;
		  }
		writer.write(footer) ;  
		  writer.close() ;
	  }

	
}

public class ReadXMLFile {
	

 
	public static void main(String argv[]) {
		
		try {
		//doMain() ;
		XMLParser.doParse(XMLParser.blogXmlFile, 
				XMLParser.outTexFile,
				XMLParser.texTemplateFile) ;
		  
		
		} catch (Exception e) {
				e.printStackTrace();
			  }
	}
	


}