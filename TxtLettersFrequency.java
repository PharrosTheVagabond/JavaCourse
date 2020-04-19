import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class TxtLettersFrequency {
    private String inputFile;
    private String outputFile;

    public TxtLettersFrequency(String inputFile, String outputFile) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    public String getInputFile() {
        return inputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void countFrequencyArray() throws IOException {
        FileReader input = new FileReader(inputFile);
        int[] frequencyArray = new int[26];
        char[] buffer = new char[1024];
        int bufferLength = 0;
        while ((bufferLength = input.read(buffer)) != -1) {
            for (int i = 0; i < bufferLength; i++) {
                int index = Character.toLowerCase(buffer[i]) - 'a';
                if (index >= 0 && index <= 25) {
                    frequencyArray[index]++;
                }
            }
        }
        input.close();
        FileWriter output = new FileWriter(outputFile);
        for (int i = 0; i < frequencyArray.length; i++) {
            output.write((char)('a' + i) + " - " + frequencyArray[i] + '\n');
        }
        output.close();
    }

    public void countFrequencyMap() throws IOException {
        FileReader input = new FileReader(inputFile);
        LinkedHashMap<Character, Integer> frequencyMap = new LinkedHashMap<Character, Integer>();
        for (int code = 'a'; code <= 'z'; code++) {
            frequencyMap.put((char)code, 0);
        }
        char[] buffer = new char[1024];
        int bufferLength = 0;
        while ((bufferLength = input.read(buffer)) != -1) {
            for (int i = 0; i < bufferLength; i++) {
                char c = Character.toLowerCase(buffer[i]);
                if (frequencyMap.containsKey(c)) {
                    frequencyMap.put(c, frequencyMap.get(c) + 1);
                }
            }
        }
        input.close();
        FileWriter output = new FileWriter(outputFile);
        for (Iterator it = frequencyMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry)it.next();
            output.write(entry.getKey() + " - " + entry.getValue() + '\n');
        }
        output.close();
    }
}
