package com.simbacode.pesapal.testcases;

/**
 *
 */
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.simbacode.payments.Pesapal;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthServiceProvider;

/**
 * @author Davide Parise mailto:bubini.mara5@gmail.com Sep 15, 2014
 *
 */
public class PostRequestTest {

    // get form details
    String amount = "1000.00";
    String desc = "desc";
    String type = "MERCHANT";
    String reference = "1111";// unique order id of the transaction,
    // generated

    String email = "abc@yahoo.com.com";
    // ONE of email or phonenumber is required
    String phonenumber = "0123456789";
    String first_name = "Acellam";
    String last_name = "Guy";

    Pesapal pesapal = null;
    private Properties props;
    private File propFile;
    String fileName = new File("").getAbsolutePath()
            + "\\pesapal.properties";

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        props = new Properties();
        propFile = new File(fileName);
        props.load(new FileInputStream(propFile));
        pesapal = new Pesapal(fileName);
    }

    @After
    public void tearDown() {
        pesapal = null;
    }
  /**
     * Test method for
     * {@link com.davide.parise.pesapal.post.PostRequest#getURL()}.
     *
     * @throws PostRequestException
     */
    @Test
    public void init() {
        assertNotNull(this.props);
        assertNotNull(this.propFile);
        assertNotNull(this.pesapal);

    }
    /**
     * Test method for
     * {@link com.davide.parise.pesapal.post.PostRequest#getURL()}.
     *
     * @throws PostRequestException
     */
    @Test
    public void testPropertiesServerURL() {

        String PostPesapalDirectOrderV4URL = props.getProperty("pesapal.serviceProvider.PostPesapalDirectOrderV4");
        assertNotNull(PostPesapalDirectOrderV4URL);

        assertEquals("Assert post PostPesapalDirectOrderV4URL", PostPesapalDirectOrderV4URL, "http://demo.pesapal.com/API/PostPesapalDirectOrderV4");
      

    }
    /**
     * Test method for
     * {@link com.davide.parise.pesapal.post.PostRequest#getURL()}.
     *
     * @throws PostRequestException
     */
    @Test
    public void testcreateOAuthAccessor() {
        String consumerKey = props.getProperty("pesapal.consumerKey");
        String callbackUrl = props.getProperty("pesapal.callbackURL");
        String consumerSecret = props.getProperty("pesapal.consumerSecret");
        String reqUrl = props.getProperty("pesapal.serviceProvider.PostPesapalDirectOrderV4");

        OAuthServiceProvider provider = new OAuthServiceProvider(reqUrl,
                reqUrl, reqUrl);
        OAuthConsumer consumer = new OAuthConsumer(callbackUrl, consumerKey,
                consumerSecret, provider);
        OAuthAccessor oa = new OAuthAccessor(consumer);
        assertNotNull(oa);

    }

    /**
     * Test method for
     * {@link com.davide.parise.pesapal.post.PostRequest#getForm()}.
     */
    /*@Test
     public void testGetIframe() {
     fail("Not yet implemented");
     }*/
}
