package pt.it.av.tnav.ml.tm.corpus;

import pt.it.av.tnav.ml.tm.ngrams.NGram;

import java.util.Iterator;

/**
 * Corpus interface provides a iterator with relevant pieces of text related with the provided ngram.
 *
 * @author <a href="mailto:mariolpantunes@gmail.com">Mário Antunes</a>
 * @version 1.0
 */
public interface Corpus {
  /**
   * Provides relevant pieces of text related with the provided ngram
   *
   * @param ngram used to search relevant pieces of text.
   * @return iterator with the relevant pieces of text.
   */
  Iterator<String> iterator(NGram ngram);
}
