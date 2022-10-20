package com.xintongthecoder.accountingbook.controller;

import java.util.ArrayList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.xintongthecoder.accountingbook.dao.AccountBookRepository;
import com.xintongthecoder.accountingbook.dao.AccountRepository;
import com.xintongthecoder.accountingbook.entity.Account;
import com.xintongthecoder.accountingbook.entity.AccountBook;
import com.xintongthecoder.accountingbook.errorHandler.AccountAccessDeniedException;
import com.xintongthecoder.accountingbook.errorHandler.AccountBookNotFoundException;
import com.xintongthecoder.accountingbook.modelAssembler.AccountBookModelAssembler;


@RestController
@RequestMapping("api/accounts")
@ExposesResourceFor(AccountBook.class)
public class AccountBookController {

        private final AccountBookRepository accountBookRepository;
        private final AccountBookModelAssembler accountBookModelAssembler;
        private final PagedResourcesAssembler<AccountBook> pagedResourcesAssembler;
        private final AccountRepository accountRepository;

        public AccountBookController(AccountBookRepository accountBookRepository,
                        AccountBookModelAssembler accountBookModelAssembler,
                        PagedResourcesAssembler<AccountBook> pagedResourcesAssembler,
                        AccountRepository accountRepository) {
                this.accountBookRepository = accountBookRepository;
                this.accountBookModelAssembler = accountBookModelAssembler;
                this.pagedResourcesAssembler = pagedResourcesAssembler;
                this.accountRepository = accountRepository;
        }

        @GetMapping(value = "{email}/books/{bookId}", produces = {"application/hal+json"})
        public ResponseEntity<PagedModel<EntityModel<AccountBook>>> one(
                        @PathVariable("email") String email, @PathVariable Long bookId,
                        @RequestParam(value = "page", defaultValue = "0") Integer page,
                        @RequestParam(value = "size", defaultValue = "10") Integer size) {
                if (!requestFromAuthorizedAccount(email, bookId)) {
                        throw new AccountAccessDeniedException("book");
                }
                Page<AccountBook> pagedBook =
                                accountBookRepository.findById(bookId, PageRequest.of(page, size));
                return ResponseEntity.ok().contentType(MediaTypes.HAL_JSON)
                                .body(pagedResourcesAssembler.toModel(pagedBook,
                                                accountBookModelAssembler));
        }

        private boolean requestFromAuthorizedAccount(String email, Long bookId) {
                if (accountBookRepository.getReferenceById(bookId) == null) {
                        throw new AccountBookNotFoundException(bookId);
                }
                return accountBookRepository.findById(bookId).get().getAccount().getEmail()
                                .equals(email);
        }

        @GetMapping(value = "/{email}/books", produces = {"application/hal+json"})
        public ResponseEntity<PagedModel<EntityModel<AccountBook>>> all(
                        @PathVariable("email") String email,
                        @RequestParam(value = "page", defaultValue = "0") Integer page,
                        @RequestParam(value = "size", defaultValue = "10") Integer size) {
                Account account = accountRepository.findByEmail(email);
                if (account == null) {
                        account = new Account();
                        account.setEmail(email);
                        account.setAccountBooks(new ArrayList<>());
                        accountRepository.save(account);
                }
                Page<AccountBook> pagedBooks = accountBookRepository.findAllByAccount(account,
                                PageRequest.of(page, size));
                return ResponseEntity.ok().contentType(MediaTypes.HAL_JSON)
                                .body(pagedResourcesAssembler.toModel(pagedBooks,
                                                accountBookModelAssembler));
        }

        @PostMapping(value = "/{email}/books", produces = {"application/hal+json"})
        public ResponseEntity<AccountBook> addBook(@PathVariable("email") String email,
                        @RequestBody AccountBook bookFromRequest) {
                Account account = accountRepository.findByEmail(email);
                bookFromRequest.setAccount(account);
                AccountBook newBook = this.accountBookRepository.save(bookFromRequest);
                return new ResponseEntity<>(newBook, HttpStatus.CREATED);
        }

        @PutMapping(value = "{email}/books/{bookId}", produces = {"application/hal+json"})
        public ResponseEntity<AccountBook> editBook(@PathVariable("email") String email,
                        @PathVariable("bookId") Long bookId,
                        @RequestBody AccountBook bookFromRequest) {
                if (!requestFromAuthorizedAccount(email, bookId)) {
                        throw new AccountAccessDeniedException("book");
                }
                Account account = accountRepository.findByEmail(email);
                bookFromRequest.setAccount(account);
                AccountBook updatedBook = accountBookRepository.save(bookFromRequest);
                return new ResponseEntity<AccountBook>(updatedBook, HttpStatus.OK);
        }

        @DeleteMapping(value = "{email}/books/{bookId}", produces = {"application/hal+json"})
        public ResponseEntity<HttpStatus> deleteBook(@PathVariable("email") String email,
                        @PathVariable("bookId") Long bookId) {
                if (!requestFromAuthorizedAccount(email, bookId)) {
                        throw new AccountAccessDeniedException("book");
                }
                accountBookRepository.deleteById(bookId);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
}
