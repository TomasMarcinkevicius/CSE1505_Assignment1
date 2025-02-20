package tudelft.wis.idm_tasks.basicJDBC_Impl;

import tudelft.wis.idm_tasks.basicJDBC.interfaces.JDBCManager;
import tudelft.wis.idm_tasks.basicJDBC.interfaces.JDBCTask2Interface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class JDBCTask2Impl implements JDBCTask2Interface {
    private final JDBCManager manager;

    public JDBCTask2Impl(JDBCManager manager) {
        this.manager = manager;
    }

    @Override
    public Connection getConnection() {
        try {
            return manager.getConnection();
        }
        catch (SQLException exception) {
            System.err.println(exception.getMessage());
            return null;
        }
    }

    @Override
    public Collection<String> getTitlesPerYear(int year) {
        String query = """
            SELECT t.primary_title
            FROM titles AS t
            WHERE t.start_year = ?
            ORDER BY t.primary_title
            """;
        Connection connection = getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, year);
            ResultSet resultSet = statement.executeQuery();

            List<String> titles = new ArrayList<>();
            while (resultSet.next()) {
                titles.add(resultSet.getString("primary_title"));
            }

            return titles;
        }
        catch (SQLException exception) {
            System.err.println(exception.getMessage());
            return Collections.emptyList();
        }

    }

    @Override
    public Collection<String> getJobCategoriesFromTitles(String searchString) {
        String query = """
            SELECT DISTINCT ci.job_category
            FROM titles AS t
                JOIN cast_info AS ci ON ci.title_id = t.title_id
            WHERE t.primary_title LIKE ?
            """;
        Connection connection = getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, "%" + searchString + "%");
            ResultSet resultSet = statement.executeQuery();

            List<String> jobCategories = new ArrayList<>();
            while (resultSet.next()) {
                jobCategories.add(resultSet.getString("job_category"));
            }

            return jobCategories;

        }
        catch (SQLException exception) {
            System.err.println(exception.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public double getAverageRuntimeOfGenre(String genre) {
        String query = """
            SELECT AVG(t.runtime)
            FROM titles AS t
                JOIN titles_genres AS tg ON t.title_id = tg.title_id
            WHERE genre = ?
            """;

        Connection connection = getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, genre);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                return 0;
            }

            return resultSet.getDouble(1);
        }
        catch (SQLException exception) {
            System.err.println(exception.getMessage());
            return 0;
        }
    }

    @Override
    public Collection<String> getPlayedCharacters(String actorFullname) {
        String query = """
            SELECT DISTINCT tpc.character_name
            FROM title_person_character AS tpc
                JOIN persons AS p ON p.person_id = tpc.person_id
            WHERE p.full_name = ?
            ORDER BY tpc.character_name
            """;

        Connection connection = getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, actorFullname);
            ResultSet resultSet = statement.executeQuery();

            List<String> playedCharacters = new ArrayList<>();
            while (resultSet.next()) {
                playedCharacters.add(resultSet.getString("character_name"));
            }

            return playedCharacters;
        }
        catch (SQLException exception) {
            System.err.println(exception.getMessage());
            return Collections.emptyList();
        }
    }
}