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
	
	Shloka(String thetitle, String thecontent) {
		title = thetitle ;
		sanskrit_verse = "" ;
		english_verse = "" ;
		commentary = thecontent ;
	}
}

public class ReadXMLFile {
	
	public static List<Shloka> shlokas = new Vector<Shloka>() ;
	
	static String longtxt = "" +
			" foo : asdasdas " +
			" asdfasf : sdwrwefgvx " +
			" bnmbnmnbmn : qwtyeryqtwerqyter " ;
			
 
	public static void main(String argv[]) {
		
		System.out.println(longtxt) ;
		
		Pattern p = Pattern.compile("[a-z]+[ ]*:[ ]*[a-z]+") ;
		Matcher m = p.matcher(longtxt) ;
		
		
		while (m.find()) {
			//System.out.println(m.find()) ;
			System.out.println(m.start() + " " + m.end() + " " + m.group());
		}
		
				//doMain() ;
	}
	
	
	static void doMain() {
 
	  try {
 
		File fXmlFile = new File("c:\\users\\gmarballi\\downloads\\blog-07-25-2012.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
 
		System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
		NodeList nList = doc.getElementsByTagName("entry");
		System.out.println("-----------------------");
 
		for (int temp = 0; temp < nList.getLength(); temp++) {
 
		   Node nNode = nList.item(temp);
		   if (nNode.getNodeType() == Node.ELEMENT_NODE) {
 
		      Element eElement = (Element) nNode;
		      
		      String title = getTagValue("title", eElement);
		      
		      if (title.contains("hapter 2")) {
		    	  
		    	  String content = getTagValue("content", eElement);
		    	  String transformed_content = transformContent(content) ;
		    	  String transformed_title = transformTitle(title) ;
		    	  System.out.println("Title : " + getTagValue("title", eElement));
		    	  System.out.println("Content : " + transformed_content);
	              //System.out.println("Nick Name : " + getTagValue("nickname", eElement));
		      //System.out.println("Salary : " + getTagValue("salary", eElement));
		    	  
		    	  shlokas.add(new Shloka(transformed_title, transformed_content)) ;
		      }
 
		   }
		}
		
		writeFile("c:/users/gmarballi/downloads/gita-out.txt") ;
	  } catch (Exception e) {
		e.printStackTrace();
	  }
  }
 
	private static String transformContent(String str) {
	
		String result = str.replaceAll("[<]i[>]", "\\\\textit{") ;
		result = result.replaceAll("[<]/i[>]", "}") ;
		
		result = result.replaceAll("<b>", "\\\\textbf{") ;
		result = result.replaceAll("</b>", "}") ;
		
		result = result.replaceAll("[&]nbsp[;]", " ") ;		
		
		result = result.replaceAll("[%]", "pct") ;		
		
		result = result.replaceAll("<br />", "~\\\\\\\\"+"\n") ;
		
		result = result.replaceAll("[&]gt[;]", ">") ;
		return result ;
		
		
	}
	
	private static String transformTitle(String str) {
		
		String result = "\\section{" + str + "}" +"\n"; 
		return result ;
		
		
	}
	
	
  private static String getTagValue(String sTag, Element eElement) {
	NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
 
        Node nValue = (Node) nlList.item(0);
 
	return nValue.getNodeValue();
  }
  
  static void writeFile(String path) throws Exception {
	  BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path)));
	  
	  for (int i = 0; i < shlokas.size(); ++i) {
		  writer.write(shlokas.get(i).title) ;
		  writer.write(shlokas.get(i).commentary) ;
	  }
	  
	  writer.close() ;
  }
  
 
}