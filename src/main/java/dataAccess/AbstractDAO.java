package dataAccess;

import connection.ConnectionFactory;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AbstractDAO<T> {

    protected final static Logger LOGGER = Logger.getLogger(AbstractDAO.class.getName());

    private final Class<T> type;


    @SuppressWarnings("unchecked")
    public AbstractDAO(){
        this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    }


    public ArrayList<T> findAll(){

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String query = createSelectedQuery();
        try{

            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            return (ArrayList<T>) createObjects(resultSet);

        }catch(SQLException e){
            LOGGER.log(Level.WARNING, type.getName() + "DAO: findAll" + e.getMessage());

        }finally{
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(preparedStatement);
            ConnectionFactory.close((Statement) connection);
        }
        return null;
    }

    public T findById(int id){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String query = createSelectedQuery(type.getDeclaredFields()[0].getName());
        try{
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            return createObjects(resultSet).get(0);

        }catch(SQLException e){
            LOGGER.log(Level.WARNING, type.getName() + "DAO:findById" + e.getMessage());

        }finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(preparedStatement);
            ConnectionFactory.close((Statement) connection);
        }
        return null;
    }

    public T findByName(String name){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String query = createSelectedQuery(type.getDeclaredFields()[1].getName());

        try{

            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            resultSet = preparedStatement.executeQuery();

            return createObjects(resultSet).get(0);

        }catch(SQLException e){
            LOGGER.log(Level.WARNING, type.getName() + "DAO : findByName" + e.getMessage());

        }finally{
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(preparedStatement);
            ConnectionFactory.close((Statement) connection);
        }
        return null;
    }


    private List<T> createObjects(ResultSet resultSet){
        List<T> list = new ArrayList<>();
        Constructor[] ctors = type.getDeclaredConstructors();
        Constructor ctor = null;
        for(int i = 0; i < ctors.length; i++){
            ctor = ctors[i];
            if(ctor.getGenericParameterTypes().length == 0)
                break;
        }
        try{
            while(resultSet.next())
            {
                ctor.setAccessible(true);
                T instance = (T)ctor.newInstance();
                for(Field field : type.getDeclaredFields()){
                    String fieldName = field.getName();
                    Object value = resultSet.getObject(fieldName);
                    PropertyDescriptor propertyDescriptor = new PropertyDescriptor(fieldName, type);
                    Method method = propertyDescriptor.getWriteMethod();
                    method.invoke(instance, value);

                }
                list.add(instance);
            }

        }catch(InstantiationException e){
            e.printStackTrace();
        }catch(IllegalAccessException e){
            e.printStackTrace();
        }catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        return list;
    }

    public String createSelectedQuery(){
        StringBuilder sb = new StringBuilder();

        sb.append("SELECT");
        sb.append(" * ");
        sb.append(type.getSimpleName());
        return sb.toString();
    }

    public String createSelectedQuery(String field){
        StringBuilder sb = new StringBuilder();

        sb.append("SELECT");
        sb.append(" * ");
        sb.append(type.getSimpleName());
        sb.append(" WHERE " + field + " =?");

        return sb.toString();
    }

    public String createSelectedQuery(int id){
        StringBuilder sb = new StringBuilder();

        sb.append("DELETE FROM "
                + type.getSimpleName() + " WHERE "
                + type.getDeclaredFields()[0].getName() + "="
                + id);
        return sb.toString();
    }

    private String createDeleteQuery(int id) {
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM "
                + type.getSimpleName() + " WHERE "
                + type.getDeclaredFields()[0].getName() + "="
                + id);
        return sb.toString();
    }

    private String createInsertQuery(T t){
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO '");
        sb.append(type.getSimpleName() + "' (");
        boolean first = true;
        boolean second = true;

        for(Field field : type.getDeclaredFields()){
            if(first){
                first = false;
            }else if(second){
                sb.append(field.getName());
                second = false;
            }else{
                sb.append(", " + field.getName());
            }
        }
        sb.append(") VALUES (");

        try{
            first = true;
            second = true;
            for(Field field : type.getDeclaredFields()){
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(field.getName(), type);
                Method method = propertyDescriptor.getReadMethod();
                Object value = method.invoke(t);

                if(first){
                    first = false;
                }else if(second){
                    sb.append("'" + value.toString() + "'");
                    second = false;
                }else{
                    sb.append(", '" + value.toString() + "'");
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        sb.append(")");
        return sb.toString();
    }

    private String createUpdateQuery(T t, int id){
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE " + type.getSimpleName() + " SET ");

        try{
            String idc = "";
            boolean first = true;

            for(Field field : type.getDeclaredFields()){
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(field.getName(), type);
                Method method = propertyDescriptor.getReadMethod();
                Object value = method.invoke(t);

                if(first){
                    sb.append(field.getName() + "='" + value.toString() + "'");
                    idc = field.getName() + "='" + id + "'";
                    first = false;
                }else{
                    sb.append(", " + field.getName() + "='" + value.toString() + "'");

                }
            }
            sb.append(" WHERE " + idc);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public int insert(T t) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String query = createInsertQuery(t);
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:insert " + e.getMessage());
        } finally {
            ConnectionFactory.close(preparedStatement);
            ConnectionFactory.close((Statement) connection);
        }
        return -1;
    }
    public boolean update(T t, int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        String query = createUpdateQuery(t, id);
        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:update " + e.getMessage());
            return false;
        } finally {
            ConnectionFactory.close(statement);
            ConnectionFactory.close((Statement) connection);
        }
        return true;
    }


    public boolean delete(int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        String query = createDeleteQuery(id);
        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:delete " + e.getMessage());
            return false;
        } finally {
            ConnectionFactory.close(statement);
            ConnectionFactory.close((Statement) connection);
        }
        return true;
    }
}
