package com.geeekbrains.cloud.cloudaplication;

import com.geekbrains.cloud.*;
import com.geekbrains.cloud.AuthMessage.AuthMessage;
import com.geekbrains.cloud.AuthMessage.AuthMessageBack;
import com.geekbrains.cloud.DirectoryMessage.DirectoryMessage;
import com.geekbrains.cloud.DirectoryMessage.FileRequestDirectory;
import com.geekbrains.cloud.DirectoryMessageBack.DirectoryMessageBack;
import com.geekbrains.cloud.DirectoryMessageBack.FileRequestDirectoryBack;
import com.geekbrains.cloud.FileRequestServ.FileRequestServ;
import com.geekbrains.cloud.FileRequestServ.FileRequestServBack;
import com.geekbrains.cloud.RegistrationMessage.RegistrationMessage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private HBox authPanel;
    @FXML
    private HBox regButtonPanel;
    @FXML
    private VBox formRegistrationPanel;
    @FXML
    private TextField nicknameField, logField, passField;

    private Network network;

    private String homeDir;

    public void setAuthenticated(boolean authenticated) {
        authPanel.setVisible(!authenticated);
        authPanel.setManaged(!authenticated);
        regButtonPanel.setVisible(!authenticated);
        regButtonPanel.setManaged(!authenticated);
        clientView.setVisible(authenticated);
        serverView.setVisible(authenticated);
    }

    public void setRegistration(boolean registration){
        formRegistrationPanel.setVisible(registration);
        formRegistrationPanel.setManaged(registration);
    }


    public void readLoop(){
        try {
            String futureDir = "";
            while (true){
                CloudMessage message = network.read();
                if (message instanceof AuthMessageBack authMessageBack){
                    if (authMessageBack.getName() != null){
                        System.out.println(authMessageBack.getName() + " подключился");
                        setAuthenticated(true);
                        if (futureDir != authMessageBack.getName()){
                            network.write(new AuthMessageClient(authMessageBack.getName()));
                        }
                    }
                }
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
                    homeDir = homeDir + "/" + fileMessage.getName();
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
            setAuthenticated(false);
            setRegistration(false);
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

    public void sendAuth(ActionEvent event) throws IOException {
        network.write(new AuthMessage("auth" + " " + loginField.getText() + " " + passwordField.getText()));
    }

    public void goRegistration(ActionEvent event) {
        setRegistration(true);
    }

    public void registrationEnd(ActionEvent event) throws IOException {
        String name = "'" + nicknameField.getText() + "'";
        String log = "'" + logField.getText() + "'";
        String pass = "'" + passField.getText() + "'";
        network.write(new RegistrationMessage(name,log,pass));
        nicknameField.clear();
        logField.clear();
        passField.clear();
        setRegistration(false);
    }
}