# <font color="blue"> NERD_KID </font> :neckbeard:

This project focuses on machine learning for classifying items found in Wikidata into 27 classes in Nerd, for instance, Institution, Location, Person.

# Goal
According to [Wikidata's statistics](https://www.wikidata.org/wiki/Special:Statistics), more than 49 million items can be found in Wikidata. With its rich and open knowledge base, it's interesting to learn how those items can be classified into 27 classes. These classes are based on [Grobid-NER](http://grobid-ner.readthedocs.io/en/latest/class-and-senses/) 's project results.

The idea of this project is to make computers <font color="red"> understand </font> how grouping millions of items in Wikidata into specific classes based on their data characteristics.

Let's take an example of an item [Albert Einstein](https://www.wikidata.org/wiki/Q937) in Wikidata which has an identifier 'Q937'. This item actually has a number of properties (e.g. 'instance of-P31', 'sex or gender-P21', etc.) as well as a number of values for each property (e.g. 'human-Q5' as a value of property 'P31', 'male-Q6581097' as a value of property 'P-21'). Based on a trained given model, computers will understand how making some predictions and classifying the Albert Einstein's item into a certain class, Person class, for instance. This project will also consider disambiguity of items. For instance, computers will not classify [Marshall Plan](https://www.wikidata.org/wiki/Q4576) into a Person class, because it's not a name of a person, rather it's an American initiative to aid Western Europe. 

![Albert Einstein](pic/AlbertEinstein.jpg)

# Tools
![Tools](pic/Tools.jpg)

# Data from Grobid-Ner's Project

*As base model (model0), this project uses data from Grobid-Ner's project by collecting their mentions and classes as well as disambiguation results with the use of Entity-Fishing Rest API*

- Firstly, the files from [Grobid-Ner](https://github.com/kermitt2/grobid-ner/tree/master/grobid-ner/resources/dataset/ner/corpus/xml/final)'s project which are in TEI-XML format need to be extracted in order to get some mentions (tokens and their positions in the original text) and also their type of classes. The type of classes which is 27 in total and their detail explanations can be seen in [Classes](https://grobid-ner.readthedocs.io/en/latest/class-and-senses/)
    - The result can be seen in ![Annotated Corpus Result](data/csv/GrobidNer/AnnotatedCorpusResult.csv) 
    - Then, every single mention (raw text) collected from previous step will be disambiguated by using Entity-Fishing Rest-API (particularly with the use of short text disambiguation service)
    - The disambiguation results are prepared in ![Json format](data/json/Result_EntityFishingShortTextDisambiguation.json) and in ![Csv format](data/csv/NewElements.csv) for evaluation purposes
    - New elements collected can also be checked manually whether they have the correct classes before being used for training or evaluation purposes. 

These 2 tasks can be done by this service:
```$ mvn exec:java -Dexec.mainClass="org.nerd.kid.preprocessing.GrobidNERTrainingDataTransformer"```

# Installation-Build-Run
**1. Installation**

*a. Clone this source* 

```$ git clone https://github.com/tantikristanti/NERD_KID.git```

*b. Download the zip file*

[NERD_KID](https://github.com/tantikristanti/NERD_KID/archive/master.zip)

**2. Build the project**

```$ mvn clean install```

**3. Prepare new data**

*New data can be collected by extracting the text with the text disambiguation service of [Entity-Fishing](http://nerd.readthedocs.io/en/latest/restAPI.html)*
- Firstly, the ***text*** and the ***language*** need to be input in order to get some mentions and disambiguation results in Json format
    - The example of the text can be seen here ![Text Example](data/txt/exampleText.txt) 
    - If the language is not mentioned, the text will be processed as English text by default.
    - The raw Json result from Entity-Fsihing can be seen in ![Entity-Fishing Text Disambiguation](data/json/Result_EntityFishingTextDisambiguation.json) 
    
- Secondly, the result from the previous step will be parsed in order to get a list of Wikidata Ids and their classes
    - The list of Wikidata Ids and Classes can be seen in ![New Elements](data/csv/NewElements.csv) 
    - It is a very simple csv file that contain Wikidata Ids and their classes. For example:
    
    ```
    WikidataID,Class
    Q76,PERSON
    Q1408,LOCATION
    ```

These 2 tasks can be done by this service:
```$ mvn exec:java -Dexec.mainClass="org.nerd.kid.extractor.MainRestAPINerdCaller"```

**4. Build a training file**
- In order to build a new training data, this service can be used:

```$ mvn exec:java -Dexec.mainClass="org.nerd.kid.arff.MainTrainerGenerator"```

*(Basically, a training file built based on the datasets prepared in ![Base Elements](data/csv/BaseElements.csv) 
 This step is done if there isn't any training file or there is a need to build a new one.)*

- The result of Arff file can be seen in ![Training](result/arff/Training.arff) 
- It is also possible to check first whether the data and features of wikidata Ids are correct/complete by checking the Csv file result located in ![Result From Arff Generator](result/csv/ResultFromArffGenerator.csv)  


*Note :* 
- CSV files must have at least the header `WikidataID` and `Class`. So:

```
WikidataID,labelWikidata,Class
Q3318231,Joko Widodo,PERSON
...
```

or

```
WikidataID,Class
Q76,PERSON
...
```

are in a correct format.

- Error in this step can appear if certain element's Id is no longer available in Wikidata. 
 Just delete the Id that want to be processed from the CSV files.
  
**5. Train, evaluate, and build the model**

*Model training using Random Forest classification [SMILE](https://github.com/haifengl/smile/)*

```$ mvn exec:java -Dexec.mainClass="org.nerd.kid.model.MainModelBuilder"```

- The evaluation result can be seen in ![Result_Trained_Model](result/txt/Result_Trained_Model.txt) 
- The model itself can be found in Xml and Zip format which are located in the temporary file `/tmp`

**6. Decompress the model**

*This service is used when no model in XML format available or there is a need to extract the model from Zip into Xml format.*
The model normally is located in `/resource/model.zip`. For decompressing the file, this service can be called:

```$ mvn exec:java -Dexec.mainClass="org.nerd.kid.model.ExtractModel"```


**7. Get new predicted classes**

*Prepare the list of Wikidata Id*

Firstly, prepare the list of new Wikidata Id that need to be predicted (in this case, Nerd_kid will predict the class for each Wikidata Id prepared)
The list then can be copied in ![New Elements](data/csv/NewElements.csv) or generated as explained in step *3. Prepare new data into*
It is a very simple csv file that contain Wikidata Id for each line without name of label and neither for the class. For example:

```
WikidataID,Class
Q76,
Q1408,
```

*To predict Wikidata Id:*


```$ mvn exec:java -Dexec.mainClass="org.nerd.kid.preannotation.MainPreAnnotation"```

- The result can be seen in ![Result Predicted Class](result/csv/ResultPredictedClass.csv)


**7. Web version**

Online version of class prediction can be accessed here [Nerd-Kid](http://nerd.huma-num.fr/kid/service/ner?id=Q1) 

User can only just change the Wikidata Id started with 'Q' and then the number.

![ResultPrediction](pic/ResultPredictionWeb.jpg)

- The result will be Wikidata Id, the properties, and the result of predicted class.

## Contact

Contact: Tanti Kristanti (<tantikristanti@gmail.com>)


