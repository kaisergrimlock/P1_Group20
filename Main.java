import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;


public class Main {
    public static void main(String[] args) throws IOException {
        String finalDate = null; // date after modification
        String unit;
        int num = 0;
        LocalDate start = null, end = null;
        int input;
        String geographicArea;
        String day, month;
        String bonus = "0"; // to add 0 to day or month

        DateTimeFormatter df = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        Scanner sc = new Scanner(System.in);

        System.out.print("\nWant to run the program? (Y/N): ");
        String answer = sc.nextLine();
        while (answer.equalsIgnoreCase("Y")) {
            // Program Name
            System.out.println("----------------------------------------------------------------------");
            System.out.println("|                       COVID TRACKING PROGRAM                       |");
            System.out.println("----------------------------------------------------------------------");

            // Get Geographic Area
            System.out.println("Enter Geographic Area");
            System.out.print("          Press 1 for Continent | Press 2 for Country: ");
            input = Integer.parseInt(sc.nextLine());

            while (input < 1 || input > 2) {
                System.out.print("          Invalid Input. Please press 1 for Continent or 2 for Country: ");
                input = Integer.parseInt(sc.nextLine());
            }

            if (input == 1) {
                System.out.print("          Input the Continent: ");
            } else {
                System.out.print("          Input the Country: ");
            }
            geographicArea = sc.nextLine();

            // Get Date
            System.out.print("Enter Time Range. You can choose one of the format below:\n");
            System.out.print("      Press 1 if you want to use a pair of start date and end date \n");
            System.out.print("      Press 2 if you want to use a number of days or weeks from a particular date \n");
            System.out.print("      Press 3 if you want to use a number of days or weeks to a particular date \n");
            System.out.print("\n* Please use date in the format MM/dd/yyyy * \n");
            System.out.print("\nEnter your choice:  ");
            String choice = sc.nextLine();

            // Time Range Format 1: start date - end date
            if (choice.equals("1")) {
                start = getStartDate();
                end = getEndDate();
                while (end.isBefore(start)) {
                    System.out.print("\nInvalid input: end date is before start date");
                    System.out.print("\nPlease try again\n");
                    start = getStartDate();
                    end = getEndDate();
                }
            }

            // Time Range Format 2: number of days or weeks FROM a particular date
            if (choice.equals("2")) {
                start = getStartDate();
                System.out.print("\nYou want to use day or week (type in 'day' or 'week'): ");
                unit = sc.nextLine();
                if (unit.equals("day")) {
                    num = getNumberOfDays();
                    end = start.plusDays(num);
                }
                if (unit.equals("week")) {
                    num = getNumberOfWeeks();
                    end = start.plusWeeks(num);
                }

                while (num < 0) {
                    System.out.print("\nInvalid input: Number is smaller than 0");
                    System.out.print("\nPlease try again\n");

                    if (unit.equals("day")) {
                        num = getNumberOfDays();
                        end = start.plusDays(num);
                    }
                    if (unit.equals("week")) {
                        num = getNumberOfWeeks();
                        end = start.plusWeeks(num);
                    }
                }
            }

            // Time Range Format 2: number of days or weeks TO a particular date
            if (choice.equals("3")) {
                end = getEndDate();
                System.out.print("\nYou want to use day or week (type in 'day' or 'week'): ");
                unit = sc.nextLine();
                if (unit.equals("day")) {
                    num = getNumberOfDays();
                    start = end.minusDays(num);
                }
                if (unit.equals("week")) {
                    num = getNumberOfWeeks();
                    start = end.minusWeeks(num);
                }

                while (num < 0) {
                    System.out.print("\nInvalid input: Number is smaller than 0");
                    System.out.print("\nPlease try again\n");

                    if (unit.equals("day")) {
                        num = getNumberOfDays();
                        start = end.minusDays(num);
                    }
                    if (unit.equals("week")) {
                        num = getNumberOfWeeks();
                        start = end.minusWeeks(num);
                    }
                }
            }

            // For New vaccinated Calculate: Get the date before the specific start date
            LocalDate d0 = start.minusDays(1);

            // Get the dates between 2 dates
            ArrayList<LocalDate> totalDates = new ArrayList<>();
            while (!start.isAfter(end)) {
                totalDates.add(start);
                start = start.plusDays(1);
            }

            // Open File to Read
            FileReader reader = new FileReader("covid-data.csv");
            BufferedReader reader1 = new BufferedReader(reader);
            reader1.readLine();
            String line;
            line = reader1.readLine();

            // Create a list to store appropriate data
            Summary list = new Summary();
            int count = 0; // Number of appropriate data
            while (line != null) {
                String[] array = line.split(",");
                String[] dates = array[3].split("/");

                if (dates[0].length() == 1 && dates[1].length() == 1) {
                    day = bonus + dates[1];
                    month = bonus + dates[0];
                    finalDate = month + "/" + day + "/" + dates[2];
                }
                if (dates[1].length() == 1 && dates[0].length() == 2) {
                    day = bonus + dates[1];
                    finalDate = dates[0] + "/" + day + "/" + dates[2];
                }
                if (dates[0].length() == 1 && dates[1].length() == 2) {
                    month = bonus + dates[0];
                    finalDate = month + "/" + dates[1] + "/" + dates[2];
                }

                // Create object data
                Data d = new Data(array[0], array[1], array[2], LocalDate.parse(finalDate, df), getLong(array[4]), getLong(array[5]), getLong(array[6]), getLong(array[7]));
                line = reader1.readLine();

                if (input == 1) {
                    if (array[1].equals(geographicArea)) {
                        for (LocalDate totalDate : totalDates) {
                            if (finalDate.equals(totalDate.format(df))) {
//                            d.display();
                                list.addDataToList(d);
                                count += 1;
                            }
                        }

                        // For New vaccinated calculate
                        if ((d0.format(df)).equals(finalDate)) {
                            long d0Vaccinated = d.getVaccinated();
                            list.setD0Vaccinated(d0Vaccinated);
                        }
                    }
                }

                if (input == 2) {
                    if (array[2].equals(geographicArea)) {
                        for (LocalDate totalDate : totalDates) {
                            if (finalDate.equals(totalDate.format(df))) {
//                            d.display();
                                list.addDataToList(d);
                                count += 1;
                            }
                        }

                        // For New vaccinated Calculate
                        if ((d0.format(df)).equals(finalDate)) {
                            long d0Vaccinated = d.getVaccinated();
                            list.setD0Vaccinated(d0Vaccinated);
                        }
                    }
                }

            }

            // Get user input for summary input
            // userInput[0]: noOfGroups;
            // userInput[1]: metric;
            // userInput[2]: result type.
            int[] userInput = new int[3];
            userInput = inputSummary(count);

            // Display Chart
            long[] value = new long[count];
            String[] rangeForTable = new String[count];
            ArrayList<ArrayList<Integer>> grouping = new ArrayList<ArrayList<Integer>>();
            // Group the date, calculate result and assign
            value = list.grouping(userInput); // Value of each group
            grouping = list.getGrouping();
            rangeForTable = list.getRange(); // Get Range of each group for Tabular Display

            inputDisplay(value, grouping, rangeForTable);

            System.out.print("\nWant to start again? (Y/N): ");
            answer = sc.nextLine();
        }
    }

