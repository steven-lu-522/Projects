import java.util.Comparator;

/**
 * Created by Steven Lu on 11/27/2016.
 */
public class TrieComparator1 implements Comparator<Trie> {
    //compares based on number of errors
    public int compare(Trie t1, Trie t2) {
        if (t1.getAvgErrors() < t2.getAvgErrors()) {
            return -1;
        } else if (t1.getAvgErrors() < t2.getAvgErrors()) {
            return 0;
        } else {
            return 1;
        }
    }
    public boolean equals(Trie t1, Trie t2) {
        return t1.getPath() == t2.getPath();
    }
}
