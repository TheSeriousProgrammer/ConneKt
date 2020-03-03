package com.vitap.wified;


import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;

import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

import java.sql.Timestamp;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/*

    Future Security Upgrade Notes :

    (If you want understand the below part search for SSLVerification and understand it , it will take 5mins may be)

    The sophos webpage ssl certificate is self signed and not signed by Certificate Authorities ,
    hence the http library throws up UnverifiedSSLCertificate error when requests are made

    For now the requests are overriden to proceed by skipping SSLVerification mechanism , but this
    causes the app to be vulnerable to phishing attacks..

    Note that the webpage login portal of the Sophos network too is vulnerable to phishing attack

    The only secure way to be immune from phishing attacks is is to use the official apps and softwares
    from sophos ..

    Those official apps are immune because, those use TrustManager mechanism in which the official
    self signed certificate from sophos is pre-loaded into the app, when making requests if the app
    receives any other certificate other than the official Self Signed Certifcate ,the request will
    be dropped and the credentials won't be sent

    In future the Self Signed Certifcate of Sophos needs to be added to the Trust Manager ,
    so that the app will become immune to phishing attacks too..

    The feature is not implemented now to due to lack of time and lack of easy availablity of
    the official self signed SSL Ceritificate from Sophos .

    (
        Note that the Sophos Network is kinda immune to ip spoofing , so it is not possible for someone to
        perfom phishing attack within the network, however if the attacker creates a fake Sophos Wifi network
        with a cloned login portal then the phishing attack will be successfull on this app and the webpage
        login portal

        But if the app is equipped with TrustManager Mechanism then all sorts of phishing attacks will go in vain
        (OfCourse only till Quantum Computers become economical but it is hypothetical for now )

    )

    (-‿-) (-‿-) (-‿-) Bleh (̿▀̿‿‿̿▀̿) (̿▀̿‿‿̿▀̿)
 */

class KeyAndTrustManagers {
    final KeyManager[] keyManagers;
    final TrustManager[] trustManagers;

    KeyAndTrustManagers(KeyManager[] keyManagers, TrustManager[] trustManagers) {
        this.keyManagers = keyManagers;
        this.trustManagers = trustManagers;
    }
}

public class requests {


    private static final MediaType text = MediaType.parse("application/x-www-form-urlencoded");

    private static final String BaseString1 = "mode=191&username=%s&password=%s&a=%s&producttype=0"; //Body text sent in login request
    private static final String BaseString2 = "mode=193&username=18BCE7176&a=%s&producttype=0" ;     //Body text sent in logout request

    static OkHttpClient httpClient ;



    static public int loginRequest(String regNo,String passKey){

        String bodyString = String.format(BaseString1, encode(regNo.toLowerCase().trim()),encode(passKey.trim()),getTimeStamp());

        System.out.println(regNo.toLowerCase()+" "+passKey);
        System.out.println(bodyString);
        RequestBody body = RequestBody.create(bodyString,text);

        Request request = new Request.Builder()
        .url("https://172.18.10.10:8090/login.xml")
        .post(body)
        .addHeader("Host", "172.18.10.10:8090")
        .addHeader("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:72.0) Gecko/20100101 Firefox/72.0")
        .addHeader("Accept", "*/*")
        .addHeader("Accept-Language", "en-US,en;q=0.5")
        .addHeader("Accept-Encoding", "gzip, deflate, br")
        .addHeader("Content-Type", "application/x-www-form-urlencoded")
        .addHeader("Content-Length", "92")
        .addHeader("Origin", "https://172.18.10.10:8090")
        .addHeader("Connection", "keep-alive")
        .build();

        /*

        returns 3 when login succefull
        returns 2 login limit exceeded condition
        returns 1 invalid password
        returns 0 when login time exceeded error

         */

        try {
            /*
            Response response = httpClient.newCall(request).execute();
            String out = response.body().string();
             */
            String out = httpClient.newCall(request).execute().body().string();

            if(out.contains("signed in")){
                System.out.println("Login Successfull");
                return 3;
            }
            else if (out.contains("maximum")){
                System.out.println("max login limit");
                return 2;
            }
            else if(out.contains("Invalid user")){
                System.out.println("Invalid username/password");
                return 1;
            }
            else{
                System.out.println("Login Time Exceeded");
                return 0 ;
            }
        }
        catch (IOException temp){
            System.out.println("Error Communicating to server");
            System.out.println(temp.toString());
            return -1 ;
        }
    }


