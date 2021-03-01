package com.jokecompany.joke;

import java.io.IOException;
import java.net.URISyntaxException;

public interface IJokeService {
    public Joke getRandomJoke(String name, String category)
            throws URISyntaxException, IOException, InterruptedException;

    public Joke getRandomJoke(String name) throws URISyntaxException, IOException, InterruptedException;

    public String[] getCategories() throws IOException, InterruptedException, URISyntaxException;
}
