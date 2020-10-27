/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.app.util;

/**
 * 将字符串转换成对应的16进制ASCII码
 *
 *
 * @author zhangjh
 */
public class AsciiUtil {

    private static String toHexUtil(int n) {
        String rt = "";
        switch (n) {
            case 10:
                rt += "A";
                break;
            case 11:
                rt += "B";
                break;
            case 12:
                rt += "C";
                break;
            case 13:
                rt += "D";
                break;
            case 14:
                rt += "E";
                break;
            case 15:
                rt += "F";
                break;
            default:
                rt += n;
        }
        return rt;
    }

    public static String toHex(int n) {
        StringBuilder sb = new StringBuilder();
        if (n / 16 == 0) {
            return toHexUtil(n);
        } else {
            String t = toHex(n / 16);
            int nn = n % 16;
            sb.append(t).append(toHexUtil(nn));
        }
        return sb.toString();
    }

    public static String parseToAsciiStrFromStr(String str) {
        StringBuilder sb = new StringBuilder();
        byte[] bs = str.getBytes();
        for (int i = 0; i < bs.length; i++) {
            sb.append(toHex(bs[i])).append("--");
        }
        return sb.toString();
    }

    public static byte[] parseToAsciiArrFromStr(String str) {
        byte[] bs = str.getBytes();
        byte[] result = new byte[bs.length];
        for (int i = 0; i < bs.length; i++) {
            result[i] = (byte) Integer.parseInt(toHex(bs[i]));
        }
        return result;
    }

    // public static void main(String args[]) {
    // String s =
    // "540001071030000001000000492956761000080006B1A14FC02300000000000000000000000000001388021122334455660260000000000000000000013880211223344556620080105163100";
    // byte[] rs = parseAsciiToArrFromStr(s);
    // for (int i = 0, len = rs.length; i < len; i++) {
    // System.out.print(rs[i]);
    // }
    // }
    public static byte[] strToByteArr(String chargeMsg) {
        byte[] result = new byte[chargeMsg.length()];
        for (int index = 0; index < chargeMsg.length(); index++) {
            result[index] = (byte) chargeMsg.charAt(index);
        }
        return result;
    }
}
