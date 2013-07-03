Required Packages
-----------------
An OpenSSL distribution is required for this Java client to work, available from http://www.openssl.org.

Package Installation
---------------------
* Use key.bat (for Windows) or key.sh (for UNIX) to generate public and private key pair, as well as a PKCS12 file which contains the user's private key and certificate.

Building the ButtonEncryption Application
------------------------------------------

````
sbt compile
````

To run the example code
------------------------

````
sbt 'run-main ButtonEncryption <CertFile> <PKCS12File> <PPCertFile> <Password> <CmdTxt> <OutputFile>'
````

For example,

````
sbt 'run-main ButtonEncryption my-pubcert.pem my-prvkey.p12 paypal_pubcert.pem password cmd=_xclick,business=x@test.com,amount=23.00,currency_code=USD,item_name=todos son asi,cert_id=ZWTMUVWGGH8T8" java_bn.html'
````

If you would like to use Sandbox, please add the word "sandbox" as last argument. For example,

````
sbt 'run-main ButtonEncryption my-pubcert.pem my-prvkey.p12 paypal_pubcert.pem password "cmd=_xclick,business=x@test.com,amount=23.00,currency_code=USD,item_name=todos son asi,cert_id=ZWTMUVWGGH8T8" java_bn.html sandbox'
````

Files included in this distribution
------------------------------------

1) The `ButtonEncryption` project. This project is a java application that will take command line options and in end, an Html file will be generated which you can use to test your settings.

2) `key.bat`, a batch file that will generate a public and private key pair, as well as a PKCS12 file which contains user's private key and certificate on windows. Just run "key <basename>" and it will genereate basename-prvkey.pem, basename-pubcert.pem and basename-prvkey.p12. This also requires the "OpenSSL for Windows" package.

3) `Key.sh`, a shell script that will generate a public and private key pair, as well as a PKCS12 file which contains user's private key and certificate on UNIX. Just run "key.sh <basename>" and it will generate basename-prvkey.pem, basename-pubcert.pem and basename-prvkey.p12.

4) sbt project files to build with Scala 2.10.2

Important
---------

See [Securing Your PayPal Payments Standard Buttons](https://developer.paypal.com/webapps/developer/docs/classic/paypal-payments-standard/integration-guide/encryptedwebpayments/#id08A3I0MK05Z)
