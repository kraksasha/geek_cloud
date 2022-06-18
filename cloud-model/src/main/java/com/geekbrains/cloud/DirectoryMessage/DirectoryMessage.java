package com.geekbrains.cloud.DirectoryMessage;

import com.geekbrains.cloud.CloudMessage;
import lombok.Data;

import java.nio.file.Path;

@Data
public class DirectoryMessage implements CloudMessage {

    private final String name;
}
