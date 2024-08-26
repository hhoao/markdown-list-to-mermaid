package com.hhoa;


import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * ${NAME}
 *
 * @author hhoa
 * @since 2024/8/24
 **/

public class Main {
    public static void main(String[] args) throws IOException {
        MarkdownListUtils docs = new MarkdownListUtils();
        byte[] bytes = Files.readAllBytes(Paths.get(Main.class.getResource("/list_example.md").getPath()));
        String mermaid = docs.convertToMermaid(new String(bytes));
        OutputStream outputStream = Files.newOutputStream(Paths.get("test.md"));
        outputStream.write(mermaid.getBytes());
        outputStream.flush();
        outputStream.close();
    }
}
