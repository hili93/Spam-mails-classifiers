import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;


public class doubleTokensFilter {

	public static void tokenize(File file,TreeMap table) throws IOException{

		BufferedReader entree = new BufferedReader(new FileReader(file));
		String ligne;
		StringTokenizer st;
		String token1,token2,tokens;
		int nbOcc;

		while ((ligne = entree.readLine()) != null)
		{
			st = new StringTokenizer(ligne, " ,.;:_-+*/\\.;\n\"'{}()=><\t!?");
			token1 = st.nextToken();
			while(st.hasMoreTokens())
			{
				token2 = st.nextToken();
				tokens = token1+ token2;
				if (table.containsKey(tokens))
				{
					nbOcc = ((Integer)table.get(tokens)).intValue();
					nbOcc++;
				}
				else 
					nbOcc = 1;
				table.put(tokens, new Integer(nbOcc));
				token1=token2;
			}
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

	public static float overlab(TreeMap tree1, TreeMap tree2){

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
		return (float)(overlap*100)/((float)(tree1.size()+tree2.size()-overlap));

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
	}
	public static float[] calculate(File mail,TreeMap tableSpamTokens,TreeMap tableHamTokens) throws IOException{
		TreeMap tokensTable = new TreeMap();
		tokenize(mail,tokensTable);

		int nbOccSpam=0,nbOccHam=0,numSpam=0,numHam=0,denum=0;
		float mailSpamValue=0,mailHamValue=0;
		int unseenSpam=0,unseenHam=0;

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
			else if(!tableSpamTokens.containsKey(mot)){
				nbOccSpam=0;
				unseenSpam++;
			}
			//checking token in non spam tree
			if(tableHamTokens.containsKey(mot))
				nbOccHam=((Integer)tableHamTokens.get(mot)).intValue();
			else if(!tableHamTokens.containsKey(mot)){
				nbOccHam=0;
				unseenHam++;
			}
			if(nbOccSpam+nbOccHam !=0){
				mailSpamValue += (float)nbOccSpam/(float)(nbOccSpam+nbOccHam); 
				mailHamValue += (float)nbOccHam/(float)(nbOccSpam+nbOccHam); 
			}
		}

		//System.out.println("unseenSpam = "+unseenSpam);
		//System.out.println("unseenHam = "+unseenHam);

		float[] value= new float[2];
		value[0]= (float)mailSpamValue/(float)tokensTable.size();
		value[1]= (float)mailHamValue/(float)tokensTable.size();

		return value;

	}
	public static void main(String[] args) throws IOException {

		float[] result1 = new float[2];
		float[] result2 = new float[2];

		int countSpam=0,countHam=0;

		float threshold = (float) 0.088;

		File spamFiles = new File(".//Spam_test/enron1/spam-train");
		String[]  listSpamFiles = spamFiles.list();

		File hamFiles = new File(".//Spam_test/enron1/ham-train");
		String[]  listHamFiles = hamFiles.list();

		TreeMap trainSpamTree = new TreeMap();
		TreeMap trainHamTree = new TreeMap();

		//Train Spam
		for(int i=0;i<listSpamFiles.length;i++){
			File currentFile = new File(".//Spam_test/enron1/spam-train/"+listSpamFiles[i]);
			tokenize(currentFile, trainSpamTree);
		}
		//Train Ham
		for(int i=0;i<listHamFiles.length;i++){
			File currentFile = new File(".//Spam_test/enron1/ham-train/"+listHamFiles[i]);
			tokenize(currentFile, trainHamTree);
		}

		System.out.println("spam tree length: "+ trainSpamTree.size());
		System.out.println("ham tree length: "+ trainHamTree.size());

		System.out.println("Overlap: "+ overlab(trainSpamTree, trainHamTree)+" %");

		listSpamFiles = new File(".//Spam_test/enron1/spam-test").list();
		listHamFiles = new File(".//Spam_test/enron1/ham-test").list();


		// Testing on ham test emails
		for(int i=0;i<listHamFiles.length;i++){
			File mail = new File(".//Spam_test/enron1/ham-test/"+listHamFiles[i]);
			result1 = calculate( mail,trainSpamTree, trainHamTree);
			if(result1[0]<result1[1])
				countHam++;
		}
		System.out.println("Ham error rate : "+(float)((float)100-(float)countHam*100/listHamFiles.length)+"%");

		// Testing on spam test emails
		for(int i=0;i<listSpamFiles.length;i++){
			File mail = new File(".//Spam_test/enron1/spam-test/"+listSpamFiles[i]);
			result2 = calculate( mail,trainSpamTree, trainHamTree);
			if(result2[0]>result2[1])
				countSpam++;
		}
		System.out.println("Spam error rate : "+ (float)((float)100-(float)countSpam*100/listSpamFiles.length)+"%");

	}

}