    public static long getLong(String s) {
        if (s.equals("")) {
            return 0;
        } else {
            return Long.parseLong(s);
        }
    }

    public static LocalDate getStartDate() {
        String startDate1;
        LocalDate start;

        Scanner sc = new Scanner(System.in);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        System.out.print("Start date: ");
        startDate1 = sc.nextLine();
        start = LocalDate.parse(startDate1, df);

        return start;
    }

    public static LocalDate getEndDate() {
        String endDate1;
        LocalDate end;

        Scanner sc = new Scanner(System.in);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        System.out.print("End date: ");
        endDate1 = sc.nextLine();
        end = LocalDate.parse(endDate1, df);

        return end;
    }

    public static Integer getNumberOfDays(){
        int num;
        Scanner sc = new Scanner(System.in);

        System.out.print("Number of days: ");
        num = Integer.parseInt(sc.nextLine());

        return num;
    }

    public static Integer getNumberOfWeeks(){
        int num;
        Scanner sc = new Scanner(System.in);

        System.out.print("Number of week: ");
        num = Integer.parseInt(sc.nextLine());

        return num;
    }

    public static int[] inputSummary(int arrayLength) {
        Scanner sc = new Scanner(System.in);
        int grouping, noOfDays;
        int[] userInput = new int[3]; // 0: noOfGroups; 1: metric; 2: result type

        userInput[0] = arrayLength; // default grouping = the number of dates;

        System.out.println("------------------------------");
        System.out.println("Please choose a grouping method\n"
                + "     1 for No Grouping\n"
                + "     2 for Grouping by number of groups\n"
                + "     3 for grouping by number of days in each groups");
        System.out.print("\nEnter your choice:  ");
        grouping = sc.nextInt();
        while (grouping < 1 || grouping > 3) {
            System.out.print("Invalid Input. Please Input Again: ");
            grouping = sc.nextInt();
        }

        if (grouping == 2) {
            System.out.print("Please enter the number of groups: ");
            userInput[0] = sc.nextInt();
            while (userInput[0] < 0) {
                System.out.print("Invalid Input. Number of Groups cannot be negative: ");
                userInput[0] = sc.nextInt();
            }
        } else if (grouping == 3) {
            System.out.print("Please enter the number of days for each group: ");
            noOfDays = sc.nextInt();
            while (noOfDays < 0) {
                System.out.print("Invalid Input. Number of Days cannot be negative: ");
                noOfDays = sc.nextInt();
            }
            while (arrayLength % noOfDays != 0) {
                System.out.print("Cannot divide group equally! Please try again: ");
                noOfDays = sc.nextInt();
            }
            userInput[0] = arrayLength/noOfDays;
        }

        System.out.println("------------------------------");
        System.out.println("Please choose a metric\n"
                + "     1 for positive cases\n"
                + "     2 for deaths\n"
                + "     3 for people vaccinated");
        System.out.print("\nEnter your choice:  ");
        userInput[1] = sc.nextInt();
        while (userInput[1] < 1 || userInput[1] > 3) {
            System.out.print("Invalid Input. Please Input Again: ");
            userInput[1] = sc.nextInt();
        }

        System.out.println("------------------------------");
        System.out.println("Please choose a result type\n" +
                "       1 for New Total: total new cases/new deaths/new vaccinated people in a group\n" +
                "       2 for Up To: total cases/deaths/vaccinated from the beginning up to the last date of a group");
        System.out.print("\nEnter your choice:  ");
        userInput[2] = sc.nextInt();
        while (userInput[2] < 1 || userInput[2] > 2) {
            System.out.print("Invalid Input. Please Input Again: ");
            userInput[2] = sc.nextInt();
        }

        return userInput;
    }

