package tudelft.wis.idm_tasks.basicJDBC.interfaces;
import java.sql.Connection;
import java.util.Collection;
// Add other necessary imports here
/**
 * Starting template for Assignment 1 - Task 2.1.
 *
 * @author Christoph Lofi, Alexandra Neagu
 */
public interface JDBCTask2Interface {
    /**
     * Establishes the connection to the PostgreSQL database.
     *
     * @return The connection object. If a connection couldn't be established, returns null
     */
    Connection getConnection();


    /**
     * Lists all the primary titles for a specific start year.
     *
     * @param year A specific start year to query the titles on
     * @return A collection of strings of the resulting primary titles
     */
    Collection<String> getTitlesPerYear(int year);

    /**
     * Lists all the job categories for titles that include a specific string in their primary title.
     *
     * @param searchString A string that will be used to filter the primary titles on
     * @return A collection of strings of the resulting job categories
     */
    Collection<String> getJobCategoriesFromTitles(String searchString);


    /**
     * Lists the average runtime of a specified genre.
     *
     * @param genre A string that specifies the genre to be queried on
     * @return A double corresponding to the average runtime of the specified genre
     */
    double getAverageRuntimeOfGenre(String genre);

    /**
     * Given a person's full name, lists all the characters they have played.
     *
     * @param actorFullname A string of the person's full name
     * @return A collection of strings corresponding to the character names the provided person has played
     */
    Collection<String> getPlayedCharacters(String actorFullname);

}

