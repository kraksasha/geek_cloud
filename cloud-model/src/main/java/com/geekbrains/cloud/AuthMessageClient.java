package com.geekbrains.cloud;

import lombok.Data;

@Data
public class AuthMessageClient implements CloudMessage{
    private final String name;
}
