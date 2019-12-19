
import java.util.*;

import java.util.stream.Collectors;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;


public class WordLadder implements DirectedGraph<String> {

    private Set<String> dictionary;
    private Set<Character> charset;


    public WordLadder() {
        dictionary = new HashSet<>();
        charset = new HashSet<>();
    }


    public WordLadder(String file) throws IOException {
        dictionary = new HashSet<>();
        charset = new HashSet<>();
        Files.lines(Paths.get(file))
            .filter(line -> !line.startsWith("#"))
            .forEach(word -> addWord(word.trim()));
    }


    /**
     * Adds the {@code word} to the dictionary, if it only contains letters.
     * The word is converted to lowercase.
     * @param word  the word
     */
    public void addWord(String word) {
        // 
        if (word.matches("\\p{L}+")) {
            word = word.toLowerCase();
            dictionary.add(word);
            for (char c : word.toCharArray()) {
                charset.add(c);
            }
        }
    }


    /**
     * @return the number of words in the dictionary
     */
    public int nrNodes() {
        return dictionary.size();
    }


    /**
     * @param  word  a graph node
     * @return the edges incident on node {@code word} as a List
     */
    public List<DirectedEdge<String>> outgoingEdges(String word) {

        /*
        This is to use without a charset.. we be hella cool B)

        LinkedList<DirectedEdge<String>> result = new LinkedList<>();

        ArrayList<String> regTerms = new ArrayList<>();
        for (int i = 0; i < word.length(); i++) {
            String newWord = word;
            char[] wordChars = newWord.toCharArray();
            wordChars[i] = '.';
            regTerms.add(new String(wordChars));
        }

        for (String string : dictionary){
                if (string.length() != word.length())
                    continue;

               for (String reg : regTerms){
                   if(string.matches(reg)){
                       result.add(new DirectedEdge<>(word, string));
                       break;
                   }
               }
            }

        return result;

         */

        LinkedList<DirectedEdge<String>> result = new LinkedList<>();

        StringBuilder sb = new StringBuilder(word);

        for (int i = 0; i < word.length(); i++){
           char lmao = sb.charAt(i);
            for (char c : charset) {
                sb.replace(i, i+1, c+"");
                if(dictionary.contains(sb.toString())){
                    result.add(new DirectedEdge(word, sb.toString()));
                }

            }
            sb.replace(i, i+1, lmao+"");
        }

        return result;
    }


    public double guessCost(String v, String w) {
        char[] fst = v.toCharArray();
        char[] snd = w.toCharArray();
        double res = 0;


        for (int i = 0; i < fst.length; i++){
            if (fst[i] == snd[i]){
                res++;
            }
        }
        return (fst.length - res);
    }


    /**
     * @return a string representation of the graph
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Word ladder with " + nrNodes() + " words, " +
                 "charset: \"" + charset.stream().map(x -> x.toString()).collect(Collectors.joining()) + "\"\n\n");
        int ctr = 0;
        s.append("Example words and ladder steps:\n");
        for (String v : dictionary) {
            if (v.length() != 5) continue;
            List<DirectedEdge<String>> edges = outgoingEdges(v);
            if (edges.isEmpty()) continue;
            if (ctr++ > 10) break;
            s.append(v + " --> " + edges.stream().map(e -> e.to()).collect(Collectors.joining(", ")) + "\n");
        }
        return s.toString();
    }


    /**
     * Unit tests the class
     * @param args  the command-line arguments
     */
    public static void main(String[] args) {
        try {
            System.out.println(new WordLadder(args[0]));
        } catch (Exception e) {
            // If there is an error, print it and a little command-line help
            e.printStackTrace();
            System.err.println();
            System.err.println("Usage: java WordLadder dictionary-file");
            System.exit(1);
        }
    }

}
