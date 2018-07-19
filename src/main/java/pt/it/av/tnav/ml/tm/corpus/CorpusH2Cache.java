package pt.it.av.tnav.ml.tm.corpus;

import pt.it.av.tnav.ml.tm.ngrams.NGram;
import pt.it.av.tnav.utils.structures.iterator.BRIterator;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * Wraps a Corpus class and provides a cache system based on disk files.
 *
 * @author <a href="mailto:mariolpantunes@gmail.com">Mário Antunes</a>
 * @version 1.0
 */
public class CorpusH2Cache implements Corpus, AutoCloseable {
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
  public CorpusH2Cache(Corpus c, Path cache) throws Exception {
    this.c = c;
    this.pCache = cache;
    Class.forName("org.h2.Driver");
    conn = DriverManager.getConnection("jdbc:h2:" + pCache.toString());
    createTable = conn.prepareStatement("CREATE TABLE IF NOT EXISTS corpus" +
        "(query VARCHAR(128) NOT NULL,date DATETIME NOT NULL, text CLOB NOT NULL, PRIMARY KEY(query, date));");

    createTable.execute();
    conn.commit();

    select = conn.prepareStatement("SELECT text FROM corpus WHERE query = ? ORDER BY date DESC LIMIT 1");
    insert = conn.prepareStatement("INSERT INTO corpus VALUES(?,?,?)");
  }


  @Override
  public Iterator<String> iterator(NGram ngram) {
    Iterator<String> rv = null;

    try {
      select.setString(1, ngram.toString());
      ResultSet rs = select.executeQuery();
      if (rs == null || !rs.first())
        rv = new CorpusIterator(c.iterator(ngram), ngram);
      else
        rv = new BRIterator(new BufferedReader(rs.getCharacterStream(1)));
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

  private class CorpusIterator implements Iterator<String> {
    private final Iterator<String> it;
    private final NGram ngram;
    private final StringBuilder sb;
    private boolean done;

    /**
     * Corpus iterator constructor.
     *
     * @param it iterator from the original corpus with the relevant content
     */
    public CorpusIterator(Iterator<String> it, NGram ngram) {
      this.it = it;
      this.ngram = ngram;
      sb = new StringBuilder();
      done = false;
    }

    @Override
    public boolean hasNext() {
      boolean rv = true;

      if (!it.hasNext()) {
        if (!done) {
          try {
            insert.setString(1, ngram.toString());
            insert.setDate(2, new Date(System.currentTimeMillis()));
            insert.setCharacterStream(3, new StringReader(sb.toString()));
            insert.execute();
            conn.commit();
            done = true;
          } catch (SQLException e) {
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
      sb.append(rv);
      sb.append(System.getProperty("line.separator"));
      return rv;
    }
  }
}
