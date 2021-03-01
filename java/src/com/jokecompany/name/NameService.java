package com.jokecompany.name;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class NameService implements INameService {
    private final String url;

    public NameService(String url) {
        this.url = url;
    }

    public Name getRandomName() throws URISyntaxException, IOException, InterruptedException {
        URI uri = new URI(this.url);
        HttpRequest request = HttpRequest
                                .newBuilder()
                                .uri(uri)
                                .build();

        HttpClient client = HttpClient.newHttpClient();
        String name = client.send(request, HttpResponse.BodyHandlers.ofString()).body();

        Gson gson = new Gson();
        return gson.fromJson(name, Name.class);
    }
}
