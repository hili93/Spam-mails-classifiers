import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class n_tokens_method {

	public static TreeMap spamTokensTable = new TreeMap();
	public static TreeMap hamTokensTable = new TreeMap();
	public static TreeMap resultsTreeMapSpam = new TreeMap();
	public static TreeMap resultsTreeMapHam = new TreeMap();
	/*
	 * Displays a treeMap
	 * 
	 */
	public static void showTreeMap(TreeMap tree){
		String word;
		int nbOcc;
		Set setKey = tree.keySet();
		Iterator it = setKey.iterator();

		if(tree.size()==0)
			System.err.println("TreeMap empty !");
		else{
			while( it.hasNext()){
				word = (String)it.next();
				nbOcc = ((Integer)tree.get(word)).intValue();
				System.out.println("\"" + word + "\" ---->" +nbOcc );
			}
		}
	}

	/*
	 * Adds a token to a treeMap and update it number of occurences
	 */
	public static void addToTreeMap(String token, TreeMap tree){
		int nbOcc=0;
		if (tree.containsKey(token))
		{
			nbOcc = ((Integer)tree.get(token)).intValue();
			nbOcc++;
		}
		else 
			nbOcc = 1;
		tree.put(token, new Integer(nbOcc));
	}

	/*
	 * To split the test to 1 words and 2 words and puts them in one TreeMap
	 * Has to be changed to put them in different TreeMaps for later calculation of probabilities
	 * 
	 */
	public static void tokenizeTrain(File file,TreeMap table) throws IOException{

		BufferedReader entree = new BufferedReader(new FileReader(file));
		String ligne;
		StringTokenizer st;
		String token1,token2,tokens;
		int nbOcc;

		while ((ligne = entree.readLine()) != null){
			st = new StringTokenizer(ligne, " ,.;:_-+*/\\.;\n\"'{}()=><\t!?");

			token1 = st.nextToken();
			addToTreeMap(token1,table);
			while(st.hasMoreTokens()){
				token2 = st.nextToken();
				tokens = token1+token2;

				addToTreeMap(tokens,table);

				addToTreeMap(token2,table);

				token1=token2;
			}
		}
	}

	public static int getNbOcc(String token,TreeMap table){
		if(table.containsKey(token))
			return ((Integer)table.get(token)).intValue();
		else
			return 1;
	}

	/*
	 * Tokenize a mail, and gets it's probability value, from the table of tokens given
	 */
	public static float tokenizeTest(File file,TreeMap tableTokens) throws IOException{
		BufferedReader entree = new BufferedReader(new FileReader(file));
		String ligne;
		StringTokenizer st;
		String token1,token2,tokens;
		int nbOccToken1=0,nbOccTokens=0;
		float mailValue=(float)0;


		while ((ligne = entree.readLine()) != null){
			st = new StringTokenizer(ligne, " ,.;:_-+*/\\.;\n\"'{}()=><\t!?");

			token1 = st.nextToken();

			while(st.hasMoreTokens()){
				token2 = st.nextToken();
				tokens = token1+token2;

				nbOccToken1 = getNbOcc(token1,tableTokens);
				nbOccTokens = getNbOcc(tokens,tableTokens);
				float tmp = (float)nbOccTokens/(float)nbOccToken1;

				mailValue += (float)tmp;

				token1=token2;
			}
		}
		return mailValue;

	}

	public static TreeMap createTokenTable(String repName) throws IOException{
		TreeMap tokensTable = new TreeMap();

		File files = new File(repName);
		String[]  listFiles = files.list();

		for(int i=0;i<listFiles.length;i++){
			File currentFile = new File(repName+listFiles[i]);
			tokenizeTrain(currentFile,tokensTable);
		}
		return tokensTable;
	}

	public static void showTrainResults(TreeMap spamTokensTable, TreeMap hamTokensTable){

		System.out.println("Training done ! ");
		System.out.println("\tSpam Tree size = "+ spamTokensTable.size());
		System.out.println("\tHam Tree size = "+ hamTokensTable.size());
	}

	public static TreeMap trainSpam(String datasetName,TreeMap spamTokensTable) throws IOException{
		String spamRepName = ".//Spam_test/"+datasetName+"/spam-train/";

		spamTokensTable = createTokenTable(spamRepName);

		//showTrainResults(spamTokensTable,hamTokensTable);
		return spamTokensTable;
	}
	public static TreeMap trainHam(String datasetName, TreeMap hamTokensTable) throws IOException {
		String hamRepName = ".//Spam_test/"+datasetName+"/ham-train/";

		hamTokensTable = createTokenTable(hamRepName);

		//showTrainResults(spamTokensTable,hamTokensTable);
		return hamTokensTable;
	}

	public static void datasetInfo(int nbHamMails,int nbSpamMails){
		System.out.println("Dataset Info :");
		System.out.println("\tNumber of ham files = "+nbHamMails);
		System.out.println("\tNumber of spam files = "+ nbSpamMails);

	}
	public static void showTestResults(String[] listSpamFiles, String[] listHamFiles,int spamCount,int hamCount,String spamOrHam){
		int nbSpamMails = listSpamFiles.length;
		int nbHamMails = listHamFiles.length;
		float spamErr = (float)spamCount*100/nbSpamMails;
		float hamErr = (float)hamCount*100/nbHamMails;


		if(spamOrHam.equals("spam"))
			System.out.println("Spam error rate = " +spamErr);
		else if(spamOrHam.equals("ham"))
			System.out.println("Ham error rate = "+ hamErr);
	}
	public static TreeMap test(String dataset, TreeMap tableSpamTokens,TreeMap tableHamTokens, TreeMap resultsTreeMap, String spamOrHam) throws IOException{
		float spamPr=(float)0,hamPr=(float)0;
		int spamErrorRate=0,hamErrorRate=0;

		File spamRep = new File(".//Spam_test/"+dataset+"/spam-test/");
		String[]  listSpamFiles = spamRep.list();

		File hamRep = new File(".//Spam_test/"+dataset+"/ham-test/");
		String[]  listHamFiles = hamRep.list();

		int nbSpamMails = listSpamFiles.length;
		int nbHamMails = listHamFiles.length;
		

		
		if(spamOrHam.equals("spam")){
			datasetInfo( nbHamMails, nbSpamMails);
			for(int i=0;i<listSpamFiles.length;i++){
				File currentFile = new File(spamRep+"/"+listSpamFiles[i]);
				if(tokenizeTest(currentFile,tableSpamTokens)>tokenizeTest(currentFile,tableHamTokens)){
					resultsTreeMap.put(currentFile.getPath(), "ham");
					spamErrorRate++;
				}
				else
					resultsTreeMap.put(currentFile.getPath(), "spam");
			}
		}
		else if(spamOrHam.equals("ham")){
			for(int i=0;i<listHamFiles.length;i++){
				File currentFile = new File(hamRep+"/"+listHamFiles[i]);
				if(tokenizeTest(currentFile,tableSpamTokens)<tokenizeTest(currentFile,tableHamTokens)){
					resultsTreeMap.put(currentFile.getPath(), "spam");
					hamErrorRate++;
				}
				else
					resultsTreeMap.put(currentFile.getPath(), "ham");
			}
		}
		showTestResults(listSpamFiles, listHamFiles, spamErrorRate, hamErrorRate,spamOrHam);

		return resultsTreeMap;
	}

	/*
	 * Writes a TreeMap in a file
	 */
	public static void writeTreeMapInFile(TreeMap table, File file) throws IOException{
		int nbWords=0,nbOcc=0;
		String mot;
		Set setKey = table.keySet();
		Iterator it = setKey.iterator();
		FileWriter out = new FileWriter(file);

		while( it.hasNext()){
			mot = (String)it.next();
			nbOcc=((Integer) table.get(mot)).intValue();

			//Writing in the file
			out.write(mot+" "+nbOcc+"\n");
			out.flush();   // Flush the buffer and write all changes to the disk
		}

		out.close(); 
	}
	public static void showResultsTreeMap(TreeMap tree){
		String mail;
		String value;
		Set setKey = tree.keySet();
		Iterator it = setKey.iterator();

		if(tree.size()==0)
			System.err.println("TreeMap empty !");
		else{
			while( it.hasNext()){
				mail = (String)it.next();
				value = (String) tree.get(mail);
				System.out.println("\"" + mail + "\" ---->" +value );
			}
		}
	}
	public static TreeMap mainN_tokens_method(TreeMap resultsTreeMap,String spamOrHam) throws IOException{
		String dataName = "enron1";

		spamTokensTable = trainSpam(dataName, spamTokensTable);
		hamTokensTable = trainHam(dataName, hamTokensTable);

		if(spamOrHam.equals("spam"))
			return test(dataName, spamTokensTable, hamTokensTable, resultsTreeMapSpam,"spam");
		else if(spamOrHam.equals("ham"))
			return test(dataName, spamTokensTable, hamTokensTable, resultsTreeMapHam,"ham");
		else{
			System.err.println("mainN_tokens_method error !!!");
			return resultsTreeMap;
		}
	}


	public static void main(String[] args) throws IOException {

		resultsTreeMapSpam = mainN_tokens_method(resultsTreeMapSpam,"spam");
		resultsTreeMapHam = mainN_tokens_method(resultsTreeMapHam,"ham");


	}
}