package servlet;

import exception.DBException;
import model.BankClient;
import service.BankClientService;
import util.PageGenerator;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class RegistrationServlet extends HttpServlet {
    BankClientService bankClientService = new BankClientService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        PageGenerator.getInstance().getPage("registrationPage.html", new HashMap<>());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String password = req.getParameter("password");
        String name = req.getParameter("name");
        Long money = Long.decode(req.getParameter("money"));
        Map<String, Object> pageVariables = new HashMap<>();

        try {
            BankClient bankClient = new BankClient(name, password, money);
            if (bankClient.equals(bankClientService.getClientByName(name))) {
                throw new DBException(new SQLException());
            } else {
                if (bankClientService.addClient(bankClient)) {
                    pageVariables.put("message", "Add client successful");
                } else {
                    throw new DBException(new SQLException());
                }
            }
        } catch (DBException e) {
            pageVariables.put("message", "Client not add");
        }

        resp.getWriter().println(new PageGenerator().getPage("resultPage.html", pageVariables));
        resp.setStatus(HttpServletResponse.SC_OK);

    }
}
