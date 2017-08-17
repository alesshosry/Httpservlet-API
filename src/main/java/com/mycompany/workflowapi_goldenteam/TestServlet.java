/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.workflowapi_goldenteam;

import com.google.appengine.api.utils.SystemProperty;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Aless
 */
public class TestServlet  extends HttpServlet {
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    
        
        PrintWriter out = resp.getWriter();
        //out.println("Dear vistor, you are accessing my first project deployed on cloud. Thank you");
        try{
           
            
             if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
    
            Class.forName("com.mysql.jdbc.GoogleDriver").newInstance();
            //url = "jdbc:google:mysql://MyFirstProject-ecd46:myinstance/WorkFlow?user=root";
             String url = "jdbc:google:mysql://workflowcnam:asia-east1:workflow/WorkFlow?user=root&password=GoldenTeam1";
        Connection connection = DriverManager.getConnection(url);
        
        Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("select user_name from wf_accounts;");
             while (resultSet.next()) {
             out.println(resultSet.getString(1));
             }
        
    } else {
        Class.forName("com.mysql.jdbc.Driver");
        String url = "jdbc:mysql://35.194.177.156:3306/WorkFlow?user=root&password=GoldenTeam1";
        Connection connection = DriverManager.getConnection(url);
        
        Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("select user_name from wf_accounts;");
             while (resultSet.next()) {
             out.println(resultSet.getString(1));
             }
        
    }       
        }catch(Exception ex){
            resp.sendError(400, ex.toString());
        }
    }
}
