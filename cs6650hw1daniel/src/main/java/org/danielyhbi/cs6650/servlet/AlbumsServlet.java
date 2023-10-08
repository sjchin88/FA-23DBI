package org.danielyhbi.cs6650.servlet;

import com.google.gson.*;
import org.danielyhbi.cs6650.data.Album;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/albums/*")
public class AlbumsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");

        // get the parameter
        String inputPath = request.getPathInfo();
        // check we have a URL!
        if (inputPath == null || inputPath.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(getErrorMessage("missing parameters!"));
            return;
        }

        String albumID = inputPath.substring(inputPath.lastIndexOf("/") + 1);

        // check if the input is a number
        try {
            Integer.parseInt(albumID);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(getErrorMessage("albumnID should be integers!"));
            return;
        }

        Album utopia = new Album("UTOPIA", "Travis Scott", 2023);

        Gson gson = new GsonBuilder()
                        .setPrettyPrinting()
                        .create();

        String jsonResponse = gson.toJson(utopia);

        PrintWriter out = response.getWriter();
        out.print(jsonResponse);
        out.flush();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");

        // Read JSON data from the request body
        BufferedReader reader = request.getReader();
        StringBuilder jsonBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBody.append(line);
        }

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonBody.toString(), JsonObject.class);

        try {
            int albumID = jsonObject.get("albumID").getAsInt();
            URL url = new URL(jsonObject.get("imageUrl").getAsString());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(getErrorMessage("Invalid Json! Album ID has to be an int. imageUrl has to be valid"));
            return;
        }

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("albumID", "123");
        jsonResponse.addProperty("imageSize", "imageSize");

        PrintWriter out = response.getWriter();
        out.print(jsonResponse);
        out.flush();
    }

    private String getErrorMessage(String msg) {
        return "{\"error\": \"" + msg + "\"}";
    }

    private String getServerResponseJSON(InputStream inputStream) throws ServletException {
        StringBuilder jsonResponse = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                jsonResponse.append(line);
            }
            reader.close();
        } catch (IOException e) {
            throw new ServletException(e);
        }

        return jsonResponse.toString();
    }
}

