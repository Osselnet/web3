package bankClientDAO;

import model.BankClient;
import util.DBHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BankClientDAO {

     private Connection connection;

    public BankClientDAO() {
        this.connection = DBHelper.getConnection();
    }

    public Connection getConnection() {
        return connection;
    }

    public List<BankClient> getAllBankClient() throws SQLException {
        List<BankClient> bankClients = new ArrayList<>();
        Statement stmt = connection.createStatement();
        stmt.execute("select * from bank_client");
        ResultSet result = stmt.getResultSet();
        while (result.next()) {
            bankClients.add(new BankClient(result.getLong("id"),
                    result.getNString("name"),
                    result.getNString("password"),
                    result.getLong("money")
            ));
        }
        result.close();
        stmt.close();
        return bankClients;
    }

    public boolean validateClient(String name, String password) {
        try {
            BankClient bankClient = getClientByName(name);
            return bankClient.getName().equals(name) && bankClient.getPassword().equals(password);
        } catch (SQLException e) {
            return false;
        }
    }

    public void updateClientsMoney(String name, String password, Long transactValue) throws SQLException {
        BankClient bankClient = getClientByName(name);
        if (!validateClient(name, password) || (transactValue < 0 && bankClient.getMoney().longValue() < Math.abs(transactValue))) {
            throw new SQLException();
        }
        PreparedStatement stmt = connection.prepareStatement("update bank_client set money = ? where id = ?");
        stmt.setLong(1, bankClient.getMoney() + transactValue);
        stmt.setLong(2, bankClient.getId());
        stmt.executeUpdate();
        stmt.close();
    }

    public BankClient getClientById(long id) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("select * from bank_client where id='" + id + "'");
        ResultSet result = stmt.getResultSet();
        result.next();
        BankClient bankClient = new BankClient(id,
                result.getNString("name"),
                result.getNString("password"),
                result.getLong("money")
        );
        result.close();
        stmt.close();
        return bankClient;
    }

    public long getClientIdByName(String name) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("select * from bank_client where name='" + name + "'");
        ResultSet result = stmt.getResultSet();
        result.next();
        long id = result.getLong(1);
        result.close();
        stmt.close();
        return id;
    }

    public BankClient getClientByName(String name) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("select * from bank_client where name='" + name + "'");
        ResultSet result = stmt.getResultSet();
        result.next();
        BankClient bankClient = new BankClient(result.getLong("id"),
                result.getNString("name"),
                result.getNString("password"),
                result.getLong("money")
        );
        result.close();
        stmt.close();
        return bankClient;
    }

    public void addClient(BankClient client) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("insert into bank_client (name, password, money) values ('" + client.getName() + "', '" +
                client.getPassword() + "', " + client.getMoney() + ")");
        stmt.close();
    }

    public void deleteClient(String name) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("DELETE FROM bank_client WHERE name='" + name + "'");
        stmt.close();
    }

    public void createTable() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("create table if not exists bank_client (id bigint auto_increment, name varchar(256), password varchar(256), money bigint, primary key (id))");
        stmt.close();
    }

    public void dropTable() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("drop table if exists bank_client");
        stmt.close();
    }
}
