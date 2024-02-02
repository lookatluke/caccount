/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.e.caccount;

import com.e.caccount.Model.Person;
import com.e.caccount.Model.UserData;
import com.e.caccount.Utils.CalendarUtil;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
// import javax.mail.Message;
// import javax.mail.MessagingException;
// import javax.mail.Session;
// import javax.mail.Transport;
// import javax.mail.internet.AddressException;
// import javax.mail.internet.InternetAddress;
// import javax.mail.internet.MimeMessage;

/**
 * FXML Controller class
 *
 * @author tritonle If the mail address is incorrect, I need to get a callback
 * method to catch this error
 * 
 * Error occurred during initialization of boot layer
java.lang.LayerInstantiationException: Package com.sun.activation.registries in both module activation and module jakarta.activation
 */
public class SendEmailController implements Initializable {

    private Person observer;
    private CalendarUtil calenderUtil;
    private Stage stage;

    private ResourceBundle bundle;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bundle = rb;
        sendBtn.setText(bundle.getString("key68"));

        if (observer == null) {
            observer = Person.getInstance();
        }
        if (calenderUtil == null) {
            calenderUtil = new CalendarUtil();
        }
        // 받는분 작성
//        recipient.setText("cgk9135@daum.net");
        // 본문내용 작성
        setListOnTheScreen();
    }

    ////////////////////////////////////////////////////////////////////////////
    //////////////////               SET MAIN             //////////////////////
    ////////////////////////////////////////////////////////////////////////////
    private CAccounting Main;

    public void setMain(CAccounting main, Stage stage) {
        this.Main = main;
        this.stage = stage;
    }

    public void setListOnTheScreen() {
        // 등록된 모든 사람의 헌금정보를 얻어온다
        List<UserData> uList = observer.getCompliedList();
        // 등록된 헌금종류를 가져와서 주정헌금은 뺸다.
        List<String> tList = observer.getTypeList();
        tList.remove("주정헌금");

        // 머릿말을 작성한다.
        String tBody = calenderUtil.getToday() + bundle.getString("key101");

        for (int k = 0; k < tList.size(); k++) {
            tBody = tBody + "\n\n" + tList.get(k) + "\n";
            List<String> tempUserList = new ArrayList<>();

            for (int i = 0; i < uList.size(); i++) {
                if (tList.get(k).equals(uList.get(i).getType())) {
                    tempUserList.add(uList.get(i).getName());
                }
            }

            Collections.sort(tempUserList);
            for (int i = 0; i < tempUserList.size(); i++) {
                tBody = tBody + tempUserList.get(i) + " ";
            }
        }

        Map<String, String> mReceivedUser = observer.getManualReceivedUser();
        tBody = tBody + "\n\n" + bundle.getString("key102") + "\n";
        for (Map.Entry<String, String> entry : mReceivedUser.entrySet()) {
            tBody = tBody + entry.getValue() + "(" + entry.getKey() + ") ";
        }

        body.setText(tBody);
    }

    private final static String host = "coucou.myasustor.com";
    private final String username = "tritonle@coucou.myasustor.com";
    private final String password = "choi7766&&^^";
    private final int port = 465;

    @FXML
    private TextField recipient;
    @FXML
    private TextArea body;
    @FXML
    private Button sendBtn;

    public void sendEmail() {
        // 창을 닫는다.
        // stage.hide();
        // Main.startProgressIndicator();
        // new Thread(() -> {
        //     try {
        //         // 정보를 담기 위한 객체
        //         Properties properties = System.getProperties();
        //         // SMTP 서버 정보 설정
        //         properties.put("mail.smtp.host", host);
        //         properties.put("mail.smtp.port", port);
        //         properties.put("mail.smtp.auth", "true");
        //         properties.put("mail.smtp.ssl.enable", "true");
        //         properties.put("mail.smtp.ssl.trust", host);

        //         // 세션생성
        //         Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
        //             String un = username;
        //             String pw = password;

        //             protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
        //                 return new javax.mail.PasswordAuthentication(un, pw);
        //             }
        //         });
        //         // 디버크 체크용
        //         session.setDebug(true);

        //         Message mimeMessage = new MimeMessage(session);
        //         mimeMessage.setFrom(new InternetAddress(username));
        //         mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient.getText()));
        //         mimeMessage.setSubject(bundle.getString("key103") + calenderUtil.getToday());
        //         mimeMessage.setText(body.getText());

        //         // Send Email
        //         Transport.send(mimeMessage);

        //         // 창을 닫는다.
        //         Platform.runLater(() -> {
        //             stage.close();
        //             Main.stopProgressIndicator();
        //         });

        //         // 확인 메세지 보여주기
        //         showAlert(bundle.getString("key104"));
        //     } catch (AddressException ex) {
        //         // Logger.getLogger(SendEmailController.class.getName()).log(Level.SEVERE, null, ex);
        //         // Show Error Message
        //         enableTextEdit();
        //         showAlert(bundle.getString("key106"));
        //     } catch (MessagingException ex) {
        //         // Logger.getLogger(SendEmailController.class.getName()).log(Level.SEVERE, null, ex);
        //         // Show Error Message
        //         enableTextEdit();
        //         showAlert(bundle.getString("key105"));
        //     }
        // }).start();
    }

    private void enableTextEdit() {
        Platform.runLater(() -> {
            Main.stopProgressIndicator();
            stage.show();
        });
    }

    public void showAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.YES);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(CAccounting.class.getResourceAsStream("images/_stageIcon.png")));
            alert.showAndWait();
        });
    }

}
