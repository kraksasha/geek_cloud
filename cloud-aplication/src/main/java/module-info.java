module com.geeekbrains.cloud.cloudaplication {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.geeekbrains.cloud.cloudaplication to javafx.fxml;
    exports com.geeekbrains.cloud.cloudaplication;
}