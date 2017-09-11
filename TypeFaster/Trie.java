/**
 * Created by Steven Lu on 11/13/2016.
 */
public class Trie {
    double time;
    double errorRate;
    int counter;
    int path;
    private Trie[] children;
    public Trie(int possibilities, int depth) {
        path = 0;
        if (depth > 0) {
            children = new Trie[possibilities];
            for (int i = 0; i < possibilities; i++) {
                children[i] = new Trie(possibilities, depth - 1, i);
            }
        } else {
            children = null;
        }
        time = 0;
        counter = 0;
    }
    public Trie(int possibilities, int depth, int path1) {
        path = path1;
        if (depth > 0) {
            children = new Trie[possibilities];
            for (int i = 0; i < possibilities; i++) {
                children[i] = new Trie(possibilities, depth - 1, path1 * 100 + i);

            }
        } else {
            children = null;
        }
        time = 0;
        counter = 0;
    }
    public void addEntry(double entry, int error) {
        time = ((time * counter) + entry ) / (counter + 1);
        errorRate = ((errorRate * counter) + error) / (counter + 1);
        counter++;
    }
    public int getCounter() {
        return counter;
    }
    public int getPath() {
        return path;
    }
    public double getTime() {
        return time;
    }
    public double getErrorRate() {
        return errorRate;
    }
    public Trie getChild(int index) {
        return children[index];
    }
    public Trie[] getChildren() {
        return children;
    }
}
