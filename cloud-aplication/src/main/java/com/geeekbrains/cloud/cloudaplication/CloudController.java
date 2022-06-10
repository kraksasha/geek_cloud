package com.geeekbrains.cloud.cloudaplication;

import com.geekbrains.cloud.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
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

    public void readLoop(){
        try {
            while (true){
                CloudMessage message = network.read();
                if (message instanceof ListFiles listFiles) {
                    Platform.runLater(() -> {
                        serverView.getItems().clear();
                        serverView.getItems().addAll(listFiles.getFiles());
                    });
                } else if (message instanceof FileMessage fileMessage) {
                    Path current = Path.of(homeDir).resolve(fileMessage.getName());
                    Files.write(current, fileMessage.getData());
                    Platform.runLater(() -> {
                        clientView.getItems().clear();
                        clientView.getItems().addAll(getFiles(homeDir));
                    });
                } else if (message instanceof DirectoryMessage fileMessage) {
                    Path current = Path.of(homeDir).resolve(fileMessage.getName());
                    homeDir = current.toString();
                    Platform.runLater(() -> {
                        clientView.getItems().clear();
                        clientView.getItems().addAll(getFiles(homeDir));
                    });
                } else if (message instanceof  DirectoryMessageBack directoryMessageBack){
                    homeDir = Path.of(homeDir).getParent().toString();
                    Platform.runLater(() -> {
                        clientView.getItems().clear();
                        clientView.getItems().addAll(getFiles(homeDir));
                    });
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
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
        String file = clientView.getSelectionModel().getSelectedItem();
        network.write(new FileMessage(Path.of(homeDir).resolve(file)));
    }

    public void download(ActionEvent actionEvent) throws Exception {
        String file = serverView.getSelectionModel().getSelectedItem();
        network.write(new FileRequest(file));
    }

    public void goToNextDirectory(ActionEvent actionEvent) throws IOException {
        String file = clientView.getSelectionModel().getSelectedItem();
        network.write(new FileRequestDirectory(file));
    }

    public void goBackFromDirectory(ActionEvent actionEvent) throws IOException {
        network.write(new FileRequestDirectoryBack());
    }

    public void goToNextDirectoryServ(ActionEvent actionEvent) throws IOException {
        String file = serverView.getSelectionModel().getSelectedItem();
        network.write(new FileRequestServ(file));
    }

    public void goBackFromDirectoryServ(ActionEvent actionEvent) throws IOException {
        network.write(new FileRequestServBack());
    }
}