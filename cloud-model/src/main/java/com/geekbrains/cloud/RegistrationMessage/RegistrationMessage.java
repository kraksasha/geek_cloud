package com.geekbrains.cloud.RegistrationMessage;

import com.geekbrains.cloud.CloudMessage;
import lombok.Data;

@Data
public class RegistrationMessage implements CloudMessage {
    private final String nickname;
    private final String login;
    private final String password;
}
