package measures;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MoveAction;

import au.com.bytecode.opencsv.CSVWriter;

import com.hp.hpl.jena.sparql.pfunction.library.str;
import com.hp.hpl.jena.tdb.store.Hash;

import opennlp.tools.util.InvalidFormatException;

import tokenization.TextTokenization;

public class MeasureTFIDF {

	HashMap<String, Integer> occurencies;
	HashMap<String, Double> tfs;
	HashMap<String, Integer> documentOccurencies;
	HashMap<String, Double> idfs;
	HashMap<String, Double> tfidfs;
	ArrayList<String[]> listOfSentences;
	ArrayList<String[]> listOfDescriptionTokens;
	List<List<String>> listOfAllDocuments;
	List<String> listOfUniqueTokens;
	int totalNumberOfTokens;
	int numberOfDocuments;
	CSVWriter csvWriter;

	public MeasureTFIDF() {
		occurencies = new HashMap<>();
		tfs = new HashMap<>();
		documentOccurencies = new HashMap<>();
		idfs = new HashMap<>();
		tfidfs = new HashMap<>();
		listOfSentences = new ArrayList<>();
		listOfDescriptionTokens = new ArrayList<>();
		listOfUniqueTokens = new ArrayList<>();
		listOfAllDocuments = null;
	}

	public List<List<String>> getListOfAllDocuments() {
		return listOfAllDocuments;
	}

	public void setListOfAllDocuments(List<List<String>> listOfAllDocuments) {
		this.listOfAllDocuments = listOfAllDocuments;
	}

	private double logOfBase(int base, int num) {
		return Math.log(num) / Math.log(base);
	}

	public HashMap<String, Double> generateDescriptionVector(
			List<String> movieDescription) throws InvalidFormatException,
			IOException {

		numberOfDocuments = listOfAllDocuments.size();
		listOfUniqueTokens = TextTokenization.getInstance().filterList(
				movieDescription);
		occurencies = calculateOccurenciesInDocument(movieDescription,
				listOfUniqueTokens);
		totalNumberOfTokens = movieDescription.size();
		tfs = calculateTFSForUniqueTokens(listOfUniqueTokens,
				totalNumberOfTokens);
		documentOccurencies = calculateDocumentOccuriences(listOfUniqueTokens,
				listOfAllDocuments);
		tfidfs = calculateTFIDFs(listOfUniqueTokens, documentOccurencies,
				numberOfDocuments, tfs);

		return tfidfs;
	}

	// filter all tokens to get only the unique ones
	public ArrayList<String> getUniqueTokens(ArrayList<String[]> allTokens) {
		ArrayList<String> listOfUniqueTokens = new ArrayList<>();

		for (String[] string : allTokens) {
			for (int i = 0; i < string.length; i++) {
				if (!listOfUniqueTokens.contains(string[i])) {
					listOfUniqueTokens.add(string[i]);
				}
			}
		}

		return listOfUniqueTokens;
	}

	// get number of occurencies of each unique token in the document
	public HashMap<String, Integer> calculateOccurenciesInDocument(
			List<String> allTokens, List<String> uniqueTokens) {
		HashMap<String, Integer> map = new HashMap<>();

		for (String uniqueToken : uniqueTokens) {
			map.put(uniqueToken, 0);

			for (String token : allTokens) {
				if (token.equals(uniqueToken)) {
					int currentNumber = map.get(uniqueToken);
					currentNumber++;
					map.put(uniqueToken, currentNumber);
				}
			}
		}

		return map;
	}

	// total number of tokens in the document
	public int calculateTotalNumberOfTokens(List<List<String>> allTokens) {
		int sumOfTokens = 0;
		for (List<String> list : allTokens) {
			sumOfTokens += list.size();
		}

		return sumOfTokens;
	}

	// calculate TF for each uniqueToken
	public HashMap<String, Double> calculateTFSForUniqueTokens(
			List<String> unique, int sumOfTokens) {
		HashMap<String, Double> map = new HashMap<>();

		for (String string : unique) {
			double occ = occurencies.get(string);
			double tf = occ / sumOfTokens;
			map.put(string, tf);
		}

		return map;

	}

	// number of documents in which the keywords appear
	public HashMap<String, Integer> calculateDocumentOccuriences(
			List<String> unique, List<List<String>> documents)
			throws InvalidFormatException, IOException {

		HashMap<String, Integer> map = new HashMap<>();

		for (String uniqueToken : unique) {
			map.put(uniqueToken, 0);
			for (List<String> list : documents) {
				primaryLoop: for (int i = 0; i < list.size(); i++) {
					if (list.get(i).equals(uniqueToken)) {
						int occ = map.get(uniqueToken);
						occ++;
						map.put(uniqueToken, occ); //
						break primaryLoop;
					}
				}
			}
		}

		return map;

	}

	// calculate the TF-IDFs for each unique token and generate vector
	public HashMap<String, Double> calculateTFIDFs(List<String> unique,
			HashMap<String, Integer> docOcc, int docNum,
			HashMap<String, Double> termFrequencies) {
		HashMap<String, Double> map = new HashMap<>();

		for (String uniqueToken : unique) {
			int docOccurencie = docOcc.get(uniqueToken);
			double num = docNum / 1 + docOccurencie;
			double idf = Math.log(num) / Math.log(2);
			map.put(uniqueToken, idf);
			double tf = termFrequencies.get(uniqueToken);
			double tfidf = tf * idf;
			map.put(uniqueToken, tfidf);
		}

		return map;
	}

	public void writeVectorToFile(HashMap<String, Double> vector,
			String fileName) throws IOException {
		csvWriter = new CSVWriter(new FileWriter("data/vectors/" + fileName
				+ ".csv"));

		Iterator it = vector.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Double> pairs = (Map.Entry<String, Double>) it
					.next();
			String[] row = new String[] { pairs.getKey(),
					pairs.getValue().toString() };
			csvWriter.writeNext(row);
			it.remove();
		}

		csvWriter.close();
	}
}
