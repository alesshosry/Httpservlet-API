/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.workflowapi_goldenteam;

import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.repackaged.com.google.gson.JsonObject;
import static com.mycompany.workflowapi_goldenteam.GetUsers.getQueryMap;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author User
 */
public class UpsertWorkflow extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        
        String strJSON = request.getParameter("workflowObj"); // your string goes here
        
        JSONArray jArray = (JSONArray) new JSONTokener(strJSON).nextValue();
        // once you get the array, you may check items like
        
        for (int i = 0; i <= jArray.length() - 1; i++) {
            JSONObject jObject = jArray.getJSONObject(i);
            strJSON += jObject.toString();
        }
        //String workflowJSONObj = request.getParameter("workflowObj");
        //Object workflowObj = JSONObject.stringToValue(workflowJSONObj);
        
        //Map callbackMap = null;
        //if(request.getQueryString() != null){
        //    callbackMap = getQueryMap(request.getQueryString());
        //}
        
        //Properties prop = new Properties();
        //prop.load(getServletContext().getResourceAsStream("/WEB-INF/config.properties"));
        
        //response.setContentType("application/json;");
        response.setContentType("application/json");
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
        response.addHeader("Access-Control-Allow-Headers", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
        response.addHeader("Access-Control-Max-Age", "1728000");
        PrintWriter out = response.getWriter();
        JSONObject result = new JSONObject();
        result.put("result", strJSON);
        out.write(result.toString());
        String url = "";
        try {

//            if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
//
//                Class.forName(prop.getProperty("googleDriverPath")).newInstance();
//                //url = "jdbc:google:mysql://MyFirstProject-ecd46:myinstance/WorkFlow?user=root";
//                url = prop.getProperty("prodURL");
//
//            } else {
//                Class.forName(prop.getProperty("localDriverPath"));
//                url = prop.getProperty("qcURL");
//            }
//
//            Connection connection = DriverManager.getConnection(url);
//
//            Statement statement = connection.createStatement();
//            ResultSet resultSet = statement.executeQuery("Select user_id,user_name,user_mail from wf_accounts;");
//           
//            JSONObject json      = new JSONObject();
//            JSONArray  UsersList = new JSONArray();
//            JSONObject user = new JSONObject();
//            
//            while (resultSet.next()) {
//                
//                user = new JSONObject();
//                
//                user.put("UserID" , resultSet.getString(1));
//                user.put("UserName" , resultSet.getString(2));
//                user.put("UserMail" , resultSet.getString(3));
//                //user.put("UserDateOfBirth" , resultSet.getString(4));
//                
//                UsersList.put(user);
//                
//                
//                //out.println("UserID: " + resultSet.getString(1) + ", UserName: " +resultSet.getString(2) + ", UserMail: " +resultSet.getString(3) + ", UserDateOfBirth: " +resultSet.getString(4));
//            }
//            
//          json.put("jsonArray",UsersList);
//          if(callbackMap != null)
//            out.write((String)callbackMap.get("callback") + "(" + jObject.toString() + ")");
//          else
//            out.write(jObject.toString());
          out.close();
        } catch (Exception ex) {
           response.sendError(400, ex.toString());
        }
    }

}
