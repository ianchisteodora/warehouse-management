package connection;

import java.awt.image.LookupOp;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionFactory {


    private static final Logger LOGGER = Logger.getLogger(ConnectionFactory.class.getName());
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DBURL = "jdbc:mysql://localhost::3306/ordersmngm";
    private static final String USER = "root";
    private static final String PASSWORD = "Teodora20!";


    private static ConnectionFactory singleInstance = new ConnectionFactory();

    private ConnectionFactory(){
        try{
            Class.forName(DRIVER);

        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public Connection createConnection() {

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DBURL, USER, PASSWORD);

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error while creating the connection to the database!");
            e.printStackTrace();
        }
        return connection;

    }

    public Connection getConnection() {

        return singleInstance.createConnection();

    }

    public static void close(Statement statement){
        if(statement != null){
            try{
                statement.close();
            }catch(SQLException e){
                LOGGER.log(Level.WARNING, "Error while trying to close the statement");
            }
        }
    }

    public static void close(ResultSet resultSet){
        if(resultSet != null){
            try{
                resultSet.close();

            }catch(SQLException e){
                LOGGER.log(Level.WARNING, "Error whlie trying to close the resultSet");
            }
        }
    }


}
