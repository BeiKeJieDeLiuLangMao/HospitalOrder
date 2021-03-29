package cy.utils;

import java.io.ByteArrayOutputStream;

/**
 * @author Chen Yang/CL10060-N/chen.yang@linecorp.com
 */
public class Base64 {
    private static final byte[] base64DecodeChars = new byte[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1};
    private static final char[] base64EncodeChars = new char[] {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};

    private Base64() {
    }

    @SuppressWarnings({"unused", "LoopStatementThatDoesntLoop"})
    public static byte[] decode(String var0) {
        byte[] var1 = var0.getBytes();
        int var2 = var1.length;
        ByteArrayOutputStream var8 = new ByteArrayOutputStream(var2);
        int var3 = 0;

        while (var3 < var2) {
            label80:
            while (true) {
                byte[] var4 = base64DecodeChars;
                int var5 = var3 + 1;
                byte var6 = var4[var1[var3]];
                if (var5 >= var2 || var6 != -1) {
                    if (var6 == -1) {
                        return var8.toByteArray();
                    }

                    while (true) {
                        var4 = base64DecodeChars;
                        var3 = var5 + 1;
                        byte var7 = var4[var1[var5]];
                        if (var3 >= var2 || var7 != -1) {
                            if (var7 == -1) {
                                return var8.toByteArray();
                            }

                            var8.write(var6 << 2 | (var7 & 48) >>> 4);
                            var5 = var3;

                            while (true) {
                                var3 = var5 + 1;
                                byte var9 = var1[var5];
                                if (var9 == 61) {
                                    return var8.toByteArray();
                                }

                                var6 = base64DecodeChars[var9];
                                if (var3 >= var2 || var6 != -1) {
                                    if (var6 == -1) {
                                        return var8.toByteArray();
                                    }

                                    var8.write((var7 & 15) << 4 | (var6 & 60) >>> 2);
                                    var5 = var3;

                                    while (true) {
                                        var3 = var5 + 1;
                                        var9 = var1[var5];
                                        if (var9 == 61) {
                                            return var8.toByteArray();
                                        }

                                        var9 = base64DecodeChars[var9];
                                        if (var3 >= var2 || var9 != -1) {
                                            if (var9 == -1) {
                                                return var8.toByteArray();
                                            }

                                            var8.write(var9 | (var6 & 3) << 6);
                                            continue label80;
                                        }

                                        var5 = var3;
                                    }
                                }

                                var5 = var3;
                            }
                        }

                        var5 = var3;
                    }
                } else {
                    var3 = var5;
                }
            }
        }

        return var8.toByteArray();
    }

    static String encode(byte[] var0) {
        StringBuilder var1 = new StringBuilder();
        int var2 = var0.length % 3;
        int var3 = var0.length;

        int var4;
        for (var4 = 0; var4 < var3 - var2; ++var4) {
            int var5 = var4 + 1;
            byte var6 = var0[var4];
            var4 = var5 + 1;
            var5 = (var6 & 255) << 16 | (var0[var5] & 255) << 8 | var0[var4] & 255;
            var1.append(base64EncodeChars[var5 >> 18]);
            var1.append(base64EncodeChars[var5 >> 12 & 63]);
            var1.append(base64EncodeChars[var5 >> 6 & 63]);
            var1.append(base64EncodeChars[var5 & 63]);
        }

        if (var2 == 1) {
            var4 = var0[var4] & 255;
            var1.append(base64EncodeChars[var4 >> 2]);
            var1.append(base64EncodeChars[(var4 & 3) << 4]);
            var1.append("==");
        } else if (var2 == 2) {
            byte var7 = var0[var4];
            var4 = var0[var4 + 1] & 255 | (var7 & 255) << 8;
            var1.append(base64EncodeChars[var4 >> 10]);
            var1.append(base64EncodeChars[var4 >> 4 & 63]);
            var1.append(base64EncodeChars[(var4 & 15) << 2]);
            var1.append("=");
        }

        return var1.toString();
    }
}
