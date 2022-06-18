package com.geekbrains.cloud.AuthMessage;

import com.geekbrains.cloud.CloudMessage;
import lombok.Data;

@Data
public class AuthMessage implements CloudMessage {
    private final String name;
}
