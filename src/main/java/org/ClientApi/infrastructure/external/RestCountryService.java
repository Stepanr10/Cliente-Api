package org.ClientApi.infrastructure.external;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@ApplicationScoped
public class RestCountryService {

    public String getNationality(String isoCode) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://restcountries.com/v3.1/alpha/" + isoCode))
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            // Cuando la respuesta sea diferente a 200
            if (response.statusCode() != 200) {
                System.err.println("Error de API RestCountryService: status=" + response.statusCode() + ", body=" + response.body());
                return "Error: status=" + response.statusCode(); 
            }

            JsonReader reader = Json.createReader(new StringReader(response.body()));
            JsonArray array = reader.readArray();
            JsonObject country = array.getJsonObject(0);

            return country.getJsonObject("demonyms").getJsonObject("eng").getString("m");

        } catch (Exception e) {
            return "Desconocido";
        }
    }
}
