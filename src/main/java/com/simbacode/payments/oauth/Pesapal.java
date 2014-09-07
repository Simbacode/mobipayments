package com.simbacode.payments.oauth;

//Java Libraries
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Very basic sample code that demonstrates how to make an OAuth 1.0
 * System-to-System request to the Pesapal API
 *
 */
public class Pesapal {

    private static final Base64 BASE64 = new Base64();

    public static void main(final String[] args) throws Exception {
        // Setup the variables necessary to create the OAuth 1.0 signature and
        // make the request
        String httpMethod = "GET";
        URL url = new URL("http://demo.pesapal.com/API/PostPesapalDirectOrderV4");
        String consumerKey = "{YOUR KEY}";
        String secret = "{YOUR SECRET}";
        String signatureMethod = "HMac-SHA1";
        String body = "";

        //get form details
        String amount = "1000.00";
        String desc = "desc";
        String type = "MERCHANT";
        String reference = "1111";//unique order id of the transaction, generated

        String email = "{YOUR EMAIL}";
        //ONE of email or phonenumber is required
        String phonenumber = "{YOUR PHONE}";

        //redirect url, the page that will handle
        String callback_url = "http://simbacode.com/redirect.php";

        String post_xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><PesapalDirectOrderInfo xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
                + "Amount=\"" + amount + "\" Description=\"" + desc + "\" Type=\"" + type + "\""
                + "Reference=\"" + reference + "\""
                + "Email=\"" + email + "\""
                + "PhoneNumber=\"" + phonenumber + "\" xmlns=\"http://www.pesapal.com\" />";
        post_xml = StringEscapeUtils.escapeXml(post_xml);

        byte[] requestBody = null;
        HttpURLConnection request = null;
        BufferedReader in = null;

        // Set the Nonce and Timestamp parameters
        String nonce = getNonce();
        String timestamp = getTimestamp();

        // Set the request body if making a POST or PUT request
        if ("POST".equals(httpMethod) || "PUT".equals(httpMethod)) {
            body = "{requestBody}";
            requestBody = body.getBytes("UTF-8");
        }

        // Create the OAuth parameter name/value pair
        Map<String, String> oauthParams = new LinkedHashMap<String, String>();
        oauthParams.put("oauth_signature_method", signatureMethod);
        oauthParams.put("oauth_timestamp", timestamp);
        oauthParams.put("oauth_nonce", nonce);
        //oauthParams.put("oauth_version", "1.0");

        // Get the OAuth 1.0 Signature
        String signature = generateSignature(httpMethod, url, oauthParams,
                requestBody, secret);
        System.out
                .println(String.format("OAuth 1.0 Signature = %s", signature));

        // Add the oauth_signature parameter to the set of OAuth Parameters
        oauthParams.put("oauth_signature", signature);

        oauthParams.put("oauth_consumer_key", consumerKey);
        oauthParams.put("oauth_callback", callback_url);
        oauthParams.put("pesapal_request_data", post_xml);


        // Genterate a string of comma delimited: keyName="URL-encoded(value)"
        // pairs
        int i = 0;
        StringBuilder sb = new StringBuilder();
        Object[] keyNames = oauthParams.keySet().toArray();
        for (Object keyName : keyNames) {
            String value = oauthParams.get((String) keyName);
            sb.append(keyName).append("=\"")
                    .append(URLEncoder.encode(value, "UTF-8")).append("\"");
            i++;

            if (keyNames.length > i) {
                sb.append("&");
            }
        }

        String authstr = String.format("%s?%s", url,
                sb.toString());
        url = new URL(authstr);

        try {
            // Setup the Request
            request = (HttpURLConnection) url.openConnection();
            request.setRequestMethod(httpMethod);

            // Send Request & Get Response
            InputStreamReader reader = new InputStreamReader(
                    request.getInputStream());
            in = new BufferedReader(reader);

            // Get the response stream
            String response = "";
            while ((response = in.readLine()) != null) {
                System.out.println(String.format("%s",
                        response));
            };

        } catch (IOException e) {
            // This exception will be raised if the serve didn't return 200 - OK
            System.out.print(e.getMessage());

        } finally {
            if (in != null) {
                in.close();
            }
            if (request != null) {
                request.disconnect();
            }
        }
    }

