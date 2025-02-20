package tudelft.wis;

import org.postgresql.core.Utils;
import tudelft.wis.idm_tasks.basicJDBC.interfaces.JDBCTask2Interface;
import tudelft.wis.idm_tasks.basicJDBC_Impl.JDBCManagerImpl;
import tudelft.wis.idm_tasks.basicJDBC_Impl.JDBCTask2Impl;

import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        runAllQueriesInTask2();
    }

    public static void runAllQueriesInTask2() {
        JDBCTask2Interface task2Solver = new JDBCTask2Impl(new JDBCManagerImpl());
        Scanner scanner = new Scanner(System.in);

        // Task 1
        System.out.print("Enter a year: ");
        int year = scanner.nextInt();
        var titlesPerYear = task2Solver.getTitlesPerYear(year);
        System.out.println(
                generateDisplayTable(
                        titlesPerYear,
                        "The first 20 titles of films produced in year " + year +
                                " (" + titlesPerYear.size() + " in total)",
                        "Title",
                        20));

        // Task 2
        scanner.nextLine();
        System.out.print("Enter a string to search: ");
        String searchString = scanner.nextLine();
        var jobCategories = task2Solver.getJobCategoriesFromTitles(searchString);
        System.out.println(
                generateDisplayTable(
                        jobCategories,
                        "The first 20 job categories that contain titles with the string '" + searchString + "' " +
                                "(" + jobCategories.size() + " in total)",
                        "Job category",
                        20));

        // Task 3
        System.out.print("Enter a genre: ");
        String genre = scanner.nextLine();
        double averageRuntime = task2Solver.getAverageRuntimeOfGenre(genre);
        if (averageRuntime == 0) {
            System.out.println("There are no films in the genre '" + genre + "'\n");
        }
        else {
            System.out.println("The average runtime of films in the genre '" + genre + "' is: " +
                    Math.round(averageRuntime * 100) / 100.0 + " minutes\n");
        }


        // Task 4
        System.out.print("Enter full name of an actor: ");
        String actorName = scanner.nextLine();
        var playedCharacters = task2Solver.getPlayedCharacters(actorName);
        System.out.println(
                generateDisplayTable(
                        playedCharacters,
                        "The first 20 characters " + actorName + " has played " +
                                "(" + playedCharacters.size() + " in total)",
                        "Character name",
                        20));
    }

    public static <T> String generateDisplayTable(Collection<T> values, String topText, String label, int limit) {
        class Utils {
            private static String makeRow(List<String> entries, List<Integer> widths) {
                StringBuilder row = new StringBuilder("|");
                for (int i = 0; i < entries.size(); i++) {
                    row.append(String.format("%-" + widths.get(i) + "s ", entries.get(i))).append("|");
                }

                return row.append('\n').toString();
            }

            private static String makeLine(List<Integer> widths) {
                StringBuilder line = new StringBuilder("+");
                for (int w : widths) {
                    line.append("-".repeat(w + 1)).append("+");
                }

                return line.append('\n').toString();
            }
        }

        StringBuilder builder = new StringBuilder("\033[1m" + topText + "\033[0m\n");
        if (values.isEmpty()) {
            builder.append("The table is empty!\n");
            return builder.toString();
        }

        int tableWidth = values.stream().limit(limit).mapToInt(v -> v.toString().length()).max().orElse(4);

        tableWidth = Math.max(tableWidth, label.length());
        int numWidth = 6;
        List<Integer> widths = List.of(numWidth, tableWidth);
        builder.append(Utils.makeLine(widths));
        builder.append(Utils.makeRow(List.of("No.", label), widths));
        builder.append(Utils.makeLine(widths));
        int i = 1;
        for (T value : values) {
            builder.append(Utils.makeRow(
                    List.of(i + ".", value.toString()),
                    widths));
            builder.append(Utils.makeLine(widths));
            if (i >= limit) {
                break;
            }
            i++;
        }

        return builder.append('\n').toString();
    }
}