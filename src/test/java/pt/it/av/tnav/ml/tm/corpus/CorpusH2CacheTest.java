package pt.it.av.tnav.ml.tm.corpus;

import pt.it.av.tnav.ml.tm.ngrams.NGram;
import pt.it.av.tnav.utils.ws.search.MockSearchEngine;
import pt.it.av.tnav.utils.ws.search.SearchEngine;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link CorpusH2Cache} class.
 *
 * @author <a href="mailto:mariolpantunes@gmail.com">Mário Antunes</a>
 * @version 1.0
 */
public class CorpusH2CacheTest {
  /**
   * Tests the caching mechanism of the CorpusDiskCache class.
   */
  @org.junit.Test
  public void test() throws Exception {
    List<SearchEngine.Result> lsr = new ArrayList<>();
    lsr.add(new SearchEngine.Result("banana", "banana", "banana"));
    lsr.add(new SearchEngine.Result("apple", "apple", "apple"));
    lsr.add(new SearchEngine.Result("peach", "peach", "peach"));

    List<String> l = new ArrayList<>(3);
    l.add("banana");
    l.add("apple");
    l.add("peach");

    String tmp = System.getProperty("java.io.tmpdir");
    Path db = Paths.get(tmp).resolve("corpus");
    Corpus c = new CorpusH2Cache(new CorpusSnippet(new MockSearchEngine(lsr)), db);

    Iterator<String> it = c.iterator(NGram.Unigram("UNIT_TEST_CORPUS_DISK_CACHE"));

    // Writes the cache
    int idx = 0;
    while (it.hasNext())
      assertTrue(it.next().equals(l.get(idx++)));
    assertTrue(idx == l.size());

    it = c.iterator(NGram.Unigram("UNIT_TEST_CORPUS_DISK_CACHE"));

    // Reads the cache
    idx = 0;
    while (it.hasNext())
      assertTrue(it.next().equals(l.get(idx++)));
    assertTrue(idx == l.size());

        /*try {
            Files.delete(Paths.get(tmp).resolve("corpus.mv.db"));
            Files.delete(Paths.get(tmp).resolve("corpus.trace.db"));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
  }
}
