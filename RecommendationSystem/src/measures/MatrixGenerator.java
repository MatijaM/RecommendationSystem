package measures;

import java.io.DataOutput;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import edu.stanford.nlp.trees.Tree;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class MatrixGenerator {

	private static MatrixGenerator instance;
	File[] files;
	RealMatrix matrix;

	public MatrixGenerator() {

	}

	public static MatrixGenerator getInstance() {
		if (instance == null) {
			instance = new MatrixGenerator();
		}

		return instance;
	}

	public RealMatrix generateMatrix(String dirName) throws IOException {
		// list of all unique tokens from all vectors the first row of the
		// matrix
		List<TreeMap<String, Double>> listOfVectors = readVectorsFromDirectory("data/"
				+ dirName);

		// these are actually the future rows for the matrix that have all
		// unique tokens
		// but values for only the ones that exist in a certain description
		List<HashMap<String, Double>> listOfCompleteVectors = new ArrayList<>();

		List<String> listOfAllUniqueTokens = generateListOfUniqueTokens(listOfVectors);

		System.out
				.println("UNIQUE TOKEN SIZE: " + listOfAllUniqueTokens.size());

		matrix = new BlockRealMatrix(listOfVectors.size(),
				listOfAllUniqueTokens.size());

		CSVWriter csvWriter = new CSVWriter(new FileWriter(
				"data/matrices/matrix1.csv"));

		String[] firstRow = new String[listOfAllUniqueTokens.size() + 1];
		firstRow[0] = "";

		TreeMap<String, Double> tempHashMap1 = generateTempHashMap(listOfAllUniqueTokens);

		int m = 1;
		Iterator it2 = tempHashMap1.entrySet().iterator();
		while (it2.hasNext()) {
			Map.Entry<String, Double> pairs = (Map.Entry<String, Double>) it2
					.next();
			// System.out.println("Key: " + pairs.getKey() + " Value: " +
			// pairs.getValue());
			firstRow[m] = pairs.getKey();
			m++;
		}

		csvWriter.writeNext(firstRow);

		// this should be the making of a row which will be
		// a Double[] for RealMatrix and String[] for CSV file

		int t = 0;

		for (TreeMap<String, Double> hashMap : listOfVectors) {
			TreeMap<String, Double> tempHashMap = generateTempHashMap(listOfAllUniqueTokens);
			// List<Double> tempListOfValues = new ArrayList<>();
			double[] matrixRow = new double[tempHashMap.size()];
			String[] row = new String[tempHashMap.size() + 1];
			row[0] = "Desc_" + t;

			for (String token : listOfAllUniqueTokens) {

				if (hashMap.containsKey(token)) {
					tempHashMap.put(token, hashMap.get(token));
				}

			}

			int j = 1;
			Iterator it = tempHashMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Double> pairs = (Map.Entry<String, Double>) it
						.next();
				// System.out.println("Key: " + pairs.getKey() + " Value: " +
				// pairs.getValue());
				matrixRow[j - 1] = pairs.getValue();
				row[j] = pairs.getValue().toString();
				j++;
			}
			matrix.setRow(t, matrixRow);
			csvWriter.writeNext(row);
			t++;
		}

		csvWriter.close();

		return matrix;
	}

	// generate a HashMap for provided keys where the values are 0.0
	public TreeMap<String, Double> generateTempHashMap(List<String> listOfKeys) {
		TreeMap<String, Double> tempMap = new TreeMap<>();

		for (String string : listOfKeys) {
			tempMap.put(string, 0.0);
		}

		// System.out.println("TempMap before return: ");

		return tempMap;
	}

	// read a specific .csv file to a HashMap
	public TreeMap<String, Double> readVectorFromFile(String fileName)
			throws IOException {
		TreeMap<String, Double> tempMap = new TreeMap<>();
		String csvFilename = fileName;
		CSVReader csvReader = new CSVReader(new FileReader(csvFilename));
		String[] row;
		while ((row = csvReader.readNext()) != null) {
			tempMap.put(row[0], Double.parseDouble(row[1]));
		}
		csvReader.close();

		return tempMap;
	}

	// generate a list of unique tokens for the entire dataset
	public List<String> generateListOfUniqueTokens(
			List<TreeMap<String, Double>> listOfVectors) {
		List<String> tempList = new ArrayList<>();

		for (TreeMap<String, Double> hashMap : listOfVectors) {
			// System.out.println("Size of map is: " + hashMap.size());
			Iterator it = hashMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Double> pairs = (Map.Entry<String, Double>) it
						.next();
				if (!tempList.contains(pairs.getKey())) {
					tempList.add(pairs.getKey());
				}

			}
		}

		return tempList;

	}

	// read all .csv files/vectors to a list of HashMaps
	public List<TreeMap<String, Double>> readVectorsFromDirectory(
			String pathName) {
		List<TreeMap<String, Double>> tempList = new ArrayList<>();

		File f = new File(pathName);
		if (f.isDirectory()) {
			files = f.listFiles();

			// for (File file : files) {
			for (int i = 0; i < files.length; i++) {
				try {
					tempList.add(readVectorFromFile(pathName + "/"
							+ files[i].getName()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return tempList;
	}

	// reduce the SVD calculated matrices and recalculate

	public RealMatrix reduceSVDMatrix(RealMatrix originalMatrix, int dimensions) {
		SingularValueDecomposition svd = new SingularValueDecomposition(
				originalMatrix);
		RealMatrix u = svd.getU();
		RealMatrix s = svd.getS();
		RealMatrix v = svd.getVT();

		for (int i = dimensions; i < s.getRowDimension(); i++) {
			s.setEntry(i, i, 0.0);
		}

		RealMatrix finalMatrix = (u.multiply(s)).multiply(v);
		return finalMatrix;
	}
}
