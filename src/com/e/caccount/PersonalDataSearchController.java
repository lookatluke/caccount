/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.e.caccount;

import com.e.caccount.CAccounting;
import com.e.caccount.Model.userModel;
import com.e.caccount.Utils.SearchlData;
import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

/**
 * FXML Controller class
 *
 * @author trito
 */
public class PersonalDataSearchController implements Initializable {

    private File url;

    @FXML
    private TextField name;
    @FXML
    private TableView<userModel> uTable;
    @FXML
    private TableColumn<userModel, String> unameColumn;
    @FXML
    private TableColumn<userModel, String> utypeColumn;
    @FXML
    private TableColumn<userModel, Number> uamountColumn;
    @FXML
    private TableColumn<userModel, String> timestampColumn;
    @FXML
    private Button folder;
    @FXML
    private Button search;
    @FXML
    private ComboBox comboyears;

    private ObservableList<userModel> uList = FXCollections.observableArrayList();
    private ObservableList<Integer> collectyears = FXCollections.observableArrayList();

    private List<userModel> receivedList = new ArrayList<>();
    private SearchlData searchPersonalData;

    private ResourceBundle bundle;

    private static final String DB_PATH = "DB/";

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url_initialize, ResourceBundle rb) {
        bundle = rb;
        unameColumn.setText(bundle.getString("key25"));

        collectyears.clear();
        setYears();

        if (searchPersonalData == null) {
            searchPersonalData = new SearchlData();
        }
        searchPersonalData.start();
        // 테이블 세팅
        uTable.setItems(uList);
        uTable.setEditable(false);
        unameColumn.setCellValueFactory(value -> value.getValue().NameProperty());
        utypeColumn.setCellValueFactory(value -> value.getValue().TypeProperty());
        uamountColumn.setCellValueFactory(value -> value.getValue().AmountProperty());
        uamountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
        timestampColumn.setCellValueFactory(value -> value.getValue().DateTimeProperty());

        url = new File(DB_PATH);
    }

    public void setYears() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();

        for (int i = 0; i < 100; i++) {
            collectyears.add(year - i);
        }

        comboyears.setItems(collectyears);
    }
    ////////////////////////////////////////////////////////////////////////////
    //////////////////               SET MAIN                //////////////////////
    ////////////////////////////////////////////////////////////////////////////
    private CAccounting Main;

    public void setMain(CAccounting main) {
        this.Main = main;
        Main.getPersonalDataSearchStage().setOnCloseRequest((event) -> {
            if (searchPersonalData != null) {
                searchPersonalData.interrupt();
            }
        });
    }

    @FXML
    public void selectFolder() {
//        DirectoryChooser directoryChooser = new DirectoryChooser();
//        url = directoryChooser.showDialog(Main.getPrimaryStage());

    }

    @FXML
    public void startFind() {
        if (url != null) {
            String user_name = name.getText().toString();

            if (!user_name.equals("") && user_name != null) {
                if (!comboyears.getSelectionModel().isEmpty()) {
                    String selectedyear = comboyears.getSelectionModel().getSelectedItem().toString();

                    Main.startProgressIndicator();
                    getDataList(user_name, selectedyear);
//                    uList.clear();
                    uList.addAll(receivedList);
                    Main.stopProgressIndicator();
                } else {
                    showAlert("해당년도를 선택해주세요", "오류", "선택오류");
                }
            } else {
                showAlert(bundle.getString("key97"), bundle.getString("key98"), bundle.getString("key99"));
            }

        } else {
            showAlert(bundle.getString("key100"), bundle.getString("key98"), bundle.getString("key99"));
        }

    }

    private void getDataList(String user_name, String selectedyear) {
        receivedList.clear();
        receivedList = searchPersonalData.getPersonalHistory(url, user_name, selectedyear);
    }

    public void showAlert(String message, String title, String headerTxt) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.YES);
        alert.setTitle(title);
        alert.setHeaderText(headerTxt);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(CAccounting.class.getResourceAsStream("images/_stageIcon.png")));
        alert.showAndWait();
    }
}
