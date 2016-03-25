package org.cellocad.authenticate;

import org.apache.log4j.Logger;
import org.cellocad.api.MainController;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Created by Bryan Der on 8/21/15.
 */

@RestController
public class LoginController extends HttpServlet {


    private static final long serialVersionUID = -2579220291590687064L;

    private static final String USER_DB_NAME = "CELLO";
    private static Logger LOGGER = Logger.getLogger("AuthenticationServlet");
    public Authenticator auth = new Authenticator(USER_DB_NAME);;



    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws java.io.IOException if an I/O error occurs
     */
    //@Override
    @RequestMapping(value="/authentication",method= RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    String authenticate(@RequestParam Map<String, String> credentials) {


        JSONObject jsonResponse = new JSONObject();

        try {

            // get the username and password parameter values
            // from the request
            String command = credentials.get("command");
            String username = credentials.get("username");
            String password = credentials.get("password");

            /**
             * Signup request
             */
            if ("signup".equals(command)) {

                try {
                    this.auth.register(username, password, false);

                    MainController main_controller = new MainController();
                    main_controller.newUserSetup(username);

                } catch (AuthenticationException e) {
                    LOGGER.warn(e.getLocalizedMessage());

                    jsonResponse.put("status", "exception");
                    jsonResponse.put("result", e.getLocalizedMessage());
                    return jsonResponse.toString();
                }
            }

            /**
             * Login request
             */
            else if ("login".equals(command)) {

                // check if the user exists and if the passwords match
                boolean bLogin = this.auth.login(username, password);

                if(!bLogin) {
                    jsonResponse.put("status", "exception");
                    jsonResponse.put("result", "Invalid Login!");
                    return jsonResponse.toString();
                }
            }


            /**
             * Invalid request
             */
            else {
                throw new AuthenticationException("Invalid Request!");
            }

            jsonResponse.put("status", "good");

        } catch (Exception e) {

            LOGGER.warn(e.getLocalizedMessage());

            jsonResponse.put("status", "exception");
            jsonResponse.put("result", e.getLocalizedMessage());
        }

        /*
         * write the response
         */
        return jsonResponse.toString();
    }


    /**
     * Processes requests for HTTP
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processGetRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        response.sendRedirect("login.html");
        PrintWriter out = response.getWriter();
        try {
        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processGetRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }


}
