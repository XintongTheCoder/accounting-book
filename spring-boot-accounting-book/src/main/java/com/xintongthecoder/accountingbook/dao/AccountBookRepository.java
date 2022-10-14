package com.xintongthecoder.accountingbook.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.xintongthecoder.accountingbook.entity.Account;
import com.xintongthecoder.accountingbook.entity.AccountBook;

public interface AccountBookRepository extends JpaRepository<AccountBook, Long> {

    Page<AccountBook> findById(Long id, Pageable pageable);

    Page<AccountBook> findAllByAccount(Account account, Pageable pageable);
}
