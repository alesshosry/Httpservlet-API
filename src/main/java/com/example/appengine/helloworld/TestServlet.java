/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.appengine.helloworld;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Aless
 */
public class TestServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    
    PrintWriter out = resp.getWriter();
    int num1 =  Integer.parseInt(req.getParameter("num1"));
    int num2 =  Integer.parseInt(req.getParameter("num2"));
    int resultat = num1 + num2;
    out.println("Resultat= " + resultat );
  }
  
 /* @Override
  public void service(ServletRequest request, ServletResponse response) 
   throws ServletException, IOException {
}*/
}
