package com.e.caccount.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parsing {

    public static String extractContent(String jsonString) {
        // 정규 표현식을 사용하여 "{"와 "}" 사이의 내용 추출
        Pattern pattern = Pattern.compile("\\{(.+?)\\}");
        Matcher matcher = pattern.matcher(jsonString);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return "";
    }

}
