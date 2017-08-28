/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.workflowapi_goldenteam;

import com.google.appengine.api.utils.SystemProperty;
import static com.mycompany.workflowapi_goldenteam.GetUsers.getQueryMap;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author eghaz
 */
public class GetWorkflowsByUsers extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map callbackMap = null;
//        if(req.getQueryString() != null){
//            callbackMap = getQueryMap(req.getQueryString());
//        }
        resp=updateResponseHeader(resp);
        resp.setContentType("application/json");
        String UserName = req.getParameter("userName");
        Properties prop = new Properties();
        prop.load(getServletContext().getResourceAsStream("/WEB-INF/config.properties"));
        
        
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
           ResultSet resultSet = statement.executeQuery("Select wf_id,wf_desc"
                   + "from wf_workflow_master "
                   + "inner join wf_accounts on "
                   + "wf_accounts.User_id=wf_workflow_master.user_id "
                   + "where wf_accounts.User_name='"+UserName+"'");
           
            JSONObject json      = new JSONObject();
            JSONArray  workflowList = new JSONArray();
            JSONObject user = new JSONObject();
            
            while (resultSet.next()) {
                
                user = new JSONObject();
                
                user.put("wf_id" , resultSet.getString(1));
                user.put("wf_desc" , resultSet.getString(2));
                //user.put("UserDateOfBirth" , resultSet.getString(4));
                
                workflowList.put(user);
                
                
                //out.println("UserID: " + resultSet.getString(1) + ", UserName: " +resultSet.getString(2) + ", UserMail: " +resultSet.getString(3) + ", UserDateOfBirth: " +resultSet.getString(4));
            }
            
          //json.put(workflowList);
          if(callbackMap != null)
            out.print((String)callbackMap.get("callback") + "(" + workflowList.toString() + ")");
          else
            out.print(workflowList.toString());
          out.flush();
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
    private HttpServletResponse updateResponseHeader(HttpServletResponse response) {

        response.setContentType("application/json");
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
        response.addHeader("Access-Control-Allow-Headers", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
        response.addHeader("Access-Control-Max-Age", "1728000");
        return response;
    }
}
