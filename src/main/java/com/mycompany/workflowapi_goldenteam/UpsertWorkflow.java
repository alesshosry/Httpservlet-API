/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.workflowapi_goldenteam;

import com.google.api.client.util.DateTime;
import com.google.api.server.spi.Strings;
import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.repackaged.com.google.gson.JsonObject;
import com.google.appengine.repackaged.com.google.protobuf.Int32Value;
import static com.mycompany.workflowapi_goldenteam.GetUsers.getQueryMap;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.util.Date;

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
        try {
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

            String strJSON = request.getParameter("workflowObj");
            String workFlowId = request.getParameter("workFlowId");
            String UserName = request.getParameter("userName");
            String workflowDesc = request.getParameter("workFlowDesc");

            String workFlowIdInt = workFlowId;

            //Add Access-Control-Allow to response Header
            response = updateResponseHeader(response);

            //Get User Id
            String userId = getUserIdByUserName(UserName, response);

            //Delete existing task for current workflow
            deleteWorkFlowTasksById(workFlowIdInt, response,statement);

            //Insert current workflow in case of new one
            if (workFlowIdInt.trim().contains("-") || workFlowIdInt.trim().equals("-1")) {
                workFlowIdInt = insertWorkflowForCurrentUser(workFlowIdInt,
                        userId,
                        workflowDesc,
                        response,
                        statement);
            }

            //Loop and insert current tasks into current workflow
            JSONArray jArray = (JSONArray) new JSONTokener(strJSON).nextValue();
            out.write(workFlowIdInt);
            for (int i = 0; i <= jArray.length() - 1; i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                
                insertWorkflowTasks(response,
                        workFlowIdInt,
                        Strings.isEmptyOrWhitespace(jObject.optString("startDate")) ? "" : jObject.optString("startDate"),
                        jObject.optString("falseRedirect"),
                        jObject.optString("trueRedirect"),
                        jObject.optString("title"),
                        jObject.optString("email"),
                        jObject.optString("description"),
                        jObject.optString("tag"),
                        jObject.optString("taskGraphIndex"),
                        jObject.optString("trueRedirect"),
                        Strings.isEmptyOrWhitespace(jObject.optString("endDate")) ? "" : jObject.optString("endDate"),
                        jObject.optString("type"),
                        statement,
                        out);

                //break;
            }

            //getAllTasks(response);

        } catch (Exception ex) {
            response.sendError(400, ex.toString());
        }
    }

    private void getAllTasks(HttpServletResponse resp) {
        try {
            Properties prop = new Properties();
            prop.load(getServletContext().getResourceAsStream("/WEB-INF/config.properties"));

            resp.setContentType("application/javascript;charset=utf-8");
            PrintWriter out = resp.getWriter();

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
            ResultSet resultSet = statement.executeQuery("select * from wf_workflow_details");
            if (resultSet.next()) {
                out.print("has next");
            } else {
                out.print("no next");
            }

        } catch (Exception ex) {

        }
    }

    private void deleteWorkFlowTasksById(String workflowID, HttpServletResponse resp, Statement statement) throws IOException {
        try {

            int resultSet = statement.executeUpdate("Delete from wf_workflow_details where wf_id='" + workflowID + "'");

        } catch (Exception ex) {
            resp.sendError(400, ex.toString());
        }

    }

    private String getUserIdByUserName(String userName, HttpServletResponse resp) throws IOException {
        try {
            Properties prop = new Properties();
            prop.load(getServletContext().getResourceAsStream("/WEB-INF/config.properties"));

            resp.setContentType("application/javascript;charset=utf-8");
            PrintWriter out = resp.getWriter();

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
            resp.sendError(400, ex.toString());
        }
        return "";
    }

    private String insertWorkflowForCurrentUser(String workflowId,
            String userId,
            String workflowDesc,
            HttpServletResponse resp,
            Statement statement) throws IOException {
        try {

            //String sqlQuery = "IF NOT EXISTS (SELECT 1 FROM wf_workflow_master WHERE user_id='" + userId + "') BEGIN ";
            if (workflowId.contains("-") || workflowId.equals("-1")) {

                String sqlQuery = "INSERT INTO wf_workflow_master "
                        + "(user_id,wf_desc,start_date,end_date,creation_date) ";
                sqlQuery += "VALUES (" + userId + ",'" + workflowDesc + "',sysdate(),sysdate(),sysdate())";

                statement.execute(sqlQuery);
                sqlQuery = "SELECT MAX(wf_id) FROM wf_workflow_master WHERE user_id='" + userId + "'";
                ResultSet resultSet = statement.executeQuery(sqlQuery);
                while (resultSet.next()) {
                    return resultSet.getString(1);
                }
            } else {
                return workflowId;
            }

        } catch (Exception ex) {
            resp.sendError(400, ex.toString());
        }
        return "";
    }

    private void insertWorkflowTasks(HttpServletResponse resp,
            String wfId,
            String taskId,
            String startDate,
            String falseRed,
            String title,
            String emails,
            String desc,
            String tag,
            String graphIndex,
            String trueRed,
            String endDate,
            String tskType, Statement statement, PrintWriter out) throws IOException {
        //PrintWriter out = resp.getWriter();
        try {
           
           
            String sqlQuery = "Insert into wf_workflow_details (wf_id,"
                    + "wf_desc,"
                    + "wf_emails,"
                    + "wf_s_date,"
                    + "wf_e_date,"
                    + "wf_f_redirect,"
                    + "wf_t_redirect,"
                    + "wf_tag,"
                    + "wf_taskgraphindex,"
                    //+ "wf_task_id,"
                    + "wf_title,"
                    + "wf_type)";
            sqlQuery += "VALUES ('" + wfId + "',"
                    + "'Desc of " + wfId + "',"
                    + "'" + emails + "',"
                    + "'" + startDate + "',"
                    + "'" + endDate + "',"
                    //+ "sysdate(),"
                    //+ "sysdate(),"
                    + "'" + falseRed + "',"
                    + "'" + trueRed + "',"
                    + "'" + tag + "',"
                    + "'" + graphIndex + "',"
                    //+ "'" + taskId + "',"
                    + "'" + title + "',"
                    + "'" + tskType + "')";
            int inserted =statement.executeUpdate(sqlQuery);
            

//            while (resultSet.next()) {
//                out.write(resultSet.getString(1));
//            }
           

        } catch (Exception ex) {
            out.print("error" + ex.toString());
            resp.sendError(400, ex.toString());
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
