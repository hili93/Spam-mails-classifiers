import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;


public class Classifier2 {
	public static int ngramCte = 6;
	public static void tokenize(File file,TreeMap table) throws IOException{

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
	/*
	 * Return the value of the token given in parameter
	 */
	public static int tokenValue(String token,TreeMap<String, Integer> spamTable){
		String mot;
		int nbOcc = 0;
		Set setKey = spamTable.keySet();
		Iterator it = setKey.iterator();


		while( it.hasNext()){
			it.next();
			if(spamTable.containsKey(token))
				nbOcc=((Integer) spamTable.get(token)).intValue();
		}

		//System.out.println(token+" in spam--> "+nbOcc);

		return nbOcc;
	}


	public static float parcourirTableOfTokens(File mail,TreeMap tableSpamTokens,TreeMap tableHamTokens) throws IOException{
		TreeMap tokensTable = new TreeMap();
		tokenize(mail,tokensTable);

		int nbOccSpam=0,nbOccHam=0,num=0,denum=0;
		float mailValue=0;

		float results=0;
		String mot;
		Set setKey = tokensTable.keySet();
		Iterator it = setKey.iterator();

		while( it.hasNext()){
			mot = (String)it.next();
			//checking token in spam tree
			if(tableSpamTokens.containsKey(mot))
				nbOccSpam=((Integer)tableSpamTokens.get(mot)).intValue();
			else if(!tableSpamTokens.containsKey(mot))
				nbOccSpam=0;

			//cheking token in non spam tree
			if(tableHamTokens.containsKey(mot))
				nbOccHam=((Integer)tableHamTokens.get(mot)).intValue();
			else if(!tableHamTokens.containsKey(mot))
				nbOccHam=0;


			num += nbOccSpam;
			denum += nbOccSpam+nbOccHam;
			//System.out.println("----------------");
			//System.out.println("+" + nbOccSpam +"="+num);
			//System.out.println(denum);

		}

		mailValue = (float)num/denum;
		//System.out.println(mailValue);

		return mailValue;

	}

	public static void writeValuesInFile(FileWriter writer, float f) throws IOException{
		writer.write(Float.toString(f)+"\n");
		writer.flush();
	}

	public static int trainAndTestListOfFilesMethod1(String datasetName, ArrayList<String> listMails,String spamOrHam) throws IOException{
		int count=0;
		float threshold = (float) 0.529999;
		Classifier2 t = new Classifier2();

		File spamFiles = new File(".//Spam_test/"+datasetName+"/spam-train/");
		String[]  listSpamFiles = spamFiles.list();

		TreeMap<String, Integer> spamTokens = new TreeMap<String, Integer>();

		for(int i=0;i<listSpamFiles.length;i++){
			File currentFile = new File(".//Spam_test/"+datasetName+"/spam-train/"+listSpamFiles[i]);
			t.tokenize(currentFile,spamTokens);
		}

		File hamFiles = new File(".//Spam_test/"+datasetName+"/ham-train/");
		String[]  listHamFiles = hamFiles.list();
		TreeMap<String, Integer> hamTokens = new TreeMap<String, Integer>();
		for(int i=0;i<listHamFiles.length;i++){
			File currentFile = new File(".//Spam_test/"+datasetName+"/ham-train/"+listHamFiles[i]);
			t.tokenize(currentFile,hamTokens);
		}
		System.out.println("training done !");

		// Testing the list of mails
		for(int i=0;i<listMails.size();i++){
			File mail = new File(listMails.get(i));

			//System.out.println(parcourirTableOfTokens( mail,spamTokens, hamTokens));
			if(spamOrHam.equals("ham"))
				if(parcourirTableOfTokens( mail,spamTokens, hamTokens)>threshold)
					count++;
			if(spamOrHam.equals("spam"))
				if(parcourirTableOfTokens( mail,spamTokens, hamTokens)<threshold)
					count++;

		}

		//System.out.println("Done !");
		System.out.println("RESULT : "+spamOrHam+" = "+(float)count*100/listMails.size()+"%");
		System.out.println("RESULT : nb of "+spamOrHam+" = "+count);
		return count;
	}
	public static TreeMap tokenizationClassifier(String datasetName,TreeMap resultsTreeMap,String spamOrHam) throws IOException{
		int count1=0,count2=0;
		float threshold = (float) 0.529999;
		Classifier2 t = new Classifier2();

		File spamFiles = new File(".//Spam_test/"+datasetName+"/spam-train/");
		String[]  listSpamFiles = spamFiles.list();

		TreeMap<String, Integer> spamTokens = new TreeMap<String, Integer>();

		for(int i=0;i<listSpamFiles.length;i++){
			File currentFile = new File(".//Spam_test/"+datasetName+"/spam-train/"+listSpamFiles[i]);
			t.tokenize(currentFile,spamTokens);
		}


		//Writing the tokens treemap in a file spamTokens.txt

		//File spamFile = new File("spamTokens.txt");
		//writeTreeMapInFile(spamTokens, spamFile);

		File hamFiles = new File(".//Spam_test/"+datasetName+"/ham-train/");
		String[]  listHamFiles = hamFiles.list();
		TreeMap<String, Integer> hamTokens = new TreeMap<String, Integer>();
		for(int i=0;i<listHamFiles.length;i++){
			File currentFile = new File(".//Spam_test/"+datasetName+"/ham-train/"+listHamFiles[i]);
			t.tokenize(currentFile,hamTokens);
		}

		listSpamFiles = new File(".//Spam_test/"+datasetName+"/spam-test/").list();
		listHamFiles = new File(".//Spam_test/"+datasetName+"/ham-test/").list();


		// Testing on ham test emails
		if(spamOrHam.equals("ham")){
			for(int i=0;i<listHamFiles.length;i++){
				File mail = new File(".//Spam_test/"+datasetName+"/ham-test/"+listHamFiles[i]);
				//System.out.println(parcourirTableOfTokens( mail,spamTokens, hamTokens));
				if(parcourirTableOfTokens( mail,spamTokens, hamTokens)>threshold){
					count1++;
					resultsTreeMap.put(mail.getPath(),"ham");
				}
				else
					resultsTreeMap.put(mail.getPath(),"spam");
			}
			System.out.println("Ham error rate : "+(float)count1*100/listHamFiles.length+"%");
		}
		else if(spamOrHam.equals("spam")){
			// Testing on spam test emails
			for(int i=0;i<listSpamFiles.length;i++){
				File mail = new File(".//Spam_test/"+datasetName+"/spam-test/"+listSpamFiles[i]);
				//System.out.println("mail "+ listSpamFiles[0]+" Value= "+parcourirTableOfTokens( mail,spamTokens, hamTokens));
				if(parcourirTableOfTokens( mail,spamTokens, hamTokens)<threshold){
					count2++;
					resultsTreeMap.put(mail.getPath(),"spam");
				}
				else
					resultsTreeMap.put(mail.getPath(),"ham");
			}
			System.out.println("Spam error rate : "+(float)count2*100/listSpamFiles.length+"%");
		}
		return resultsTreeMap;
	}

	public static void trainAndTestMethode1(String datasetName) throws IOException{
		int count1=0,count2=0;
		float threshold = (float) 0.529999;
		Classifier2 t = new Classifier2();

		File spamFiles = new File(".//Spam_test/"+datasetName+"/spam-train/");
		String[]  listSpamFiles = spamFiles.list();

		TreeMap<String, Integer> spamTokens = new TreeMap<String, Integer>();

		for(int i=0;i<listSpamFiles.length;i++){
			File currentFile = new File(".//Spam_test/"+datasetName+"/spam-train/"+listSpamFiles[i]);
			t.tokenize(currentFile,spamTokens);
		}


		//Writing the tokens treemap in a file spamTokens.txt

		//File spamFile = new File("spamTokens.txt");
		//writeTreeMapInFile(spamTokens, spamFile);

		File hamFiles = new File(".//Spam_test/"+datasetName+"/ham-train/");
		String[]  listHamFiles = hamFiles.list();
		TreeMap<String, Integer> hamTokens = new TreeMap<String, Integer>();
		for(int i=0;i<listHamFiles.length;i++){
			File currentFile = new File(".//Spam_test/"+datasetName+"/ham-train/"+listHamFiles[i]);
			t.tokenize(currentFile,hamTokens);
		}

		listSpamFiles = new File(".//Spam_test/"+datasetName+"/spam-test/").list();
		listHamFiles = new File(".//Spam_test/"+datasetName+"/ham-test/").list();


		// Testing on ham test emails
		for(int i=0;i<listHamFiles.length;i++){
			File mail = new File(".//Spam_test/"+datasetName+"/ham-test/"+listHamFiles[i]);
			//System.out.println(parcourirTableOfTokens( mail,spamTokens, hamTokens));
			if(parcourirTableOfTokens( mail,spamTokens, hamTokens)>threshold)
				count1++;
		}

		//System.out.println("Done !");
		System.out.println("HAAAAAM  TEST "+(float)count1*100/listHamFiles.length+"%");


		// Testing on spam test emails
		for(int i=0;i<listSpamFiles.length;i++){
			File mail = new File(".//Spam_test/"+datasetName+"/spam-test/"+listSpamFiles[i]);
			//System.out.println("mail "+ listSpamFiles[0]+" Value= "+parcourirTableOfTokens( mail,spamTokens, hamTokens));
			if(parcourirTableOfTokens( mail,spamTokens, hamTokens)<threshold)
				count2++;
		}
		System.out.println("SPAAAAM   TEST "+(float)count2*100/listSpamFiles.length+"% " );


	}

	/*
	 * Cross validation method for training
	 */
	public static void trainAndTestMethod2() throws IOException{
		File spamFiles = new File(".//Spam_test/train+test-spam/");
		String[]  listSpamFiles = spamFiles.list();

		File hamFiles = new File(".//Spam_test/train+test-nonSpam/");
		String[]  listHamFiles = hamFiles.list();

		// Preparing dataset

		// Spam Tokens tables
		TreeMap<String, Integer> spamTokens1 = new TreeMap<String, Integer>();
		TreeMap<String, Integer> spamTokens2 = new TreeMap<String, Integer>();
		TreeMap<String, Integer> spamTokens3 = new TreeMap<String, Integer>();
		TreeMap<String, Integer> spamTokens4 = new TreeMap<String, Integer>();
		TreeMap<String, Integer> spamTokens5 = new TreeMap<String, Integer>();
		// Ham Tokens Tables
		TreeMap<String, Integer> hamTokens1 = new TreeMap<String, Integer>();
		TreeMap<String, Integer> hamTokens2 = new TreeMap<String, Integer>();
		TreeMap<String, Integer> hamTokens3 = new TreeMap<String, Integer>();
		TreeMap<String, Integer> hamTokens4 = new TreeMap<String, Integer>();
		TreeMap<String, Integer> hamTokens5 = new TreeMap<String, Integer>();

		for(int i=0;i<99;i++){
			File currentFile = new File(".//Spam_test/train+test-spam/"+listSpamFiles[i]);
			//System.out.println(listSpamFiles[i]);
			tokenize(currentFile,spamTokens2);
			tokenize(currentFile,spamTokens3);
			tokenize(currentFile,spamTokens4);
			tokenize(currentFile,spamTokens5);
		}
		for(int i=100;i<199;i++){
			File currentFile = new File(".//Spam_test/train+test-spam/"+listSpamFiles[i]);
			tokenize(currentFile,spamTokens1);
			tokenize(currentFile,spamTokens3);
			tokenize(currentFile,spamTokens4);
			tokenize(currentFile,spamTokens5);
		}
		for(int i=200;i<299;i++){
			File currentFile = new File(".//Spam_test/train+test-spam/"+listSpamFiles[i]);			
			tokenize(currentFile,spamTokens1);
			tokenize(currentFile,spamTokens2);
			tokenize(currentFile,spamTokens4);
			tokenize(currentFile,spamTokens5);
		}
		for(int i=300;i<399;i++){
			File currentFile = new File(".//Spam_test/train+test-spam/"+listSpamFiles[i]);
			tokenize(currentFile,spamTokens1);
			tokenize(currentFile,spamTokens2);
			tokenize(currentFile,spamTokens3);
			tokenize(currentFile,spamTokens5);
		}
		for(int i=400;i<listSpamFiles.length;i++){
			File currentFile = new File(".//Spam_test/train+test-spam/"+listSpamFiles[i]);
			tokenize(currentFile,spamTokens1);
			tokenize(currentFile,spamTokens2);
			tokenize(currentFile,spamTokens3);
			tokenize(currentFile,spamTokens4);
		}
		System.out.println("Training Spam done !");
		// training Ham
		for(int i=100;i<199;i++){
			File currentFile = new File(".//Spam_test/train+test-nonSpam/"+listHamFiles[i]);
			tokenize(currentFile,hamTokens2);
			tokenize(currentFile,hamTokens3);
			tokenize(currentFile,hamTokens4);
			tokenize(currentFile,hamTokens5);
		}
		for(int i=200;i<299;i++){
			File currentFile = new File(".//Spam_test/train+test-nonSpam/"+listHamFiles[i]);
			tokenize(currentFile,hamTokens1);
			tokenize(currentFile,hamTokens3);
			tokenize(currentFile,hamTokens4);
			tokenize(currentFile,hamTokens5);
		}
		for(int i=300;i<399;i++){
			File currentFile = new File(".//Spam_test/train+test-nonSpam/"+listHamFiles[i]);
			tokenize(currentFile,hamTokens1);
			tokenize(currentFile,hamTokens2);
			tokenize(currentFile,hamTokens3);
			tokenize(currentFile,hamTokens5);
		}
		for(int i=400;i<listSpamFiles.length;i++){
			File currentFile = new File(".//Spam_test/train+test-nonSpam/"+listHamFiles[i]);
			tokenize(currentFile,hamTokens1);
			tokenize(currentFile,hamTokens2);
			tokenize(currentFile,hamTokens3);
			tokenize(currentFile,hamTokens4);
		}


		System.out.println("Training Ham done !");

		float threshold=(float) 0.6854142;
		int countSpam=0;
		int countHam=0;
		float sumSpam=0,sumHam=0;

		//Step1 test
		for(int i=0;i<99;i++){
			File mail = new File(".//Spam_test/train+test-spam/"+listSpamFiles[i]);
			if(parcourirTableOfTokens( mail,spamTokens1, hamTokens1)>threshold)
				countSpam++;
		}
		//System.out.println("-------------");
		for(int i=0;i<99;i++){
			File mail = new File(".//Spam_test/train+test-nonSpam/"+listHamFiles[i]);
			if(parcourirTableOfTokens( mail,spamTokens1, hamTokens1)<threshold)
				countHam++;
		}

		sumSpam+=countSpam*100/99;
		sumHam+=(float)countHam*100/99;

		System.out.println("countSpam = "+(float)countSpam*100/99);
		System.out.println("countHam = "+(float)countHam*100/99);

		System.out.println("-----> Step1 Done !");


		countSpam=0;countHam=0;
		//Step2 test
		for(int i=100;i<199;i++){
			File mail = new File(".//Spam_test/train+test-spam/"+listSpamFiles[i]);
			if(parcourirTableOfTokens( mail,spamTokens2, hamTokens2)>threshold)
				countSpam++;
		}
		System.out.println("-------------");
		for(int i=100;i<199;i++){
			File mail = new File(".//Spam_test/train+test-nonSpam/"+listHamFiles[i]);
			if(parcourirTableOfTokens( mail,spamTokens2, hamTokens2)<threshold)
				countHam++;				
		}

		sumSpam+=(float)countSpam*100/99;
		sumHam+=(float)countHam*100/99;

		System.out.println("countSpam = "+(float)countSpam*100/99);
		System.out.println("countHam = "+(float)countHam*100/99);
		System.out.println("-----> Step2 Done !");


		countSpam=0;countHam=0;
		//Step3 test
		for(int i=200;i<299;i++){
			File mail = new File(".//Spam_test/train+test-spam/"+listSpamFiles[i]);
			if(parcourirTableOfTokens( mail,spamTokens3, hamTokens3)>threshold)
				countSpam++;
		}
		System.out.println("-------------");
		for(int i=200;i<299;i++){
			File mail = new File(".//Spam_test/train+test-nonSpam/"+listHamFiles[i]);
			if(parcourirTableOfTokens( mail,spamTokens3, hamTokens3)<threshold)
				countHam++;
		}

		sumSpam+=(float)countSpam*100/99;
		sumHam+=(float)countHam*100/99;

		System.out.println("countSpam = "+(float)countSpam*100/99);
		System.out.println("countHam = "+(float)countHam*100/99);
		System.out.println("-----> Step3 Done !");

		countSpam=0;countHam=0;
		//Step4 test
		for(int i=300;i<399;i++){
			File mail = new File(".//Spam_test/train+test-spam/"+listSpamFiles[i]);
			if(parcourirTableOfTokens( mail,spamTokens4, hamTokens4)>threshold)
				countSpam++;
		}
		System.out.println("-------------");
		for(int i=300;i<399;i++){
			File mail = new File(".//Spam_test/train+test-nonSpam/"+listHamFiles[i]);
			if(parcourirTableOfTokens( mail,spamTokens4, hamTokens4)<threshold)
				countHam++;
		}

		sumSpam+=(float)countSpam*100/99;
		sumHam+=(float)countHam*100/99;

		System.out.println("countSpam = "+(float)countSpam*100/99);
		System.out.println("countHam = "+(float)countHam*100/99);
		System.out.println("-----> Step4 Done !");

		countSpam=0;countHam=0;
		//Step5 test
		for(int i=400;i<listSpamFiles.length;i++){
			File mail = new File(".//Spam_test/train+test-spam/"+listSpamFiles[i]);
			if(parcourirTableOfTokens( mail,spamTokens5, hamTokens5)>threshold)
				countSpam++;
		}
		System.out.println("-------------");
		for(int i=400;i<listHamFiles.length;i++){
			File mail = new File(".//Spam_test/train+test-nonSpam/"+listHamFiles[i]);
			if(parcourirTableOfTokens( mail,spamTokens5, hamTokens5)<threshold)
				countHam++;	
		}

		sumSpam+=(float)countSpam*100/80;
		sumHam+=(float)countHam*100/80;

		System.out.println("countSpam = "+(float)countSpam*100/80);
		System.out.println("countHam = "+(float)countHam*100/80);
		System.out.println("-----> Step5 Done !");


		System.out.println("Average countSpam = "+(float)sumSpam/5);
		System.out.println("Average countHam = "+(float)sumHam/5);

	}
	/*-----------------------------------------------------------------------NGRAM METHOD---------------------------------------------------------------------------------*/
	public static float[] calculate(File mail,TreeMap tableSpamTokens,TreeMap tableHamTokens) throws IOException{
		TreeMap tokensTable = new TreeMap();
		ngram(ngramCte,loadFile(mail),tokensTable);

		int nbOccSpam=0,nbOccHam=0,numSpam=0,numHam=0,denum=0;
		float mailSpamValue=0,mailHamValue=0;

		float tokensSpamValue=0,tokensHamValue=0;

		float results=0;
		String mot;
		Set setKey = tokensTable.keySet();
		Iterator it = setKey.iterator();

		while( it.hasNext()){
			mot = (String)it.next();
			//checking token in spam tree
			if(tableSpamTokens.containsKey(mot))
				nbOccSpam=((Integer)tableSpamTokens.get(mot)).intValue();
			else if(!tableSpamTokens.containsKey(mot))
				nbOccSpam=0;

			//checking token in non spam tree
			if(tableHamTokens.containsKey(mot))
				nbOccHam=((Integer)tableHamTokens.get(mot)).intValue();
			else if(!tableHamTokens.containsKey(mot))
				nbOccHam=0;


			/*numSpam += nbOccSpam;
			numHam += nbOccHam;
			denum += nbOccSpam+nbOccHam;*/
			if(nbOccSpam+nbOccHam !=0){
				mailSpamValue += (float)nbOccSpam/(float)(nbOccSpam+nbOccHam); 
				mailHamValue += (float)nbOccHam/(float)(nbOccSpam+nbOccHam); 
			}
		}

		/*mailSpamValue = (float)numSpam / (float)denum;
		mailHamValue = (float)numHam/(float)denum;*/




		float[] value= new float[2];
		value[0]= (float)mailSpamValue/(float)tokensTable.size();
		value[1]= (float)mailHamValue/(float)tokensTable.size();
		//	System.out.println(value[0]);
		return value;

	}
	public static void updateTree(String word, TreeMap tree){
		int nbOcc=0;
		if (tree.containsKey(word)){
			nbOcc = ((Integer)tree.get(word)).intValue();
			nbOcc++;
		}
		else 
			nbOcc = 1;
		tree.put(word, new Integer(nbOcc));
	}

	public static void showTreeMap(TreeMap tree){
		String word;
		int nbOcc;
		Set setKey = tree.keySet();
		Iterator it = setKey.iterator();

		while( it.hasNext()){
			word = (String)it.next();
			nbOcc = ((Integer)tree.get(word)).intValue();
			System.out.println("\"" + word + "\" ---->" +nbOcc );
		}
		System.out.println("Done !");
	}

	public static void ngram(int n,String x,TreeMap<String,Integer> tree){
		for(int i=0;i<=x.length()-n;i++){
			updateTree(x.substring(i, i+n),tree);
			updateTree(x.substring(i, i+n-1),tree);
		}
	}
	public static String loadFile(File f) throws IOException {

		BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));
		StringWriter out = new StringWriter();
		int b;
		while ((b=in.read()) != -1)
			out.write(b);
		out.flush();
		out.close();
		in.close();