    public static void inputDisplay(long[] value, ArrayList<ArrayList<Integer>> grouping, String[] range) {
        Scanner sc = new Scanner(System.in);
        int userInput;

        System.out.println("-------------------------------------------");
        System.out.println("Please choose a way to display summary data");
        System.out.println("        1 for Tabular Display");
        System.out.println("        2 for Chart Display");
        System.out.print("\nEnter your choice:  ");
        userInput = sc.nextInt();
        while (userInput < 1 || userInput > 2) {
            System.out.print("Invalid Input.Please Input Again: ");
            userInput = sc.nextInt();
        }

        if (userInput == 1) {
            // Tabular Display
            DataDisplay c = new Tabular("Tabular Display", value, range);
            c.display();
        }

        if (userInput == 2) {
            // Chart Display
            DataDisplay c = new Chart("Chart", value);
            ((Chart)c).createChart();
            ((Chart)c).representData();
            ((Chart)c).display();
        }
    }
}


class Data {
    String iso, continent, country;
    LocalDate date;
    long cases, deaths, vaccinated, population;

    public Data(String iso, String cont, String cou, LocalDate date, long cas, long dea, long vacc, long pop) {
        this.iso = iso;
        this.continent = cont;
        this.country = cou;
        this.date = date;
        this.cases = cas;
        this.deaths = dea;
        this.vaccinated = vacc;
        this.population = pop;
    }

    public long getCases() {
        return cases;
    }

    public long getDeaths() {
        return deaths;
    }

    public long getVaccinated() {
        return vaccinated;
    }

    public String getDate() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String stringDate = date.format(df);
        return stringDate;
    }

    public String toString() {
        return "ISO: " + iso + "\n" + "Continent: " + continent + "\n" + "Country: " + country + "\n" +
                "Date: " + date + "\n" + "Cases: " + cases + "\n" + "Deaths: " + deaths + "\n" +
                "Vaccinated: " + vaccinated + "\n" + "Population: " + population + "\n" + "---------------------------";
    }
}


