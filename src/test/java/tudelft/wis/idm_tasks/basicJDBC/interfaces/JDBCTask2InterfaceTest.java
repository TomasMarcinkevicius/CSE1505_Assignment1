package tudelft.wis.idm_tasks.basicJDBC.interfaces;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tudelft.wis.idm_tasks.basicJDBC_Impl.JDBCManagerImpl;
import tudelft.wis.idm_tasks.basicJDBC_Impl.JDBCTask2Impl;

import static org.junit.jupiter.api.Assertions.*;

class JDBCTask2InterfaceTest {

    JDBCTask2Interface task2Solver;

    @BeforeEach
    void setup() {
        task2Solver = new JDBCTask2Impl(new JDBCManagerImpl());
    }

    @Test
    void getConnection() {
        assertNotNull(task2Solver.getConnection());
    }

    @Test
    void getTitlesPerYear() {
        var titles = task2Solver.getTitlesPerYear(1960);

        assertEquals(17044, titles.size());
    }

    @Test
    void getJobCategoriesFromTitles() {
        var categories = task2Solver.getJobCategoriesFromTitles("Hundreds of");

        assertEquals(9, categories.size());
    }

    @Test
    void getAverageRuntimeOfGenre() {
        double average = task2Solver.getAverageRuntimeOfGenre("Action");

        assertEquals(47.4597724514381715, average, 1e-6);
    }

    @Test
    void getPlayedCharacters() {
        var characters = task2Solver.getPlayedCharacters("Scoot McNairy");

        assertEquals(43, characters.size());
    }
}