package service;

import bankClientDAO.BankClientDAO;
import exception.DBException;
import model.BankClient;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BankClientService {

    private final BankClientDAO bankClientDAO = new BankClientDAO();

    public BankClientService() {
    }

    public BankClient getClientById(long id) throws DBException {
        try {
            return bankClientDAO.getClientById(id);
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public BankClient getClientByName(String name) {
        try {
            return bankClientDAO.getClientByName(name);
        } catch (SQLException ignored) {
        }
        return null;
    }

    public List<BankClient> getAllClient() {
        try {
            return bankClientDAO.getAllBankClient();
        } catch (SQLException ignored) {
        }
        return new ArrayList<>();
    }

    public boolean deleteClient(String name) {
        boolean result = false;
        try {
            bankClientDAO.deleteClient(name);
            result = true;
        } catch (SQLException ignored) {
        }
        return result;
    }

    public boolean addClient(BankClient client) {
        boolean result = false;
        try {
            if (!bankClientDAO.validateClient(client.getName(), client.getPassword())) {
                bankClientDAO.addClient(client);
                result = true;
            }
        } catch (SQLException ignored) {
        }
        return result;
    }

    public boolean sendMoneyToClient(BankClient sender, String name, Long value) {
        boolean result = false;

        Connection connection = bankClientDAO.getConnection();

        try {
            connection.setAutoCommit(false);
            bankClientDAO.updateClientsMoney(sender.getName(), sender.getPassword(), value * -1);

            BankClient client = bankClientDAO.getClientByName(name);
            bankClientDAO.updateClientsMoney(client.getName(), client.getPassword(), value);
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
        try {
            bankClientDAO.dropTable();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public void createTable() throws DBException {
        try {
            bankClientDAO.createTable();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }
}
