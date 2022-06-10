module com.geeekbrains.cloud.cloudaplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.geekbrains.cloud.model;
    requires io.netty.codec;

    opens com.geeekbrains.cloud.cloudaplication to javafx.fxml;
    exports com.geeekbrains.cloud.cloudaplication;
}