		return out.toString();
	}


	public static int overlab(TreeMap tree1, TreeMap tree2){

		String word = null;
		int nbOcc=0,overlap=0;
		Set setKey1 = tree1.keySet();
		Iterator it1 = setKey1.iterator();

		Set setKey2 = tree2.keySet();
		Iterator it2 = setKey2.iterator();

		TreeMap treeResult = new TreeMap();
		while( it1.hasNext()){
			word = (String)it1.next();
			updateTree(word,treeResult);
		}
		while( it2.hasNext()){
			word = (String)it2.next();
			updateTree(word,treeResult);
		}

		Set setKey3 = treeResult.keySet();
		Iterator it3 = setKey3.iterator();
		while(it3.hasNext()){
			word = (String)it3.next();
			nbOcc = ((Integer)treeResult.get(word)).intValue();
			if(nbOcc==2)
				overlap++;
		}
		return overlap;

	}

	public static TreeMap mainNGram(String dataset,TreeMap resultsTreeMap2,String spamOrHam) throws IOException{
		float[] result1 = new float[2];
		float[] result2 = new float[2];
		int countSpam=0,countHam=0;
		float threshold = (float)0.5999997;


		File spamFiles = new File(".//Spam_test/"+dataset+"/spam-train");
		String[]  listSpamFiles = spamFiles.list();

		File hamFiles = new File(".//Spam_test/"+dataset+"/ham-train");
		String[]  listHamFiles = hamFiles.list();

		TreeMap<String,Integer> spamTree = new TreeMap<String,Integer>();
		TreeMap<String,Integer> hamTree = new TreeMap<String,Integer>();

		// ngram cte
		System.out.println("NGram cte = "+ngramCte);
		// Training spam files
		for(int i=0;i<listSpamFiles.length;i++){
			File currentFile = new File(".//Spam_test/"+dataset+"/spam-train/"+listSpamFiles[i]);
			ngram(ngramCte,loadFile(currentFile),spamTree);
		}
		// Training ham files
		for(int i=0;i<listHamFiles.length;i++){
			File currentFile = new File(".//Spam_test/"+dataset+"/ham-train/"+listHamFiles[i]);
			ngram(ngramCte,loadFile(currentFile),hamTree);
		}

		// Calculating the overlab

		System.out.println("spamTree size = "+spamTree.size());
		System.out.println("hamTree size = "+hamTree.size());
		int ov = overlab(spamTree,hamTree);
		System.out.println("overlap = "+ov+" ===== "+ (float)(ov*100/(spamTree.size()+hamTree.size()))+"%");


		listSpamFiles = new File(".//Spam_test/"+dataset+"/spam-test").list();
		listHamFiles = new File(".//Spam_test/"+dataset+"/ham-test").list();


		System.out.println("-------------------------------------------------------------------------------");

		countHam=0;countSpam=0;
		if(spamOrHam.equals("ham")){
			// Testing on ham test emails
			for(int i=0;i<listHamFiles.length;i++){
				File mail = new File(".//Spam_test/"+dataset+"/ham-test/"+listHamFiles[i]);
				result1 = calculate( mail,spamTree, hamTree);
				//if(result1[0]<result1[1])
				//System.out.println(result1[0]);
				if(result1[0]<threshold){
					countHam++;
					resultsTreeMap2.put(mail.getPath(), "ham");
				}
				else
					resultsTreeMap2.put(mail.getPath(), "spam");
			}
			System.out.println("Ham error rate : "+((float)100-(float)countHam*100/listHamFiles.length)+"%");
		}
		else if(spamOrHam.equals("spam")){
			// Testing on spam test emails
			for(int i=0;i<listSpamFiles.length;i++){
				File mail = new File(".//Spam_test/"+dataset+"/spam-test/"+listSpamFiles[i]);
				result2 = calculate( mail,spamTree, hamTree);
				if(result2[0]>result2[1]){
					//if(result2[0]>threshold){
					countSpam++;
					resultsTreeMap2.put(mail.getPath(), "spam");
				}
				else
					resultsTreeMap2.put(mail.getPath(), "ham");
			}
			System.out.println("Spam error rate : "+ ((float)100-(float)countSpam*100/listSpamFiles.length)+"%");
		}
		return resultsTreeMap2;
	}

	public static int NGramC3(String dataset,ArrayList<String> listOfMails,String spamOrHam) throws IOException{
		float[] result1 = new float[2];
		float[] result2 = new float[2];
		int countSpam=0,countHam=0;
		float threshold = (float)0.5999997;

		TreeMap resultsTreeMap = new TreeMap();
		File spamFiles = new File(".//Spam_test/"+dataset+"/spam-train");
		String[]  listSpamFiles = spamFiles.list();

		File hamFiles = new File(".//Spam_test/"+dataset+"/ham-train");
		String[]  listHamFiles = hamFiles.list();

		TreeMap<String,Integer> spamTree = new TreeMap<String,Integer>();
		TreeMap<String,Integer> hamTree = new TreeMap<String,Integer>();

		// ngram cte
		//System.out.println("NGram cte = "+ngramCte);
		// Training spam files
		for(int i=0;i<listSpamFiles.length;i++){
			File currentFile = new File(".//Spam_test/"+dataset+"/spam-train/"+listSpamFiles[i]);
			ngram(ngramCte,loadFile(currentFile),spamTree);
		}
		// Training ham files
		for(int i=0;i<listHamFiles.length;i++){
			File currentFile = new File(".//Spam_test/"+dataset+"/ham-train/"+listHamFiles[i]);
			ngram(ngramCte,loadFile(currentFile),hamTree);
		}


		listSpamFiles = new File(".//Spam_test/"+dataset+"/spam-test").list();
		listHamFiles = new File(".//Spam_test/"+dataset+"/ham-test").list();


		countHam=0;countSpam=0;
		if(spamOrHam.equals("ham")){
			// Testing on ham test emails
			for(int i=0;i<listOfMails.size();i++){
				File mail = new File(listOfMails.get(i));
				result1 = calculate( mail,spamTree, hamTree);
				//if(result1[0]<result1[1])
				//System.out.println(result1[0]);
				if(result1[0]<threshold){
					countHam++;
					resultsTreeMap.put(mail.getPath(), "ham");
				}
				else
					resultsTreeMap.put(mail.getPath(), "spam");
			}
			System.out.println("Ham error rate : "+((float)100-(float)countHam*100/listHamFiles.length)+"%");
			return countHam;
		}
		else if(spamOrHam.equals("spam")){
			// Testing on spam test emails
			for(int i=0;i<listOfMails.size();i++){
				File mail = new File(listOfMails.get(i));
				result2 = calculate( mail,spamTree, hamTree);
				if(result2[0]>result2[1]){
					//if(result2[0]>threshold){
					countSpam++;
					resultsTreeMap.put(mail.getPath(), "spam");
				}
				else
					resultsTreeMap.put(mail.getPath(), "ham");
			}

			System.out.println("Spam error rate : "+ ((float)100-(float)countSpam*100/listSpamFiles.length)+"%");
			return countSpam;
		}
		return 0;
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
	public static void main(String[] args) throws IOException {

		/*TreeMap resultsTreeMapSpam2 = new TreeMap();
		TreeMap resultsTreeMapHam2 = new TreeMap();

		resultsTreeMapSpam2 = mainNGram("enron1",resultsTreeMapSpam2,"spam");
		System.out.println(resultsTreeMapSpam2.size());

		resultsTreeMapHam2 = mainNGram("enron1",resultsTreeMapHam2,"ham");
		System.out.println(resultsTreeMapHam2.size());
		 */
		/*		File spamFiles = new File(".//Spam_test/"+"enron1"+"/ham-test");
		String[]  listSpamFiles = spamFiles.list();
		ArrayList mails = new ArrayList();

		for(int i=0;i<2;i++){
			mails.add(".//Spam_test/"+"enron1"+"/ham-test/"+listSpamFiles[i]);
			//System.out.println(mails.get(i));
		}

		trainAndTestListOfFilesMethod1("enron1", mails,"ham");
		//trainAndTestMethode1("enron1");
		 */

		TreeMap resultsTreeMap = new TreeMap();
		resultsTreeMap = tokenizationClassifier("enron1", resultsTreeMap,"spam");

	}
}
