/**
 * @author xiaoyi
 */


package com.techprimers.db.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techprimers.db.constants.ParameterStringBuilder;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Component
public class WeatherService {

    public static String APIURLWITHZIP = "https://api.darksky.net/forecast/fc2dc7dc8c7132ebb5ad2a67cef91e9d/37.8267,-122.4233";

    /**
     * this is the method to get weather api call with zip
     */
    public String getWeatherByZip(String zip) throws IOException {

        if (zip == null) {
            return null;
        }


        URL obj = new URL(APIURLWITHZIP);

        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + APIURLWITHZIP);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        //JSONObject currently = new JSONObject(response.toString());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(response.toString());
        System.out.println(response.toString());
        String currently = node.get("currently").get("summary").asText();

        System.out.println(currently);

        //JSONObject summary = new JSONObject(currently.getString("currently"));
        return currently;
    }
}
