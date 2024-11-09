import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Main {
    static HashMap<Character, Integer> patternMatch = new HashMap<>();
    static ArrayList<String> pattern = new ArrayList<>();
    static ConcurrentHashMap<String, HashSet<String>> dictionary = new ConcurrentHashMap<>();
    static ConcurrentLinkedQueue<char[]> boards = new ConcurrentLinkedQueue<>();

    public static void main(String[] args) throws IOException {
        System.out.println("Starting TopologicalCrosswords. Attention : only prefix-fillable patterns are supported !");
        System.out.println("Reading pattern from file : pattern.txt");

        Path patternPath = Paths.get("pattern.txt");
        if (!Files.exists(patternPath)) {
            System.err.println("File not found : pattern.txt. Aborting.");
            return;
        }

        pattern = (ArrayList<String>) Files.readAllLines(patternPath);

        String translation = pattern.removeFirst();
        for (int i = 0; i < translation.length(); i++) {
            patternMatch.put(translation.charAt(i), i);
        }

        System.out.println("Reading words from file : words.txt");
        Path vocabulary = Paths.get("words.txt");
        if (!Files.exists(vocabulary)) {
            System.err.println("File not found : words.txt. Aborting.");
            return;
        }

        System.out.println("Creating dictionary...");

        HashSet<Integer> possibleLengths = new HashSet<>();
        pattern.forEach(l -> possibleLengths.add(l.length()));

        dictionary.put("*", new HashSet<>());

        Files.readAllLines(vocabulary).forEach(word -> {
            if (!possibleLengths.contains(word.length())) {
                return; //Cull unnecessary words !
            }
            word = Normalizer.normalize(word.strip().toLowerCase(), Normalizer.Form.NFKC).replaceAll("\\p{M}", "");
            for (int i = 0; i < word.length(); i++) {
                String substring = word.substring(0, i+1);
                if (!dictionary.containsKey(substring)) {
                    dictionary.put(substring, new HashSet<>());
                }
                dictionary.get(substring).add(word);
                dictionary.get("*").add(word);
            }
        });

        System.out.println("Done !");

        System.out.println("Starting main process...");

        int startLength = pattern.getFirst().length();

        dictionary.get("*").stream().parallel().filter(w -> w.length() == startLength).forEach(start -> {
            char[] board = new char[patternMatch.size()];
            place(board, start, pattern.getFirst());
            fill(board, 1);
        });

        Path resultPath = Paths.get("result");
        if (!Files.exists(resultPath)) {
            Files.createFile(resultPath);
        }
        
        try (FileWriter writer = new FileWriter("result")) {
            for (char[] result : boards) {
                String str = String.valueOf(result);
                writer.write(str + "\n");
            }
        }
        System.out.println("DONE !");
    }

    public static void fill(char[] board, int index) {
        if (index == pattern.size()) {
            boards.add(board);
            System.out.println("Solution found : " + String.valueOf(board));
            return;
        }

        String fillPattern = pattern.get(index);
        String alreadyThere = get(board, fillPattern);

        if (alreadyThere.length() == fillPattern.length()) {
            //Full word: just check if valid.
            if (!dictionary.get("*").contains(alreadyThere)) {
                return;
            }
            //Onto the next one !
            fill(board, index+1);
        } else if (alreadyThere.isEmpty()) {
            dictionary.get("*").stream().filter(w -> w.length() == fillPattern.length()).forEach(w -> {
                char[] newBoard = board.clone();
                place(newBoard, w, fillPattern);
                fill(newBoard, index+1);
            });
        } else {
            if (!dictionary.containsKey(alreadyThere)) {
                //Nothing starting with that !
                return;
            }
            dictionary.get(alreadyThere).stream().filter(w -> w.length() == fillPattern.length()).forEach(w -> {
                char[] newBoard = board.clone();
                place(newBoard, w, fillPattern);
                fill(newBoard, index+1);
            });
        }
    }

    public static void place(char[] board, String str, String template) {
        for (int i = 0; i < template.length(); i++) {
            board[patternMatch.get(template.charAt(i))] = str.charAt(i);
        }
    }

    public static String get(char[] board, String template) {
        int length = template.length();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char character = board[patternMatch.get(template.charAt(i))];
            if (character != 0) {
                result.append(character);
            }
        }
        return result.toString();
    }
}