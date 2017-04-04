package pt.it.av.atnog.ml.tm.corpus;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import pt.it.av.atnog.ml.tm.corpus.Corpus;
import pt.it.av.atnog.ml.tm.ngrams.NGram;
import pt.it.av.atnog.utils.ws.search.SearchEngine;

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
         * @param ngram
         */
        public CorpusIterator(final NGram ngram) {
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
                String url = it.next().url;
                Document d;
                if (!url.contains("http://") && !url.contains("https://"))
                    d = Jsoup.connect("http://" + url).get();
                else
                    d = Jsoup.connect(url).get();
                rv = d.text();
            } catch (IOException e) {
                e.printStackTrace();
                rv = "";
            }
            return rv;
        }
    }
}
