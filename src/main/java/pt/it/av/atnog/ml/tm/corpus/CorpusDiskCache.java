package pt.it.av.atnog.ml.tm.corpus;

import pt.it.av.atnog.ml.tm.ngrams.NGram;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Wraps a Corpus class and provides a pCache system based on disk files.
 *
 * @author <a href="mailto:mariolpantunes@gmail.com">Mário Antunes</a>
 * @version 1.0
 */
public class CorpusDiskCache implements Corpus {
    private final Corpus c;
    private final Path pCache;

    /**
     * Corpus disk pCache constructor
     *
     * @param c     another corpus class
     * @param cache directory where the pCache is written/read
     */
    public CorpusDiskCache(Corpus c, Path cache) {
        this.c = c;
        this.pCache = cache;
    }

    @Override
    public Iterator<String> iterator(NGram ngram) {
        Path file = pCache.resolve(ngram + ".gz");
        if (Files.isReadable(file))
            return new CorpusCacheIterator(file);
        else
            return new CorpusIterator(c.iterator(ngram), file);
    }

    /**
     * Implements a corpus iterator that reads the corpus from the pCache (disk file).
     */
    private class CorpusCacheIterator implements Iterator<String> {
        private BufferedReader in;
        private String line = null;

        /**
         * Corpus pCache iterator constructor.
         *
         * @param file the file that contains the corpus content
         */
        public CorpusCacheIterator(Path file) {
            try {
                this.in = new BufferedReader(new InputStreamReader(new GZIPInputStream(
                        Files.newInputStream(file)), "UTF-8"));
                line = in.readLine();
            } catch (IOException e) {
                //should not happen
                e.printStackTrace();
            }
        }

        @Override
        public boolean hasNext() {
            boolean rv = true;
            if (line == null) {
                rv = false;
                if (in != null) {
                    try {
                        in.close();
                        in = null;
                    } catch (IOException e) {
                        //should not happen
                        e.printStackTrace();
                    }
                }
            }
            return rv;
        }

        @Override
        public String next() {
            String rv = line;
            try {
                line = in.readLine();
            } catch (IOException e) {
                //should not happen
                e.printStackTrace();
                line = null;
            }
            return rv;
        }
    }

    /**
     * Implements a corpus iterator that consumes content from the original corpus and store it in the pCache.
     */
    private class CorpusIterator implements Iterator<String> {
        private final Iterator<String> it;
        private BufferedWriter out = null;


        /**
         * Corpus iterator constructor.
         *
         * @param it   iterator from the original corpus with the relevant content
         * @param file file in the pCache that will hold the content
         */
        public CorpusIterator(Iterator<String> it, Path file) {
            this.it = it;
            if (it.hasNext()) {
                try {
                    this.out = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream
                            (Files.newOutputStream(file)), "UTF-8"));
                } catch (IOException e) {
                    //should not happen
                    e.printStackTrace();
                }
            }
        }

        @Override
        public boolean hasNext() {
            boolean rv = true;

            if (!it.hasNext()) {
                if (out != null) {
                    try {
                        out.close();
                        out = null;
                    } catch (IOException e) {
                        //should not happen
                        e.printStackTrace();
                    }
                }
                rv = false;
            }
            return rv;
        }

        @Override
        public String next() {
            String rv = it.next();
            try {
                out.write(rv);
                out.newLine();
            } catch (IOException e) {
                //should not happen
                e.printStackTrace();
            }
            return rv;
        }
    }
}
