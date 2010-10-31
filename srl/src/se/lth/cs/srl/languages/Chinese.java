package se.lth.cs.srl.languages;

import is2.lemmatizer.LemmatizerInterface;
import is2.parser.Parser;
import is2.tag3.Tagger;

import java.io.File;
import java.util.Map;
import java.util.regex.Pattern;

import se.lth.cs.srl.corpus.Predicate;
import se.lth.cs.srl.corpus.Sentence;
import se.lth.cs.srl.corpus.Word;
import se.lth.cs.srl.options.FullPipelineOptions;
import se.lth.cs.srl.preprocessor.Preprocessor;
import se.lth.cs.srl.preprocessor.SimpleChineseLemmatizer;
import se.lth.cs.srl.preprocessor.tokenization.StanfordChineseSegmenterWrapper;
import se.lth.cs.srl.preprocessor.tokenization.Tokenizer;
import se.lth.cs.srl.util.BohnetHelper;
import se.lth.cs.srl.util.FileExistenceVerifier;

public class Chinese extends Language {

	private static Pattern CALSPattern=Pattern.compile("^A0|A1|A2|A3|A4$");
	@Override
	public String getCoreArgumentLabelSequence(Predicate pred,Map<Word, String> proposition) {
		Sentence sen=pred.getMySentence();
		StringBuilder ret=new StringBuilder();
		for(int i=1,size=sen.size();i<size;++i){
			Word w=sen.get(i);
			if(pred==w){
				ret.append(" "+pred.getSense());
			}
			if(proposition.containsKey(w)){
				String label=proposition.get(w);
				if(CALSPattern.matcher(label).matches())
					ret.append(" "+label);
			}
		}
		return ret.toString();
	}

	@Override
	public String getDefaultSense(String lemma) {
		return lemma+".01";
	}

	@Override
	public Pattern getFeatSplitPattern() {
		throw new Error("You are wrong here.");
	}

	@Override
	public L getL() {
		return L.chi;
	}

	@Override
	public String getLexiconURL(Predicate pred) {
		return null;
	}

	@Override
	public Preprocessor getPreprocessor(FullPipelineOptions options) {
		Tokenizer tokenizer=(options.loadPreprocessorWithTokenizer ? new StanfordChineseSegmenterWrapper(options.tokenizer) : null); 
		LemmatizerInterface lemmatizer=new SimpleChineseLemmatizer();
		Tagger tagger=BohnetHelper.getTagger(options.tagger);
		Parser parser=BohnetHelper.getParser(options.parser);
		return new Preprocessor(tokenizer,lemmatizer,tagger,null,parser);
	}

	@Override
	public String verifyLanguageSpecificModelFiles(FullPipelineOptions options) {
		if(options.loadPreprocessorWithTokenizer){
			File serDictionaryFile=new File(options.tokenizer,"dict-chris6.ser.gz");
			File ctbFile=new File(options.tokenizer,"ctb.gz");
			return FileExistenceVerifier.verifyFiles(serDictionaryFile,ctbFile);
		} else {
			return null;
		}
	}

}
