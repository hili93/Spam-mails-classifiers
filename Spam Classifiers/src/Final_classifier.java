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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class Final_classifier {

	public static TreeMap resultsTreeMapSpam1 = new TreeMap();
	public static TreeMap resultsTreeMapHam1 = new TreeMap();
	public static TreeMap resultsTreeMapSpam2 = new TreeMap();
	public static TreeMap resultsTreeMapHam2 = new TreeMap();
	public static int countSpam =0,countHam=0;

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

	public static ArrayList comparingTreeMaps(TreeMap tree1,TreeMap tree2,String spamOrHam){
		int i=0,nbSame=0;
		ArrayList nonSameMails = new ArrayList<>();
		String mail1,mail2;
		String value1,value2;
		Set setKey1 = tree1.keySet();
		Iterator it1 = setKey1.iterator();

		Set setKey2 = tree2.keySet();
		Iterator it2 = setKey2.iterator();

		while( it1.hasNext()){
			mail1 = (String)it1.next();
			value1 = (String) tree1.get(mail1);
			if(tree2.containsKey(mail1)){
				value2 = (String)tree2.get(mail1);
				if(value1==value2)
					nbSame++;
				else{
					nonSameMails.add(mail1);
					i++;
				}
			}
			else
				System.err.println("KEY NOT FOUND !!!!!!!");
		}
		System.out.println("nb of "+spamOrHam+"= "+ nbSame);

		return nonSameMails;

	}
	public static int errorRate(TreeMap tree,String spamOrHam){
		String mail,value;
		int count=0;
		Set setKey = tree.keySet();
		Iterator it = setKey.iterator();

		while( it.hasNext()){
			mail = (String)it.next();
			value = (String) tree.get(mail);
			if(value.equals(spamOrHam))
				count++;
		}
		return count;
	}
	public static void main(String[] args) throws IOException {

		/*String dataset= "enron1";
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>RESULTS CLASSIFIER 1 :");

		n_tokens_method c1 = new n_tokens_method();
		resultsTreeMapSpam1 =c1.mainN_tokens_method(resultsTreeMapSpam1,"spam");

		resultsTreeMapHam1 = c1.mainN_tokens_method(resultsTreeMapHam1,"ham");

		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>RESULTS CLASSIFIER 2 :");

		Classifier2 c2 = new Classifier2();
		resultsTreeMapSpam2 = c2.mainNGram(dataset,resultsTreeMapSpam2,"spam");
		resultsTreeMapHam2 = c2.mainNGram(dataset,resultsTreeMapHam2,"ham");

		//Comparing the results of the two classifiers
		ArrayList spamsToReTest = comparingTreeMaps(resultsTreeMapSpam1,resultsTreeMapSpam2,"spam");
		System.out.println("1 size = "+spamsToReTest.size());

		ArrayList hamsToReTest = comparingTreeMaps(resultsTreeMapHam1,resultsTreeMapHam2,"ham");
		System.out.println("2 size = "+hamsToReTest.size());

		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>RESULTS CLASSIFIER 3 :");

		countSpam=(500-spamsToReTest.size())+c2.trainAndTestListOfFilesMethod1(dataset, spamsToReTest,"spam");
		countHam=(500-hamsToReTest.size())+c2.trainAndTestListOfFilesMethod1(dataset, hamsToReTest,"ham");

		System.out.println("final result: nb spams= "+countSpam);
		System.out.println("final result: nb Hams= "+countHam);
		 */
		String dataset= "enron1";
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>RESULTS CLASSIFIER 1 :");

		n_tokens_method c1 = new n_tokens_method();
		resultsTreeMapSpam1 =c1.mainN_tokens_method(resultsTreeMapSpam1,"spam");

		resultsTreeMapHam1 = c1.mainN_tokens_method(resultsTreeMapHam1,"ham");

		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>RESULTS CLASSIFIER 2 :");

		Classifier2 c2 = new Classifier2();
		resultsTreeMapSpam2 = c2.tokenizationClassifier(dataset, resultsTreeMapSpam2,"spam");
		resultsTreeMapHam2 = c2.tokenizationClassifier(dataset, resultsTreeMapHam2,"ham");

		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>RESULTS COMPARISON CLASSIFIER 1 & 2 :");

		//Comparing the results of the two classifiers
		ArrayList spamsToReTest = comparingTreeMaps(resultsTreeMapSpam1,resultsTreeMapSpam2,"spam");
		System.out.println("\t1 size = "+spamsToReTest.size());

		ArrayList hamsToReTest = comparingTreeMaps(resultsTreeMapHam1,resultsTreeMapHam2,"ham");
		System.out.println("\t2 size = "+hamsToReTest.size());

		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>RESULTS CLASSIFIER 3 :");

		countSpam=(500-spamsToReTest.size())+c2.NGramC3( dataset,spamsToReTest,"spam");
		countHam=(500-hamsToReTest.size())+c2.NGramC3(dataset,hamsToReTest,"ham");

		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>FINAL RESULTS :");
		System.out.println("final result: nb spams= "+countSpam);
		System.out.println("final result: nb Hams= "+countHam);
	}
}
