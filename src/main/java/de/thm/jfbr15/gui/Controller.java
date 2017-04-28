package de.thm.jfbr15.gui;

import de.jensd.fx.glyphs.GlyphIcon;
import de.thm.jfbr15.model.Database;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class Controller {

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

    private boolean isConnected() {
        return database != null;
    }
    public void quit(){
        System.exit(0);
    }
    public void toggleConnect(){
        if(!isConnected()){
            try {
                database = new Database(connectionURLField.getText(), userField.getText(), passwordField.getText());
                dbIcon.setFill(Color.LIMEGREEN);
                toggleConnectButton.setText("Disconnect");
                executeQueryButton.setDisable(false);
                data = FXCollections.observableArrayList();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                database.close();
                database = null;
                dbIcon.setFill(Color.BLACK);
                toggleConnectButton.setText("Connect");
                executeQueryButton.setDisable(true);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public void executeQuery(){
        try {
            ResultSet resultSet = database.sendQuery(sqlInputArea.getText());
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            table.getItems().clear();
            table.getColumns().clear();
            /**********************************
             * TABLE COLUMN ADDED DYNAMICALLY *
             **********************************/
            for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                final int j = i-1;
                String columnName = resultSetMetaData.getColumnName(i);
                TableColumn column = new TableColumn(columnName);
                column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });
                table.getColumns().add(column);
                System.out.println("Column ["+i+"] ");

            }
            /********************************
             * Data added to ObservableList *
             ********************************/
            while (resultSet.next()){
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                    String columnValue = resultSet.getString(resultSetMetaData.getColumnLabel(i));
                    row.add(columnValue);
                }
                System.out.println("Row added "+row );

                data.add(row);
            }
            //FINALLY ADDED TO TableView
            table.setItems(data);
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("SQLException");
            alert.setHeaderText(e.getLocalizedMessage());
            alert.showAndWait();
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
    }

}
