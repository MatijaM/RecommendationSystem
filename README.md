#About

The goal of this app is to enable the computation of similarity of movies based on their plots. It is developed using the following research paper as a role model: [Tania Farinella, Sonia Bergamaschi, Laura Po: A Non-intrusive Movie Recommendation System. OTM Conferences (2) 2012: 736-751](http://www.dbgroup.unimo.it/paper/odbase2012.pdf)

The complete application workflow consists of 6 major steps:

1. Collecting movie data (movie URI, title and plot) from an RDF triplestore and saving the data to a local storage
2. Reading the data from the local storage and creating vectors of tokenized movie plots to be used in step 3; movie plots are also "cleaned" from stop-words.
3. Generate [TF-IDF (Term Frequency –Inverse Document Frequency)](http://en.wikipedia.org/wiki/Tf%E2%80%93idf) vectors for each tokenized movie plot
4. Use TF-IDF vectors from step 3 to generate a matrix on which [Singular Value Decomposition (SVD)](http://en.wikipedia.org/wiki/Singular_value_decomposition) will be performed; rows of this matrix represent movie plots, while columns represent tokens extracted from the movie plots
5. Perform SVD to reduce the dimension of the matrix
6. 6.	Starting from the reduced matrix (produced in step 5), calculate [cosine similarity](http://en.wikipedia.org/wiki/Cosine_similarity) between all pairs of the rows from the matrix (i.e., vectors representing movie plots), and save the values in separate .csv files for every movie, for further use.

#Implementation

[Jena RDF API](http://jena.apache.org/documentation/rdf/index.html) is used for retrieving movie URIs, titles and plots from an RDF triple contains numerous movie data gathered from [DBPedia](http://dbpedia.org/About), whereas the movie plots were added from Wikipedia.

Movie plot tokenization is performed using [Stanford Core-nlp framework](http://nlp.stanford.edu/software/corenlp.shtml#Download), with added list of stop-words from the [LUCENE](http://lucene.apache.org/core/) library.

TF-IDF calculations are performed using a custom written class.

Matrices and vectors are generated using [RealMatrix interface](http://commons.apache.org/proper/commons-math/apidocs/org/apache/commons/math3/linear/RealMatrix.html) and it's implementation [BlockRealMatrix](https://commons.apache.org/proper/commons-math/apidocs/org/apache/commons/math3/linear/BlockRealMatrix.html) and [RealVector](https://commons.apache.org/proper/commons-math/apidocs/org/apache/commons/math3/linear/BlockRealMatrix.html).

SVD calculation is performed using the Apache [SVD class](https://commons.apache.org/proper/commons-math/apidocs/org/apache/commons/math3/linear/SingularValueDecomposition.html)

Cosine similarity is calculated by a method that is a part of the RealVector implementation.

All of the data is written into appropriate .csv files using [opencsv library](http://sourceforge.net/projects/opencsv/).


For steps 1 and 2 of the above given workflow, dataRetrieval/ **DataSelection** class is used.
Custom TF-IDF calculations are performed using the methods from measures/**MeasureTFIDF** class.
All of the matrix generation and calculation is performed using the methods from the measures/**MatrixGenerator** class.
The measures/**CosineSimilarity** class is obviously used forcalculating similarity of vectors representing movie plots.

#Notes

In order to run the application, it is necessary to download the [Stanford Core-nlp](http://nlp.stanford.edu/software/corenlp.shtml#Download) library and add it to the build path of the project.


#Acknowledgements

This application has been developed as a part of the project assignment for the subject [Intelligent Systems](http://is.fon.rs/) at the Faculty of Organization Sciences, University of Belgrade, Serbia.