    static public boolean logoutRequest(String regNo){

        String bodyString = String.format(BaseString2, encode(regNo.trim()),getTimeStamp());

        RequestBody body = RequestBody.create(bodyString,text);

        Request request = new Request.Builder()
                .url("https://172.18.10.10:8090/login.xml")
                .post(body)
                .addHeader("Host", "172.18.10.10:8090")
                .addHeader("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:72.0) Gecko/20100101 Firefox/72.0")
                .addHeader("Accept","*/*")
                .addHeader("Accept-Language", "en-US,en;q=0.5")
                .addHeader("Accept-Encoding", "gzip, deflate, br")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Content-Length", "92")
                .addHeader("Origin", "https://172.18.10.10:8090")
                .addHeader("Connection", "keep-alive")
                .build();

        try {
            Response response = httpClient.newCall(request).execute();
            return true ;
        }
        catch (IOException temp){
            System.out.println(2);
            System.out.println(temp.toString());
            return false ;
        }
    }

    private static String encode(String inp){
        try {
            return URLEncoder.encode(inp,"UTF-8");
        }
        catch(UnsupportedEncodingException a){
            return "tf";
        }
    }

    public static String getTimeStamp(){
        return String.valueOf(new Timestamp(System.currentTimeMillis()).getTime());
    }

    requests(){
        //httpClient = safeSelfSignedOkhttpClient();
        httpClient = getUnsafeOkHttpClient();
    }

    public static void main(String a[]) {

        //httpClient = safeSelfSignedOkhttpClient();
        httpClient = getUnsafeOkHttpClient();
        //logoutRequest("18BCE7176");
        System.out.println();
        System.out.println();
        loginRequest("18BCE7176","olalalalala");
        System.out.println();
        System.out.println();
        logoutRequest("18BCE7176");

    }

