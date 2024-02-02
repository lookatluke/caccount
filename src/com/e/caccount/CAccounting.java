package com.e.caccount;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.e.caccount.Model.Coins;
import com.e.caccount.Model.Person;
import com.e.caccount.Model.UserData;
import com.e.caccount.Model.UserDataWrapper;
import com.e.caccount.Utils.BarcodeReaderDecoder;
import com.e.caccount.Utils.CalendarUtil;
import com.e.caccount.Utils.FileUtil;
import com.e.caccount.Utils.NetworkConnection;
import com.e.caccount.Utils.ProgressDialog;
import javafx.application.Preloader.ProgressNotification;
import javafx.application.Preloader.StateChangeNotification;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;

/**
 * JavaFX App
 */
public class CAccounting extends Application {

    private static Scene scene;

    private Stage PrimaryStage;
    private BorderPane RootLayout;
    private RootLayoutController rootController;

    private Locale locale;
    private ResourceBundle bundle;

    private Person person = Person.getInstance();
    private BarcodeReaderDecoder barcodereaderDecoder = BarcodeReaderDecoder.getInstance();
    private NetworkConnection networkConnection = NetworkConnection.getInstance();

    private String Title = "C ACCOUNT Ver. 3.0";

    public static void main(String[] args) {
        launch();
    }

    ////////////////////////////////////////////////////////////////////////////
    //////////////////                PRELOADER           //////////////////////
    ////////////////////////////////////////////////////////////////////////////
    BooleanProperty ready = new SimpleBooleanProperty(false);

