package pt.it.av.tnav.ml.tm.corpus;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import pt.it.av.tnav.ml.tm.ngrams.NGram;
import pt.it.av.tnav.utils.ws.search.SearchEngine;

import java.io.IOException;
import java.util.Iterator;

/**
 * Implement a pt.it.av.atnog.ml.corpus based on the content of web pages from a search engine.
 */
public class CorpusWeb implements Corpus {
  private final SearchEngine se;

  /**
   * CorpusWeb constructor.
   *
   * @param se search engine used to find relevant web pages
   */
  public CorpusWeb(final SearchEngine se) {
    this.se = se;
  }

  @Override
  public Iterator<String> iterator(final NGram ngram) {
    return new CorpusIterator(ngram);
  }

  /**
   * Iterator that wraps a search engine iterator and extracts the content of relevant web pages.
   */
  private class CorpusIterator implements Iterator<String> {
    private final Iterator<SearchEngine.Result> it;

    /**
     * CorpusIterator constructor.
     *
     * @param ngram used to search relevant pieces of text.
     */
    private CorpusIterator(final NGram ngram) {
      this.it = se.searchIt(ngram.toString());
    }

    @Override
    public boolean hasNext() {
      return it.hasNext();
    }

    @Override
    public String next() {
      String rv;

      try {
        String uri = it.next().uri;
        Document d;
        if (!uri.contains("http://") && !uri.contains("https://"))
          d = Jsoup.connect("http://" + uri).get();
        else
          d = Jsoup.connect(uri).get();
        rv = d.text();
      } catch (IOException e) {
        e.printStackTrace();
        rv = "";
      }
      return rv;
    }
  }
}
