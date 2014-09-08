/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.simbacode.payments;

import java.util.Properties;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

import net.oauth.client.httpclient4.HttpClient4;
import net.oauth.client.OAuthClient;
import net.oauth.OAuthServiceProvider;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthAccessor;
import net.oauth.OAuth;
import net.oauth.OAuthMessage;
import net.oauth.OAuthException;
import org.apache.commons.lang3.StringEscapeUtils;

// See the readme.txt and manpage.txt for more information
public class Pesapal {

    private Properties props;
    private File propFile;

    
    public static void main(String[] argv) throws Exception {
        URL location = Pesapal.class.getProtectionDomain().getCodeSource().getLocation();
        new Pesapal(new File("").getAbsolutePath() + "\\pesapal.properties").execute("request");
    }

    public Pesapal(String fileName) throws IOException {
        props = new Properties();
        propFile = new File(fileName);
        props.load(new FileInputStream(propFile));
    }

    private OAuthAccessor createOAuthAccessor() {
        String consumerKey = props.getProperty("pesapal.consumerKey");
        String callbackUrl = "http://simbacode.com/redirect";
        String consumerSecret = props.getProperty("pesapal.consumerSecret");

        String reqUrl = props.getProperty("pesapal.serviceProvider.requestTokenURL");
        String authzUrl = props.getProperty("pesapal.serviceProvider.userAuthorizationURL");
        String accessUrl = props.getProperty("pesapal.serviceProvider.accessTokenURL");

        OAuthServiceProvider provider = new OAuthServiceProvider(reqUrl, authzUrl, accessUrl);
        OAuthConsumer consumer = new OAuthConsumer(callbackUrl, consumerKey,
                consumerSecret, provider);
        return new OAuthAccessor(consumer);
    }

    private void updateProperties(String msg) throws IOException {
        props.store(new FileOutputStream(propFile), msg);
    }

    private OAuthMessage sendRequest(Map map, String url) throws IOException,
            URISyntaxException, OAuthException {
        List<Map.Entry> params = new ArrayList<Map.Entry>();
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry p = (Map.Entry) it.next();
            params.add(new OAuth.Parameter((String) p.getKey(),
                    (String) p.getValue()));
        }
        OAuthAccessor accessor = createOAuthAccessor();
        accessor.tokenSecret = props.getProperty("pesapal.consumerSecret");
        OAuthClient client = new OAuthClient(new HttpClient4());
        return client.invoke(accessor, "GET", url, params);
    }

    public void execute(String operation) throws IOException, OAuthException,
            URISyntaxException {
        if ("request".equals(operation)) {
            OAuthAccessor accessor = createOAuthAccessor();
            OAuthClient client = new OAuthClient(new HttpClient4());

            //get form details
            String amount = "1000.00";
            String desc = "desc";
            String type = "MERCHANT";
            String reference = "1111";//unique order id of the transaction, generated

            String email = "abc@yahoo.com.com";
            //ONE of email or phonenumber is required
            String phonenumber = "0123456789";
            String first_name = "Acellam";
            String last_name = "Guy";


            String post_xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><PesapalDirectOrderInfo xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" Amount=\""
                    + amount + "\" Description=\"" + desc + "\" Type=\"" + type
                    + "\" Reference=\"" + reference + "\" FirstName=\"" + first_name
                    + "\" LastName=\"" + last_name + "\" Email=\"" + email + "\" PhoneNumber=\""
                    + phonenumber + "\" xmlns=\"http://www.pesapal.com\" />";
            post_xml = StringEscapeUtils.escapeXml(post_xml);

            Collection<? extends Map.Entry> parameters = new ArrayList<Map.Entry>();
            List<Map.Entry> p = (parameters == null) ? new ArrayList<Map.Entry>(
                    1)
                    : new ArrayList<Map.Entry>(parameters);
            p.add(new OAuth.Parameter("pesapal_request_data",
                    post_xml));
            parameters = p;
            client.getRequestToken(accessor, "GET", parameters);

            props.setProperty("pesapal.requestToken", accessor.requestToken);
            props.setProperty("pesapal.tokenSecret", accessor.tokenSecret);

            updateProperties("Last action: added requestToken");
            System.out.println(propFile.getCanonicalPath() + " updated");
        } else {
            // access the resource
            Properties paramProps = new Properties();
            paramProps.setProperty("pesapal.oauth_token",
                    props.getProperty("accessToken"));

            OAuthMessage response = sendRequest(paramProps, operation);
            System.out.println(response.readBodyAsString());
        }

    }
}