class Summary {
    ArrayList<Data> list = new ArrayList<Data>();
    ArrayList<ArrayList<Integer>> grouping = new ArrayList<ArrayList<Integer>>();
    long d0Vaccinated;     // For New Vaccinated Calculate

    // For New Vaccinated Calculate
    public void setD0Vaccinated(long d0Vaccinated) {
        this.d0Vaccinated = d0Vaccinated;
    }

    public void addDataToList(Data d) {
        list.add(d);
    }

    public ArrayList<ArrayList<Integer>> getGrouping() {
        return grouping;
    }

    public long[] grouping(int[] userInput) {
        // userInput[0] = Number of Groups
        long[] groupCalculateAnswer = new long[userInput[0]];

        int startPoint = 1;
        int endPoint = 0;

        int[] divide = divide(list.size(), userInput[0]);

        for (int i = 0; i < userInput[0]; i++) {
            ArrayList<Integer> eachGroup = new ArrayList<>(); // inner
            if (divide[i] == 1) {
                eachGroup.add(startPoint);
                grouping.add(eachGroup);
                startPoint += 1;
            } else {
                eachGroup.add(startPoint);
                endPoint = startPoint + divide[i] - 1;
                eachGroup.add(endPoint);
                grouping.add(eachGroup);
                startPoint += divide[i];
            }
        }

        System.out.println("Grouping: " + grouping);

        if (userInput[2] == 1) {
            // New Total
            groupCalculateAnswer = newTotalCalculate(grouping, userInput[1]); // userInput[1] = metric;
        }

        if (userInput[2] == 2) {
            // Up To
            groupCalculateAnswer = upToCalculate(grouping, userInput[1]); // userInput[1] = metric;
        }

        System.out.println("Group Calculate Answer: " + Arrays.toString(groupCalculateAnswer));

        return groupCalculateAnswer;
    }

    public static int[] divide(int x, int n) {
        // This method is used to divided the groups as equally as possible
        // Divide X groups into N parts

        int[] divide = new int[10000];

        if (x % n == 0) {
            for (int i = 0; i < n; i++) {
                divide[i] = x / n;
            }
        } else {
            int zp = n - (x % n);
            int pp = x / n;
            for (int i = 0; i < n; i++) {
                if (i >= zp) {
                    divide[i] = pp + 1;
                } else {
                    divide[i] = pp;
                }
            }
        }
        return divide;
    }

    public long[] newTotalCalculate(ArrayList<ArrayList<Integer>> grouping, int userInputMetric) {
        long[] newTotalAnswer = new long[grouping.size()];

        // Calculate New Vaccinated from Total Vaccinated
        long[] newVaccinated = new long[list.size()];

        // Loop through the data to calculate New Vaccinated
        for (int index = 0; index < list.size(); index++) {
            if (index == 0) {
                newVaccinated[index] = list.get(index).getVaccinated() - d0Vaccinated;
            } else {
                newVaccinated[index] = list.get(index).getVaccinated() - list.get(index - 1).getVaccinated();
            }
        }

        for (int i = 0; i < grouping.size(); i++) {
            int smallGroupSize = grouping.get(i).size();

            // New Cases
            if (userInputMetric == 1) {
                long sum = 0;
                // Loop from start date to end date of each group
                for (int j = grouping.get(i).get(0); j < smallGroupSize + grouping.get(i).get(0); j++) {
                    sum += list.get(j - 1).getCases();
                    newTotalAnswer[i] = sum;
                }
            }

            // New Deaths
            if (userInputMetric == 2) {
                long sum = 0;
                // Loop from start date to end date of each group
                for (int j = grouping.get(i).get(0); j < smallGroupSize + grouping.get(i).get(0); j++) {
                    sum += list.get(j - 1).getDeaths();
                    newTotalAnswer[i] = sum;
                }
            }

            // Total vaccinated
            // Note
            if (userInputMetric == 3) {
                long sum = 0;
                for (int j = grouping.get(i).get(0); j < smallGroupSize + grouping.get(i).get(0); j++) {
                    sum += newVaccinated[j - 1]; // Check again j, newVaccinated E {0,1,2,3,4,...}, grouping E {1,2,3,4,...}
                    newTotalAnswer[i] = sum;
                }
            }
        }

        return newTotalAnswer;
    }

