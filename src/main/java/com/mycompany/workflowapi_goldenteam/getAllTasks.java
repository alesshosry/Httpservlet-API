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
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author eghaz
 */
public class getAllTasks extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            Properties prop = new Properties();
            prop.load(getServletContext().getResourceAsStream("/WEB-INF/config.properties"));

            response.setContentType("application/javascript;charset=utf-8");
            PrintWriter out = response.getWriter();

            String url = "";
            if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {

                Class.forName(prop.getProperty("googleDriverPath")).newInstance();
                url = prop.getProperty("prodURL");

            } else {
                Class.forName(prop.getProperty("localDriverPath"));
                url = prop.getProperty("qcURL");
            }
            Connection connection = DriverManager.getConnection(url);

            Statement statement = connection.createStatement();
            //ResultSet resultSet = statement.executeQuery("select * from wf_workflow_details");
            ResultSet resultSet = statement.executeQuery("select * from wf_workflow_master");
            while (resultSet.next()) {
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    if (i > 1) {
                       out.print(",  ");
                    }
                    String columnValue = resultSet.getString(i);
                    out.print(columnValue + " " + resultSet.getMetaData().getColumnName(i));
                }
               out.println("");
            } 

        } catch (Exception ex) {

        }

    }

}
