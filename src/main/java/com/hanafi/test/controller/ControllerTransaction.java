/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hanafi.test.controller;

import com.hanafi.test.database.BalanceRepo;
import com.hanafi.test.database.ProductRepo;
import com.hanafi.test.database.TransactionRepo;
import com.hanafi.test.helper.JwtUtil;
import com.hanafi.test.helper.OtherHelper;
import com.hanafi.test.model.response.ApiResponse;
import com.hanafi.test.model.response.BadRequestHandling;
import com.hanafi.test.model.response.Transaction;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Hanafi
 */
@RestController
@AllArgsConstructor
public class ControllerTransaction {

    private final BalanceRepo balanceRepo;
    private final ProductRepo productRepo;
    private final TransactionRepo transactionRepo;
    private final JwtUtil jwtUtil;
    private final OtherHelper otherHelper;

    @GetMapping(path = "/balance", produces = {"application/json"})
    public ApiResponse<?> balance(@RequestHeader("Authorization") String authString) throws Exception {
        try {
            String email = "";
            if (!authString.startsWith("Bearer ")) {
                return ApiResponse.failed("108", "Header Authorization Tidak Valid");
            }

            try {
                String token = authString.substring(7);
                email = jwtUtil.extractUsername(token);
            } catch (ExpiredJwtException e) {
                return ApiResponse.failed("108", "Token Expired");
            } catch (JwtException e) {
                return ApiResponse.failed("108", "Token Tidak Valid");
            }

            Optional<com.hanafi.test.model.response.Balance> balance = balanceRepo.findByEmail(email);

            if (balance.isEmpty()) {
                return ApiResponse.failed("104", "Balance Tidak Ditemukan");
            }

            return ApiResponse.sucess("Get Balance Berhasil", balance.get());
        } catch (Exception er) {
            throw new Exception("Terjadi kesalahan Sistem");
        }
    }

    @Transactional
    @PostMapping(path = "/topup", consumes = {"application/json"}, produces = {"application/json"})
    public ApiResponse<?> topup(@RequestBody com.hanafi.test.model.request.Balance requestBalance, @RequestHeader("Authorization") String authString) throws Exception {
        String email = "";
        long nominal = 0;
        if (!authString.startsWith("Bearer ")) {
            return ApiResponse.failed("108", "Header Authorization Tidak Valid");
        }

        try {
            String token = authString.substring(7);
            email = jwtUtil.extractUsername(token);
        } catch (ExpiredJwtException e) {
            return ApiResponse.failed("108", "Token Expired");
        } catch (JwtException e) {
            return ApiResponse.failed("108", "Token Tidak Valid");
        }

        try {
            nominal = Long.parseLong(requestBalance.getTop_up_amount());
        } catch (NumberFormatException er) {
            throw new BadRequestHandling("Type data harus angka");
        }

        if (nominal < 0) {
            return ApiResponse.failed("104", "Balance Tidak boleh minus");
        }

        if (!balanceRepo.plusBalance(email, nominal)) {
            return ApiResponse.failed("114", "Gagal Topup");
        }

        Optional<com.hanafi.test.model.response.Balance> balance = balanceRepo.findByEmail(email);

        if (balance.isEmpty()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ApiResponse.failed("104", "Balance Tidak Ditemukan");
        }

        return ApiResponse.sucess("Top Up Balance berhasil", balance.get());
    }

    @Transactional
    @PostMapping(path = "/transaction", consumes = {"application/json"}, produces = {"application/json"})
    public ApiResponse<?> transaction(@RequestBody com.hanafi.test.model.request.Transaction requestPayment, @RequestHeader("Authorization") String authString) throws Exception {
        String email = "";
        if (!authString.startsWith("Bearer ")) {
            return ApiResponse.failed("108", "Header Authorization Tidak Valid");
        }

        try {
            String token = authString.substring(7);
            email = jwtUtil.extractUsername(token);
        } catch (ExpiredJwtException e) {
            return ApiResponse.failed("108", "Token Expired");
        } catch (JwtException e) {
            return ApiResponse.failed("108", "Token Tidak Valid");
        }

        Optional<com.hanafi.test.model.response.Balance> balance = balanceRepo.findByEmail(email);

        if (balance.isEmpty()) {
            return ApiResponse.failed("104", "Balance Tidak Ditemukan");
        }
        Optional<com.hanafi.test.model.response.Product> product = productRepo.findByCode(requestPayment.getService_code());

        if (product.isEmpty()) {
            return ApiResponse.failed("104", "Service ataus Layanan tidak ditemukan");
        }

        com.hanafi.test.model.response.Balance balancei = balance.get();
        com.hanafi.test.model.response.Product producti = product.get();

        if (balancei.getBalance() < producti.getService_tariff()) {
            return ApiResponse.failed("121", "Saldo anda tidak cukup");
        }

        String invoice = otherHelper.generateInvoiceNumber();

        if (!transactionRepo.insertTrx(email, producti.getService_code(), producti.getService_name(), invoice, producti.getService_tariff())) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ApiResponse.failed("114", "Transaksi Gagal (insert)");
        }

        Optional<Transaction> findByInvoice = transactionRepo.findByInvoice(invoice);
        if (findByInvoice.isEmpty()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ApiResponse.failed("114", "Transaksi Gagal (get)");
        }

        if (!balanceRepo.reduceBalance(email, producti.getService_tariff())) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ApiResponse.failed("114", "Transaksi Gagal (reduce balance)");
        }

        return ApiResponse.sucess("Transaksi Berhasil", findByInvoice.get());
    }

    @GetMapping(path = "/transaction/history", produces = {"application/json"})
    public ApiResponse<?> transactionHistory(@RequestHeader("Authorization") String authString, @RequestParam(required = false, name = "offset") Integer offset, @RequestParam(required = false, name = "limit") Integer limit) throws Exception {
        String email = "";
        if (!authString.startsWith("Bearer ")) {
            return ApiResponse.failed("108", "Header Authorization Tidak Valid");
        }

        try {
            String token = authString.substring(7);
            email = jwtUtil.extractUsername(token);
        } catch (ExpiredJwtException e) {
            return ApiResponse.failed("108", "Token Expired");
        } catch (JwtException e) {
            return ApiResponse.failed("108", "Token Tidak Valid");
        }

        List<Transaction> findAll = new ArrayList<>();
        if (offset == null && limit == null) {
            findAll.addAll(transactionRepo.findAll(email));
        } else {
            offset = offset == null ? 0 : offset;
            limit = limit == null ? 0 : limit;
            findAll.addAll(transactionRepo.findAll(email, offset, limit));
        }

        if (findAll.size() > 1) {
            findAll = findAll.stream().sorted(Comparator.comparing(Transaction::getCreated_on).reversed()).collect(Collectors.toList());
        }

        return ApiResponse.sucess("Transaksi Berhasil", findAll);
    }
}
