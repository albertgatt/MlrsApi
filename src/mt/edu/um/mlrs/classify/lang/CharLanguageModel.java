package mt.edu.um.mlrs.classify.lang;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

import mt.edu.um.mlrs.exception.TaggerException;

import com.aliasi.lm.NGramProcessLM;
import com.aliasi.lm.TrieCharSeqCounter;

public class CharLanguageModel {
	private NGramProcessLM _mlm;
	private int _mNgram;
	private int _numChars;
	private double _lambda;
	
	public CharLanguageModel(int maxNGram) {
		this(maxNGram, Character.MAX_VALUE, maxNGram);
	}
	
	public CharLanguageModel(int maxNGram, int numChars, double lambda) {
		this._mNgram = maxNGram;
		this._numChars = numChars;
		this._lambda = lambda;
		this._mlm = new NGramProcessLM(this._mNgram, this._numChars, this._lambda);
	}
	
	public CharLanguageModel(String modelFile) {
		try {
			FileInputStream fileIn = new FileInputStream(modelFile);
			BufferedInputStream objIn = new BufferedInputStream(fileIn);			
			this._mlm = NGramProcessLM.readFrom(objIn);
			objIn.close();
			
			this._mNgram = this._mlm.maxNGram();
			this._lambda = this._mlm.getLambdaFactor();
			
		} catch (Exception e) {
			throw new RuntimeException(
					"Could not build model from supplied model file.", e);
		}
	}
	
	public double log2ConditionalEstimate(CharSequence seq) {
		return this._mlm.log2ConditionalEstimate(seq);
	}
	
	public double log2Estimate(CharSequence seq) {
		return this._mlm.log2Estimate(seq);
	}
	
	public double probability(CharSequence seq) {
		return this._mlm.prob(seq);
	}
	
	public double averageProbability(CharSequence seq, int length) {
		int onset = 0;
		double counter = 0.0D;
		double prob = 0.0D;
		int seqLength = seq.length();
		int offset;
		
		while(onset < seqLength) {
			offset = onset + length; 
			
			if(offset > seqLength) {
				offset = seqLength-1;
			}
			
			prob += Math.pow(2, log2Estimate(seq, onset, offset));
			counter++;
			onset += offset;
		}
		
		return prob/counter;
	}
	
	public double maxProbability(CharSequence seq, int length) {
		int onset = 0;
		double prob = 0.0D;
		double maxprob = 0.0D;
		int seqLength = seq.length();
		int offset;
		
		while(onset < seqLength) {
			offset = onset + length; 
			
			if(offset > seqLength) {
				offset = seqLength;
			}
			
			prob = Math.pow(2, log2Estimate(seq, onset, offset));
			
			if(maxprob < prob) {
				maxprob = prob;
			}
			
			onset += offset;						
		}
		
		return maxprob;
	}
	
	public <T extends CharSequence> double maxProbability(Collection<T> sequences) {
		double[] probs = this.probabilities(sequences);
		double max = 0.0D;
		
		for(double d: probs) {
			if(d > max) {
				max = d;
			}
		}
		
		return max;
	}
	
	private <T extends CharSequence> double[] probabilities(Collection<T> sequences) {
		double[] probabilities = new double[sequences.size()];
		int counter = 0;
		
		for(T s: sequences) {
			probabilities[counter] = this.probability(s);
			counter++;
		}
		
		return probabilities;		
	}
	
	public <T extends CharSequence> double averageProbability(Collection<T> sequences) {
		double[] probs = this.probabilities(sequences);
		double average = 0.0D;
		
		for(double d: probs) {
			average += d;
		}
		
		return average/probs.length;
	}
	
	public double log2Estimate(CharSequence seq, int onset, int offset) {
		char[] chars = seq.toString().toCharArray();
		return this._mlm.log2Estimate(chars, onset, offset);
	}
	
	
	
	public void train(CharSequence data) {
		this._mlm.train(data);
	}	
	
	public void writeModel(String outputFile) {
		try {
			FileOutputStream fileOut = new FileOutputStream(
					new File(outputFile));
			BufferedOutputStream bufOut = new BufferedOutputStream(fileOut);				
			this._mlm.writeTo(bufOut);
			bufOut.close();
			
		} catch (FileNotFoundException fnfe) {
			throw new TaggerException("No such file: " + outputFile, fnfe);
		
		} catch (IOException ioe) {
			throw new TaggerException("Error compiling model to file.", ioe);
		}
	}
	
	public void prune(int count) {
		TrieCharSeqCounter counter = this._mlm.substringCounter();
		counter.prune(count);
	}
	
	
	
	
}
