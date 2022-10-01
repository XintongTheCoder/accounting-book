import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AccountBookListComponent } from './components/account-book-list/account-book-list.component';
import { AccountBookService } from './services/account-book.service';
import { SpendingItemListComponent } from './components/spending-item-list/spending-item-list.component';
import { MatCardModule } from '@angular/material/card';

import { MatTableModule } from '@angular/material/table';
import { AccountService } from './services/account.service';

@NgModule({
  declarations: [
    AppComponent,
    AccountBookListComponent,
    SpendingItemListComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    MatCardModule,
    MatTableModule,
  ],
  providers: [AccountBookService, AccountService],
  bootstrap: [AppComponent],
})
export class AppModule {}
