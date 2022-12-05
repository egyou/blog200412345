package iducs.javaweb.blog200412345.controller;

import iducs.javaweb.blog200412345.repository.DAOImplMysql;
import iducs.javaweb.blog200412345.repository.DAOImplOracle;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "JDBCTestController", urlPatterns = {"/main/oracle", "/main/mysql"})
public class JDBCTestController extends HttpServlet {
    protected void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String uri = request.getRequestURI();
        String action = uri.substring(uri.lastIndexOf('/') + 1);
        if(action.equals("oracle"))
            System.out.println("241: " + new DAOImplOracle().getConnection());
        else if(action.equals("mysql"))
            System.out.println("242 : " + new DAOImplMysql().getConnection());
        else if(action.equals("local"))
            System.out.println("local : " + new DAOImplOracle().getConnection());
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doProcess(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doProcess(request, response);
    }
}
