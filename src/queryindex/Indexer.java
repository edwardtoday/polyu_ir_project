/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package queryindex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * The indexer class to do the whole thing.
 */
public class Indexer {
    // Only one indexer for each runtime. Singleton here.

    private static Indexer instance = null;

    protected Indexer() {
        // Exists only to defeat instantiation.
    }

    public static Indexer getInstance() {
        if (instance == null) {
            instance = new Indexer();
        }
        return instance;
    }
    private String dataPath = "";
    private File stoplist;
    private HashMap<String, HashMap<File, HashSet<Integer>>> index =
            new HashMap<String, HashMap<File, HashSet<Integer>>>();
    private boolean verbose = false;
    private int docCount = 0;
    private File[] cachedFiles;
    private String[] cachedFilenames;
    private Double[] cachedWeights;
    private HashSet<String> stopwords = new HashSet<String>();
    private HashSet<String> toHighlight = new HashSet<String>();

    /**
     * Clear all index related storage.
     */
    public void clearAll() {
        index.clear();
        docCount = 0;
        cachedFiles = null;
        cachedFilenames = null;
        cachedWeights = null;
    }

    public void LoadStoplist() {
        if (stoplist.canRead()) {
            FileReader fr = null;
            BufferedReader inputText = null;
            try {
                fr = new FileReader(stoplist);
                inputText = new BufferedReader(fr);
                String line = inputText.readLine();
                while (line != null) {
                    stopwords.add(line.toLowerCase());
                    line = inputText.readLine();
                }
            } // Display messages if something goes wrong
            catch (IOException e) {
                // TODO Any message to display?
            } // Always be sure to close the input stream!
            finally {
                try {
                    if (fr != null) {
                        fr.close();
                    }
                    if (inputText != null) {
                        inputText.close();
                    }
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Load files into the index.
     *
     * If the parameter is a directory, all files in it will be indexed,
     * including those in sub-folders.
     * @param file
     */
    public void RecursiveLoad(File file) {
        if (!file.isDirectory()) {
            // Load the file
            FileReader fr = null;
            BufferedReader inputText = null;
            try {
                fr = new FileReader(file);
                inputText = new BufferedReader(fr);
                docCount++;
                Integer pos = 0;
                String line = inputText.readLine();
                while (line != null) {
                    //split using whitespaces or .
                    String[] words = line.toLowerCase().split("(\\s+|\\.)");
                    for (int i = 0; i < words.length; i++) {
                        pos++;
                        // check for stopwords
                        if (!stopwords.contains(words[i])) {
                            if (index.containsKey(words[i])) {
                                // Word exists in index
                                HashMap<File, HashSet<Integer>> entry = index.get(words[i]);
                                HashSet<Integer> positions;
                                if (entry.containsKey(file)) {
                                    // Word also exists in current file before, add a new position
                                    positions = entry.get(file);
                                } else {
                                    // First occurance of the word in current file, add the file record
                                    positions = new HashSet<Integer>();
                                }
                                positions.add(pos);
                                entry.put(file, positions);
                                index.put(words[i], entry);
                            } else {
                                // Add word to index
                                HashSet<Integer> positions = new HashSet<Integer>();
                                positions.add(pos);
                                HashMap<File, HashSet<Integer>> entry = new HashMap<File, HashSet<Integer>>();
                                entry.put(file, positions);
                                index.put(words[i], entry);
                            }
                        }
                    }
                    line = inputText.readLine();
                }
            } // Display messages if something goes wrong
            catch (IOException e) {
                // TODO Any message to display?
            } // Always be sure to close the input stream!
            finally {
                try {
                    if (fr != null) {
                        fr.close();
                    }
                    if (inputText != null) {
                        inputText.close();
                    }
                } catch (IOException e) {
                }
            }
        } else {
            // Load every file in the directory
            final File[] files = file.listFiles();
            for (final File file_in_folder : files) {
                RecursiveLoad(file_in_folder);
            }
        }
    }

    /**
     * Print the whole inverted index
     */
    public void PrintList() {
        for (String key : index.keySet()) {
            System.out.println(key + ":" + index.get(key).toString());
        }
    }

    /**
     * Search for a word in the index.
     * @param query_word
     * @return A set of documents containing that word
     */
    public HashMap<File, Double> SearchWord(String query_word) {
        HashMap<File, Double> retVal = new HashMap<File, Double>();

        HashMap<File, HashSet<Integer>> entry = index.get(query_word);
        if (entry != null) {
            // Calculate the weight of each document
            for (File file : entry.keySet()) {
                Double tf = new Double(entry.get(file).size());
                Double idf = Math.log(
                        docCount / new Double(entry.keySet().size()));
                Double weight = tf * idf;
                retVal.put(file, weight);
            }
        }

        if (verbose) {
            System.out.println("SearchWord: " + query_word + " : " + retVal.keySet());
        }

        return retVal;
    }

    /**
     * Search for a phrase.
     * @param phrase
     * @return A set of documents containing that phrase.
     */
    public HashMap<File, Double> SearchPhrase(String phrase) {
        if (!phrase.contains(" ")) {
            return SearchWord(phrase);
        }

        HashMap<File, Double> retVal = new HashMap<File, Double>();

        String[] words = phrase.split("(\\s+)");
        // Find the intersection first
        ArrayList<HashSet<File>> entryList = new ArrayList<HashSet<File>>();
        for (String word : words) {
            entryList.add(new HashSet<File>(SearchWord(word).keySet()));
        }
        HashSet<File> intersection = entryList.get(0);
        for (HashSet<File> one_entry : entryList) {
            intersection.retainAll(one_entry);
        }
        HashMap<File, HashSet<Integer>> entry = new HashMap<File, HashSet<Integer>>();

        // Verify the following words of each occurance of the first word
        for (File filename : intersection) {
            HashSet<Integer> positions = index.get(words[0]).get(filename);
            for (Integer pos : positions) {
                boolean verified = true;
                Integer offset = 0;
                for (String word : words) {
                    if (!index.get(word).get(filename).contains(pos + offset)) {
                        verified = false;
                        break;
                    }
                    offset++;
                }
                if (verified) {
                    HashSet<Integer> verified_pos = entry.get(filename);
                    if (verified_pos == null) {
                        verified_pos = new HashSet<Integer>();
                    }
                    verified_pos.add(pos);
                    entry.put(filename, verified_pos);
                }
            }
        }

        if (entry != null) {
            // Calculate the weight of each document
            for (File file : entry.keySet()) {
                Double tf = new Double(entry.get(file).size());
                Double idf = Math.log(
                        docCount / new Double(entry.keySet().size()));
                Double weight = tf * idf;
                retVal.put(file, weight);
            }
        }

        if (verbose) {
            System.out.println("SearchPhrase: " + phrase + " : " + retVal.keySet());
        }
        return retVal;
    }
    
    public boolean TestLogic(String query) {
        getToHighlight().clear();
        cachedFiles = null;
        cachedFilenames = null;
        cachedWeights = null;
        return (query.contains(" AND ") && query.contains(" OR "));
    }

    /**
     * Search for a query string. Supports strings with AND or OR operators.
     * @param query
     * @return A set of documents matching the query logic.
     */
    public Map<File, Double> SearchLogic(String query) {
        getToHighlight().clear();
        if (query.contains(" AND ") && query.contains(" OR ")) {
            return new HashMap<File, Double>();
        }

        String[] phrases = query.split("( AND | OR )");

        ArrayList<HashMap<File, Double>> entryList = new ArrayList<HashMap<File, Double>>();
        HashSet<File> fileSet;
        for (String phrase : phrases) {
            if (!phrase.equals("AND") && !phrase.equals("OR")) {
                getToHighlight().add(phrase.toLowerCase());
                entryList.add(SearchPhrase(phrase.toLowerCase()));
            }
        }

        if (query.contains(" AND ")) {
            HashSet<File> intersection = new HashSet<File>(entryList.get(0).keySet());
            for (HashMap<File, Double> document : entryList) {
                intersection.retainAll(document.keySet());
            }
            fileSet = intersection;
        } else if (query.contains(" OR ")) {
            HashSet<File> union = new HashSet<File>(entryList.get(0).keySet());
            for (HashMap<File, Double> result : entryList) {
                union.addAll(result.keySet());
            }
            fileSet = union;
        } else {
            fileSet = new HashSet<File>(entryList.get(0).keySet());
        }


        HashMap<File, Double> weightMap = new HashMap<File, Double>();
        // Calculate weight for each document in document_set
        for (File doc : fileSet) {
            Double weight = 0.0;
            for (HashMap<File, Double> entry : entryList) {
                if (entry.containsKey(doc)) {
                    weight += entry.get(doc);
                }
            }
            weightMap.put(doc, weight);
        }

        ValueComparator bvc = new ValueComparator(weightMap);
        @SuppressWarnings("unchecked")
        TreeMap<File, Double> sortedMap = new TreeMap<File, Double>(bvc);
        sortedMap.putAll(weightMap);

        cachedFiles = new File[sortedMap.size()];
        cachedFilenames = new String[sortedMap.size()];
        cachedWeights = new Double[sortedMap.size()];

        int resultNumber = 0;
        Iterator iterator = sortedMap.entrySet().iterator();
        while (iterator.hasNext()) {
            @SuppressWarnings("unchecked")
            Map.Entry<File, Double> entry = (Map.Entry<File, Double>) iterator.next();
            cachedFiles[resultNumber] = entry.getKey();
            cachedFilenames[resultNumber] = entry.getKey().getName();
            cachedWeights[resultNumber] = entry.getValue();
            resultNumber++;
        }

        if (verbose) {
            System.out.println(query + " : " + sortedMap);
        }
        return sortedMap;
    }

    /**
     * @return the dataPath
     */
    public String getDataPath() {
        return dataPath;
    }

    /**
     * @param dataPath the dataPath to set
     */
    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    /**
     * @param verbose_output the verbose_output to set
     */
    public void setVerbose_output(boolean verbose_output) {
        this.verbose = verbose_output;
    }

    /**
     * @return the num_of_documents
     */
    public int getNum_of_documents() {
        return docCount;
    }

    /**
     * @return the cachedFiles
     */
    public File[] getCachedFiles() {
        return cachedFiles;
    }

    /**
     * @return the cachedFilenames
     */
    public String[] getCachedFilenames() {
        return cachedFilenames;
    }

    /**
     * @return the cachedWeights
     */
    public Double[] getCachedWeights() {
        return cachedWeights;
    }

    /**
     * @return the stoplist
     */
    public File getStoplist() {
        return stoplist;
    }

    /**
     * @param stoplist the stoplist to set
     */
    public void setStoplist(File stoplist) {
        this.stoplist = stoplist;
    }

    /**
     * @return the stopwords
     */
    public HashSet<String> getStopwords() {
        return stopwords;
    }

    /**
     * @return the phrasesToHighlight
     */
    public HashSet<String> getToHighlight() {
        return toHighlight;
    }
}

class ValueComparator implements Comparator {

    private Map base;

    public ValueComparator(Map base) {
        this.base = base;
    }

    @Override
    public int compare(Object a, Object b) {

        if ((Double) base.get(a) < (Double) base.get(b)) {
            return 1;
        } else if ((Double) base.get(a) == (Double) base.get(b)) {
            return 0;
        } else {
            return -1;
        }
    }
}