package tokenization;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;

public class TextTokenization {

	public static TextTokenization instance;
	StanfordCoreNLP pipeline;
	List<String> stopwordList = Arrays.asList("a", "an", "and", "are", "as",
			"at", "be", "but", "by", "for", "if", "in", "into", "is", "it",
			"no", "not", "of", "on", "or", "such", "that", "the", "their",
			"then", "there", "these", "they", "this", "to", "was", "will",
			"with");

	private TextTokenization() throws InvalidFormatException, IOException {
		/*
		 * modelIn = new FileInputStream("data/en-token.bin"); model = new
		 * TokenizerModel(modelIn); tokenizer = new TokenizerME(model);
		 */
		pipeline = generatePipeline();
	}

	public static TextTokenization getInstance() throws InvalidFormatException,
			IOException {
		if (instance == null) {
			instance = new TextTokenization();
		}
		return instance;
	}

	public StanfordCoreNLP generatePipeline() {
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma, parse");
		StanfordCoreNLP snlp = new StanfordCoreNLP(props);
		return snlp;
	}

	public List<String> tokenizeText(String text) {

		List<String> tempTokenList = new ArrayList<>();
		List<String> tokenList = new ArrayList<>();
		List<String> filteredTokenList = new ArrayList<>();

		Annotation document = new Annotation(text);
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		//
		// String punctuations = ". , ! ? ( ) # @ $ % ^ & | * \" : ; ` `` ";
		List<String> punctuations = new ArrayList<>();
		punctuations.add(".");
		punctuations.add(",");
		punctuations.add("!");
		punctuations.add("?");
		punctuations.add("(");
		punctuations.add(")");
		punctuations.add("#");
		punctuations.add("@");
		punctuations.add("$");
		punctuations.add("^");
		punctuations.add("&");
		punctuations.add("|");
		punctuations.add("*");
		punctuations.add("\"\"");
		punctuations.add(":");
		punctuations.add(";");
		punctuations.add("-lrb-");
		punctuations.add("-lsb-");
		punctuations.add("-rrb-");
		punctuations.add("-rsb-");
		punctuations.add("`");
		punctuations.add("``");
		punctuations.add("&#160;");
		punctuations.add("--");
		punctuations.add("...");

		String numberRegex = "^[0-9]+$";

		for (CoreMap sentence : sentences) {
			// traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific methods
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// this is the text of the token
				// String word = token.get(TextAnnotation.class);
				// System.out.println("Value of word: " + word);
				// this is the POS tag of the token
				// String pos = token.get(PartOfSpeechAnnotation.class);

				// this is the NER label of the token
				// String ne = token.get(NamedEntityTagAnnotation.class);

				String lema = token.getString(LemmaAnnotation.class);
				if (!punctuations.contains(lema) && !lema.matches(numberRegex)
						&& !stopwordList.contains(lema.toLowerCase())) {
					// System.out.println("Lema is: " + lema);
					tempTokenList.add(lema);
				}
			}
		}

		System.out.println("Temp token list size: " + tempTokenList.size());
		// concatenate tokens that should be possesive(John + 's)
		String regex = "^'[a-zA-Z]$";
		String regex1 = "^'ll$";

		for (int j = 0; j < tempTokenList.size(); j++) {
			if (j != tempTokenList.size() - 1) {
				if ((!tempTokenList.get(j).matches(regex) && !tempTokenList
						.get(j).matches(regex1))
						&& (tempTokenList.get(j + 1).matches(regex) || tempTokenList
								.get(j + 1).matches(regex1))) {
					System.out.println("Nasao: " + tempTokenList.get(j + 1));
					tokenList.add(tempTokenList.get(j)
							+ tempTokenList.get(j + 1));
				} else {
					tokenList.add(tempTokenList.get(j));
				}
			} else {
				tokenList.add(tempTokenList.get(j));
			}
		}

		return tokenList;
	}

	public List<String> filterList(List<String> unfilteredList) {
		List<String> filteredTokenList = new ArrayList<>();
		// filtering unique tokens
		for (String token : unfilteredList) {
			if (!filteredTokenList.contains(token)) {
				filteredTokenList.add(token);
			}
		}

		return filteredTokenList;
	}

	public List<List<String>> getAllDocuments(String fileName)
			throws IOException {

		List<List<String>> listOfDocumentTokens = new ArrayList<>();

		BufferedReader br = new BufferedReader(new FileReader("data/"
				+ fileName + ".txt"));
		String line;

		while ((line = br.readLine()) != null) { // process the line.
			listOfDocumentTokens.add(tokenizeText(line));
		}

		br.close();
		return listOfDocumentTokens;
	}

}
