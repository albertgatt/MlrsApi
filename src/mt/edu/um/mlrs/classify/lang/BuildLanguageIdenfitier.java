package mt.edu.um.mlrs.classify.lang;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import com.aliasi.classify.Classification;
import com.aliasi.classify.Classified;
import com.aliasi.classify.DynamicLMClassifier;
import com.aliasi.util.AbstractExternalizable;
import com.aliasi.util.Strings;

public class BuildLanguageIdenfitier {

	public static void main(String[] args) throws Exception {
	    File dataDir = new File(args[0]);
	    File modelFile = new File(args[1]);
	    int nGram = Integer.parseInt(args[2]);
	    int numChars = Integer.parseInt(args[3]);

	    String[] categories = dataDir.list();

	    @SuppressWarnings("unchecked")
	    DynamicLMClassifier classifier
	        = DynamicLMClassifier
	          .createNGramProcess(categories,nGram);

	    char[] csBuf = new char[numChars];
	    for (int i = 0; i < categories.length; ++i) {
	        String category = categories[i];
	        File trainingFile = new File(new File(dataDir,category),
	                                     category + ".txt");
	        FileInputStream fileIn
	            = new FileInputStream(trainingFile);
	        InputStreamReader reader
	            = new InputStreamReader(fileIn,Strings.UTF8);
	        reader.read(csBuf);
	        String text = new String(csBuf,0,numChars);
	        Classification c = new Classification(category);
	        Classified<CharSequence> classified
	            = new Classified<CharSequence>(text,c);
	        classifier.handle(classified);
	        reader.close();
	    }
	    
	    AbstractExternalizable.compileTo(classifier,modelFile);
	}

	
}
