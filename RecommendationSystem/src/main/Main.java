package main;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.commons.math3.analysis.function.Cos;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.apache.jena.atlas.iterator.Iter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.hp.hpl.jena.tdb.store.Hash;

import tokenization.TextTokenization;

import au.com.bytecode.opencsv.CSVWriter;

import measures.CosineSimilarity;
import measures.MatrixGenerator;
import measures.MeasureTFIDF;

import dataRetrieval.DataSelection;
import edu.stanford.nlp.trees.Tree;

public class Main {

	/**
	 * @param args
	 * @throws IOException
	 */

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		// STEP 1, just for the first time, retrieve data and save to .csv file.
		
		 /*DataSelection d = new DataSelection();
		 d.getAndWriteDataToFile("titlesAndDescriptionsAll");*/
		 
		// STEP 2, read data from the .csv file created in step 1, to create
		// TF-IDF vectors in STEP 3
		// System.out.println("Test for commit");
		DataSelection d = new DataSelection();
		TreeMap<String, String> descriptions = d
				.generateDescTreeMapsFromFile("titlesAndDescriptions50");

		// STEP 3, create TF-IDF vectors for each description and write them to
		// separate .csv files

		TreeMap<String, List<String>> treeMapOfAllDocumentsTokenized = new TreeMap<>();
		List<List<String>> listOfAllDocumentsTokenized = new ArrayList<>();
		List<String> listOfMovieTitles = new ArrayList<>();
		MeasureTFIDF m = new MeasureTFIDF();
		Iterator descIt = descriptions.entrySet().iterator();

		while (descIt.hasNext()) {
			Map.Entry<String, String> pairs = (Map.Entry<String, String>) descIt
					.next();

			List<String> tokenizedText = TextTokenization.getInstance()
					.tokenizeText(pairs.getValue());
			treeMapOfAllDocumentsTokenized.put(pairs.getKey(), tokenizedText);
			listOfAllDocumentsTokenized.add(tokenizedText);
			listOfMovieTitles.add(pairs.getKey());
		}
	
		m.setListOfAllDocuments(listOfAllDocumentsTokenized);
	
		Iterator descIt1 = descriptions.entrySet().iterator();
		while (descIt1.hasNext()) {
			Map.Entry<String, String> pairs = (Map.Entry<String, String>) descIt1
					.next();

			List<String> tokenizedText = treeMapOfAllDocumentsTokenized
					.get(pairs.getKey());

			HashMap<String, Double> map = m
					.generateDescriptionVector(tokenizedText);

			m.writeVectorToFile(map, pairs.getKey());
			

		}

		// STEP 4 Generate regular matrix for all documents

		RealMatrix matrix = MatrixGenerator.getInstance().generateMatrix(
				"vectors");
		
		// STEP 5 Calculate SVD for the matrix generated in STEP 4
		// and reduce the S matrix to recalculate the matrix for cosine
		// similarity, depending on the number of documents, set the dimension
		// for the S matrix

		RealMatrix matrixNew = MatrixGenerator.getInstance().reduceSVDMatrix(
				matrix, 10);


		// STEP 6 Calculate cosine similarity and write to individual .csv files
		CosineSimilarity.getInstance().calculateCosineSimilarity(matrixNew,
				listOfMovieTitles);
	}
}
