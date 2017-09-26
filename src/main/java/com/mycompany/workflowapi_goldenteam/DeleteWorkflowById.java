/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.workflowapi_goldenteam;

import com.google.api.server.spi.Strings;
import com.google.appengine.api.utils.SystemProperty;
import static com.mycompany.workflowapi_goldenteam.GetUsers.getQueryMap;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
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
 * @author Cynthia
 */
public class DeleteWorkflowById extends HttpServlet {
    
   @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try 
        {            
            PrintWriter out = response.getWriter();

            Properties prop = new Properties();
            prop.load(getServletContext().getResourceAsStream("/WEB-INF/config.properties"));

            response.setContentType("application/javascript;charset=utf-8");

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
            
            String workFlowId = request.getParameter("workflowId");
            
            String workFlowIdInt = workFlowId;

            //Add Access-Control-Allow to response Header
            response = updateResponseHeader(response);

            //Delete existing task for current workflow
            deleteWorkFlowById(workFlowIdInt, response,statement);

            out.write("success");
            
        } catch (Exception ex) {
            response.sendError(400, ex.toString());
        }
    }
    
    private HttpServletResponse updateResponseHeader(HttpServletResponse response) {

        response.setContentType("application/json");
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
        response.addHeader("Access-Control-Allow-Headers", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
        response.addHeader("Access-Control-Max-Age", "1728000");
        return response;
    }
    
     private void deleteWorkFlowById(String workflowID, HttpServletResponse resp, Statement statement) throws IOException {
        try {

            int resultSet1 = statement.executeUpdate("Delete from wf_workflow_details where wf_id='" + workflowID + "'");
            int resultSet2 = statement.executeUpdate("Delete from wf_workflow_master where wf_id='" + workflowID + "'");
        
        } catch (Exception ex) {
            resp.sendError(400, ex.toString());
        }

    }
    
     
    
}