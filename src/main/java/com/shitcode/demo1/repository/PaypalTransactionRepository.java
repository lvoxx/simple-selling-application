package com.shitcode.demo1.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shitcode.demo1.entity.PaypalTransaction;

public interface PaypalTransactionRepository extends JpaRepository<PaypalTransaction, UUID> {

}
