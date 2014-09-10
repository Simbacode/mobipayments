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
 * @version 0.0.2
 */
public class Pesapal {

	private Properties props;
	private File propFile;

	/**
	 * Constructor for the main pesapal lib java class
	 *
	 * @param fileName
	 *            the path of the config file that contains pesapal details.
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
	 * @param reqUrl
	 *            the URL for making requests
	 *
	 * @see OAuthAccessor
	 * @return OAuthAccessor
	 */
	private OAuthAccessor createOAuthAccessor(String reqUrl) {

		String consumerKey = props.getProperty("pesapal.consumerKey");
		// This is really not needed for pesapal servers at this point.
		// TODO see how to remove this
		String callbackUrl = props.getProperty("pesapal.callbackURL");
		String consumerSecret = props.getProperty("pesapal.consumerSecret");

		OAuthServiceProvider provider = new OAuthServiceProvider(reqUrl,
				reqUrl, reqUrl);
		OAuthConsumer consumer = new OAuthConsumer(callbackUrl, consumerKey,
				consumerSecret, provider);
		return new OAuthAccessor(consumer);
	}

	/**
	 * Makes request to the pesapal server and returns the response that
	 * contains All the OATH details.
	 * 
	 * Use this to post a transaction to PesaPal. PesaPal will return a response
	 * with a page which contains the available payment options and will
	 * redirect to your site once the user has completed the payment process. A
	 * tracking id will be returned as a query parameter – this can be used
	 * subsequently to track the payment status on pesapal for this transaction.
	 *
	 * @see OAuthMessage
	 * @param amount
	 *            the amount of money for the good/service
	 * @param desc
	 *            description of what is being paid for good or service
	 * @param type
	 *            the type of pesapal account eg Merchant
	 * @param reference
	 *            the unique id to your request.
	 * @param email
	 * @param phonenumber
	 * @param first_name
	 * @param last_name
	 * @return {@link OAuthMessage}
	 * @throws IOException
	 * @throws OAuthException
	 * @throws URISyntaxException
	 */
	@SuppressWarnings("rawtypes")
	public OAuthMessage PostPesapalDirectOrderV4(String amount, String desc,
			String type, String reference, String email, String phonenumber,
			String first_name, String last_name) throws IOException,
			OAuthException, URISyntaxException {
		String reqUrl = props
				.getProperty("pesapal.serviceProvider.PostPesapalDirectOrderV4");
		OAuthAccessor accessor = createOAuthAccessor(reqUrl);
		OAuthClient client = new OAuthClient(new HttpClient4());

		String callbackUrl = props.getProperty("pesapal.callbackURL");
		callbackUrl = URLEncoder.encode(callbackUrl, "UTF-8");

		// construct pesapal xml
		String post_xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><PesapalDirectOrderInfo xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" Amount=\""
				+ amount
				+ "\" Description=\""
				+ desc
				+ "\" Type=\""
				+ type
				+ "\" Reference=\""
				+ reference
				+ "\" FirstName=\""
				+ first_name
				+ "\" LastName=\""
				+ last_name
				+ "\" Email=\""
				+ email
				+ "\" PhoneNumber=\""
				+ phonenumber
				+ "\" xmlns=\"http://www.pesapal.com\" />";
		post_xml = StringEscapeUtils.escapeXml(post_xml);

		// add other parameters
		Collection<? extends Map.Entry> parameters = new ArrayList<Map.Entry>();
		List<Map.Entry> p = (parameters == null) ? new ArrayList<Map.Entry>(1)
				: new ArrayList<Map.Entry>(parameters);
		p.add(new OAuth.Parameter("pesapal_request_data", post_xml));
		p.add(new OAuth.Parameter("oauth_callback", callbackUrl));
		parameters = p;

		// make request
		return client.getRequestResponse(accessor, "GET", parameters);

	}

