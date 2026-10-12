package com.pranav.indiafin_api.service;

public class GstinChecksum {

    private static  final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    static int contribution(char c, int index) {
        int factor = (index % 2 == 0) ? 1 : 2;
        int product = CHARS.indexOf(c) * factor;
        return product / 36 + product % 36;
    }

    static int sumOf(String first14) {
        int sum = 0;
        for (int i = 0; i < first14.length(); i++) {
            sum += contribution(first14.charAt(i), i);
        }
        return sum;
    }
    static char checkChar(String first14) {
        int sum = sumOf(first14);
        int check = (36 - (sum % 36)) % 36;
        return CHARS.charAt(check);
    }

    public static boolean isValid(String gstin) {
        return checkChar(gstin.substring(0, 14)) == gstin.charAt(14);
    }

    public static void main(String[] args) {
        System.out.println(checkChar("27AAPFU0939F1Z"));
        System.out.println(checkChar("27ABCPE1234F1Z"));
    }

}
