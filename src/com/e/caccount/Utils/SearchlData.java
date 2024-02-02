package com.e.caccount.Utils;

import com.e.caccount.Model.userModel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SearchlData extends Thread {

    public SearchlData() {

    }

    @Override
    public void run() {

    }

    static List<userModel> result = new ArrayList();

    public List<userModel> getPersonalHistory(File rfile, String name, String year) {
        result.clear();
        int temp_int = Integer.parseInt(year);
        showFile(rfile, name, temp_int);
        getSum();
        return result;
    }

    public void showFile(File rfile, String name, int year) {
        File file = rfile;

        if (file.isDirectory()) {
            File[] fileList = file.listFiles();
            for (File tFile : fileList) {
                if (tFile.isDirectory()) {
                    showFile(tFile, name, year);
                } else {
                    readFile(tFile.getPath(), name, year);
                }
            }
        } else {
            file.getName();
        }
    }

    public void readFile(String filePath, String name, int year) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder doc = factory.newDocumentBuilder();
            File file = new File(filePath);
            Document parser = doc.parse(file);
            NodeList list = parser.getElementsByTagName("USER");
            for (int i = 0; i < list.getLength(); i++) {
                Node userNode = list.item(i);

                Element userEle = (Element) userNode;
                NodeList userName = userEle.getElementsByTagName("name");
                NodeList userType = userEle.getElementsByTagName("type");
                NodeList userAmount = userEle.getElementsByTagName("amount");
                String userNameValue = userName.item(0).getChildNodes().item(0).getNodeValue();
                String userTypeValue = userType.item(0).getChildNodes().item(0).getNodeValue();
                String userAmountValue = userAmount.item(0).getChildNodes().item(0).getNodeValue();

                if (name.equals(userNameValue)) {
                    userModel userMod = new userModel();
                    userMod.setName(userNameValue);
                    userMod.setAmount(Integer.parseInt(userAmountValue));
                    userMod.setType(userTypeValue);
                    userMod.setDateTime(getConvertedDate(file.getName().substring(0, 8)));
                    if (getCovertedYear(file.getName().substring(0, 8)) == year) {
                        result.add(userMod);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getConvertedDate(String date) {
        String year = date.substring(0, 4);
        String month = date.substring(4, 6);
        String day = date.substring(6);
        return year + ". " + month + ". " + day;
    }

    private int getCovertedYear(String date) {
        String year = date.substring(0, 4);
        int conv_year = Integer.parseInt(year);
        return conv_year;
    }

    private int sum;

    private void getSum() {
        sum = 0;
        result.stream()
                .forEach(e -> {
                    sum += e.getAmount();
                });
        userModel uModel = new userModel("TOTAL", "", sum, "");
        result.add(uModel);
    }
}
