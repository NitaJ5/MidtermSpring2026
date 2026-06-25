package persistence;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import persistence.mapper.GameMapper;

public class MyBatisUtil {

    private static final SqlSessionFactory SESSION_FACTORY = buildSessionFactory();

    private static SqlSessionFactory buildSessionFactory() {
        PooledDataSource dataSource = new PooledDataSource(
                "org.sqlite.JDBC",
                "jdbc:sqlite:uno.db",
                "",
                ""
        );

        Environment environment = new Environment(
                "development",
                new JdbcTransactionFactory(),
                dataSource
        );

        Configuration configuration = new Configuration(environment);
        configuration.addMapper(GameMapper.class);

        return new SqlSessionFactoryBuilder().build(configuration);
    }

    public static SqlSessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }
}