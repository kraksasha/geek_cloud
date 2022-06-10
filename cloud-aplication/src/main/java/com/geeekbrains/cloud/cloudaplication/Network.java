package com.geeekbrains.cloud.cloudaplication;

import com.geekbrains.cloud.CloudMessage;
import com.geekbrains.cloud.FileMessage;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Network {

    private int port;
    private ObjectDecoderInputStream is;
    private ObjectEncoderOutputStream os;

    public Network(int port) throws IOException {
        this.port = port;
        Socket socket = new Socket("localhost",port);
        os = new ObjectEncoderOutputStream(socket.getOutputStream());
        is = new ObjectDecoderInputStream(socket.getInputStream());
    }

    public CloudMessage read() throws IOException, ClassNotFoundException {
        return (CloudMessage) is.readObject();
    }

    public void write(CloudMessage msg) throws IOException {
        os.writeObject(msg);
        os.flush();
    }
}
