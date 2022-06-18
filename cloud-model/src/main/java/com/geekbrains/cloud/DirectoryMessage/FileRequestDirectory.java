package com.geekbrains.cloud.DirectoryMessage;

import com.geekbrains.cloud.CloudMessage;
import lombok.Data;

@Data
public class FileRequestDirectory implements CloudMessage {
    private final String name;
}
