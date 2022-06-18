package com.geekbrains.cloud.FileRequestServ;

import com.geekbrains.cloud.CloudMessage;
import lombok.Data;

@Data
public class FileRequestServ implements CloudMessage {
    private final String name;
}
