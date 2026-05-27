/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hanafi.test.helper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 *
 * @author Hanafi
 */
@Component
public class OtherHelper {

    public String generateInvoiceNumber() {
        String prefix = "INV";
        String datePart = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String randomPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return prefix + "-" + datePart + "-" + randomPart;
    }

}
