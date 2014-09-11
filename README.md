#About

Goal of this app is to generate data that could be used in comparison of movies based on their plots. It is developed using the following paper as a rolemodel: [A Non-Intrusive Movie Recommendation System](http://www.dbgroup.unimo.it/paper/odbase2012.pdf)

The complete application workflow is divised into 6 steps:

1. Data collection from a TDB storage and saving it to local storage
2. Read the data from the local storage and create vectors of tokenized movie plots to be used in step 3 
3. Generate [TF-IDF](http://en.wikipedia.org/wiki/Tf%E2%80%93idf) vectors
4. Use vectors from step 3 to generate a matrix on which [SVD](http://en.wikipedia.org/wiki/Singular_value_decomposition) will be performed
5. Perform SVD and recalculate the matrix
6. Calculate [cosine similarity](http://en.wikipedia.org/wiki/Cosine_similarity) between all of the vectors from the matrix and save those values in separate .csv files for every movie, for further use.

#Implementation

[Jena RDF API](http://jena.apache.org/documentation/rdf/index.html) is used for retrieving movie titles and plots from a database that is an upgrade of the DBPedia data, whereas the movie plots were added from Wikipedia.

Movie plot tokenization is performed using [Stanford Core-nlp](http://nlp.stanford.edu/software/corenlp.shtml#Download), with added list of stopwords from the [LUCENE](http://lucene.apache.org/core/) library.

TF-IDF calculations are performed using a custom written class.

Matrices and vectors are generated using [RealMatrix interface](http://commons.apache.org/proper/commons-math/apidocs/org/apache/commons/math3/linear/RealMatrix.html) and it's implementation [BlockRealMatrix](https://commons.apache.org/proper/commons-math/apidocs/org/apache/commons/math3/linear/BlockRealMatrix.html) and [RealVector](https://commons.apache.org/proper/commons-math/apidocs/org/apache/commons/math3/linear/BlockRealMatrix.html).

SVD calculation is performed using the appache [SVD class](https://commons.apache.org/proper/commons-math/apidocs/org/apache/commons/math3/linear/SingularValueDecomposition.html)

Cosine similarity is calculated by a method contained in the RealVector implementation.

All of the data is written into appropriate .csv files using [opencsv library](http://sourceforge.net/projects/opencsv/).

#Class implementation

For steps 1 and 2 **DataSelection** class is used.
Custom TF-IDF calculations are performed using the methods from **MeasureTFIDF** class.
All of the matrix generation and calculation is performed using the methods from **MatrixGenerator** class.
The **CosineSimilarity** class is obviously used for the vector aproximation calculations.

#Notes

In order to run the application, it is neccesary to download the [Stanford Core-nlp](http://nlp.stanford.edu/software/corenlp.shtml#Download) library and add it to the build path of the project.


#Acknowledgements

This application has been developed as a part of the project assignment for the subject [Intelligent Systems](http://is.fon.rs/) at the Faculty of Organization Sciences, University of Belgrade, Serbia.

