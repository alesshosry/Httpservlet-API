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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
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
        Map callbackMap = null;
        if(req.getQueryString() != null){
            callbackMap = getQueryMap(req.getQueryString());
        }
        
        Properties prop = new Properties();
        prop.load(getServletContext().getResourceAsStream("/WEB-INF/config.properties"));
        
        resp.setContentType("application/javascript;charset=utf-8");
        PrintWriter out = resp.getWriter();

        String url = "";
        try {

            if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {

                Class.forName(prop.getProperty("googleDriverPath")).newInstance();
                //url = "jdbc:google:mysql://MyFirstProject-ecd46:myinstance/WorkFlow?user=root";
                url = prop.getProperty("prodURL");

            } else {
                Class.forName(prop.getProperty("localDriverPath"));
                url = prop.getProperty("qcURL");
            }

            Connection connection = DriverManager.getConnection(url);

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("Select user_id,user_name,user_mail from wf_accounts;");
           
            JSONObject json      = new JSONObject();
            JSONArray  UsersList = new JSONArray();
            JSONObject user = new JSONObject();
            
            while (resultSet.next()) {
                
                user = new JSONObject();
                
                user.put("UserID" , resultSet.getString(1));
                user.put("UserName" , resultSet.getString(2));
                user.put("UserMail" , resultSet.getString(3));
                //user.put("UserDateOfBirth" , resultSet.getString(4));
                
                UsersList.put(user);
                
                
                //out.println("UserID: " + resultSet.getString(1) + ", UserName: " +resultSet.getString(2) + ", UserMail: " +resultSet.getString(3) + ", UserDateOfBirth: " +resultSet.getString(4));
            }
            
          json.put("jsonArray",UsersList);
          if(callbackMap != null)
            out.write((String)callbackMap.get("callback") + "(" + json.toString() + ")");
          else
            out.write(json.toString());
          out.close();
        } catch (Exception ex) {
           resp.sendError(400, ex.toString());
        }
    } 
    
    public static Map<String, String> getQueryMap(String query)  
    {  
        String[] params = query.split("&");  
        Map<String, String> map = new HashMap<String, String>();  
        for (String param : params)  
        {  String [] p=param.split("=");
            String name = p[0];  
          if(p.length>1)  {String value = p[1];  
            map.put(name, value);
          }  
        }  
        return map;  
    } 
}
