package tudelft.wis.idm_solutions.BoardGameTracker.JDBC_Implementation;

import tudelft.wis.idm_tasks.boardGameTracker.BgtException;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;

public class Utils {
    public static void throwBgtException(Exception exception) throws BgtException {
        System.err.println(exception.getMessage());
        exception.printStackTrace();
        throw new BgtException();
    }

    public static <T> Collection<T> resultSetToCollection(
            Function<Map<String, Object>, T> constructorFunc,
            ResultSet resultSet)
            throws BgtException {
        try {
            Collection<T> collection = new LinkedList<>();
            while (resultSet.next()) {
                Map<String, Object> values = new HashMap<>();
                ResultSetMetaData metaData = resultSet.getMetaData();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    values.put(metaData.getColumnName(i), resultSet.getObject(i));
                }

                collection.add(constructorFunc.apply(values));
            }

            return collection;
        }
        catch (SQLException exception) {
            throwBgtException(exception);
            return null;
        }
    }

    public static <T> T pickAny(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return null;
        }

        return collection.iterator().next();
    }
}
