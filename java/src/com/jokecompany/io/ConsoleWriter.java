package com.jokecompany.io;

public class ConsoleWriter implements IWriter {
    public void write(String s) {
        System.out.println(s);
    }
}
