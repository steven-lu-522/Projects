import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * Created by Steven Lu on 11/14/2016.
 */
public class BackEnd {
    //Use a Trie to store the data on speeds between different keys, and error rates for each transition
    //because each of these is a sequence of the original character and the character after it.
    //Thus, using a trie would be the fastest way of accessing that exact data point.
    private Trie data;
    private ArrayList<int[]> textSamples;
    private int pageMin, index, err;
    private String curr;
    private long time;
    public BackEnd() {
        data = new Trie(StringCompression.getNumChars(), 2);
        textSamples = new ArrayList<int[]>();
        pageMin = 400;
        index = 0;
        curr = "";
        time = 0;
        err = 0;
    }
    public BackEnd(int minPageLength) {
        data = new Trie(StringCompression.getNumChars(), 2);
        textSamples = new ArrayList<int[]>();
        pageMin = minPageLength;
        index = 0;
        curr = "";
        time = 0;
        err = 0;
    }
    public int importFile(String fileName) {
        try {
            //Parse the file, storing "pages," along with information in compressed int array. Make sure "pages" are within
            //given page size range.
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String previous = "";
            String current = "";
            try {
                String line = br.readLine();
                while (line != null) {
                    current += " " + line;
                    while (current.lastIndexOf('.') >= pageMin) {
                        int sampleEnding = pageMin + current.substring(pageMin).indexOf('.') + 1;
                        textSamples.add(StringCompression.compressString(current.substring(0, sampleEnding).trim()));
                        previous = current.substring(0, sampleEnding);
                        current = current.substring(sampleEnding).trim();
                    }
                    line = br.readLine();
                }
            } catch (IOException io) {
                System.out.println("Error reading file. Please try again.");
                return 0;
            }
            if (current.length() > 0) {
                //Catches the last text sample. The previous sample is used to pad the current
                // one to make it greater than the minimum page length.
                if (previous.length() > 0) {
                    textSamples.add(StringCompression.compressString((findLastSentence(previous).trim() + " " + current).trim()));
                } else {
                    textSamples.add(StringCompression.compressString(current));
                }
            }
            return 1;

        } catch (FileNotFoundException f) {
            System.out.println("Invalid file name. Please try again.");
            return 0;
        }
    }
    public char expected() {
        return curr.charAt(index);
    }
    public boolean correct() {
        //Updates data after correct character was typed. Returns whether the end of the passage was reached.
        addData((System.nanoTime() - time) / 1000000, err);
        index++;
        err = 0;
        return index < curr.length();
    }
    public void incorrect() {
        err = 1;
    }
    public void newSample() {
        curr = StringCompression.decompressString(textSamples.get((int) (textSamples.size() * Math.random())));
        index = 0;
        err = 0;
    }
    public String getCurrentSample() {
        return curr;
    }
    public void addData(double time, int numErrors) {
        if (index > 0) {
            data.getChild(StringCompression.intForChar(curr.charAt(index - 1))).getChild(StringCompression.intForChar(curr.charAt(index))).addEntry(time, numErrors);
        }
    }
    public ArrayList<int[]> getTextSamples() {
        return textSamples;
    }
    public void start() {
        time = System.nanoTime();
    }
    public Trie[] findSlowestSequences() {
        //Use a min heap (represented by the priority queue) that keeps the 5 slowest times. Compare the head of
        //the PQ (smallest of the largest values) with each new value and remove it / add new value if head is faster
        PriorityQueue<Trie> pq = new PriorityQueue<Trie>(new TrieComparator());
        for (int i = 0; i < StringCompression.getNumChars(); i++) {
            for (int j = 0; j < StringCompression.getNumChars(); j++) {
                if (data.getChild(i).getChild(j).getCounter() > 0) {
                    if (pq.size() < 5) {
                        pq.add(data.getChild(i).getChild(j));
                    } else if (pq.peek().getTime() < data.getChild(i).getChild(j).getTime()) {
                        pq.poll();
                        pq.add(data.getChild(i).getChild(j));
                    }
                }
            }
        }
        Trie[] retval = new Trie[5];
        for (int k = 0; k < 5; k++) {
            retval[5 - k - 1] = pq.poll();
        }
        return retval;
    }
    public Trie[] findMostErrors() {
        //Use a min heap (represented by the priority queue) that keeps the 5 most avg errors. Compare the head of
        //the PQ (smallest of the largest values) with each new value and remove it / add new value if head has
        //less avg errors
        PriorityQueue<Trie> pq = new PriorityQueue<Trie>(new TrieComparator1());
        for (int i = 0; i < StringCompression.getNumChars(); i++) {
            for (int j = 0; j < StringCompression.getNumChars(); j++) {
                if (data.getChild(i).getChild(j).getCounter() > 0) {
                    if (pq.size() < 5) {
                        pq.add(data.getChild(i).getChild(j));
                    } else if (pq.peek().getErrorRate() < data.getChild(i).getChild(j).getErrorRate()) {
                        pq.poll();
                        pq.add(data.getChild(i).getChild(j));
                    }
                }
            }
        }
        Trie[] retval = new Trie[5];
        for (int k = 0; k < 5; k++) {
            retval[5 - k - 1] = pq.poll();
        }
        return retval;
    }
    private String findLastSentence(String s) {
        //Assumes the string itself is at least 1 sentence long.
        int i = s.substring(0,s.length() - 1).lastIndexOf('.');
        if ( i != -1) {
            return s.substring(i + 1);
        }
        return s;
    }
    public static void main(String[] args) {
        BackEnd b = new BackEnd();
        b.importFile("Stranger.txt");
        int i = 0;
        int sum = 0;
        for (int[] arr: b.getTextSamples()) {
            String s = StringCompression.decompressString(arr);
            sum += s.length();
            System.out.println("Sample " + i + ": " + s);
            i++;
        }
        System.out.println("Average number of characters per sample: " + ((double)sum / i));
    }
}
