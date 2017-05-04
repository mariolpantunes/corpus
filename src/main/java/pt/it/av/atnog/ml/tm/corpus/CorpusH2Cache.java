package pt.it.av.atnog.ml.tm.corpus;

import pt.it.av.atnog.ml.tm.ngrams.NGram;

import java.io.BufferedReader;
import java.io.Reader;
import java.nio.file.Path;
import java.sql.*;
import java.util.Iterator;

import org.h2.jdbcx.JdbcDataSource;

/**
 * Wraps a Corpus class and provides a cache system based on disk files.
 *
 * @author <a href="mailto:mariolpantunes@gmail.com">Mário Antunes</a>
 * @version 1.0
 */
public class CorpusH2Cache implements Corpus, AutoCloseable{
    private final Corpus c;
    private final Path pCache;
    private Connection conn;
    private PreparedStatement createTable, insert, select;


    /**
     * Corpus disk pCache constructor
     *
     * @param c     another corpus class
     * @param cache directory where the pCache is written/read
     */
    public CorpusH2Cache(Corpus c, Path cache) throws Exception{
        this.c = c;
        this.pCache = cache;
        Class.forName("org.h2.Driver");
        conn = DriverManager.getConnection("jdbc:h2:"+pCache.toString());
        createTable = conn.prepareStatement("CREATE TABLE IF NOT EXISTS corpus"+
                "(query VARCHAR(128) NOT NULL,date DATETIME NOT NULL, text CLOB NOT NULL, PRIMARY KEY(query, date));");

        createTable.execute();
        conn.commit();

        select = conn.prepareStatement("SELECT text FROM corpus WHERE query = ? ORDER BY date DESC LIMIT 1");
        insert = conn.prepareStatement("");
    }


    @Override
    public Iterator<String> iterator(NGram ngram) {
        Iterator<String> rv = null;

        try {
            select.setString(1, ngram.toString());
            ResultSet rs = select.executeQuery();
            if (rs == null || !rs.first())
                rv = new CorpusIterator();
            else
                rv = new CorpusH2Iterator(rs.getCharacterStream(1));
            //rs.close();
        } catch (Exception e) {
            //should not happen
            e.printStackTrace();
        }

        return rv;
    }

    @Override
    public void close() throws SQLException {
        createTable.close();
        insert.close();
        select.close();
        conn.close();
    }

    private class CorpusH2Iterator implements Iterator<String> {
        private BufferedReader br;

        public CorpusH2Iterator(Reader r) {
            br = new BufferedReader(r);
        }


        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public String next() {
            return null;
        }
    }

    private class CorpusIterator implements Iterator<String> {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public String next() {
            return null;
        }
    }
}
