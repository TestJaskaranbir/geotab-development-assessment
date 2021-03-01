package com.jokecompany.name;

import java.io.IOException;
import java.net.URISyntaxException;

public interface INameService {
    public Name getRandomName() throws URISyntaxException, IOException, InterruptedException;
}
