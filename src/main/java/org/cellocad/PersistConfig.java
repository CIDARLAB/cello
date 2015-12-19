//package org.cellocad;
//
//
//import org.apache.derby.jdbc.EmbeddedDriver;
//import org.springframework.context.annotation.Bean;
//import org.springframework.jdbc.datasource.SimpleDriverDataSource;
//
//import javax.sql.DataSource;
//
//public class PersistConfig {
//
//    String databaseLocation = "jdbc:derby:resources/derbydb2;create=true";
//    String databaseUsername = "";
//    String databasePassword = "";
//
//    @Bean
//    public DataSource dataSource() {
//        DataSource dataSource = new SimpleDriverDataSource( new EmbeddedDriver(), databaseLocation, databaseUsername, databasePassword);
//        return dataSource;
//    }
//}
