/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.e.caccount;

import com.e.caccount.Model.Person;
import com.e.caccount.Utils.CalendarUtil;
import com.e.caccount.Utils.FileUtil;
import com.e.caccount.Utils.NetworkConnection;
import com.e.caccount.Utils.QRcodeGenerator;
import com.e.caccount.Utils.XMLtoEXCEL;
import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author trito
 */
public class RootLayoutController implements Initializable {

    ////////////////////////////////////////////////////////////////////////////
    ////////////////// VARIABLES //////////////////////
    ////////////////////////////////////////////////////////////////////////////
    private NetworkConnection networkConnection = NetworkConnection.getInstance();
    private QRcodeGenerator qRcodeGenerator = new QRcodeGenerator();
    private Person person = Person.getInstance();

    ////////////////////////////////////////////////////////////////////////////
    ////////////////// INITIATION //////////////////////
    ////////////////////////////////////////////////////////////////////////////
    private ResourceBundle bundle;

    @FXML
    private Menu bundles1;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bundle = rb;
        bundles1.setText(bundle.getString("key1"));
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////// SET MAIN //////////////////////
    ////////////////////////////////////////////////////////////////////////////
    private CAccounting Main;

    public void setMain(CAccounting main) {
        this.Main = main;
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////// MENU ITEM METHOS //////////////////////
    ////////////////////////////////////////////////////////////////////////////
    private static final String DB_PATH = "DB/";

    @FXML
    public void handleSave() {
        new FileUtil().DirectoriesCheck(DB_PATH);
        File personFile = Main.getPersonFilePath();
        if (personFile != null) {
            Main.saveUserDataToXMLfile(personFile);
        } else {
            String path = DB_PATH + new CalendarUtil().getFileName() + ".xml";
            System.out.println(path);
            File file = new File(path);
            Main.saveUserDataToXMLfile(file);
        }
    }

    // SAVE AS...
    public void SaveASData() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)",
                new String[] { "*.xml" });
        fileChooser.getExtensionFilters().add(extFilter);

        String userDirectoryString = System.getProperty("user.home");
        File userDirectory = new File(userDirectoryString);
        if (!new FileUtil().DirectoriesCheck(DB_PATH)) {
            userDirectory = new File(DB_PATH);
        } else {
            if (!userDirectory.canRead()) {
                userDirectory = new File("c:/");
            }
        }
        fileChooser.setInitialDirectory(userDirectory);

        File file = fileChooser.showSaveDialog(Main.getPrimaryStage());

        if (file != null) {
            if (!file.getPath().endsWith(".xml")) {
                file = new File(file.getPath() + ".xml");
            }
            Main.saveUserDataToXMLfile(file);
        }
    }

    public void LoadData() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)",
                new String[] { "*.xml" });
        fileChooser.getExtensionFilters().add(extFilter);

        String userDirectoryString = System.getProperty("user.home");
        File userDirectory = new File(userDirectoryString);
        if (!new FileUtil().DirectoriesCheck(DB_PATH)) {
            userDirectory = new File(DB_PATH);
        } else {
            if (!userDirectory.canRead()) {
                userDirectory = new File("c:/");
            }
        }
        fileChooser.setInitialDirectory(userDirectory);

        File file = fileChooser.showOpenDialog(Main.getPrimaryStage());

        if (file != null) {
            Main.loadUserDataFromXMLfile(file);
        }
    }

    public void ExitProgram() {
        Main.TerminateApplication();
    }

    public void SaveFileAsExcel() {
        File file_save = Main.getPersonFilePath();
        if (file_save != null) {
            if (file_save.exists()) {
                handleSave();
                // Save Excel
                FileChooser fileChooser = new FileChooser();
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel files (*.xls)",
                        new String[] { "*.xls" });
                fileChooser.getExtensionFilters().add(extFilter);
                File file = fileChooser.showSaveDialog(Main.getPrimaryStage());

                if (file != null) {
                    if (!file.getPath().endsWith(".xls")) {
                        file = new File(file.getPath() + ".xls");
                    }
                    XMLtoEXCEL xmlToexcel = new XMLtoEXCEL(file);
                    xmlToexcel.startXMLtoEXCEL();
                }
            } else {
                showAlert(bundle.getString("key114"), bundle.getString("key98"), bundle.getString("key115"));
            }
        } else {
            showAlert(bundle.getString("key114"), bundle.getString("key98"), bundle.getString("key115"));
        }
    }

    public void OpenServer() {
        if (networkConnection.getServerRunning()) {
            showAlert(this.bundle.getString("key82"), this.bundle.getString("key8"), this.bundle.getString("key83"));
        } else {
            networkConnection.openServer();
        }
    }

    public void CloseServer() {
        if (networkConnection.getServerRunning()) {
            networkConnection.closeServer();
        } else {
            showAlert(this.bundle.getString("key84"), this.bundle.getString("key8"), this.bundle.getString("key85"));
        }
    }

    public void InsertMessiveData() {
        Main.openMassiveDataInsert();
    }

    public void OpenQRcodeMaker() {
        Main.openQRCodemaker();
    }

    public void OpenServerQRcodeImage() {
        if (networkConnection.getServerRunning()) {
            //
        } else {
            showAlert(this.bundle.getString("key86"), this.bundle.getString("key17"), this.bundle.getString("key73"));
        }
    }

    public void OpenClientSwichter() {
        if (networkConnection.getClientRunning()) {
            showAlert("네트워크가 이미 사용중입니다.\n클라이언트 모드를 실행할 수 없습니다.", "서버", "네트워크 오류");
        } else {
            Main.openClientMode();
            Main.getPrimaryStage().hide();
            networkConnection.StartNewClientThread();
        }
    }

    public void CloseClientSwitcher() {
        if (networkConnection.getClientRunning()) {
            showAlert("클라이언트 모드를 종료합니다.", "종료", "클라이언드 모드");
            networkConnection.closeClientModeStage();
        } else {
            showAlert("클라이언트 모드가 사용중이지 않습니다.\n클라이언트 모드를 종료 할 수 없습니다.", "실행", "클라이언드 모드");
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

    public void SaveOneTypeToExcel() {
        Main.openSelectOneTypeToExcel();
    }

    public void startOver() {
        // YES NO 입력에 따라 다르게 실행
        Main.StartNewFile();
    }

    public void openSendEmail() {
        Main.openSendEmail();
    }

    public void openReorderPage() {
        Main.openReorderStage();
    }

    public void openPersonalDataSearchPage() {
        Main.openPersonalDataSearchStage();
    }

    public void openYearEndTaxPage() {
        // Main.openYearEndTaxStage();
    }

    public void changeLanguage_en() {
        Main.changeLanguageAndReOpenStages(new Locale("en", "EN"));
        Main.setLanguagePreference(new Locale("en", "EN"));
    }

    public void changeLanguage_kr() {
        Main.changeLanguageAndReOpenStages(new Locale("kr", "KR"));
        Main.setLanguagePreference(new Locale("kr", "KR"));
    }
}