	/**
	 * Makes request to the pesapal server and returns the response that
	 * contains All the OATH details.
	 * 
	 * Use this to post a transaction to PesaPal. PesaPal will return a response
	 * with a page which contains the available payment options and will
	 * redirect to your site once the user has completed the payment process. A
	 * tracking id will be returned as a query parameter – this can be used
	 * subsequently to track the payment status on pesapal for this transaction.
	 *
	 * @see OAuthMessage
	 * @param post_xml
	 *            The XML formated order data.Take not of space.
	 * @return {@link OAuthMessage}
	 * @throws IOException
	 * @throws OAuthException
	 * @throws URISyntaxException
	 */
	@SuppressWarnings("rawtypes")
	public OAuthMessage PostPesapalDirectOrderV4(String post_xml)
			throws IOException, OAuthException, URISyntaxException {
		String reqUrl = props
				.getProperty("pesapal.serviceProvider.PostPesapalDirectOrderV4");
		OAuthAccessor accessor = createOAuthAccessor(reqUrl);
		OAuthClient client = new OAuthClient(new HttpClient4());

		String callbackUrl = props.getProperty("pesapal.callbackURL");
		callbackUrl = URLEncoder.encode(callbackUrl, "UTF-8");

		post_xml = StringEscapeUtils.escapeXml(post_xml);

		// add other parameters
		Collection<? extends Map.Entry> parameters = new ArrayList<Map.Entry>();
		List<Map.Entry> p = (parameters == null) ? new ArrayList<Map.Entry>(1)
				: new ArrayList<Map.Entry>(parameters);
		p.add(new OAuth.Parameter("pesapal_request_data", post_xml));
		p.add(new OAuth.Parameter("oauth_callback", callbackUrl));
		parameters = p;

		// make request
		return client.getRequestResponse(accessor, "GET", parameters);

	}

	/**
	 * Use this to query the status of the transaction. When a transaction is
	 * posted to PesaPal, it may be in a PENDING, COMPLETED or FAILED state. If
	 * the transaction is PENDING, the payment may complete or fail at a later
	 * stage. Both the unique order id generated by your system and the pesapal
	 * tracking id are required as input parameters.
	 * 
	 * @param reference
	 *            the order id/ reference id you created during
	 *            {@link Pesapal#PostPesapalDirectOrderV4}
	 * @param trackingId
	 *            the reference that was returned by pesapal server during post
	 *            order
	 * @return {@link OAuthMessage}
	 * @throws IOException
	 * @throws OAuthException
	 * @throws URISyntaxException
	 */
	@SuppressWarnings("rawtypes")
	public OAuthMessage QueryPaymentStatus(String reference, String trackingId)
			throws IOException, OAuthException, URISyntaxException {

		String reqUrl = props
				.getProperty("pesapal.serviceProvider.QueryPaymentStatus");

		OAuthAccessor accessor = createOAuthAccessor(reqUrl);
		OAuthClient client = new OAuthClient(new HttpClient4());

		reference = URLEncoder.encode(reference, "UTF-8");
		trackingId = URLEncoder.encode(trackingId, "UTF-8");

		// add other parameters
		Collection<? extends Map.Entry> parameters = new ArrayList<Map.Entry>();
		List<Map.Entry> p = (parameters == null) ? new ArrayList<Map.Entry>(1)
				: new ArrayList<Map.Entry>(parameters);
		p.add(new OAuth.Parameter("pesapal_merchant_reference", reference));
		p.add(new OAuth.Parameter("pesapal_transaction_tracking_id", trackingId));
		parameters = p;

		// make request
		return client.getRequestResponse(accessor, "GET", parameters);

	}

	/**
	 * Same as {@link Pesapal#QueryPaymentStatus(String, String)}, but only the
	 * unique order id generated by your system is required as the input
	 * parameter.
	 * 
	 * @param reference
	 *            the unique id generated by your app
	 * @return {@link OAuthMessage}
	 */
	@SuppressWarnings("rawtypes")
	public OAuthMessage QueryPaymentStatusByMerchantRef(String reference)
			throws IOException, OAuthException, URISyntaxException {

		String reqUrl = props
				.getProperty("pesapal.serviceProvider.QueryPaymentStatusByMerchantRef");

		OAuthAccessor accessor = createOAuthAccessor(reqUrl);
		OAuthClient client = new OAuthClient(new HttpClient4());

		reference = URLEncoder.encode(reference, "UTF-8");

		// add other parameters
		Collection<? extends Map.Entry> parameters = new ArrayList<Map.Entry>();
		List<Map.Entry> p = (parameters == null) ? new ArrayList<Map.Entry>(1)
				: new ArrayList<Map.Entry>(parameters);
		p.add(new OAuth.Parameter("pesapal_merchant_reference", reference));
		parameters = p;

		// make request
		return client.getRequestResponse(accessor, "GET", parameters);

	}

