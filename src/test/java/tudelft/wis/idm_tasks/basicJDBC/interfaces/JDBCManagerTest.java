package tudelft.wis.idm_tasks.basicJDBC.interfaces;

import org.junit.jupiter.api.Test;
import tudelft.wis.idm_tasks.basicJDBC_Impl.JDBCManagerImpl;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class JDBCManagerTest {
    @Test
    void testGetConnection() throws SQLException {
        assertNotNull(new JDBCManagerImpl().getConnection());
    }
}