/**
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.example.appengine.helloworld;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")

public class HelloServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        PrintWriter out = resp.getWriter();
        //out.println("Dear vistor, you are accessing my first project deployed on cloud. Thank you");

        try {

            //Class.forName("com.mysql.jdbc.Driver");
            //...
            //String url = String.format("jdbc:mysql://35.184.122.50:3306/WorkFlow?useSSL=false&useUnicode=true&characterEncoding=UTF-8");
            // Connection connection = DriverManager.getConnection(url, "root", "GoldenTeam1");
           // String url = String.format("jdbc:mysql://35.184.122.50:3306/WorkFlow?user=root&useSSL=false&useUnicode=true&characterEncoding=UTF-8");
            Class.forName("com.mysql.jdbc.GoogleDriver");

            String url = String.format("jdbc:google:mysql://myfirstproject-ecd46:myinstance/WorkFlow?user=root&useSSL=false&useUnicode=true&characterEncoding=UTF-8");
//myfirstproject-ecd46:us-central1:myinstance
            Connection connection = DriverManager.getConnection(url);

            //Where x is your instance IP (get it from the panel), y is the instance name, z is the database (not account) user name (usually "root") and w is the user password (you have to set it. look at Google's tutorials).
            /*   try (Statement statement = connection.createStatement()) {
             ResultSet resultSet = statement.executeQuery("select user_name from wf_accounts;");
             while (resultSet.next()) {
             out.println(resultSet.getString(1));
             }

             }*/
        } catch (Exception ex) {
            out.println("Error: " + ex.toString());

        }
    }

    /* @Override
     public void service(ServletRequest request, ServletResponse response) 
     throws ServletException, IOException {
     }*/
}
