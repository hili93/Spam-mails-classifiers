import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;



public class Preprocess {
	static String[] months = {"january", 
		"february", 
		"march", 
		"april", 
		"may", 
		"june", 
		"july", 
		"august", 
		"september", 
		"october", 
		"november", 
	"december"};
	static String[] stopWords = {"a", "Subject", "subject",
		"able", 
		"about", 
		"across", 
		"after", 
		"all", 
		"almost", 
		"also", 
		"am", 
		"among", 
		"an", 
		"and", 
		"any", 
		"are", 
		"as", 
		"at", 
		"be", 
		"because", 
		"been", 
		"but", 
		"by", 
		"can", 
		"cannot", 
		"could", 
		"dear", 
		"did", 
		"do", 
		"does", 
		"either", 
		"else", 
		"ever", 
		"every", 
		"for", 
		"from", 
		"get", 
		"got", 
		"had", 
		"has", 
		"have", 
		"he", 
		"her", 
		"hers", 
		"him", 
		"his", 
		"how", 
		"however", 
		"i", 
		"if", 
		"in", 
		"into", 
		"is", 
		"it", 
		"its", 
		"just", 
		"least", 
		"let", 
		"like", 
		"likely", 
		"may", 
		"me", 
		"might", 
		"most", 
		"must", 
		"my", 
		"neither", 
		"no", 
		"nor", 
		"not", 
		"of", 
		"off", 
		"often", 
		"on", 
		"only", 
		"or", 
		"other", 
		"our", 
		"own", 
		"rather", 
		"said", 
		"say", 
		"says", 
		"she", 
		"should", 
		"since", 
		"so", 
		"some", 
		"than", 
		"that", 
		"the", 
		"their", 
		"them", 
		"then", 
		"there", 
		"these", 
		"they", 
		"this",  
		"to", 
		"too",  
		"us", 
		"wants", 
		"was", 
		"we", 
		"were", 
		"what", 
		"when", 
		"where", 
		"which", 
		"while", 
		"who", 
		"whom", 
		"why", 
		"will", 
		"with", 
		"would", 
		"yet", 
		"you", 
	"your"};							



	public static void tokenize(File file,TreeMap table) throws IOException{

		BufferedReader entree = new BufferedReader(new FileReader(file));
		String ligne;
		StringTokenizer st;
		String mot;
		int nbOcc;

		while ((ligne = entree.readLine()) != null)
		{
			st = new StringTokenizer(ligne, " ,.;:_-+*/\\.;\n\"'{}()=><\t!?#%@|");
			while(st.hasMoreTokens())
			{
				mot = st.nextToken();
				if (table.containsKey(mot))
				{
					nbOcc = ((Integer)table.get(mot)).intValue();
					nbOcc++;
				}
				else nbOcc = 1;
				table.put(mot, new Integer(nbOcc));
			}
		}


		Set setKey = table.keySet();
		Iterator it = setKey.iterator();

		while( it.hasNext()){
			mot = (String)it.next();
			nbOcc = ((Integer)table.get(mot)).intValue();
			//System.out.println("\"" + mot + "\" ---->" +nbOcc );
		}

	}
	public static void writeTreeMapInFile(File file,TreeMap tree) throws IOException{
		String mot=null;
		int c=0;
		FileWriter writer=new FileWriter(file);

		Set setKey = tree.keySet();
		Iterator it = setKey.iterator();

		while( it.hasNext()){
			c=0;
			mot = (String)it.next();
			for(int i=0;i<months.length;i++)
				if(mot.equals(months[i]))
					c=1;
			for(int i=0;i<stopWords.length;i++)
				if(mot.equals(stopWords[i]))
					c=1;
			if(mot.matches("[0-9]+"))
				c=1;
			if(c==0)
				writer.write(mot + " ");

		}
		writer.close();

	}
	public static void main(String[] args) throws IOException {

		File spamFiles = new File(".//Spam_test/enron1/spam-test");
		String[]  listSpamFiles = spamFiles.list();

		TreeMap tree;

		for(int i=0;i<listSpamFiles.length;i++){
			tree = new TreeMap();
			File currentFile = new File(".//Spam_test/enron1/spam-test/"+listSpamFiles[i]);

			tokenize(currentFile,tree);
			writeTreeMapInFile(currentFile,tree);

			System.out.println(i);
		}
		System.out.println("done !");

	}
}
