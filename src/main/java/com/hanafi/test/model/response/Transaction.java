/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hanafi.test.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author Hanafi
 *
 * <pre>
 *{
 * "invoice_number": "INV17082023-001",
 * "service_code": "PLN_PRABAYAR",
 * "service_name": "PLN Prabayar",
 * "transaction_type": "PAYMENT",
 * "total_amount": 10000,
 * "created_on": "2023-08-17T10:10:10.000Z"
 *}
 * </pre>
 *
 */
@AllArgsConstructor
@Getter
public class Transaction {

    private final String invoice_number, service_code, service_name, transaction_type, created_on;
    private final long total_amount;
}
