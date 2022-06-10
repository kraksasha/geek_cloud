package com.geekbrains.cloud;

import lombok.Data;

@Data
public class FileRequestServ implements CloudMessage{
    private final String name;
}
