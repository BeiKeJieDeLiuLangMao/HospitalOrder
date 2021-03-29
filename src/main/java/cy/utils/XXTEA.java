package cy.utils;

import java.nio.charset.StandardCharsets;

/**
 * @author Chen Yang/CL10060-N/chen.yang@linecorp.com
 */
class XXTEA {

    private XXTEA() {
    }

    private static int MX(int var0, int var1, int var2, int var3, int var4, int[] var5) {
        return (var0 ^ var1) + (var5[var3 & 3 ^ var4] ^ var2) ^ (var2 >>> 5 ^ var1 << 2) + (var1 >>> 3 ^ var2 << 4);
    }

    private static byte[] encrypt(String var0, String var1) {
        return encrypt(var0.getBytes(StandardCharsets.UTF_8), var1.getBytes(StandardCharsets.UTF_8));
    }

    private static byte[] encrypt(byte[] var0, byte[] var1) {
        return var0.length == 0 ? var0 : toByteArray(encrypt(toIntArray(var0, true), toIntArray(fixKey(var1), false)), false);
    }

    private static int[] encrypt(int[] var0, int[] var1) {
        int var2 = var0.length - 1;
        if (var2 < 1) {
            return var0;
        } else {
            int var3 = 52 / (var2 + 1) + 6;
            int var4 = var0[var2];

            for (int var5 = 0; var3 > 0; --var3) {
                int var6 = -1640531527 + var5;
                int var7 = var6 >>> 2 & 3;

                int var8;
                for (var5 = 0; var5 < var2; var5 = var8) {
                    var8 = var5 + 1;
                    int var9 = var0[var8];
                    var4 = var0[var5] + MX(var6, var9, var4, var5, var7, var1);
                    var0[var5] = var4;
                }

                var8 = var0[0];
                var4 = var0[var2] + MX(var6, var8, var4, var5, var7, var1);
                var0[var2] = var4;
                var5 = var6;
            }

            return var0;
        }
    }

    static String encryptToBase64String(String var0, String var1) {
        byte[] var2 = encrypt(var0, var1);
        return var2 == null ? null : Base64.encode(var2);
    }

    private static byte[] fixKey(byte[] var0) {
        if (var0.length == 16) {
            return var0;
        } else {
            byte[] var1 = new byte[16];
            if (var0.length < 16) {
                System.arraycopy(var0, 0, var1, 0, var0.length);
            } else {
                System.arraycopy(var0, 0, var1, 0, 16);
            }

            return var1;
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static byte[] toByteArray(int[] var0, boolean var1) {
        int var2 = var0.length << 2;
        int var3;
        if (var1) {
            label27:
            {
                var3 = var0[var0.length - 1];
                int var4 = var2 - 4;
                if (var3 >= var4 - 3) {
                    var2 = var3;
                    if (var3 <= var4) {
                        break label27;
                    }
                }

                return null;
            }
        }

        byte[] var5 = new byte[var2];

        for (var3 = 0; var3 < var2; ++var3) {
            var5[var3] = (byte) (var0[var3 >>> 2] >>> ((var3 & 3) << 3));
        }

        return var5;
    }

    private static int[] toIntArray(byte[] var0, boolean var1) {
        int var2;
        if ((var0.length & 3) == 0) {
            var2 = var0.length >>> 2;
        } else {
            var2 = (var0.length >>> 2) + 1;
        }

        int[] var3;
        if (var1) {
            var3 = new int[var2 + 1];
            var3[var2] = var0.length;
        } else {
            var3 = new int[var2];
        }

        int var4 = var0.length;

        for (var2 = 0; var2 < var4; ++var2) {
            int var5 = var2 >>> 2;
            var3[var5] |= (var0[var2] & 255) << ((var2 & 3) << 3);
        }

        return var3;
    }
}
