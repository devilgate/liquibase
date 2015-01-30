package liquibase.datatype.core;

import java.util.ArrayList;
import java.util.List;

import liquibase.database.Database;
import liquibase.database.core.*;
import liquibase.datatype.DataTypeInfo;
import liquibase.datatype.DatabaseDataType;
import liquibase.datatype.LiquibaseDataType;
import liquibase.statement.DatabaseFunction;

@DataTypeInfo(name="tinyint", aliases = "java.sql.Types.TINYINT", minParameters = 0, maxParameters = 1, priority = LiquibaseDataType.PRIORITY_DEFAULT)
public class TinyIntType  extends LiquibaseDataType {

    // DBs that need to be set as SMALLINT because they don't do tiny.
    private static final List<Class<? extends Database>> SMALLINT_DBS = new ArrayList<Class<? extends Database>>();
    static {
        SMALLINT_DBS.add(DerbyDatabase.class);
        SMALLINT_DBS.add(PostgresDatabase.class);
        SMALLINT_DBS.add(FirebirdDatabase.class);
        SMALLINT_DBS.add(DB2Database.class);
    }
    private boolean autoIncrement;

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    @Override
    public DatabaseDataType toDatabaseDataType(Database database) {

        if (SMALLINT_DBS.contains(database.getClass())) {
            return new DatabaseDataType("SMALLINT");
        }
        if (database instanceof MSSQLDatabase || database instanceof MySQLDatabase) {
            return new DatabaseDataType("TINYINT");
        }
        if (database instanceof OracleDatabase) {
            return new DatabaseDataType("NUMBER",3);
        }
        return super.toDatabaseDataType(database);
    }

    @Override
    public String objectToSql(Object value, Database database) {
        if (value == null || value.toString().equalsIgnoreCase("null")) {
            return null;
        }
        if (value instanceof DatabaseFunction) {
            return value.toString();
        }

        return formatNumber(value.toString());
    }
}
