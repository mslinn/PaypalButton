package com.paypal.crypto.sample;

import org.bouncycastle.cms.CMSException;

import java.io.*;
import java.security.*;
import java.security.cert.CertStoreException;
import java.security.cert.CertificateException;

public class ButtonEncryption {
    private static String keyPath  = null;
    private static String certPath = null;
    private static String paypalCertPath = null;
    private static String keyPass  = "password";
    private static String cmdText  = null;       //cmd=_xclick,business=sample@paypal.com,amount=1.00,currency_code=USD
    private static String output   = "test.html";

    public static void main(String[] args) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        if (args.length != 6 && args.length != 7) {
            System.out.println("Usage: sbt 'run-main ButtonEncryption <CertFile> <PrivKeyFile> <PPCertFile> <Password> <CmdTxt> <OutputFile> [sandbox]'");
            System.out.println("  CertFile:    Your Public Cert");
            System.out.println("  PKCS12File:  Your Private Key in PKCS12 format");
            System.out.println("  PPCertFile:  PayPal's Public Cert");
            System.out.println("  KeyPassword: Password to sign with the private Key");
            System.out.println("  CmdTxt:      The button command, eg: 'cmd=_xclick,business=...'");
            System.out.println("  OutputFile:  File where the html will get written");
            System.out.println("  Sandbox:     Optional. Put 'sandbox' here to test on sandbox accounts, or leave blank for live testing.\n");
            System.out.println("For example:\n  sbt 'run-main ButtonEncryption my-pubcert.pem my-prvkey.p12 paypal_pubcert.pem password cmd=_xclick,business=x@test.com,amount=23.00,currency_code=USD,item_name=todos son asi,cert_id=ZWTMUVWGGH8T8\" java_bn.html'");
            return;
        }

        certPath = args[0];
        keyPath = args[1];
        paypalCertPath = args[2];
        keyPass = args[3];
        cmdText = args[4];
        output = args[5];
        String stage = "";
        if (args.length == 7)
            stage = args[6] + ".";

        try {
            ClientSide clientSide = new ClientSide(keyPath, certPath, paypalCertPath, keyPass);
            String result = clientSide.getButtonEncryptionValue(cmdText);
            if (result != null && !result.equals("")) {
                File outputFile = new File(output);
                if (outputFile.exists())
                    outputFile.delete();
                try {
                    OutputStream fout = new FileOutputStream(output);
                    OutputStream bout = new BufferedOutputStream(fout);
                    OutputStreamWriter out = new OutputStreamWriter(bout, "US-ASCII");
                    out.write("<form action=\"https://www." + stage + "paypal.com/cgi-bin/webscr\" method=\"post\">");
                    out.write("  <input type=\"hidden\" name=\"cmd\" value=\"_s-xclick\">");
                    out.write("  <input type=\"image\" src=\"https://www." + stage +
                                "paypal.com/en_US/i/btn/x-click-but23.gif\" border=\"0\" name=\"submit\" ");
                    out.write("    alt=\"Make payments with PayPal - it's fast, free and secure!\">");
                    out.write("  <input type=\"hidden\" name=\"encrypted\" value=\"" + result + "\">");
                    out.write("</form>");
                    out.flush();
                    out.close();
                } catch (UnsupportedEncodingException e) {
                    System.out.println("This Java VM does not support the ASCII character set.");
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (NoSuchAlgorithmException | KeyStoreException | CertificateException | CMSException | UnrecoverableKeyException |
                 InvalidAlgorithmParameterException | CertStoreException | NoSuchProviderException | IOException e) {
            e.printStackTrace();
        }
    }
}
