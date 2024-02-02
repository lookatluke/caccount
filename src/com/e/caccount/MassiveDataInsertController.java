/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.e.caccount;

import com.e.caccount.CAccounting;
import com.e.caccount.Model.Person;
import com.e.caccount.Model.UserData;
import com.e.caccount.Utils.BarcodeReaderDecoder;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author trito
 */
public class MassiveDataInsertController implements Initializable, Observer {

    @FXML
    private TextField amount;
    @FXML
    private TableView<UserData> uTable;
    @FXML
    private TableColumn<UserData, String> unameColumn;
    @FXML
    private TableColumn<UserData, String> utypeColumn;

    @FXML
    private Button insert;

    private Observable oberverBarcodereaderDecoderReg;
    private BarcodeReaderDecoder barcodereaderDecoder = BarcodeReaderDecoder.getInstance();

    private ObservableList<UserData> uList = FXCollections.observableArrayList();

    private Observable oberverPersonReg;
    private Person person = Person.getInstance();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        registerOberver();
        setNumberOnlyTextField();

        uTable.setItems(uList);
        unameColumn.setCellValueFactory(value -> value.getValue().NameProperty());
        utypeColumn.setCellValueFactory(value -> value.getValue().TypeProperty());

        setTableViewProperties();
    }

    ////////////////////////////////////////////////////////////////////////////
    //////////////////               SET MAIN             //////////////////////
    ////////////////////////////////////////////////////////////////////////////
    private CAccounting Main;

    public void setMain(CAccounting main) {
        this.Main = main;
    }

    public void registerOberver() {
        oberverPersonReg = Person.getInstance();
        oberverPersonReg.addObserver(this);

        oberverBarcodereaderDecoderReg = BarcodeReaderDecoder.getInstance();
        oberverBarcodereaderDecoderReg.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof BarcodeReaderDecoder) {
//            switch (arg.toString()) {
//                case "ENTER":
            insertDecodedString(arg.toString());
//                    break;
//            }
        }
    }

    private void insertDecodedString(String decodedStr) {
//        String[] dStr = barcodereaderDecoder.getDecodedStrings();
        String[] dStr = barcodereaderDecoder.splitComma(decodedStr);
        System.out.println(dStr);

        Platform.runLater(() -> {
            if (dStr != null) {
                boolean check = false;
                for (int i = 0; uList.size() > i; i++) {
                    if (dStr[0].equals(uList.get(i).getName())
                            && dStr[1].equals(uList.get(i).getType())) {
                        check = true;
                        break;
                    }
                }

                if (!check) {
                    UserData uData = new UserData(dStr[0], dStr[1], 0);
                    uList.add(uData);
                }
            }
        });

        barcodereaderDecoder.setDefaultDecodedString();
    }

    public void setTableViewProperties() {
        uTable.setEditable(true);
        uTable.getSelectionModel().setCellSelectionEnabled(true);
        uTable.prefWidthProperty().bind(unameColumn.widthProperty().add(utypeColumn.widthProperty()));
        setTableRightClickMenu(uTable);
    }

    //삭제부터 해보기
    public void setTableRightClickMenu(TableView<UserData> tempTable) {
        tempTable.setRowFactory(new Callback<TableView<UserData>, TableRow<UserData>>() {
            @Override
            public TableRow<UserData> call(TableView<UserData> tableView) {
                final TableRow<UserData> row = new TableRow<>();
                final ContextMenu contextMenu = new ContextMenu();
                final MenuItem removeMenuItem = new MenuItem("삭제");
                removeMenuItem.setOnAction((event) -> {
                    tempTable.getItems().remove(row.getItem()); //ListData is automatically deleted when the table cell is deleted.
                    uList.remove(row.getItem());
                });
                contextMenu.getItems().add(removeMenuItem);

                // Set context menu on row, but use a binding to make it only show for non-empty rows:  
                row.contextMenuProperty().bind(
                        Bindings.when(row.emptyProperty())
                                .then((ContextMenu) null)
                                .otherwise(contextMenu)
                );
                return row;
            }
        });
    }

    public void insertData() {
        if (checkAmount()) {
            for (UserData data : uList) {
                person.setUserData(data.getName(), data.getType(), Integer.valueOf(amount.getText()));
            }

            uList.clear();
            amount.clear();
        }
    }

    public boolean checkAmount() {
        if (amount.getText().equals("") || amount.getText() == null) {
            return false;
        } else {
            return true;
        }
    }

    public void setNumberOnlyTextField() {
        amount.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,9}([\\.]\\d{0,4})?")) {
                    amount.setText(oldValue);
                }
            }
        });
    }

}
