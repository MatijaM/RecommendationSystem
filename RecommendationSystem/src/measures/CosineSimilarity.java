package measures;

import java.util.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Math;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.jena.atlas.iterator.Iter;
import org.omg.CORBA.CODESET_INCOMPATIBLE;

import au.com.bytecode.opencsv.CSVWriter;

public class CosineSimilarity {

	public static CosineSimilarity instance;

	public CosineSimilarity() {

	}

	public static CosineSimilarity getInstance() {
		if (instance == null) {
			instance = new CosineSimilarity();
		}
		return instance;
	}

	public void calculateCosineSimilarity(RealMatrix m, List<String> movieTitles) {

		for (int i = 0; i < m.getRowDimension(); i++) {
			TreeMap<String, Double> cosineValues = new TreeMap<>();
			String title = movieTitles.get(i);
			RealVector currentVector = m.getRowVector(i);
			for (int j = 0; j < m.getRowDimension(); j++) {
				if (j != i) {
					double cosine = currentVector.cosine(m.getRowVector(j));
					cosineValues.put(movieTitles.get(j), cosine);
				}
			}
			writeTreeMapToFile(cosineValues, title);
		}
	}

	public void writeTreeMapToFile(TreeMap<String, Double> cosineValues,
			String fileName) {

		try {
			CSVWriter writer = new CSVWriter(new FileWriter(
					"data/cosineVectors/" + fileName + ".csv"));

			Iterator it = cosineValues.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Double> pairs = (Map.Entry<String, Double>) it
						.next();
				String[] row = new String[] { pairs.getValue().toString(),
						pairs.getKey() };
				writer.writeNext(row);
				it.remove();
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