    public static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    System.out.println(session.getCipherSuite());
                    //System.out.println(session.getLocalCertificates().length);
                    return true;
                }
            });

            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return null;
    }


    public static OkHttpClient safeSelfSignedOkhttpClient(){
        SSLSocketFactory sslSocketFactory;
        OkHttpClient client;
        try {
            KeyAndTrustManagers keyAndTrustManagers =
                    trustManagerForCertificates(trustedCertificatesInputStream());
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyAndTrustManagers.keyManagers, keyAndTrustManagers.trustManagers, null);
            sslSocketFactory = sslContext.getSocketFactory();

            X509TrustManager trustManager = (X509TrustManager) keyAndTrustManagers.trustManagers[0];
            client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, trustManager)
                    .build();
            return client;
        } catch (GeneralSecurityException e) {
            System.out.println(e.toString());
        }
        return null;

    }
    /*
        The Below Code Snipppet was obtained from https://gist.github.com/reline/e5df5c8bf700d6d2be1c567632386a5c
        The Snippet is used to create a TrustManager with sophos certificate pre-installed to prevent
        Phishing Attacks
     */
    private static InputStream trustedCertificatesInputStream() {

        //The Sophos certifcate for 172.18.10.10:8090 is added here
        String entrustRootCertificateAuthority = "" +
                /*"-----BEGIN CERTIFICATE-----"+
                "MIIFPDCCBCSgAwIBAgIFFTImRCAwDQYJKoZIhvcNAQELBQAwgbAxCzAJBgNVBAYTAklOMQ4wDAYDVQQIDAVJbmRpYTEQMA4GA1UEBwwHVmVsbG9yZTEnMCUGA1UECgweVmVsbG9yZSBPbmxpbmUgU3lzdGVtcyAoUCkgTHRkMQswCQYDVQQLDAJPVTEiMCAGA1UEAwwZU29waG9zX0NBX0M1MTAyODg4UUI0NlYxMzElMCMGCSqGSIb3DQEJARYWb25saW5lQG1kMy52c25sLm5ldC5pbjAiGA8yMDE1MDgwMTAwMDAwMFoYDzIwMzYxMjMxMjM1OTU5WjCBwTELMAkGA1UEBhMCSU4xDjAMBgNVBAgMBUluZGlhMRAwDgYDVQQHDAdWZWxsb3JlMScwJQYDVQQKDB5WZWxsb3JlIE9ubGluZSBTeXN0ZW1zIChQKSBMdGQxCzAJBgNVBAsMAk9VMTMwMQYDVQQDDCpTb3Bob3NBcHBsaWFuY2VDZXJ0aWZpY2F0ZV9DNTEwMjg4OFFCNDZWMTMxJTAjBgkqhkiG9w0BCQEWFm9ubGluZUBtZDMudnNubC5uZXQuaW4wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDUXcNYzAE//5QpzKvsodMmmWsz9IR3c5NqG6Jg56Ja/hx/T0cMRe7o4yx1wlYWr4M/izdTk0lRYcPA+yqyUz2Q63M+izKOoTy7ityz6kKCeJxO+njO+3yHrYnIh8G4CC8ZNuStaZwZPv2g36vDjWhlwGTd9C0nU00OgZaaWVZkr/QLE+uQj6X12vo3brN8pakZc8sajgKoAiiMscnvyK/qUhmr2bkBmR70+0PmOyMTjwRO6SJSiEDZcsjs78uYmxvY7ZAwNKax/EXxuxAXbTFQNEorme1Zpuu1cRuU7NYQjNOCG3/IbUQNAj5H1IJUUnofow2p9kCAzwHZ3TG6/LGlAgMBAAGjggFEMIIBQDAJBgNVHRMEAjAAMCwGCWCGSAGG+EIBDQQfFh1PcGVuU1NMIEdlbmVyYXRlZCBDZXJ0aWZpY2F0ZTAdBgNVHQ4EFgQUP+gxLwKGBZJXtMHinFsVTs+YktIwgeUGA1UdIwSB3TCB2oAURhm5PrE4UazwsgDe18JzrKYKwO2hgbakgbMwgbAxCzAJBgNVBAYTAklOMQ4wDAYDVQQIDAVJbmRpYTEQMA4GA1UEBwwHVmVsbG9yZTEnMCUGA1UECgweVmVsbG9yZSBPbmxpbmUgU3lzdGVtcyAoUCkgTHRkMQswCQYDVQQLDAJPVTEiMCAGA1UEAwwZU29waG9zX0NBX0M1MTAyODg4UUI0NlYxMzElMCMGCSqGSIb3DQEJARYWb25saW5lQG1kMy52c25sLm5ldC5pboIJAKxaw/tP4p8WMA0GCSqGSIb3DQEBCwUAA4IBAQAm1eUnzCMxBW3trwvzXqM4/IO1hcm6YzzZnbDpQ8m7H/OhI04cXDNt5nZDj/Ds47QdhdcyYNzpN+dzraespy75FAZZj6I0RT5URjicYtKSy0zSyD7hJV+XJ1wmLeFkNI0uF+BJRItJuKz6sdn+1b8c4FtnowBrxDg4LBrkJ+2y6mJs/ZN/WqzUbcTIOqT2IEvgfGg/Ny2HEAvNQvPT7ftfDPF79XDysh0472il1rD1SeU7bpVwLVGKetQirKhQx6vF6aomrQRPIj05VXGsotpcDVApswy8W5iTBXvHKJtvXBnpo05kgmjMeI77L2UXcHbD0OZ+ZXBR0xdVD75H6B2D"+
                "-----END CERTIFICATE-----";*/


                "-----BEGIN CERTIFICATE-----\n" +
                "MIIFPDCCBCSgAwIBAgIFFTImRCAwDQYJKoZIhvcNAQELBQAwgbAxCzAJBgNVBAYT\n" +
                "AklOMQ4wDAYDVQQIDAVJbmRpYTEQMA4GA1UEBwwHVmVsbG9yZTEnMCUGA1UECgwe\n" +
                "VmVsbG9yZSBPbmxpbmUgU3lzdGVtcyAoUCkgTHRkMQswCQYDVQQLDAJPVTEiMCAG\n" +
                "A1UEAwwZU29waG9zX0NBX0M1MTAyODg4UUI0NlYxMzElMCMGCSqGSIb3DQEJARYW\n" +
                "b25saW5lQG1kMy52c25sLm5ldC5pbjAiGA8yMDE1MDgwMTAwMDAwMFoYDzIwMzYx\n" +
                "MjMxMjM1OTU5WjCBwTELMAkGA1UEBhMCSU4xDjAMBgNVBAgMBUluZGlhMRAwDgYD\n" +
                "VQQHDAdWZWxsb3JlMScwJQYDVQQKDB5WZWxsb3JlIE9ubGluZSBTeXN0ZW1zIChQ\n" +
                "KSBMdGQxCzAJBgNVBAsMAk9VMTMwMQYDVQQDDCpTb3Bob3NBcHBsaWFuY2VDZXJ0\n" +
                "aWZpY2F0ZV9DNTEwMjg4OFFCNDZWMTMxJTAjBgkqhkiG9w0BCQEWFm9ubGluZUBt\n" +
                "ZDMudnNubC5uZXQuaW4wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDU\n" +
                "XcNYzAE//5QpzKvsodMmmWsz9IR3c5NqG6Jg56Ja/hx/T0cMRe7o4yx1wlYWr4M/\n" +
                "izdTk0lRYcPA+yqyUz2Q63M+izKOoTy7ityz6kKCeJxO+njO+3yHrYnIh8G4CC8Z\n" +
                "NuStaZwZPv2g36vDjWhlwGTd9C0nU00OgZaaWVZkr/QLE+uQj6X12vo3brN8pakZ\n" +
                "c8sajgKoAiiMscnvyK/qUhmr2bkBmR70+0PmOyMTjwRO6SJSiEDZcsjs78uYmxvY\n" +
                "7ZAwNKax/EXxuxAXbTFQNEorme1Zpuu1cRuU7NYQjNOCG3/IbUQNAj5H1IJUUnof\n" +
                "ow2p9kCAzwHZ3TG6/LGlAgMBAAGjggFEMIIBQDAJBgNVHRMEAjAAMCwGCWCGSAGG\n" +
                "+EIBDQQfFh1PcGVuU1NMIEdlbmVyYXRlZCBDZXJ0aWZpY2F0ZTAdBgNVHQ4EFgQU\n" +
                "P+gxLwKGBZJXtMHinFsVTs+YktIwgeUGA1UdIwSB3TCB2oAURhm5PrE4UazwsgDe\n" +
                "18JzrKYKwO2hgbakgbMwgbAxCzAJBgNVBAYTAklOMQ4wDAYDVQQIDAVJbmRpYTEQ\n" +
                "MA4GA1UEBwwHVmVsbG9yZTEnMCUGA1UECgweVmVsbG9yZSBPbmxpbmUgU3lzdGVt\n" +
                "cyAoUCkgTHRkMQswCQYDVQQLDAJPVTEiMCAGA1UEAwwZU29waG9zX0NBX0M1MTAy\n" +
                "ODg4UUI0NlYxMzElMCMGCSqGSIb3DQEJARYWb25saW5lQG1kMy52c25sLm5ldC5p\n" +
                "boIJAKxaw/tP4p8WMA0GCSqGSIb3DQEBCwUAA4IBAQAm1eUnzCMxBW3trwvzXqM4\n" +
                "/IO1hcm6YzzZnbDpQ8m7H/OhI04cXDNt5nZDj/Ds47QdhdcyYNzpN+dzraespy75\n" +
                "FAZZj6I0RT5URjicYtKSy0zSyD7hJV+XJ1wmLeFkNI0uF+BJRItJuKz6sdn+1b8c\n" +
                "4FtnowBrxDg4LBrkJ+2y6mJs/ZN/WqzUbcTIOqT2IEvgfGg/Ny2HEAvNQvPT7ftf\n" +
                "DPF79XDysh0472il1rD1SeU7bpVwLVGKetQirKhQx6vF6aomrQRPIj05VXGsotpc\n" +
                "DVApswy8W5iTBXvHKJtvXBnpo05kgmjMeI77L2UXcHbD0OZ+ZXBR0xdVD75H6B2D\n" +
                "-----END CERTIFICATE-----\n";

        return new Buffer()
                .writeUtf8(entrustRootCertificateAuthority)
                .inputStream();
    }

    private static KeyAndTrustManagers trustManagerForCertificates(InputStream in)
            throws GeneralSecurityException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(in);
        if (certificates.isEmpty()) {
            throw new IllegalArgumentException("expected non-empty set of trusted certificates");
        }

        // Put the certificates a key store.
        char[] password = "password".toCharArray(); // Any password will work.
        KeyStore keyStore = newEmptyKeyStore(password);
        int index = 0;
        for (Certificate certificate : certificates) {
            String certificateAlias = Integer.toString(index++);
            keyStore.setCertificateEntry(certificateAlias, certificate);
        }

        // Use it to build an X509 trust manager.
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
                KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        return new KeyAndTrustManagers(
                keyManagerFactory.getKeyManagers(),
                trustManagerFactory.getTrustManagers());
    }

    private static KeyStore newEmptyKeyStore(char[] password) throws GeneralSecurityException {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream in = null; // By convention, 'null' creates an empty key store.
            keyStore.load(in, password);
            return keyStore;
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}
