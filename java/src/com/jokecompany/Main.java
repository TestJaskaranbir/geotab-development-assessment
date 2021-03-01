package com.jokecompany;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;

import com.jokecompany.io.ConsoleWriter;
import com.jokecompany.io.IWriter;
import com.jokecompany.joke.IJokeService;
import com.jokecompany.joke.JokeService;
import com.jokecompany.name.INameService;
import com.jokecompany.name.NameService;

public class Main {

    public static void main(String[] args) throws InterruptedException, IOException, URISyntaxException {
        Reader reader = new InputStreamReader(System.in);
        IWriter writer = new ConsoleWriter();
        IJokeService jokeService = new JokeService("https://api.chucknorris.io/jokes/");
        INameService nameService = new NameService("https://www.names.privserv.com/api/");

        App app = new App(reader, writer, jokeService, nameService);
        app.Run();
    }
}
