package servlet;

import exception.DBException;
import model.BankClient;
import service.BankClientService;
import util.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MoneyTransactionServlet extends HttpServlet {

    BankClientService bankClientService = new BankClientService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PageGenerator.getInstance().getPage("moneyTransactionPage.html", new HashMap<>());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> pageVariables = new HashMap<>();

        BankClient bankClient = new BankClient();
        bankClient.setName(req.getParameter("senderName"));
        bankClient.setPassword(req.getParameter("senderPass"));
        if (bankClientService.sendMoneyToClient(bankClient, req.getParameter("nameTo"), Long.decode(req.getParameter("count")))) {
            pageVariables.put("message", "The transaction was successful");
        } else {
            pageVariables.put("message", "transaction rejected");
        }
        resp.getWriter().println(new PageGenerator().getPage("resultPage.html", pageVariables));
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