	/**
	 * Same as {@link Pesapal#QueryPaymentStatus(String, String)}, but
	 * additional information is returned.
	 * 
	 * @param reference
	 *            the order id/ reference id you created during
	 *            {@link Pesapal#PostPesapalDirectOrderV4}
	 * @param trackingId
	 *            the reference that was returned by pesapal server during post
	 *            order
	 * @return {@link OAuthMessage}
	 * @throws IOException
	 * @throws OAuthException
	 * @throws URISyntaxException
	 */
	@SuppressWarnings("rawtypes")
	public OAuthMessage QueryPaymentDetails(String reference, String trackingId)
			throws IOException, OAuthException, URISyntaxException {

		String reqUrl = props
				.getProperty("pesapal.serviceProvider.QueryPaymentDetails");

		OAuthAccessor accessor = createOAuthAccessor(reqUrl);
		OAuthClient client = new OAuthClient(new HttpClient4());

		reference = URLEncoder.encode(reference, "UTF-8");
		trackingId = URLEncoder.encode(trackingId, "UTF-8");

		// add other parameters
		Collection<? extends Map.Entry> parameters = new ArrayList<Map.Entry>();
		List<Map.Entry> p = (parameters == null) ? new ArrayList<Map.Entry>(1)
				: new ArrayList<Map.Entry>(parameters);
		p.add(new OAuth.Parameter("pesapal_merchant_reference", reference));
		p.add(new OAuth.Parameter("pesapal_transaction_tracking_id", trackingId));
		parameters = p;

		// make request
		return client.getRequestResponse(accessor, "GET", parameters);

	}

	/**
	 * <p>
	 * This is to be used with a java services/servlet/web app that you have
	 * configured to be as your pesapal server IPN URL. When the web app
	 * receives the response from Pesapal, Get the parameters and pass the
	 * details to this function so that pesapal gives you the details about the
	 * transaction status in the format:
	 * pesapal_notification_type=CHANGE&pesapal_transaction_tracking_id =<the
	 * unique tracking id of the transaction>&pesapal_merchant_reference=<the
	 * merchant reference>. Also remember to parse the header of this response
	 * to get the payment status.
	 * 
	 * </p>
	 * <p>
	 * After that remember to send back a response to pesapal in the same format
	 * using your web app.This is to acknowledge the receipt of the sent IPN.
	 * Send back the response after doing some things such as updating records
	 * in your data store.
	 * </p>
	 *
	 * @param notificationType
	 *            this one of the notification types specified by pesapal
	 * @param reference
	 *            the order id/ reference id you created during
	 *            {@link Pesapal#PostPesapalDirectOrderV4}
	 * @param trackingId
	 *            the reference that was returned by pesapal server during post
	 *            order
	 * @return {@link OAuthMessage}
	 * @throws IOException
	 * @throws OAuthException
	 * @throws URISyntaxException
	 */
	@SuppressWarnings("rawtypes")
	public OAuthMessage InstantPaymentNotification(String notificationType,
			String reference, String trackingId) throws IOException,
			OAuthException, URISyntaxException {

		String reqUrl = props
				.getProperty("pesapal.serviceProvider.querypaymentstatus");

		if (notificationType == "CHANGE" && trackingId != "") {
			OAuthAccessor accessor = createOAuthAccessor(reqUrl);
			OAuthClient client = new OAuthClient(new HttpClient4());

			reference = URLEncoder.encode(reference, "UTF-8");
			trackingId = URLEncoder.encode(trackingId, "UTF-8");

			// add other parameters
			Collection<? extends Map.Entry> parameters = new ArrayList<Map.Entry>();
			List<Map.Entry> p = (parameters == null) ? new ArrayList<Map.Entry>(
					1) : new ArrayList<Map.Entry>(parameters);
			p.add(new OAuth.Parameter("pesapal_merchant_reference", reference));
			p.add(new OAuth.Parameter("pesapal_transaction_tracking_id",
					trackingId));
			parameters = p;

			// make request
			return client.getRequestResponse(accessor, "GET", parameters);
		} else {
			return null;
		}

	}

}
