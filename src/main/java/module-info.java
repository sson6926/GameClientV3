module com.demofx.game_client_v3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires javafx.media;

    exports com.ltm.game_client_v3;
    exports com.ltm.game_client_v3.controller;
    exports com.ltm.game_client_v3.views;

    opens com.ltm.game_client_v3.views to javafx.fxml;
    opens com.ltm.game_client_v3.controller to javafx.fxml;
}