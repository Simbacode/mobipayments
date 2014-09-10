/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.simbacode.payments;

import java.io.File;

/**
 *
 * @author User
 */
public class PesapalCMD {   
    
    public static void main(String[] argv) throws Exception {
        
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
        
        //test
        System.out.println(new Pesapal(new File("").getAbsolutePath() + "\\pesapal.properties").PostPesapalDirectOrderV4(amount, desc, type, reference, email, phonenumber, first_name, last_name).readBodyAsString());
    }
}
