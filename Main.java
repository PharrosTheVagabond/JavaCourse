import java.util.Scanner;

public class Main2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        NameAndAgeInfo info = new NameAndAgeInfo();
        System.out.println("Введите ФИО и дату рождения в формате ДД.ММ.ГГГГ:");
        String input = scanner.nextLine();
        scanner.close();
        try {
            info.parseData(input);
        }
        catch (NumberFormatException ex1) {
            System.out.println("Неверный формат введённой даты.");
        }
        catch (ArrayIndexOutOfBoundsException ex2) {
            System.out.println("Предоставлена неполная или неверная информация.");
        }
        System.out.println(info.getBirthdateString());
        System.out.println(info.getInfo());
    }
}
