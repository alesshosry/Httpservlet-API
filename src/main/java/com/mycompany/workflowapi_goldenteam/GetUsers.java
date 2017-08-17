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
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Aless
 */
public class GetUsers extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        //out.println("Dear vistor, you are accessing my first project deployed on cloud. Thank you");

        String url = "";
        try {

            if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {

                Class.forName("com.mysql.jdbc.GoogleDriver").newInstance();
                //url = "jdbc:google:mysql://MyFirstProject-ecd46:myinstance/WorkFlow?user=root";
                url = "jdbc:google:mysql://workflowcnam:asia-east1:workflow/WorkFlow?user=root&password=GoldenTeam1";

            } else {
                Class.forName("com.mysql.jdbc.Driver");
                url = "jdbc:mysql://35.194.177.156:3306/WorkFlow?user=root&password=GoldenTeam1";
            }

            Connection connection = DriverManager.getConnection(url);

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("Select user_id,user_name,user_mail,date_of_birh from wf_accounts;");
           
            JSONObject json      = new JSONObject();
            JSONArray  UsersList = new JSONArray();
            JSONObject user;
            
            while (resultSet.next()) {
                
                user = new JSONObject();
                
                user.put("UserID" , resultSet.getString(1));
                user.put("UserName" , resultSet.getString(2));
                user.put("UserMail" , resultSet.getString(3));
                user.put("UserDateOfBirth" , resultSet.getString(4));
                
                UsersList.put(user);
                
                
                //out.println("UserID: " + resultSet.getString(1) + ", UserName: " +resultSet.getString(2) + ", UserMail: " +resultSet.getString(3) + ", UserDateOfBirth: " +resultSet.getString(4));
            }
            
          json.put("result",UsersList);
            
          out.println(json.toString());

        } catch (Exception ex) {
           resp.sendError(400, ex.toString());
        }
    } 
}
