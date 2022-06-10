package com.geekbrains.cloud;

import lombok.Data;

import java.nio.file.Path;

@Data
public class DirectoryMessage implements CloudMessage {
    private final String name;

    public DirectoryMessage(Path path){
        name = path.getFileName().toString();
    }
}
