package com.chipmandal.encoding;

import java.nio.charset.StandardCharsets;

public interface BaseEncoding {
    public static final char[] standardAlphabet =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz~`!@#$%^&*-_=+|\\;:\"'/?.>,<(){}[]".toCharArray();

    public static final Base94 Base94 = new Base94(standardAlphabet);

    byte[] encode(byte [] input);
    String encodeString(byte [] input);
    byte[] decode(byte [] input);
    byte[] decodeString(String input);


}
