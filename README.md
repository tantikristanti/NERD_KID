# <font color="blue"> NERD_KID </font> :neckbeard:

This project will focus on machine learning for classifying items found in Wikidata into 27 classes in Nerd, for instance, Institution, Location, Person.

# Goal
According to [Wikidata's statistics](https://www.wikidata.org/wiki/Special:Statistics), more than 29 million items can be found in Wikidata. With its rich and open knowledge base, it's interesting to learn how those items can be classified into 27 classes. These classes are based on [Grobid-NER](http://grobid-ner.readthedocs.io/en/latest/class-and-senses/) 's project results.

The idea of this project is to make computers <font color="red"> understand </font> how grouping millions of items in Wikidata into specific classes based on their data characteristics.

Let's take an example of an item [Albert Einstein](https://www.wikidata.org/wiki/Q937) in Wikidata which has an identifier 'Q937'. This item actually has a number of properties (e.g. 'instance of-P31', 'sex or gender-P21', etc.) as well as a number of values for each property (e.g. 'human-Q5' as a value of property 'P31', 'male-Q6581097' as a value of property 'P-21'). Based on a trained given model, computers will understand how making some predictions and classifying the Albert Einstein's item into a certain class, Person class, for instance. This project will also consider disambiguity of items. For instance, computers will not classify [Marshall Plan](https://www.wikidata.org/wiki/Q4576) into a Person class, because it's not a name of a person, rather it's an American initiative to aid Western Europe. 

![Albert Einstein](pic/AlbertEinstein.jpg)

# Tools
![Tools](pic/Tools.jpg)

# Installation-Build-Run
**1. Installation**

*a. Clone this source* 

```$ git clone https://github.com/tantikristanti/NERD_KID.git```

*b. Download the zip file*

[NERD_KID](https://github.com/tantikristanti/NERD_KID/archive/master.zip)

**2. Preparing the data or models**

*All data or models can be put in* `/data/`

**3. Build the project**

```$ mvn clean install```

**4. Execute the application**

*a. Getting instances and classes correspond to each class from [Grobid-Ner](https://github.com/kermitt2/grobid-ner/tree/master/grobid-ner/resources/dataset/ner/corpus/xml/final)*

```$ mvn exec:java -Dexec.mainClass="org.nerd.kid.preprocessing.OpenXMLFileGrobidNer"```

- New data is in XML format which can be easily copied directly to the file `/data/xml/annotatedCorpus.xml`
- The result can be seen in `/result/resultCSVAnnotatedCorpus.csv`

*b. Model training using Random Forest classification [SMILE](https://github.com/haifengl/smile/)*

```$ mvn exec:java -Dexec.mainClass="org.nerd.kid.main.Main"```

- Input needed are 1) training data file (and testing data file if exist) ; 2) percentage of training data ; 3) name of output file
- Output can be seen in `/result/`

*c. Access NERD's Rest API*

```$ mvn exec:java -Dexec.mainClass="org.nerd.kid.rest.MainCallRestAPINerd"```

- The example of how to enter the correct URL and Query can be seen in "data/example/exampleCurlNERD.txt"

*d. Building new data by accessing Wikidata's API and getting the new predicted result*

```$ mvn exec:java -Dexec.mainClass="org.nerd.kid.rest.MainCallAPIWikidata"```

- The result of predicted class can be seen in `/result/Predicted_Testing.csv`

**5. Example of Prediction of Class**

*To predict Wikidata Id:*
- Copy the list of Wikidata Id to be predicted into `data/preannotation/dataPreannotation.csv`
- It is possible also to change the training data located in `data/Training.arff`
- Run the service of prediction :

```$ mvn exec:java -Dexec.mainClass="org.nerd.kid.preannotation.MainPreAnnotation"```

*The example of result can be seen in the picture*
![ResultPrediction](pic/ResultPrediction.jpg)

## Contact

Contact: Tanti Kristanti (<tantikristanti@gmail.com>)

