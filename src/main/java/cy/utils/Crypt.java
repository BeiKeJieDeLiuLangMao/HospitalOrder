package cy.utils;

/**
 * @author Chen Yang/CL10060-N/chen.yang@linecorp.com
 */
public final class Crypt {
    public static String xxteaEncode(String var0, String var1) {
        return XXTEA.encryptToBase64String(var0, var1);
    }
}
