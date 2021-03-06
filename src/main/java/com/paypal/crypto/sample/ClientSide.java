package com.paypal.crypto.sample;

import org.bouncycastle.cms.*;
import org.bouncycastle.util.encoders.Base64;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.*;
import java.security.cert.*;
import java.util.ArrayList;
import java.util.Enumeration;

public class ClientSide {
    private String keyPath;
    private String certPath;
    private String paypalCertPath;
    private String keyPass;

    public ClientSide(String keyPath, String certPath, String payPalCertPath, String keyPass) {
        this.keyPath = keyPath;
        this.certPath = certPath;
        this.paypalCertPath = payPalCertPath;
        this.keyPass = keyPass;
    }

    public String getButtonEncryptionValue(String _data) throws IOException, CertificateException, KeyStoreException,
            UnrecoverableKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException,
            CertStoreException, CMSException {
        _data = _data.replace(',', '\n');
        CertificateFactory cf = CertificateFactory.getInstance("X509", "BC");

        // Read the private iey
        KeyStore ks = KeyStore.getInstance("PKCS12", "BC");
        ks.load(new FileInputStream(keyPath), keyPass.toCharArray());

        String keyAlias = null;
        Enumeration<String> aliases = ks.aliases();
        while (aliases.hasMoreElements()) {
            keyAlias = aliases.nextElement();
        }

        PrivateKey privateKey = (PrivateKey) ks.getKey(keyAlias, keyPass.toCharArray());
        X509Certificate certificate = (X509Certificate) cf.generateCertificate(new FileInputStream(certPath));
        X509Certificate payPalCert = (X509Certificate) cf.generateCertificate(new FileInputStream(paypalCertPath));

        byte[] data = _data.getBytes();

        // Sign the Data with my signing only key pair
        CMSSignedDataGenerator signedGenerator = new CMSSignedDataGenerator();
        signedGenerator.addSigner(privateKey, certificate, CMSSignedDataGenerator.DIGEST_SHA1);

        ArrayList<X509Certificate> certList = new ArrayList<X509Certificate>();
        certList.add(certificate);
        CertStore certStore = CertStore.getInstance("Collection", new CollectionCertStoreParameters(certList));
        signedGenerator.addCertificatesAndCRLs(certStore);

        CMSProcessableByteArray cmsByteArray = new CMSProcessableByteArray(data);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        cmsByteArray.write(baos);
        System.out.println("CMSProcessableByteArray contains [" + baos.toString() + "]");

        CMSSignedData signedData = signedGenerator.generate(cmsByteArray, true, "BC");
        byte[] signed = signedData.getEncoded();
        CMSEnvelopedDataGenerator envGenerator = new CMSEnvelopedDataGenerator();
        envGenerator.addKeyTransRecipient(payPalCert);
        CMSEnvelopedData envData = envGenerator.generate(new CMSProcessableByteArray(signed),
                CMSEnvelopedDataGenerator.DES_EDE3_CBC, "BC");

        byte[] pkcs7Bytes = envData.getEncoded();
        return new String(DERtoPEM(pkcs7Bytes, "PKCS7"));
    }

    public static byte[] DERtoPEM(byte[] bytes, String headfoot) {
        ByteArrayOutputStream pemStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(pemStream);
        byte[] stringBytes = Base64.encode(bytes);

        System.out.println("Converting " + stringBytes.length + " bytes");

        String encoded = new String(stringBytes);
        if (headfoot != null)
            writer.print("-----BEGIN " + headfoot + "-----\n");

        // write 64 chars per line till done
        int i = 0;
        while ((i + 1) * 64 < encoded.length()) {
            writer.print(encoded.substring(i * 64, (i + 1) * 64));
            writer.print("\n");
            i++;
        }
        if (encoded.length() % 64 != 0) {
            writer.print(encoded.substring(i * 64)); // write remainder
            writer.print("\n");
        }
        if (headfoot != null)
            writer.print("-----END " + headfoot + "-----\n");
        writer.flush();
        return pemStream.toByteArray();
    }
}
