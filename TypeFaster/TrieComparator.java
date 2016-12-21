import java.util.Comparator;

/**
 * Created by Steven Lu on 11/26/2016.
 */
public class TrieComparator implements Comparator<Trie> {
    //Compares based on time
    public int compare(Trie t1, Trie t2) {
        if (t1.getTime() < t2.getTime()) {
            return -1;
        } else if (t1.getTime() == t2.getTime()) {
            return 0;
        } else {
            return 1;
        }
    }
    public boolean equals(Trie t1, Trie t2) {
        return t1.getPath() == t2.getPath();
    }
}
