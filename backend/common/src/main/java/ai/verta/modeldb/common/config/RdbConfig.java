package ai.verta.modeldb.common.config;

import ai.verta.modeldb.common.exceptions.ModelDBException;
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.dialect.SQLServer2008Dialect;

public class RdbConfig {
  public String RdbDatabaseName;
  // TODO: replace driver with "io.opentracing.contrib.jdbc.TracingDriver" if tracing is enabled
  public String RdbDriver;
  public String RdbDialect;
  public String RdbUrl;
  public String RdbUsername;
  public String RdbPassword;
  public String sslMode = "DISABLED";

  public void Validate(String base) throws InvalidConfigException {
    if (RdbDatabaseName == null || RdbDatabaseName.isEmpty())
      throw new InvalidConfigException(base + ".RdbDatabaseName", Config.MISSING_REQUIRED);
    if (RdbDriver == null || RdbDriver.isEmpty())
      throw new InvalidConfigException(base + ".RdbDriver", Config.MISSING_REQUIRED);
    if (RdbDialect == null || RdbDialect.isEmpty())
      throw new InvalidConfigException(base + ".RdbDialect", Config.MISSING_REQUIRED);
    if (RdbUrl == null || RdbUrl.isEmpty())
      throw new InvalidConfigException(base + ".RdbUrl", Config.MISSING_REQUIRED);
    if (RdbUsername == null || RdbUsername.isEmpty())
      throw new InvalidConfigException(base + ".RdbUsername", Config.MISSING_REQUIRED);
    if (sslMode == null || sslMode.isEmpty())
      throw new InvalidConfigException(base + ".sslMode", Config.MISSING_REQUIRED);
    if (!isPostgres() && !isMysql() && !isMssql()) {
      throw new InvalidConfigException(base + ".RdbDialect", "Unknown or unsupported dialect.");
    }
  }

  public boolean isPostgres() {
    return RdbDialect.equals(PostgreSQLDialect.class.getName());
  }

  public boolean isMysql() {
    return RdbDialect.equals(MySQL5Dialect.class.getName());
  }

  public boolean isMssql() {
    return RdbDialect.equals(SQLServer2008Dialect.class.getName());
  }

  public static String buildConnectionString(RdbConfig rdb) {
    if (rdb.isMssql()) {
      return rdb.RdbUrl
          + ";databaseName="
          + rdb.RdbDatabaseName;
    }
    return rdb.RdbUrl
        + "/"
        + rdb.RdbDatabaseName
        + "?createDatabaseIfNotExist=true&useUnicode=yes&characterEncoding=UTF-8"
        + "&sslMode="
        + rdb.sslMode;
  }

  public static String buildDatabaseName(RdbConfig rdb) {
    final var dbName = rdb.RdbDatabaseName;
    if (dbName.contains("-")) {
      if (rdb.isPostgres()) {
        throw new ModelDBException("Postgres does not support database names containing -");
      }
      if (rdb.isMysql()) {
        return String.format("`%s`", dbName);
      }
      if (rdb.isMssql()) {
        return String.format("\"%s\"", dbName);
      }
    }
    return dbName;
  }
}
