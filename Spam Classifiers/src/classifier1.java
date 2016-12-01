import java.io.*;
import java.util.*;

public class classifier1 {

	public String[] listRepository(File repository){
		String[] listFiles=repository.list();

		for(int i=0;i<listFiles.length;i++){
			//System.out.println(listFiles[i]);
		}	
		return listFiles;
	}

	public void showFileContent(String file) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(file)) ;
		String line = null;
		while ((line = br.readLine()) != null) {
			//System.out.println(line);
		}

	}

	public static void numberOfWords(File file,TreeMap table) throws IOException{

		BufferedReader entree = new BufferedReader(new FileReader(file));
		String ligne;
		StringTokenizer st;
		String mot;
		int nbOcc;

		while ((ligne = entree.readLine()) != null)
		{
			st = new StringTokenizer(ligne, " ,.;:_-+*/\\.;\n\"'{}()=><\t!?");
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

	static <K,V extends Comparable<? super V>>
	SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
		SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
				new Comparator<Map.Entry<K,V>>() {
					@Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
						int res = e1.getValue().compareTo(e2.getValue());
						return res != 0 ? res : 1;
					}
				}
				);
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}

	public static void intercectionPercentage(TreeMap resultTable,File file1,File file2) throws IOException{

		numberOfWords( file1, resultTable);
		numberOfWords( file2, resultTable);

		int nbWords=0;
		String mot;
		Set setKey = resultTable.keySet();
		Iterator it = setKey.iterator();

		while( it.hasNext()){
			mot = (String)it.next();
			if(((Integer)resultTable.get(mot)).intValue() !=1)
				nbWords++;

		}

		System.out.println("********************");

		//System.out.println("Number of words in common: "+nbWords);

	}

	/*
	 * Calcuates the overlap between two treeMaps
	 */
	public static int wordsInCommon(TreeMap table1,TreeMap table2){

		String mot1,mot2;
		int nbWords=0;

		Set setKey1 = table1.keySet();
		Iterator it1 = setKey1.iterator();

		Set setKey2 = table2.keySet();
		Iterator it2 = setKey2.iterator();

		while( it1.hasNext()){
			mot1 = (String)it1.next();
			it2 = setKey2.iterator();
			while(it2.hasNext()){
				mot2 = (String)it2.next();
				if(mot1.equals(mot2))
					nbWords++;
			}
		}

		return nbWords;
	}
	// Takes the tokens whom repeatence number is more than 1, and place them in the result table
	public static void repeatPercentage(TreeMap tokensTable,TreeMap resultTable){
		String mot;
		int nbOcc;

		Set setKey = tokensTable.keySet();
		Iterator it = setKey.iterator();

		while( it.hasNext()){
			mot = (String)it.next();
			nbOcc=((Integer)tokensTable.get(mot)).intValue();
			if( nbOcc>10)
				resultTable.put(mot, new Integer(nbOcc));
		}
	}

	/**
	 * Checks if a mail is a spam or not 
	 * @return 
	 * @throws IOException 
	 * 
	 **/

	public static float checkMail(File mail, TreeMap spamTree) throws IOException{

		TreeMap tokensTable = new TreeMap();
		numberOfWords(mail,tokensTable);

		int nbOccSpam=0,nbOccNonSpam=0, totalOccSpam=0,totalOccNonSpam =0,Total=0;

		float results=0;
		String mot;
		Set setKey = tokensTable.keySet();
		Iterator it = setKey.iterator();

		while( it.hasNext()){
			mot = (String)it.next();
			//checking in spam tree
			if(spamTree.containsKey(mot))
				nbOccSpam=((Integer)spamTree.get(mot)).intValue();
			else if(!spamTree.containsKey(mot))
				nbOccSpam=0;

			//cheking in non spam tree
			/*if(nonSpamTree.containsKey(mot))
				nbOccNonSpam=((Integer)nonSpamTree.get(mot)).intValue();
			else if(!nonSpamTree.containsKey(mot))
				nbOccNonSpam=0;
			 */


			totalOccSpam += nbOccSpam*100/350;
			totalOccNonSpam += nbOccNonSpam*100/350;
			Total += (nbOccSpam*100/350)+(nbOccNonSpam*100/350);
		}

		results = (float) nbOccSpam*100/Total;
		//results[1] = (float) nbOccNonSpam/Total;
		//System.out.println("Result Spam: "+ results[0]);
		//System.out.println("Result non Spam: "+ results[1]);

		return results;

	}
	public static void main(String[] arg) throws IOException{

		classifier1 r = new classifier1();
		float SpamMax=0,SpamMin=(float) 0.01;
		float NonSpamMin=(float) 0.01,NonSpamMax=0;
		float[] ResultsSpam = null, ResultsNonSpam=null;
		float ResultSpam = 0, ResultNonSpam =0;
		int spams=0,nonspams=0;
		String[] listSpamTestFiles,listNonSpamTestFiles;
		File file;
		File repSpamTest = new File(".//Spam_test/spam-train");
		listSpamTestFiles = r.listRepository(repSpamTest);
		File repNonSpamTest = new File(".//Spam_test/nonSpam-train");
		listNonSpamTestFiles = r.listRepository(repNonSpamTest);



		/*repSpamTest = new File(".//Spam_test/spam-test");
		listSpamTestFiles = r.listRepository(repSpamTest);

		repNonSpamTest = new File(".//Spam_test/nonspam-test");
		listNonSpamTestFiles = r.listRepository(repNonSpamTest);*/

		/*System.out.println("--->Choose a file: ");
		sc = new Scanner(System.in);
		String fileName = sc.nextLine();

		r.showFileContent(".//Spam_test/"+repName+"/"+fileName);
		file = new File(".//Spam_test/"+repName+"/"+fileName);

		 */
		listSpamTestFiles= r.listRepository(repSpamTest);
		System.out.println("****************");		

		listNonSpamTestFiles= r.listRepository(repNonSpamTest);
		System.out.println("****************");		

		TreeMap spamTestTable = new TreeMap();
		TreeMap nonSpamTestTable = new TreeMap();

		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("result_spam_train.txt"), "utf-8"));

		for(int i=0;i<listSpamTestFiles.length;i++){
			//System.out.println("file name :"+rep.getPath()+"/"+listFiles[i]);
			File currentFile = new File(repSpamTest.getPath()+"/"+listSpamTestFiles[i]);
			r.numberOfWords(currentFile,spamTestTable);
		}

		for(int i=0;i<listNonSpamTestFiles.length;i++){
			//System.out.println("file name :"+rep.getPath()+"/"+listFiles[i]);
			File currentFile = new File(repNonSpamTest.getPath()+"/"+listNonSpamTestFiles[i]);
			r.numberOfWords(currentFile,nonSpamTestTable);
		}

		System.out.println("11111111111111111");
		System.out.println("Total number of tockens Spam (words): "+spamTestTable.size());
		System.out.println("*******************************************");			
		//System.out.println(entriesSortedByValues(spamTestTable));
		System.out.println("*******************************************");

		System.out.println("11111111111111111");
		System.out.println("Total number of nonSpam tockens(words): "+nonSpamTestTable.size());
		System.out.println("*******************************************");			
		//System.out.println(entriesSortedByValues(nonSpamTestTable));
		System.out.println("*******************************************");
		// Writing the words on a file result.txt
		TreeMap table = new TreeMap();
		//writeInFile( table, writer);


		// Calculate the number of words in common between two files.
		TreeMap resultTable = new TreeMap();
		File file1 = new File("result_non_spam_train.txt");
		File file2 = new File("result_spam_train.txt");
		intercectionPercentage(resultTable, file1, file2);


		//writer.close();

		/* We will now calculate the percentage of repeated tokens in both spam and non spam */
		TreeMap resultSpamTable = new TreeMap();
		TreeMap resultNonSpamTable = new TreeMap();

		repeatPercentage(spamTestTable, resultSpamTable);
		System.out.println("Number of tokens left in spam: "+resultSpamTable.size()+"-->"+(float)100*resultSpamTable.size()/spamTestTable.size()+"%");

		repeatPercentage(nonSpamTestTable, resultNonSpamTable);
		System.out.println("Number of tokens left in non spam: "+resultNonSpamTable.size()+"-->"+(float)100*resultNonSpamTable.size()/nonSpamTestTable.size()+"%\n");

		// verifying the result tables
		System.out.println("verifying the result tables");
		System.out.println("non spam "+r.entriesSortedByValues(resultNonSpamTable));
		System.out.println("spam" +r.entriesSortedByValues(resultSpamTable));

		System.out.println("Words in common between "+spamTestTable.size()+"and "+nonSpamTestTable.size()+": "+r.wordsInCommon(spamTestTable,nonSpamTestTable));
		System.out.println("Words in common resultTables between "+resultSpamTable.size()+"and "+resultNonSpamTable.size()+": "+r.wordsInCommon(resultSpamTable,resultNonSpamTable));


		// The user types a repository name to test it
		System.out.println("-->Choose repository: ");
		Scanner  sc = new Scanner(System.in);
		String repName = sc.nextLine();



		File mailsToCheck = new File(".//Spam_test/"+repName);

		String[] listMails = r.listRepository(mailsToCheck);
		ResultsSpam = new float[listMails.length];
		ResultsNonSpam = new float[listMails.length];
		int cnt=0;
		float Results =0;

		for (int i=0;i<listMails.length;i++){
			File mailToCheck = new File(".//Spam_test/"+repName+"/"+listMails[i]);
			Results = checkMail(mailToCheck,resultSpamTable);
			ResultsSpam[i]= Results;
			//ResultsNonSpam[i]= Results[1];
		}
		float averageSpam = 0,averageNonSpam=0;
		System.out.println("-------------------------------");
		for(int i=0;i<listMails.length;i++){
			System.out.println("Result spam equation= "+ResultsSpam[i]);
			//System.out.println("Result nonspam equation= "+ResultsNonSpam[i]);

			if(SpamMax<ResultsSpam[i])
				SpamMax = ResultsSpam[i];
			if(SpamMin>ResultsSpam[i] && ResultsSpam[i]!=0)
				SpamMin = ResultsSpam[i];
			if(ResultsSpam[i]>0.0027)
				spams++;
			if(ResultsSpam[i]!=0){
				averageSpam+=ResultsSpam[i];
				cnt++;
			}
			/*
			if(NonSpamMax<ResultsNonSpam[i])
				NonSpamMax = ResultsNonSpam[i];
			if(NonSpamMin>ResultsNonSpam[i] && ResultsNonSpam[i]!=0)
				NonSpamMin = ResultsNonSpam[i];

			averageNonSpam+=ResultsNonSpam[i];
			 */
		}



		System.out.println("----------------------------------------------------");
		System.out.println("Number of mails: "+ResultsSpam.length);
		System.out.println("----------------------------------------------------");
		System.out.println("The average of result spam equation: "+ (float)averageSpam/cnt);
		System.out.println("MAX = "+SpamMax+"\tMin = "+SpamMin);

		/*System.out.println("----------------------------------------------------");
		System.out.println("The average of result nonspam equation: "+ (float)averageNonSpam/ResultsNonSpam.length);
		System.out.println("MAX = "+NonSpamMax+"\tMin = "+NonSpamMin);
		 */
		System.out.println("----------------------------------------------------");
		System.out.println("Number of zeros : "+ (ResultsSpam.length-cnt));
		System.out.println("----------------------------------------------------");
		System.out.println("number of spams: "+(float)spams*100/ResultsSpam.length+"%");

	}
}
