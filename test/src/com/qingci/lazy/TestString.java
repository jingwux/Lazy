package com.qingci.lazy;

public class TestString {
    public static void main(String[] args) {

        StringBuilder sb = new StringBuilder();
        String sourceClass = "DO1";
        String targetClass = "VO1";
        sb.append("@Component \r\n")
                .append("public class ").append("sourceClass").append("2").append("targetClass").append("Converter implements BasPOJO<").append(sourceClass).append(", ").append(targetClass).append("> {\r\n")
        .append("}")
        ;
        System.out.println("sb.toString() = " + sb.toString());
    }

}
