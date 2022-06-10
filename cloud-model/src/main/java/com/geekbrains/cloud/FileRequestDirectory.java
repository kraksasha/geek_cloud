package com.geekbrains.cloud;

import lombok.Data;

@Data
public class FileRequestDirectory implements CloudMessage{
    private final String name;
}
