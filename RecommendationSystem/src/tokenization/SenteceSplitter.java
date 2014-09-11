package tokenization;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.InvalidFormatException;

public class SenteceSplitter {

	private static SenteceSplitter instance;
	SentenceDetectorME sdetector;
	
	private SenteceSplitter() throws InvalidFormatException, IOException{
		InputStream is = new FileInputStream("data/en-sent.bin");
		SentenceModel model = new SentenceModel(is);
		sdetector = new SentenceDetectorME(model);

	}
	
	public static SenteceSplitter getInstance() throws InvalidFormatException, IOException{
		if(instance == null){
			instance = new SenteceSplitter();
		}
		
		return instance;
	}
	
	public String[] splitSentences(String text){
		String[] sentences = sdetector.sentDetect(text);
				
		return sentences;
	}
	
}
