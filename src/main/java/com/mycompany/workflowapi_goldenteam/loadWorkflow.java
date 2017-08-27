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
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author User
 */
public class loadWorkflow extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        PrintWriter out = response.getWriter();
        JSONObject jsonObj = new JSONObject();
        response = updateResponseHeader(response);
        String UserName = request.getParameter("userName");
        String WorkflowId = request.getParameter("workflowId");

        //GET USER ID BY USERNAME
        if (UserName != "" && UserName != null) {
            String userId = getUserIdByUserName(UserName);
            if (userId != "" && userId != null && WorkflowId != "" && WorkflowId != null) {
                //GET TASKS BY WORKFLOW ID AND USER ID
                //CREATE JSON OBJECT AND SEND IT TO RESPONSE
                String tasks=getTasksByWorkflowIdAndUserId(userId, WorkflowId);
                
                out.print(tasks.toString());
                

            } else {
                jsonObj.put("workflowObj", "");
                out.write(jsonObj.toString());
            }
        } else {
            jsonObj.put("workflowObj", "");
            out.write(jsonObj.toString());
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    private String getUserIdByUserName(String userName) throws IOException {
        try {
            Properties prop = new Properties();
            prop.load(getServletContext().getResourceAsStream("/WEB-INF/config.properties"));

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
            ResultSet resultSet = statement.executeQuery("Select user_id from wf_accounts where user_name='" + userName + "'");

            while (resultSet.next()) {

                return resultSet.getString(1);
            }
            return "";

        } catch (Exception ex) {
            ex.toString();
        }
        return "";
    }

    private String getTasksByWorkflowIdAndUserId(String userId, String workflowId) throws IOException {
        try {
            JSONObject tasksObj = new JSONObject();
            JSONArray taskArray = new JSONArray();

            Properties prop = new Properties();
            prop.load(getServletContext().getResourceAsStream("/WEB-INF/config.properties"));

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
            ResultSet resultSet = statement.executeQuery("select wf_workflow_details.* "
                    + "from wf_workflow_master, "
                    + "wf_workflow_details "
                    + "where wf_workflow_master.wf_id = " + workflowId
                    + " and wf_workflow_master.user_id = " + userId
                    + " and wf_workflow_details.wf_id = wf_workflow_master.wf_id");

            while (resultSet.next()) {
                JSONObject taskDetails = new JSONObject();
                taskDetails.put("description", resultSet.getString("wf_desc"));
                taskDetails.put("email", resultSet.getString("wf_emails"));
                taskDetails.put("startDate", resultSet.getString("wf_s_date"));
                taskDetails.put("endDate", resultSet.getString("wf_e_date"));
                taskDetails.put("falseRedirect", resultSet.getString("wf_f_redirect"));
                taskDetails.put("trueRedirect", resultSet.getString("wf_t_redirect"));
                taskDetails.put("tag", resultSet.getString("wf_tag"));
                taskDetails.put("taskGraphIndex", resultSet.getString("wf_taskgraphindex"));
                taskDetails.put("taskId", resultSet.getString("wf_task_id"));
                taskDetails.put("title", resultSet.getString("wf_title"));
                taskDetails.put("type", resultSet.getString("wf_type"));

                taskArray.put(taskDetails);
            }
            //tasksObj.put("workflowObj", taskArray.toString());
            return taskArray.toString();

        } catch (Exception ex) {
            return (ex.toString());
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
}
