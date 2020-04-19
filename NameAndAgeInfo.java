import java.util.Calendar;
import java.util.GregorianCalendar;

public class NameAndAgeInfo {
    private String lastName = "";
    private String firstName = "";
    private String patronymic = "";
    private Calendar birthdate = new GregorianCalendar();

    public NameAndAgeInfo() {}

    public NameAndAgeInfo(String lastName, String firstName, String patronymic, int day, int month, int year) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.patronymic = patronymic;
        birthdate.set(year, month - 1, day);
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setBirthdate(int day, int month, int year) {
        birthdate.set(year, month - 1, day);
    }

    public String getBirthdateString() {
        return (birthdate.get(Calendar.DAY_OF_MONTH) + "." + (birthdate.get(Calendar.MONTH) + 1) + "." +
                birthdate.get(Calendar.YEAR));
    }

    public void parseData(String data) throws NumberFormatException, ArrayIndexOutOfBoundsException {
        String[] dataSplit = data.split(" ", 4);
        lastName = dataSplit[0];
        firstName = dataSplit[1];
        patronymic = dataSplit[2].replaceAll(",", "");
        String[] date = dataSplit[3].split("[\\.]");
        birthdate.set(Integer.parseInt(date[2]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[0]));
    }

    public String getInfo() {
        String gender = "";
        if (patronymic.length() >= 3) {
            if (patronymic.substring(patronymic.length() - 3).toLowerCase().equals("вна")) {
                gender = "женщина, ";
            } else if (patronymic.substring(patronymic.length() - 3).toLowerCase().equals("вич")) {
                gender = "мужчина, ";
            }
        }

        String initials = "";
        if (firstName.length() > 0) {
            initials += firstName.charAt(0) + ". ";
        }
        if (patronymic.length() > 0) {
            initials += patronymic.charAt(0) + "., ";
        }

        Calendar age = Calendar.getInstance();
        age.add(Calendar.YEAR, -birthdate.get(Calendar.YEAR));
        age.add(Calendar.MONTH, -birthdate.get(Calendar.MONTH));
        age.add(Calendar.DAY_OF_MONTH, -birthdate.get(Calendar.DAY_OF_MONTH));
        int ageInYears = age.get(Calendar.ERA) == 1 ? age.get(Calendar.YEAR) : 0;
        String yearsNoun = "";
        switch (ageInYears % 10) {
            case 1:
                yearsNoun = " год";
                break;
            case 2: case 3: case 4:
                yearsNoun = " года";
                break;
            default:
                yearsNoun = " лет";
        }
        if (ageInYears % 100 / 10 == 1) {
            yearsNoun = " лет";
        }
        return (lastName + ' ' + initials + gender + ageInYears + yearsNoun);
    }
}
