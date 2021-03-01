package com.jokecompany;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.util.stream.Stream;

import com.jokecompany.io.IWriter;
import com.jokecompany.joke.IJokeService;
import com.jokecompany.joke.Joke;
import com.jokecompany.name.INameService;

// This class acts as client, so exceptions are handled here and not propagated.
public class App {
    private final BufferedReader reader;
    private final IWriter writer;

    private final IJokeService jokeService;
    private final INameService nameService;

    public App(Reader reader, IWriter writer, IJokeService jokeService, INameService nameService) {
        this.reader = new BufferedReader(reader);
        this.writer = writer;

        this.jokeService = jokeService;
        this.nameService = nameService;
    }

    public void Run() {
        try {
            processInput();
        }
        catch(IOException e) {
            // Use stdout cos error can originate from writer
            System.out.println("Critical IO-error occurred: " + e.getMessage());
        }
        catch(Exception e) {
            this.writer.write("Encountered a problem: " + e.getMessage());
            this.writer.write("Please try again...");
            this.writer.write("\n---\n");
            Run();
        }
    }

    private void processInput() throws IOException, RuntimeException {
        this.writer.write("\nPress ? to get instructions.");
        String input = this.reader.readLine();
        input = input.toLowerCase();

        String exitKey = "x";

        while (!input.equals(exitKey)) {
            switch (input) {
                case "?":
                    this.writer.write("Press c to get categories");
                    this.writer.write("Press r to get random jokes");
                    this.writer.write("Press x to exit");
                    break;

                case "c":
                    this.writer.write("Fetching categories, please wait...");
                    String categories = this.getJokeCategories();
                    this.writer.write(categories);
                    break;

                case "r":
                    // Get name
                    this.writer.write("Want to use a random name? y/n");
                    String nameChoice = reader.readLine();
                    String name = "";
                    if (nameChoice.equals("y")) {
                        name = this.getRandomName();
                        this.writer.write("Will use the name: " + name);
                    } else {
                        this.writer.write("Enter your name:");
                        name = reader.readLine();
                    }

                    // Get category
                    this.writer.write("Want to specify a category? y/n");
                    String categoryChoice = reader.readLine();
                    String category = "";
                    if (categoryChoice.equals("y")) {
                        this.writer.write("Enter a category;");
                        category = reader.readLine();
                    }

                    // Get jokes
                    this.writer.write("How many jokes do you want? (1-9)");
                    int jokeCount = Integer.parseInt(reader.readLine());
                    while (jokeCount < 1 || jokeCount > 9) {
                        this.writer.write("Number of jokes must be between 1 and 9 (inclusive).");
                        this.writer.write("Please try again.");
                        this.writer.write("How many jokes do you want? (1-9)");
                        jokeCount = Integer.parseInt(reader.readLine());
                    }
                    String jokes = this.getRandomJokes(name, category, jokeCount);
                    this.writer.write(jokes);

                    break;

                default:
                    this.writer.write("Invalid input, please try again.");
            }

            this.writer.write("\n---\n");
            this.writer.write("Enter your input, or press ? to get instructions.");
            input = reader.readLine();
        }
    }

    private String getRandomJokes(String replaceName, String category, int count) throws RuntimeException {
        Joke[] jokes = new Joke[count];
        while (--count >= 0) {
            try {
                jokes[count] = jokeService.getRandomJoke(replaceName, category);
            } catch (URISyntaxException | IOException | InterruptedException e) {
                throw new RuntimeException("Errored fetching joke: " + e.getMessage());
            }
        }

        // Combine all jokes into newline-separated single string
        String mergedJokes = Stream.of(jokes)
                                .filter(j -> j.getValue() != null)
                                .map(j -> j.getValue())
                                .reduce("", (out, j) -> out += j + "\n");

        if (mergedJokes == null || mergedJokes.isEmpty()) {
            throw new RuntimeException(
                "No jokes were fetched, ensure that category specified is correct."
            );
        }
        // Remove last newline char
        return mergedJokes.substring(0, mergedJokes.length() - 1);
    }

    private String getJokeCategories() throws RuntimeException {
        try {
            return String.join(",", jokeService.getCategories());
        } catch (IOException | InterruptedException | URISyntaxException e) {
            throw new RuntimeException("Errored fetching categories: " + e.getMessage());
        }
    }

    private String getRandomName() throws RuntimeException {
        try {
            return this.nameService.getRandomName().getFullname();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException("Errored fetching random-name: " + e.getMessage());
        }
    }
}