    private void longStart() {
        //simulate long init in background
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int max = 10;
                for (int i = 1; i <= max; i++) {
                    Thread.sleep(200);
                    // Send progress to preloader
                    notifyPreloader(new ProgressNotification(((double) i) / max));
                }
                // After init is ready, the app is ready to be shown
                // Do this before hiding the preloader stage to prevent the 
                // app from exiting prematurely
                ready.setValue(Boolean.TRUE);

                notifyPreloader(new StateChangeNotification(
                        StateChangeNotification.Type.BEFORE_START));

                return null;
            }
        };
        new Thread(task).start();
    }

    @Override
    public void start(Stage stage) throws IOException {
        longStart();

        this.PrimaryStage = stage;
        loadLanguagePreference();
        bundle = ResourceBundle.getBundle("bundles.MyBundle", locale);

        openRootLayout(locale);

        DefaultTypeSetting();
        setPersonFilePath(null);
        networkConnection.setMain(this);
        dbFolderCheck();
        existFileCheck();
    }

    private static final String DB_PATH = "DB/";

    private void dbFolderCheck() {
        new FileUtil().DirectoriesCheck(DB_PATH);
    }

    private void existFileCheck() {
        String path = DB_PATH + new CalendarUtil().getFileName() + ".xml";
        File file = new File(path);
        if (file.exists()) {
            loadUserDataFromXMLfile(file);
        }
    }

    public void openRootLayout(Locale locale) {
        try {
            FXMLLoader loader = loadFXML("RootLayout");
            RootLayout = (BorderPane) loader.load();
            Scene scene = new Scene(RootLayout);

            rootController = loader.getController();
            rootController.setMain(this);

            openAccountBook(locale);

            PrimaryStage.setScene(scene);
            PrimaryStage.setTitle(Title);
            PrimaryStage.getIcons().add(new Image(CAccounting.class.getResourceAsStream("images/_stageIcon.png")));

            PrimaryStage.show();

            closeAll();
        } catch (IOException ex) {
            // Logger.getLogger(CAccounting.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void openAccountBook(Locale locale) {
        try {
            FXMLLoader loader = loadFXML("AccountBook");
            AnchorPane ACbook = (AnchorPane) loader.load();

            AccountBookController controller = loader.getController();
            controller.setMain(this);

            RootLayout.setCenter(ACbook);
        } catch (IOException ex) {
            // Logger.getLogger(CAccounting.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Stage QRStage;

    public void openQRCodemaker() {
        try {
            QRStage = new Stage();
            FXMLLoader loader = loadFXML("QRcodeMaker");
            AnchorPane page = (AnchorPane) loader.load();
            Scene scene = new Scene(page);

            QRcodeMakerController controller = loader.getController();
            controller.setMain(this);

            QRStage.setScene(scene);
            QRStage.setTitle(Title + " - QR Code Maker");
            QRStage.getIcons().add(new Image(CAccounting.class.getResourceAsStream("images/_stageIcon.png")));
            QRStage.show();
        } catch (IOException ex) {
            // Logger.getLogger(CAccounting.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Stage MassiveDataInsertStage;

    public void openMassiveDataInsert() {
        try {
            MassiveDataInsertStage = new Stage();
            FXMLLoader loader = loadFXML("MassiveDataInsert");
            AnchorPane page = (AnchorPane) loader.load();
            Scene scene = new Scene(page);

            MassiveDataInsertController controller = loader.getController();
            controller.setMain(this);

            MassiveDataInsertStage.setScene(scene);
            MassiveDataInsertStage.setTitle(Title + " - Massive Data Insert");
            MassiveDataInsertStage.getIcons()
                    .add(new Image(CAccounting.class.getResourceAsStream("images/_stageIcon.png")));
            MassiveDataInsertStage.show();
        } catch (IOException ex) {
            // Logger.getLogger(CAccounting.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Stage OneTypeToExcel;

    public void openSelectOneTypeToExcel() {
        try {
            OneTypeToExcel = new Stage();
            FXMLLoader loader = loadFXML("OneTypeToExcel");
            AnchorPane page = (AnchorPane) loader.load();
            Scene scene = new Scene(page);

            OneTypeToExcelController controller = loader.getController();
            controller.setMain(this);

            OneTypeToExcel.setScene(scene);
            OneTypeToExcel.setTitle(Title + " - One type to Excel");
            OneTypeToExcel.getIcons().add(new Image(CAccounting.class.getResourceAsStream("images/_stageIcon.png")));
            OneTypeToExcel.show();
        } catch (IOException ex) {
            // Logger.getLogger(CAccounting.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Stage clientModeStage;

    public void openClientMode() {
        try {
            clientModeStage = new Stage();
            FXMLLoader loader = loadFXML("ClientMode");
            AnchorPane page = (AnchorPane) loader.load();
            Scene scene = new Scene(page);

            ClientModeController controller = loader.getController();
            controller.setMain(this);

            clientModeStage.setScene(scene);
            clientModeStage.initModality(Modality.APPLICATION_MODAL);
            clientModeStage.setTitle(Title + " - Client Mode");
            clientModeStage.getIcons()
                    .add(new Image(CAccounting.class.getResourceAsStream("resources/_stageIcon.png")));
            clientModeStage.show();

            clientModeStage.setOnCloseRequest((EventHandler<WindowEvent>) new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    networkConnection.closeClientModeStage();
                }
            });
        } catch (IOException ex) {
            // Logger.getLogger(CAccounting.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Stage mailStage;

    public void openSendEmail() {
        try {
            mailStage = new Stage();
            FXMLLoader loader = loadFXML("SendEmail");
            AnchorPane page = (AnchorPane) loader.load();
            Scene scene = new Scene(page);

            SendEmailController controller = loader.getController();
            controller.setMain(this, mailStage);

            mailStage.setScene(scene);
            mailStage.initModality(Modality.APPLICATION_MODAL);
            mailStage.setTitle(Title + " - Mail Sender");
            mailStage.getIcons().add(new Image(CAccounting.class.getResourceAsStream("images/_stageIcon.png")));
            mailStage.show();
        } catch (IOException ex) {
            // Logger.getLogger(CAccounting.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Stage reorderStage;

    public void openReorderStage() {
        try {
            reorderStage = new Stage();
            FXMLLoader loader = loadFXML("ExcelOrder");
            AnchorPane page = (AnchorPane) loader.load();
            Scene scene = new Scene(page);

            ExcelOrderController controller = loader.getController();
            controller.setMain(this);

            reorderStage.setScene(scene);
            reorderStage.initModality(Modality.APPLICATION_MODAL);
            reorderStage.setTitle(Title + " - Table reorder");
            reorderStage.getIcons().add(new Image(CAccounting.class.getResourceAsStream("images/_stageIcon.png")));
            reorderStage.show();
        } catch (IOException ex) {
            // Logger.getLogger(CAccounting.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Stage personaldatasearchStage;

    public void openPersonalDataSearchStage() {
        try {
            personaldatasearchStage = new Stage();
            FXMLLoader loader = loadFXML("PersonalDataSearch");
            AnchorPane page = (AnchorPane) loader.load();
            Scene scene = new Scene(page);

            PersonalDataSearchController controller = loader.getController();
            controller.setMain(this);

            personaldatasearchStage.setScene(scene);
            personaldatasearchStage.initModality(Modality.APPLICATION_MODAL);
            personaldatasearchStage.setTitle(Title + " - Personal data search");
            personaldatasearchStage.getIcons()
                    .add(new Image(CAccounting.class.getResourceAsStream("images/_stageIcon.png")));
            personaldatasearchStage.show();
        } catch (IOException ex) {
            // Logger.getLogger(CAccounting.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Stage SaveAsExcelFileStage;

    public void openSaveAsExcelFileStage() {
        try {
            SaveAsExcelFileStage = new Stage();
            FXMLLoader loader = loadFXML("SaveExcelFile");
            AnchorPane page = (AnchorPane) loader.load();
            Scene scene = new Scene(page);

            SaveExcelFileController controller = loader.getController();
            controller.setMain(this);

            SaveAsExcelFileStage.setScene(scene);
            SaveAsExcelFileStage.initModality(Modality.APPLICATION_MODAL);
            SaveAsExcelFileStage.setTitle(Title + " - Save as Excel");
            SaveAsExcelFileStage.getIcons()
                    .add(new Image(CAccounting.class.getResourceAsStream("images/_stageIcon.png")));
            SaveAsExcelFileStage.show();
        } catch (IOException ex) {
            // Logger.getLogger(CAccounting.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Stage getSaveAsExcelStage() {
        return this.SaveAsExcelFileStage;
    }

    public void StartNewFile() {
        person.RemoveAllDATA();
        DefaultTypeSetting();
        setPersonFilePath(null);
    }

    public Stage getClientModeStage() {
        return clientModeStage;
    }

    public Stage getOneTypeToExcelStage() {
        return OneTypeToExcel;
    }

    public Stage getQRcodeStage() {
        return QRStage;
    }

    public Stage getReorderStage() {
        return this.reorderStage;
    }

    public Stage getPersonalDataSearchStage() {
        return this.personaldatasearchStage;
    }

    public void hideQRstage() {
        QRStage.hide();
    }

    public void setSaveButtonAlert() {
        ButtonType OK = new ButtonType(this.bundle.getString("key74"), ButtonBar.ButtonData.OK_DONE);
        ButtonType CANCEL = new ButtonType(this.bundle.getString("key75"), ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert = new Alert(AlertType.WARNING, this.bundle.getString("key76"), OK, CANCEL);
        alert.setHeaderText(this.bundle.getString("key77"));
        alert.setTitle(this.bundle.getString("key78"));
        alert.initOwner(PrimaryStage.getScene().getWindow());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.orElse(CANCEL) == OK) {
            rootController.handleSave();
        } else {
            System.exit(0);
        }
    }

    public void setOverrideAlert(File file) {
        ButtonType OK = new ButtonType(this.bundle.getString("key74"), ButtonBar.ButtonData.OK_DONE);
        ButtonType CANCEL = new ButtonType(this.bundle.getString("key75"), ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert = new Alert(AlertType.WARNING, "[" + file.getName() + "] " + this.bundle.getString("key113"), OK,
                CANCEL);
        alert.setHeaderText(this.bundle.getString("key108"));
        alert.setTitle(this.bundle.getString("key77"));
        alert.initOwner(PrimaryStage.getScene().getWindow());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.orElse(CANCEL) == OK) {
            saveUserDataToXMLfile(file);
        } else {

        }
    }

    public Stage getPrimaryStage() {
        return this.PrimaryStage;
    }

    public void showAlert(String message, String title, String headerTxt) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.YES);
        alert.setTitle(title);
        alert.setHeaderText(headerTxt);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(CAccounting.class.getResourceAsStream("images/_stageIcon.png")));
        alert.showAndWait();
    }

    public void DefaultTypeSetting() {
        String[] TempTypes = {"십일조", "주정헌금", "감사헌금", "선교헌금", "생일감사", "오병이어", "건축헌금", "성단봉사", "일천번제"};
        for (int i = 0; i < TempTypes.length; i++) {
            person.setUserData("NEWTABLECREATION", TempTypes[i], 0);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////// XML FILE CONTROLL //////////////////////
    ////////////////////////////////////////////////////////////////////////////
    public void saveUserDataToXMLfile(File file) {
        List<UserData> tempList = person.getCompliedList();
        // Start Progress
        startProgressIndicator();
        // Load Data
        try {
            JAXBContext context = JAXBContext.newInstance(UserDataWrapper.class);

            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            UserDataWrapper wrapper = new UserDataWrapper();
            wrapper.setUserData(tempList);
            // wrapper.setCoins(person.getCoinsList());

            m.marshal(wrapper, file);

            setPersonFilePath(file);

            // Stop Progress
            stopProgressIndicator();

        } catch (JAXBException ex) {
            // Stop Progress
            stopProgressIndicator();
            // Logger.getLogger(CAccounting.class.getName()).log(Level.SEVERE, "An error
            // occured during the saving", ex);
        }
    }

    public void loadUserDataFromXMLfile(File file) {
        // Start Progress
        startProgressIndicator();
        // Load Data
        try {
            JAXBContext context = JAXBContext.newInstance(UserDataWrapper.class);
            Unmarshaller um = context.createUnmarshaller();

            UserDataWrapper wrapper = (UserDataWrapper) um.unmarshal(file);
            person.RemoveAllDATA();

            setPersonFilePath(file);

            SeperateTypes(wrapper.getUserData());
            // setCoinsAndBills(wrapper.getCoins());
        } catch (JAXBException ex) {
            // Logger.getLogger(CAccounting.class.getName()).log(Level.SEVERE, "An error
            // occured during the loading", ex);
        }
        // Stop Progress
        stopProgressIndicator();
    }

    public void setUserData(final String name, final String type, final Integer amount) {
        Platform.runLater(() -> {
            person.setUserData(name, type, amount);
        });
    }

    public void SeperateTypes(List<UserData> tempList) {
        try {
            person.setUserData(tempList.get(0).getName(), tempList.get(0).getType(), tempList.get(0).getAmount());
            String type = tempList.get(0).getType();

            for (int i = 1; i < tempList.size(); i++) {
                if (tempList.get(i).getType().equals(type)) {
                    setUserData(tempList.get(i).getName(), tempList.get(i).getType(), tempList.get(i).getAmount());
                } else {
                    person.setUserData(tempList.get(i).getName(), tempList.get(i).getType(),
                            tempList.get(i).getAmount());
                    type = tempList.get(i).getType();
                }
            }
        } catch (Exception ex) {
            System.out.println(ex);
            StartNewFile();
        }
    }

    public void setCoinsAndBills(List<Coins> tempList) {
        tempList.stream()
                .forEach(e -> {
                    person.setBaekwon(e.getBaekwon());
                    person.setOhBaekwon(e.getOhBaekwon());
                    person.setChoenwon(e.getChoenwon());
                    person.setOhChoenwon(e.getOhChoenwon());
                    person.setManwon(e.getManwon());
                    person.setOhManwon(e.getOhManwon());
                });
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////// FILE PREFERENCE //////////////////////
    ////////////////////////////////////////////////////////////////////////////
    public File getPersonFilePath() {
        Preferences prefs = Preferences.userNodeForPackage(CAccounting.class);
        String filePath = prefs.get("filePath", null);
        System.out.println("getPersonFilePath : " + filePath);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

    public void setPersonFilePath(File file) {
        Preferences prefs = Preferences.userNodeForPackage(CAccounting.class);
        if (file != null) {
            prefs.put("filePath", file.getPath());
            PrimaryStage.setTitle(Title + " - [ " + file.getName() + " ]");
        } else {
            prefs.remove("filePath");
            PrimaryStage.setTitle(Title);
        }

    }

    public void setLanguagePreference(Locale locale) {
        Preferences prefs = Preferences.userNodeForPackage(CAccounting.class);
        if (locale != null) {
            prefs.put("languagePref", locale.getLanguage());
        } else {
            this.locale = new Locale.Builder().setLanguage("kr").setRegion("KR").build();
        }
    }

    ///////////////////////////////////// Progress Indicator
    ///////////////////////////////////// ////////////////////////////////////
    private ProgressDialog pd = null;
    private boolean tf;

    public void startProgressIndicator() {
        tf = true;

        pd = new ProgressDialog(PrimaryStage.getOwner(), "Processing...");
        pd.addTaskEndNotification(result -> {
            pd = null;
        });

        pd.exec("100", inputParam -> {
            while (tf) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return new Integer(1);
        });
    }

    public void stopProgressIndicator() {
        tf = false;
    }

    private FXMLLoader loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml + ".fxml"));
        fxmlLoader.setResources(bundle);
        return fxmlLoader;
    }

    public void loadLanguagePreference() {
        Preferences prefs = Preferences.userNodeForPackage(CAccounting.class);
        String language = prefs.get("languagePref", null);
        if (language != null) {
            this.locale = new Locale.Builder().setLanguage(language).build();
        } else {
            this.locale = new Locale.Builder().setLanguage("kr").setRegion("KR").build();
        }
    }

    public void closeAll() {
        PrimaryStage.setOnCloseRequest(t -> {
            TerminateApplication();
        });
    }

    public void TerminateApplication() {
        if (networkConnection.getServerRunning()) {
            networkConnection.closeServer();
        }

        barcodereaderDecoder.stop();

        // 강제 세이브해서 Mysql과 자료를 맞추자
        rootController.handleSave();

        // JavaFX 애플리케이션 종료
        Platform.exit();

        // // (선택 사항) 실행 중인 Java 가상 머신도 종료
        System.runFinalization();

        System.exit(0);
    }

    public void changeLanguageAndReOpenStages(Locale locale) {
        this.locale = locale;
        bundle = ResourceBundle.getBundle("bundles.MyBundle", locale);
        openRootLayout(this.locale);
        List<UserData> tempList = person.getCompliedList();
        person.RemoveAllDATA();
        // DefaultTypeSetting();
        SeperateTypes(tempList);
    }

}
