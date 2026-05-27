/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hanafi.test.model.request;

import lombok.Getter;

/**
 *
 * @author Hanafi
 *
 * <pre>
 *{
 * "email": "user@nutech-integrasi.com",
 * "first_name": "User",
 * "last_name": "Nutech",
 * "password": "abcdef1234"
 *}
 * </pre>
 */
@Getter
public class Registration {

    private String email,password,first_name, last_name;

}
