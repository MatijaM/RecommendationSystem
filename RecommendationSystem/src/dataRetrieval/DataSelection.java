package dataRetrieval;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.TreeMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

import edu.stanford.nlp.trees.Tree;

public class DataSelection {

	public void getAndWriteDataToFile(String filename) throws IOException {

		// Model m = ModelFactory.createDefaultModel();
		String q = "select ?movie ?plot " + "where{"
				+ "?movie <http://moviesion.com/ontology/plot> ?plot. "
				+ "}";
		System.out.println(q);
		Query query = QueryFactory.create(q);
		QueryExecution qExe = QueryExecutionFactory.sparqlService(
				"http://s2-ls3.rnet.ryerson.ca:8890/sparql", query);
		ResultSet results = qExe.execSelect();

		// OutputStream out = new FileOutputStream("data/" + filename +
		// ".json");

		// ResultSetFormatter.outputAsJSON(out, results);

		CSVWriter csvWriter = new CSVWriter(new FileWriter(
				"data/"+filename+".csv"));

		String movie;
		String desc;

		for (; results.hasNext();) {
			String[] row = new String[2];

			QuerySolution soln = results.nextSolution();
			Resource r = soln.getResource("movie");
			Literal l = soln.getLiteral("plot");

			movie = r.toString().substring(r.toString().lastIndexOf("/")+1);
			desc = l.toString();
			row[0] = movie;
			row[1] = desc;

			csvWriter.writeNext(row);
		}

		csvWriter.close();

		/*
		 * BufferedWriter writer = null; writer = new BufferedWriter(new
		 * FileWriter("data/" + filename + ".txt"));
		 * 
		 * for (; results.hasNext();) { QuerySolution soln =
		 * results.nextSolution();
		 * 
		 * writer.write(soln.toString()); writer.newLine(); // Get a result
		 * variable - // must be a resource Resource r =
		 * soln.getResource("movie"); // Get a result variable - must be a
		 * literal // Literal l = soln.getLiteral("plot");
		 * 
		 * System.out.println(soln.toString());
		 * 
		 * movie = r.toString();
		 * 
		 * writer.write("<-" + movie.substring(r.toString().lastIndexOf("/") +
		 * 1) + "<->" + l.toString() + "->");
		 * 
		 * writer.write(l.toString());
		 * 
		 * writer.write(movie.substring(r.toString().lastIndexOf("/") + 1));
		 * writer.newLine();
		 * 
		 * }
		 * 
		 * writer.close(); System.out.println("Data written into the file: " +
		 * filename + ".txt");
		 */
	}

	public TreeMap<String, String> generateDescTreeMapsFromFile(String fileName) {
		CSVReader csvReader = null;
		TreeMap<String, String> tempMap = new TreeMap<>();
		try {
			csvReader = new CSVReader(new FileReader("data/" + fileName
					+ ".csv"));
			String[] row;
			while ((row = csvReader.readNext()) != null) {
				tempMap.put(row[0], row[1]);
			}

			csvReader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tempMap;

	}

	public void getJsonData(String fileName) {

		JSONParser parser = new JSONParser();

		try {
			Object obj = parser.parse(new FileReader("data/titles10Json.json"));
			JSONObject jsonObject = (JSONObject) obj;
			System.out.println("file? " + jsonObject.toJSONString());

			// String res = (String) jsonObject.get("head");

			/*
			 * JSONArray arr = (JSONArray) jsonObject.get("bindings");
			 * 
			 * Iterator<String> iterator = arr.iterator(); while
			 * (iterator.hasNext()) { System.out.println(iterator.next()); }
			 */
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
