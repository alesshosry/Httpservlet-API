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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Aless
 */
public class CreateNewUser extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        PrintWriter out = resp.getWriter();
        //out.println("Dear vistor, you are accessing my first project deployed on cloud. Thank you");

        String url = "";                  
                
        try {
                        
            String UserName=req.getParameter("UserName").toString();
            String UserMail=req.getParameter("UserMail").toString();
            String UserGender=req.getParameter("UserGender").toString();
            String UserBirthDate = req.getParameter("UserBirthDate").toString();
            
            if (UserName == null){
                throw new Exception("UserName is null");            
            }
            
            if (UserMail == null){
                throw new Exception("UserMail is null");            
            }
             
            if (UserGender == null){
                throw new Exception("UserGender is null");            
            }
              
            if (UserBirthDate == null){
                throw new Exception("UserBirthDate is null");            
            }
            
            /*out.println("UserName: "+UserName);
             out.println("UserMail: " +UserMail);
             out.println("UserGender:" + UserGender);
             out.println("UserBirthDate:" + UserBirthDate);*/
            
             
            // String UserBirthDate="2008-05-04";  
            
           if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {

                Class.forName("com.mysql.jdbc.GoogleDriver").newInstance();
                //url = "jdbc:google:mysql://MyFirstProject-ecd46:myinstance/WorkFlow?user=root";
                url = "jdbc:google:mysql://workflowcnam:asia-east1:workflow/WorkFlow?user=root&password=GoldenTeam1";

            } else {
                Class.forName("com.mysql.jdbc.Driver");
                url = "jdbc:mysql://35.194.177.156:3306/WorkFlow?user=root&password=GoldenTeam1&useSSL=false";
            }

            Connection connection = DriverManager.getConnection(url);
                         
            Statement statement = connection.createStatement();
            int resultSet = statement.executeUpdate("Insert into wf_accounts(user_name,user_mail,user_gender,date_of_birh,creation_ddate)values('" + UserName + "','" + UserMail + "','" + UserGender + "','" + UserBirthDate + "', sysdate());");
           // int resultSet = statement.executeUpdate("Insert into wf_accounts(user_name,user_mail,user_gender,creation_ddate)values('frou frou','frou@feou.com','U',sysdate());");
            
           // int resultSet = statement.executeUpdate("update wf_accounts set date_of_birh = '2008-05-04' Where user_name = 'frou frou';");
 
            //while (resultSet.next()) {
                
            
            JSONObject user = new JSONObject();
            user.put("result", resultSet);
            
            out.println(user.toString());
            //}

        } catch (Exception ex) {
            resp.sendError(400, ex.toString());
        }
    }
}
