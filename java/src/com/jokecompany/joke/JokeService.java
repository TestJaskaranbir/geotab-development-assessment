package com.jokecompany.joke;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class JokeService implements IJokeService {
    private final String randomURL;
    private final String categoriesURL;

    public JokeService(String baseURL) {
        this.randomURL = joinURLs(baseURL, "random");
        this.categoriesURL = joinURLs(baseURL, "categories");
    }

    private String joinURLs(String... urls) {
        return String.join("/", urls);
    }

    private String addQueryParams(String url, Map<String, String> params) {
        String paramsStr = "";

        for (Map.Entry<String, String> param : params.entrySet()) {
            paramsStr += String.format("%s=%s&", param.getKey(), param.getValue());
        }
        // Remove trailing "&"
        paramsStr = paramsStr.substring(0, paramsStr.length() - 1);

        return String.format("%s?%s", url, paramsStr);
    }

    public Joke getRandomJoke(String name) throws URISyntaxException, IOException, InterruptedException {
        return this.getRandomJoke(name, null);
    }

    public Joke getRandomJoke(String name, String category)
            throws URISyntaxException, IOException, InterruptedException {
        // Build URL
        String url = this.randomURL;
        if (category != null && !category.isEmpty()) {
            HashMap<String, String> urlParams = new HashMap<String, String>();
            urlParams.put("category", category);
            url = this.addQueryParams(url, urlParams);
        }

        // Send request
        HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).build();
        HttpClient client = HttpClient.newHttpClient();
        String joke = client.send(request, HttpResponse.BodyHandlers.ofString()).body();

        if (name != null && !name.isEmpty()) {
            joke = joke.replaceAll("Chuck Norris", name);
        }

        Gson gson = new Gson();
        Joke jokeObj = gson.fromJson(joke, Joke.class);
        return jokeObj;
    }

    public String[] getCategories() throws IOException, InterruptedException, URISyntaxException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = new URI(this.categoriesURL);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).build();

        String responseBody = client.send(request, HttpResponse.BodyHandlers.ofString()).body();

        Gson gson = new Gson();
        String[] categories = gson.fromJson(responseBody, String[].class);
        return categories;
    }
}