    public long[] upToCalculate(ArrayList<ArrayList<Integer>> grouping, int userInputMetric) {
        long[] upToAnswer = new long[grouping.size()];
        long answer;

        // Loop through all of the group
        for (int i = 0; i < grouping.size(); i++) {
            int smallGroupSize = grouping.get(i).size();
            long sum = 0;

            // New Cases
            if (userInputMetric == 1) {
                // Loop from start date to end date of each group
                for (int j = 0; j < grouping.get(i).get(smallGroupSize - 1); j++) {
                    sum += list.get(j).getCases();
                    upToAnswer[i] = sum;
                }
            }

            // New Deaths
            if (userInputMetric == 2) {
                for (int j = 0; j < grouping.get(i).get(smallGroupSize - 1); j++) {
                    sum += list.get(j).getDeaths();
                    upToAnswer[i] = sum;
                }
            }

            // Total vaccinated
            if (userInputMetric == 3) {
                // Read the value of the last date
                for (int j = 0; j < grouping.get(i).get(smallGroupSize - 1); j++) {
                    answer = list.get(grouping.get(i).get(smallGroupSize - 1) - 1).getVaccinated();
                    upToAnswer[i] = answer;
                }
            }
        }

        return upToAnswer;
    }

    // Get range of each group
    public String[] getRange() {
        String[] range = new String[grouping.size()];

        for (int i = 0; i < grouping.size(); i++) {
            int smallGroupSize = grouping.get(i).size();
            range[i] = list.get(grouping.get(i).get(0) - 1).getDate() + " - " + list.get(smallGroupSize + grouping.get(i).get(0) - 2).getDate();
        }

        return range;
    }
}


abstract class DataDisplay {
    String name;
    long[] value;

    public abstract void display();
}


class Chart extends DataDisplay{
    String[][] chart = new String[24][80];

    public Chart(String n, long[] v) {
        name = n;
        value = v;
    }

    // For Chart Display
    public void createChart() {
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 80; j++) {
                if (i == 23) {
                    chart[i][j] = "_";
                } else if (j == 0) {
                    chart[i][j] = "|";
                } else {
                    chart[i][j] = " ";
                }
                if (i == 23 && j == 0) {
                    chart[i][j] = "|";
                }
            }
        }
    }

    // For Chart Display
    public void display() {
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 80; j++) {
                System.out.print(chart[i][j]);
            }
            System.out.println();
        }
    }

    // For Chart Display
    public void representData() {
        double max = getMax();
        double min = getMin();
        double valueRange = 22/(max - min + 1); //deltaY/deltaV
        int distanceBetweenColumn = 78/(value.length);
        int y; // y corresponding i
        int x = 1; // x corresponding j


        // Represent Data Points
        for (int index = 0; index < value.length; index++) {
            if (value[index] == min) {
                chart[22][x] = "*";
            } else {
                double valuePoint = (value[index] * valueRange) - min*valueRange;
                if (valuePoint < 1) {
                    valuePoint = 1;
                }
                y = (int) Math.ceil(23 - valuePoint);
                chart[y][x] = "*";
            }

            x += distanceBetweenColumn;
        }
        System.out.println("---------------------------------- CHART TABLE ----------------------------------");
    }

    public long getMax() {
        // This method is used to find the max
        long max = value[0];
        for (int i = 1; i < value.length; i++) {
            if (value[i] > max) {
                max = value[i];
            }
        }
        return max;
    }

    public long getMin() {
        // This method is used to find the min
        long min = value[0];
        for (int i = 1; i < value.length; i++) {
            if (value[i] < min) {
                min = value[i];
            }
        }
        return min;
    }
}

class Tabular extends DataDisplay{
    // The range of each group
    String[] range;

    public Tabular(String n, long[] v, String[] r) {
        name = n;
        value = v;
        range = r;
    }

    // For Tabular Display
    public void display() {
        System.out.println("\n------------------ TABLE ----------------");
        System.out.println("|           Range           |   Value   |");
        System.out.println("----------------------------------------");
        for (int i = 0; i < range.length; i++) {
            System.out.printf("|\t" +range[i] +"\t\t|");
            System.out.printf("\t" +value[i] + "\t|\n");
        }
    }
}

// DONE HERE !! ❤
