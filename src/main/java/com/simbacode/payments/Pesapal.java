/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.simbacode.payments;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthServiceProvider;
import net.oauth.client.OAuthClient;
import net.oauth.client.httpclient4.HttpClient4;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * This is main Pesapal Oauth 1.0 java class
 *
 * @author Acellam Guy
 * @version 0.1
 */
public class Pesapal {

    private Properties props;
    private File propFile;

    /**
     * Constructor for the java class
     *
     * @param fileName the path of the config file that contains pesapal
     * details.
     * @throws IOException
     */
    public Pesapal(String fileName) throws IOException {
        props = new Properties();
        propFile = new File(fileName);
        props.load(new FileInputStream(propFile));
    }

    /**
     * This is used to create the consumer for Oauth Request
     *
     * @see OAuthAccessor
     * @return OAuthAccessor
     */
    private OAuthAccessor createOAuthAccessor() {

        String consumerKey = props.getProperty("pesapal.consumerKey");
        String callbackUrl = props.getProperty("pesapal.callbackURL");
        String consumerSecret = props.getProperty("pesapal.consumerSecret");

        String reqUrl = props.getProperty("pesapal.serviceProvider.requestTokenURL");
        String authzUrl = props.getProperty("pesapal.serviceProvider.userAuthorizationURL");
        String accessUrl = props.getProperty("pesapal.serviceProvider.accessTokenURL");

        OAuthServiceProvider provider = new OAuthServiceProvider(reqUrl, authzUrl, accessUrl);
        OAuthConsumer consumer = new OAuthConsumer(callbackUrl, consumerKey,
                consumerSecret, provider);
        return new OAuthAccessor(consumer);
    }

    /**
     * Makes request to the pesapal server and returns the response that
     * contains All the OATH details.
     *
     * @see OAuthMessage
     * @param amount the amount of money for the good/service
     * @param desc description of what is being paid for good or service
     * @param type the type of pesapal account eg Merchant
     * @param reference the unique id to your request.
     * @param email
     * @param phonenumber
     * @param first_name
     * @param last_name
     * @return
     * @throws IOException
     * @throws OAuthException
     * @throws URISyntaxException
     */
    public OAuthMessage execute(String amount, String desc, String type, String reference, String email, String phonenumber, String first_name, String last_name) throws IOException, OAuthException,
            URISyntaxException {
        
        OAuthAccessor accessor = createOAuthAccessor();
        OAuthClient client = new OAuthClient(new HttpClient4());
        
        String callbackUrl = props.getProperty("pesapal.callbackURL");
        callbackUrl = URLEncoder.encode(callbackUrl, "UTF-8");
 
        //construct pesapal xml
        String post_xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><PesapalDirectOrderInfo xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" Amount=\""
                + amount + "\" Description=\"" + desc + "\" Type=\"" + type
                + "\" Reference=\"" + reference + "\" FirstName=\"" + first_name
                + "\" LastName=\"" + last_name + "\" Email=\"" + email + "\" PhoneNumber=\""
                + phonenumber + "\" xmlns=\"http://www.pesapal.com\" />";
        post_xml = StringEscapeUtils.escapeXml(post_xml);

        //add other parameters
        Collection<? extends Map.Entry> parameters = new ArrayList<Map.Entry>();
        List<Map.Entry> p = (parameters == null) ? new ArrayList<Map.Entry>(
                1)
                : new ArrayList<Map.Entry>(parameters);
        p.add(new OAuth.Parameter("pesapal_request_data",
                post_xml));
        p.add(new OAuth.Parameter("oauth_callback",
                callbackUrl));
        parameters = p;
        
        //make request
        return client.getRequestResponse(accessor, "GET", parameters);

    }
}