    /**
     * Generates a random nonce
     *
     * @return A unique identifier for the request
     */
    private static String getNonce() {
        //return RandomStringUtils.randomAlphanumeric(32);
        return "x" + System.currentTimeMillis();
    }

    /**
     * Generates an integer representing the number of seconds since the unix
     * epoch using the date/time the request is issued
     *
     * @return A timestamp for the request
     */
    private static String getTimestamp() {
        return Long.toString((System.currentTimeMillis() / 1000));
    }

    /**
     * Generates an OAuth 1.0 signature
     *
     * @param httpMethod The HTTP method of the request
     * @param URL The request URL
     * @param oauthParams The associative set of signable oAuth parameters
     * @param requestBody The serialized POST/PUT message body
     * @param secret Alphanumeric string used to validate the identity of the
     * education partner (Private Key)
     *
     * @return A string containing the Base64-encoded signature digest
     *
     * @throws UnsupportedEncodingException
     */
    private static String generateSignature(String httpMethod, URL url,
            Map<String, String> oauthParams, byte[] requestBody, String secret)
            throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, GeneralSecurityException {
        // Ensure the HTTP Method is upper-cased
        httpMethod.toUpperCase();

        // Construct the URL-encoded OAuth parameter portion of the signature
        // base string
        String encodedParams = normalizeParams(httpMethod, url, oauthParams,
                requestBody);

        // URL-encode the relative URL
        String encodedUri = URLEncoder.encode(url.getPath(), "UTF-8");

        // Build the signature base string to be signed with the Consumer Secret
        String baseString = String.format("%s&%s&%s", httpMethod, encodedUri,
                encodedParams);

        return computeSignature(baseString, secret);
    }

    private static String computeSignature(String baseString, String keyString) throws GeneralSecurityException, UnsupportedEncodingException {

        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKey skey = new SecretKeySpec(keyString.getBytes("UTF-8"), "HmacSHA1");
        mac.init(skey);
        String signature =
                new String(BASE64.encode(mac.doFinal(baseString.toString().getBytes("UTF-8"))));

        return signature;

    }

    /**
     * Normalizes all OAuth signable parameters and url query parameters
     * according to OAuth 1.0
     *
     * @param httpMethod The upper-cased HTTP method
     * @param URL The request URL
     * @param oauthParams The associative set of signable oAuth parameters
     * @param requstBody The serialized POST/PUT message body
     *
     * @return A string containing normalized and encoded oAuth parameters
     *
     * @throws UnsupportedEncodingException
     */
    private static String normalizeParams(String httpMethod, URL url,
            Map<String, String> oauthParams, byte[] requestBody)
            throws UnsupportedEncodingException {

        // Use a new LinkedHashMap for the OAuth signature creation
        Map<String, String> kvpParams = new LinkedHashMap<String, String>();
        kvpParams.putAll(oauthParams);

        // Place any query string parameters into a key value pair using equals
        // ("=") to mark
        // the key/value relationship and join each parameter with an ampersand
        // ("&")
        if (url.getQuery() != null) {
            for (String keyValue : url.getQuery().split("&")) {
                String[] p = keyValue.split("=");
                kvpParams.put(URLEncoder.encode(p[0], "UTF-8"),
                        URLEncoder.encode(p[1], "UTF-8"));
            }

        }

        // Include the body parameter if dealing with a POST or PUT request
        if ("POST".equals(httpMethod) || "PUT".equals(httpMethod)) {
            String body = Base64.encodeBase64String(requestBody).replaceAll(
                    "\r\n", "");
            body = URLEncoder.encode(body, "UTF-8");
            kvpParams.put("body", URLEncoder.encode(body, "UTF-8"));
        }

        // Sort the parameters in lexicographical order, 1st by Key then by
        // Value; separate with ("=")
        TreeMap<String, String> sortedParams = new TreeMap<String, String>(
                String.CASE_INSENSITIVE_ORDER);
        sortedParams.putAll(kvpParams);

        // Remove unwanted characters and replace the comma delimiter with an
        // ampersand
        String stringParams = sortedParams.toString().replaceAll("[{} ]", "");
        stringParams = stringParams.replace(",", "&");

        // URL-encode the equals ("%3D") and ampersand ("%26")
        String encodedParams = URLEncoder.encode(stringParams, "UTF-8");

        return encodedParams;
    }
}
