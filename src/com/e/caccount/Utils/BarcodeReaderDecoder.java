/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.e.caccount.Utils;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

/**
 *
 * @author trito
 */
public class BarcodeReaderDecoder extends Observable implements NativeKeyListener {

    ////////////////////////////////////////////////////////////////////////////
    //////////////////             SINGLE TON             //////////////////////
    ////////////////////////////////////////////////////////////////////////////
    private static BarcodeReaderDecoder INSTANCE = null;

    public static BarcodeReaderDecoder getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BarcodeReaderDecoder();
        }
        return INSTANCE;
    }

    public void RegisterKeys() {
        try {
            //Register default modifiers
            registerExtraKeys();
            registerSystemKeys();
            registerLowercases();

            if (GlobalScreen.isNativeHookRegistered()) {
                stop();
            }

            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(this);

        } catch (Exception ex) {
            System.err.println("Failed to register native hook : " + ex.getMessage());
            System.exit(1);
        }
    }

    public void stop() {
        try {
            GlobalScreen.removeNativeKeyListener(this);
            GlobalScreen.unregisterNativeHook();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private StringBuilder decodingNeed = new StringBuilder();
    private StringBuilder decodedString = new StringBuilder();

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {

        String Char = getMatchedCharacter(e);
        if (Char.length() != 0) {
            decodedString.append(CharacterConv(Char));
        }
    }

    private String getMatchedCharacter(NativeKeyEvent e) {
        int modifierValue = e.getModifiers();

        if (modifierValue == 8
                && !e.getKeyText(e.getKeyCode()).equals("Alt")) {
            // Get Converted Decimal to get Korean Character
            int convertedDecimal = GetConvertedDecimal(e);
            decodingNeed.append(String.valueOf(convertedDecimal));

            return DecodingToChracter();
        } else if (e.getKeyText(e.getKeyCode()).equals("Enter")) {
            //Observer에 알려준다
            if (decodedString.length() > 2) {
                setChanged();
                notifyObservers(decodedString);
            }

            return "";
        } else if (modifierValue == 1) {
            if (lowercases.containsKey(e.getKeyText(e.getKeyCode()))) {
                String Char = lowercases.get(e.getKeyText(e.getKeyCode()).toString());
                return Char;
            } else {
                return "";
            }
        } else {
            return isUnCodedCharacterContain(e);
        }
    }

//    public String[] getDecodedStrings() {
//        System.out.println(decodedString);
//        String[] text = splitComma(decodedString.toString());
//        return text;
//    }

    public void setDefaultDecodedString() {
        decodedString = new StringBuilder();
    }

    public String[] splitComma(String str) {
        String[] parts = str.split(",");

        if (parts.length == 2) {
            return parts;
        } else {
            return null;
        }
    }

    private int GetConvertedDecimal(NativeKeyEvent e) {

        StringBuilder stb = new StringBuilder();
        // Get RawCode() in Decimal form
        int decimal = e.getRawCode();

        // Make Binary form
        while (decimal > 0) {
            int bit = decimal % 2;
            stb.append(bit);
            decimal /= 2;
        }

        // Get Last Four Digits
        stb.reverse();
        String lastFourDigits = stb.substring(stb.length() - 4);

        int returnDecimal = binaryTodecimal(lastFourDigits);

        return returnDecimal;
    }

    private int binaryTodecimal(String binary) {
        int decimal = 0;
        int base = 1;

        for (int i = binary.length() - 1; i >= 0; i--) {
            int digit = Integer.parseInt(String.valueOf(binary.charAt(i)));
            decimal += digit * base;
            base *= 2;
        }
        return decimal;
    }

    private String DecodingToChracter() {
        if (decodingNeed.length() == 5) {
            int tempInt = getDecondingNeddInteger();
            String utf16String = Character.toString((char) tempInt);

            //initialize
            decodingNeed = new StringBuilder();

            return utf16String;
        } else {
            return "";
        }
    }

    private int getDecondingNeddInteger() {
        String tempStr = decodingNeed.toString();
        int tempInt = Integer.parseInt(tempStr);
        return tempInt;
    }

    private String isUnCodedCharacterContain(NativeKeyEvent e) {
        if (extraKeys.containsKey(e.getKeyText(e.getKeyCode()).toString())) {
            String str = extraKeys.get(e.getKeyText(e.getKeyCode()).toString());
            return str;
        } else {
            if (systemKeys.contains(e.getKeyText(e.getKeyCode()).toString())) {
                return "";
            } else {
                return e.getKeyText(e.getKeyCode()).toString();
            }
        }
    }

    private String CharacterConv(String str) {
        if (Character.isUpperCase(str.charAt(0))) {
            // System.out.println(str.toLowerCase() + " lowerCase");
            return str.toLowerCase();
        } else {
            // System.out.println(str.toUpperCase() + " upperCase");
            return str.toUpperCase();
        }
    }

    private boolean isCapsLockOn() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        boolean isCapsLockOn = toolkit.getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
        return isCapsLockOn;
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        // Do nothing
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        // Do nothing
    }

    ArrayList<String> systemKeys = new ArrayList<String>();

    private void registerSystemKeys() {
        systemKeys.add("Escape");
        systemKeys.add("F1");
        systemKeys.add("F2");
        systemKeys.add("F3");
        systemKeys.add("F4");
        systemKeys.add("F5");
        systemKeys.add("F6");
        systemKeys.add("F7");
        systemKeys.add("F8");
        systemKeys.add("F9");
        systemKeys.add("F10");
        systemKeys.add("F11");
        systemKeys.add("F12");
        systemKeys.add("Print Screen");
        systemKeys.add("Insert");
        systemKeys.add("Delete");
        systemKeys.add("Tab");
        systemKeys.add("Caps Lock");
        systemKeys.add("Ctrl");
        systemKeys.add("Meta");
        systemKeys.add("Alt");
        systemKeys.add("Escape");
        systemKeys.add("Left");
        systemKeys.add("Right");
        systemKeys.add("Up");
        systemKeys.add("Down");
        systemKeys.add("Backspace");

    }

    Map<String, String> extraKeys = new HashMap<String, String>();

    private void registerExtraKeys() {
        extraKeys.put("Back Quote", "`");
        extraKeys.put("Minus", "-");
        extraKeys.put("Equals", "=");
        extraKeys.put("Close Bracket", "]");
        extraKeys.put("pen Bracket", "[");
        extraKeys.put("Semicolon", ";");
        extraKeys.put("Quote", "'");
        extraKeys.put("Slash", "/");
        extraKeys.put("Period", ".");
        extraKeys.put("Comma", ",");
        extraKeys.put("Back Slash", "\\");
    }

    Map<String, String> lowercases = new HashMap<String, String>();

    private void registerLowercases() {
        lowercases.put("Back Quote", "~");
        lowercases.put("1", "!");
        lowercases.put("2", "@");
        lowercases.put("3", "#");
        lowercases.put("4", "$");
        lowercases.put("5", "%");
        lowercases.put("6", "^");
        lowercases.put("7", "&");
        lowercases.put("8", "*");
        lowercases.put("9", "(");
        lowercases.put("0", ")");
        lowercases.put("Minus", "_");
        lowercases.put("Equals", "+");
        lowercases.put("Q", "q");
        lowercases.put("W", "w");
        lowercases.put("E", "e");
        lowercases.put("R", "r");
        lowercases.put("T", "t");
        lowercases.put("Y", "y");
        lowercases.put("U", "u");
        lowercases.put("I", "i");
        lowercases.put("O", "o");
        lowercases.put("P", "p");
        lowercases.put("Open Bracket", "{");
        lowercases.put("Close Bracket", "}");
        lowercases.put("Back Slash", "|");
        lowercases.put("A", "a");
        lowercases.put("S", "s");
        lowercases.put("D", "d");
        lowercases.put("F", "f");
        lowercases.put("G", "g");
        lowercases.put("H", "h");
        lowercases.put("J", "j");
        lowercases.put("K", "k");
        lowercases.put("L", "l");
        lowercases.put("Semicolon", ":");
        lowercases.put("Quote", "\"");
        lowercases.put("Z", "z");
        lowercases.put("X", "x");
        lowercases.put("C", "c");
        lowercases.put("V", "v");
        lowercases.put("B", "b");
        lowercases.put("N", "n");
        lowercases.put("M", "m");
        lowercases.put("Comma", "<");
        lowercases.put("Period", ">");
        lowercases.put("Slash", "?");
    }

}
