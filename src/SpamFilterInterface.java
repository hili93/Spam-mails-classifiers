import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.swing.JCheckBox;
import javax.swing.JProgressBar;

import java.awt.Color;
import java.awt.Window.Type;

public class SpamFilterInterface extends JFrame {

	private JPanel contentPane;
	public static int[] tableNbOcc = null;
	public static float[][] pointsSpam = null;
	public static float[][] pointsHam=null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SpamFilterInterface frame = new SpamFilterInterface();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	/**
	 * Create the frame.
	 */
	public SpamFilterInterface() {
		setBackground(Color.LIGHT_GRAY);
		setTitle("Spam Filter - Unimap");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 0, 1453, 1077);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(50, 50, 50, 50));
		setContentPane(contentPane);
		//		 contentPane.setExtendedState( JPanel.MAXIMIZED_BOTH );


		Iterator it ;


		JButton btnRunTrain = new JButton("Run Spam train");
		btnRunTrain.setBounds(10, 11, 145, 23);
		btnRunTrain.setForeground(Color.RED);
		btnRunTrain.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionSpamTrain) {
				try{
					File spamFiles = new File(".//Spam_test/spam-train/");
					String[]  listSpamFiles = spamFiles.list();

					TreeMap<String, Integer> spamTokens = new TreeMap<String, Integer>();

					for(int i=0;i<listSpamFiles.length;i++){
						File currentFile = new File(".//Spam_test/spam-train/"+listSpamFiles[i]);
						tokenize(currentFile,spamTokens);

					}
					System.out.println("Train spam done !");

				}catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		contentPane.setLayout(null);
		contentPane.add(btnRunTrain);

		JButton btnRunHamTrain = new JButton("Run Ham Train");
		btnRunHamTrain.setBounds(202, 11, 145, 23);
		btnRunHamTrain.setForeground(Color.BLUE);
		btnRunHamTrain.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionHamTrain) {
				try {

					File hamFiles = new File(".//Spam_test/nonspam-train/");
					String[]  listHamFiles = hamFiles.list();
					TreeMap<String, Integer> hamTokens = new TreeMap<String, Integer>();
					for(int i=0;i<listHamFiles.length;i++){
						File currentFile = new File(".//Spam_test/nonspam-train/"+listHamFiles[i]);
						tokenize(currentFile,hamTokens);
					}
					System.out.println("Train ham done !");

					pointsHam = new float[hamTokens.size()][2];

					Iterator it;
					it = hamTokens.keySet().iterator();

					int i=0,nbOcc;
					tableNbOcc = new int[hamTokens.size()];
					while( it.hasNext()){
						String mot = (String)it.next();
						nbOcc = ((Integer)hamTokens.get(mot)).intValue();
						pointsHam[i][1] = i;
						pointsHam[i][0]= nbOcc;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		});
		contentPane.add(btnRunHamTrain);

		JButton btnNewButton = new JButton("Graph");
		btnNewButton.setBounds(142, 69, 89, 23);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				Graph graph = new Graph(500, 600);

				//	points = {{2,3 , 3,4}, {0,5 , 3,4}, {4,2 , 3,5}, {5,1, 3,7}, {7,2, 1,9}};
				//graph.setPoints(pointsSpam);
				graph.setPoints(pointsHam);

				graph.setBounds(300, 180, 1000, 700);
				contentPane.add(graph);

				setVisible(true);
			}
		});
		contentPane.add(btnNewButton);

		JButton btnNewButton_1 = new JButton("Run Spam Test");
		btnNewButton_1.setBounds(10, 124, 145, 23);
		btnNewButton_1.setForeground(Color.RED);
		contentPane.add(btnNewButton_1);

		JButton btnRunHamTest = new JButton("Run Ham Test");
		btnRunHamTest.setBounds(202, 124, 145, 23);
		btnRunHamTest.setForeground(Color.BLUE);
		contentPane.add(btnRunHamTest);





	}

	/*****************************************************FUNCTIONS********************************************************************************/

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
}
