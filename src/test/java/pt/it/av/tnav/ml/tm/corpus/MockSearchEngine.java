package pt.it.av.tnav.ml.tm.corpus;

import pt.it.av.tnav.utils.ws.search.SearchEngine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A mockup of the {@link SearchEngine} class.
 * Used for unit testing
 *
 * @author <a href="mailto:mariolpantunes@gmail.com">Mário Antunes</a>
 * @version 1.0
 */
public class MockSearchEngine implements SearchEngine {
  List<SearchEngine.Result> l = new ArrayList<>(3);

  /**
   * MockSearchEngine constructor.
   */
  public MockSearchEngine() {
    l.add(new SearchEngine.Result("banana", "banana", "banana"));
    l.add(new SearchEngine.Result("apple", "apple", "apple"));
    l.add(new SearchEngine.Result("peach", "peach", "peach"));
  }

  @Override
  public List<Result> search(String s) {
    return l;
  }

  @Override
  public Iterator<SearchEngine.Result> searchIt(String s) {
    return l.iterator();
  }
}
