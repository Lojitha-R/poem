package com.example.poem.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.stereotype.Service;

@Service
public class PoemService {

    public void savePoem(String author, String title, String content) {

        try {
            File folder = new File("poems");

            if (!folder.exists()) {
                folder.mkdir();
            }

            String safeTitle = title.replaceAll("[^a-zA-Z0-9]", "_");

            String fileName = "poems/" + safeTitle + ".txt";

            FileWriter writer = new FileWriter(fileName);
            writer.write("Author: " + author + "\n");
            writer.write("Title: " + title + "\n\n");
            writer.write(content);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}