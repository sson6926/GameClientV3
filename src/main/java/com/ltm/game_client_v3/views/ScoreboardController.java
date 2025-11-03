package com.ltm.game_client_v3.views;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.*;
import javafx.beans.property.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class ScoreboardController {

    @FXML
    private TableView<PlayerScore> scoreTable;
    @FXML
    private TableColumn<PlayerScore, Number> colRank;
    @FXML
    private TableColumn<PlayerScore, String> colNickname;
    @FXML
    private TableColumn<PlayerScore, Number> colWins;
    @FXML
    private TableColumn<PlayerScore, Number> colMatches;
    @FXML
    private TableColumn<PlayerScore, Number> colScore;

    private final ObservableList<PlayerScore> data = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colRank.setCellValueFactory(c -> c.getValue().rankProperty());
        colNickname.setCellValueFactory(c -> c.getValue().nicknameProperty());
        colWins.setCellValueFactory(c -> c.getValue().winsProperty());
        colMatches.setCellValueFactory(c -> c.getValue().matchesProperty());
        colScore.setCellValueFactory(c -> c.getValue().scoreProperty());

        scoreTable.setItems(data);
    }

    /** 📥 Nhận dữ liệu từ controller khác */
    public void setRanking(JSONArray rankingArray) {
        data.clear();
        for (int i = 0; i < rankingArray.length(); i++) {
            JSONObject o = rankingArray.getJSONObject(i);
            data.add(new PlayerScore(
                    i + 1,
                    o.optString("nickname"),
                    o.optInt("totalWins"),
                    o.optInt("totalMatches"),
                    o.optInt("totalScore")
            ));
        }
    }

    @FXML
    private void onClose() {
        Stage stage = (Stage) scoreTable.getScene().getWindow();
        stage.close();
    }

    // Model lớp nhỏ
    public static class PlayerScore {
        private final IntegerProperty rank;
        private final StringProperty nickname;
        private final IntegerProperty wins;
        private final IntegerProperty matches;
        private final IntegerProperty score;

        public PlayerScore(int rank, String nickname, int wins, int matches, int score) {
            this.rank = new SimpleIntegerProperty(rank);
            this.nickname = new SimpleStringProperty(nickname);
            this.wins = new SimpleIntegerProperty(wins);
            this.matches = new SimpleIntegerProperty(matches);
            this.score = new SimpleIntegerProperty(score);
        }

        public IntegerProperty rankProperty() { return rank; }
        public StringProperty nicknameProperty() { return nickname; }
        public IntegerProperty winsProperty() { return wins; }
        public IntegerProperty matchesProperty() { return matches; }
        public IntegerProperty scoreProperty() { return score; }
    }
}