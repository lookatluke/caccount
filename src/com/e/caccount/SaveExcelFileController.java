/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.e.caccount;

import com.e.caccount.Utils.XMLtoEXCEL;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author trito
 */
public class SaveExcelFileController implements Initializable {

    private ResourceBundle bundle;
    @FXML
    private Label bundles4;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bundle = rb;
        bundles4.setText(bundle.getString("key65"));
        // TODO
    }

    ////////////////////////////////////////////////////////////////////////////
    //////////////////               SET MAIN                //////////////////////
    ////////////////////////////////////////////////////////////////////////////
    private CAccounting Main;

    public void setMain(CAccounting main) {
        this.Main = main;
    }

    @FXML
    RadioButton hori_btn;
    @FXML
    RadioButton verti_btn;
    @FXML
    Button create_btn;

    private File file;

    public void SaveAsExcelFile() {
        Main.getSaveAsExcelStage().hide();
        file = Main.getPersonFilePath();
        if (file != null) {
            Main.saveUserDataToXMLfile(file);
        } else {
            showAlert(bundle.getString("key107"), bundle.getString("key4"),bundle.getString("key108"));
            SaveASData();
        }

        if (file != null) {
            if (verti_btn.isSelected()) {
                SaveExcelVertically();
            } else if (hori_btn.isSelected()) {
                SaveExcelHorizontally();
            }
        } else {
            showAlert( bundle.getString("key109"),  bundle.getString("key4"), bundle.getString("key108"));
        }
        Main.getSaveAsExcelStage().close();
    }

    public void SaveExcelVertically() {
        try {
            String userDir = System.getProperty("user.dir");
            String exefile = userDir + "\\xmltoexcel2.exe";
            File saeFile = new File(exefile);
            if (!saeFile.exists()) {
                showAlert(saeFile.getAbsolutePath().toString() + bundle.getString("key110"),  bundle.getString("key111"), bundle.getString("key108"));
                //gets program.exe from inside the JAR file as an input stream
                InputStream is = CAccounting.class.getResource("externalApp/xmltoexcel2.exe").openStream();
                //sets the output stream to a system folder
                OutputStream os = new FileOutputStream(exefile);

                //2048 here is just my preference
                byte[] b = new byte[9030323];
                int length;

                while ((length = is.read(b)) != -1) {
                    os.write(b, 0, length);
                }

                is.close();
                os.close();
            }
            showAlert(bundle.getString("key112"), bundle.getString("key4"), bundle.getString("key108"));
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File savePath = directoryChooser.showDialog(Main.getPrimaryStage());

            if (savePath != null) {
                Runtime.getRuntime().exec(new String[]{exefile, file.getAbsolutePath().toString(), savePath.getAbsolutePath().toString()});
            } else {
                showAlert(bundle.getString("key109"), bundle.getString("key4"), bundle.getString("key108"));
            }
        } catch (Exception ex) {
            showAlert(ex.toString(), "nuul", "null");
            Logger.getLogger(RootLayoutController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void SaveExcelHorizontally() {
        showAlert(bundle.getString("key112"), bundle.getString("key4"), bundle.getString("key108"));
        // Save Excel
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel files (*.xls)", new String[]{"*.xls"});
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(Main.getPrimaryStage());

        if (file != null) {
            if (!file.getPath().endsWith(".xls")) {
                file = new File(file.getPath() + ".xls");
            }
            XMLtoEXCEL xmlToexcel = new XMLtoEXCEL(file);
            xmlToexcel.startXMLtoEXCEL();
        }
    }

    public void SaveASData() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", new String[]{"*.xml"});
        fileChooser.getExtensionFilters().add(extFilter);
        file = fileChooser.showSaveDialog(Main.getPrimaryStage());

        if (file != null) {
            if (!file.getPath().endsWith(".xml")) {
                file = new File(file.getPath() + ".xml");
            }
            Main.saveUserDataToXMLfile(file);
        }
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
