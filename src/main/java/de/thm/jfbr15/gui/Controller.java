package de.thm.jfbr15.gui;

import de.jensd.fx.glyphs.GlyphIcon;
import de.thm.jfbr15.model.Database;
import de.thm.jfbr15.model.Result;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

public class Controller {
    @FXML
    private Node rootView;
    @FXML
    private TextField connectionURLField;
    @FXML
    private TextField userField;
    @FXML
    private TextField passwordField;
    @FXML
    private GlyphIcon dbIcon;
    @FXML
    private Button toggleConnectButton;
    @FXML
    private Button executeQueryButton;
    @FXML
    private TableView table;
    @FXML
    private TextArea sqlInputArea;

    private Database database;
    private ObservableList<ObservableList> data;
    private FileChooser fileChooser;
    private boolean isConnected() {
        return database != null;
    }
    public void quit(){
        try {
            database.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
    public void toggleConnect(){
        try {
            if(!isConnected()){
                    database = new Database(connectionURLField.getText(), userField.getText(), passwordField.getText());
                    dbIcon.setFill(Color.LIMEGREEN);
                    toggleConnectButton.setText("Disconnect");
                    executeQueryButton.setDisable(false);
                    data = FXCollections.observableArrayList();
            } else {
                    database.close();
                    database = null;
                    dbIcon.setFill(Color.BLACK);
                    toggleConnectButton.setText("Connect");
                    executeQueryButton.setDisable(true);
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setHeaderText(e.getLocalizedMessage());
            alert.showAndWait();
        }
    }
    public void executeQuery(){
        try {
            Result result = database.sendQuery(sqlInputArea.getText());

            // CLEAN DATABASE
            table.getItems().clear();
            table.getColumns().clear();

            /**********************************
             * TABLE COLUMN ADDED DYNAMICALLY *
             **********************************/
            for (String columnName : result.getColumnNames()) {
                TableColumn column = new TableColumn(columnName);
                int index = result.getColumnNames().indexOf(columnName);
                column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(index).toString());
                    }
                });
                table.getColumns().add(column);
            }

            /********************************
             * Data added to ObservableList *
             ********************************/
            for(List<String> row : result.getData()){
                ObservableList<String> observableRow = FXCollections.observableArrayList();
                observableRow.addAll(row);
                data.add(observableRow);
            }
            table.setItems(data);

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("SQL Error");
            alert.setHeaderText(e.getLocalizedMessage());
            alert.showAndWait();
        }
    }
    public void openFile(){
        File file = fileChooser.showOpenDialog(rootView.getScene().getWindow());
        try {
            String fileContent = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            sqlInputArea.setText(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void saveFile(){
        File file = fileChooser.showSaveDialog(rootView.getScene().getWindow());
        String fileContent = sqlInputArea.getText();
        try {
            FileUtils.writeStringToFile(file, fileContent, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        boolean debugging = true;
        if(debugging){
            connectionURLField.setText("jdbc:postgresql://localhost:5432/postgres");
            userField.setText("postgres");
            passwordField.setText("1234");
            sqlInputArea.setText("SELECT * FROM BOOKS");
        }
        fileChooser = new FileChooser();
        fileChooser.setTitle("View Pictures");
        fileChooser.setInitialDirectory(
                FileUtils.getUserDirectory()
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("SQL Files", "*.sql"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
    }
}
