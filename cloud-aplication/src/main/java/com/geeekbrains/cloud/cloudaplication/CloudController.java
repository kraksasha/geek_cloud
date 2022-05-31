package com.geeekbrains.cloud.cloudaplication;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class CloudController implements Initializable {

    @FXML
    public ListView<String> clientView;
    @FXML
    public ListView<String> serverView;

    private Network network;

    private String homeDir;

    private byte buf[];

    public void readLoop(){
        try {
            while (true){
                String command = network.readString();
                if (command.equals("#list#")){
                    Platform.runLater(()->serverView.getItems().clear());
                    int len = network.readInt();
                    for (int i = 0; i < len; i++) {
                        String file = network.readString();
                        Platform.runLater(()->serverView.getItems().add(file));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            buf = new byte[256];
            homeDir = System.getProperty("user.home");
            clientView.getItems().clear();
            clientView.getItems().addAll(getFiles(homeDir));
            network = new Network(8189);
            Thread readThread = new Thread(this::readLoop);
            readThread.setDaemon(true);
            readThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> getFiles(String dir){
        String list[] = new File(dir).list();
        assert list != null;
        return Arrays.asList(list);
    }

    public void upload(ActionEvent actionEvent) throws IOException {
        network.getOs().writeUTF("#file#");
        String file = clientView.getSelectionModel().getSelectedItem();
        network.getOs().writeUTF(file);
        File toSend = Path.of(homeDir).resolve(file).toFile();
        network.getOs().writeLong(toSend.length());
        try(FileInputStream fis = new FileInputStream(toSend)){
            while (fis.available() > 0){
                int read = fis.read(buf);
                network.getOs().write(buf,0,read);
            }
        }
        network.getOs().flush();
    }

    public void download(ActionEvent actionEvent) {
    }
}