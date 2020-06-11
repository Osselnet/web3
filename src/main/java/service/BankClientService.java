package service;

import dao.BankClientDAO;
import exception.DBException;
import model.BankClient;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BankClientService {

    public BankClientService() {
    }

    public BankClient getClientById(long id) throws DBException {
        try {
            return getBankClientDAO().getClientById(id);
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public BankClient getClientByName(String name) {
        try {
            return getBankClientDAO().getClientByName(name);
        } catch (SQLException ignored) {
        }
        return null;
    }

    public List<BankClient> getAllClient() {
        try {
            return getBankClientDAO().getAllBankClient();
        } catch (SQLException ignored) {
        }
        return new ArrayList<>();
    }

    public boolean deleteClient(String name) {
        boolean result = false;
        try {
            getBankClientDAO().deleteClient(name);
            result = true;
        } catch (SQLException ignored) {
        }
        return result;
    }

    public boolean addClient(BankClient client) {
        boolean result = false;
        BankClientDAO dao = getBankClientDAO();
        try {
            if (!dao.validateClient(client.getName(), client.getPassword())) {
                dao.addClient(client);
                result = true;
            }
        } catch (SQLException ignored) {
        }
        return result;
    }

    public boolean sendMoneyToClient(BankClient sender, String name, Long value) {
        boolean result = false;
        Connection connection = getMysqlConnection();
        try {
            connection.setAutoCommit(false);
            BankClientDAO dao = new BankClientDAO(connection);
            dao.updateClientsMoney(sender.getName(), sender.getPassword(), value * -1);

            BankClient client = dao.getClientByName(name);
            dao.updateClientsMoney(client.getName(), client.getPassword(), value);
            result = true;
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ignored) {
            }
        }
        return result;
    }

    public void cleanUp() throws DBException {
        BankClientDAO dao = getBankClientDAO();
        try {
            dao.dropTable();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public void createTable() throws DBException {
        BankClientDAO dao = getBankClientDAO();
        try {
            dao.createTable();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    private static Connection getMysqlConnection() {
        try {
            DriverManager.registerDriver((Driver) Class.forName("com.mysql.jdbc.Driver").newInstance());

            StringBuilder url = new StringBuilder();

            url.
                    append("jdbc:mysql://").        //db type
                    append("localhost:").           //host name
                    append("3306/").                //port
                    append("db_example?").          //db name
                    append("user=root&").          //login
                    append("password=ca5e59f2");       //password

            System.out.println("URL: " + url + "\n");

            return DriverManager.getConnection(url.toString());
        } catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }

    private static BankClientDAO getBankClientDAO() {
        return new BankClientDAO(getMysqlConnection());
    }
}
