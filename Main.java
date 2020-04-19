import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите имя файла, содержащего текст.");
        String inputFileName = scanner.nextLine();
        scanner.close();
        TxtLettersFrequency qwe = new TxtLettersFrequency(inputFileName, "output.txt");
        try {
            qwe.countFrequencyArray();
            qwe.setOutputFile("output2.txt");
            qwe.countFrequencyMap();
        }
        catch (FileNotFoundException ex1) {
            System.out.println("Ошибка открытия файла.");
        }
        catch (IOException ex2) {
            System.out.println("Ошибка ввода/вывода.");
        }
    }
}
