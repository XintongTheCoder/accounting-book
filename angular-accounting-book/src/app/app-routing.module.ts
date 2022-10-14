import { Injector, NgModule } from '@angular/core';
import { Router, RouterModule, Routes } from '@angular/router';
import { AccountBookListComponent } from './components/account-book-list/account-book-list.component';
import { SpendingItemListComponent } from './components/spending-item-list/spending-item-list.component';
import { OktaAuthGuard, OktaCallbackComponent } from '@okta/okta-angular';
import { LandingComponent } from './components/landing/landing.component';
import { LoginComponent } from './components/login/login.component';
import { OktaAuth } from '@okta/okta-auth-js';

function sendToLoginPage(oktaAuth: OktaAuth, injector: Injector) {
  const router = injector.get(Router);
  router.navigate(['/login']);
}

const routes: Routes = [
  { path: '', component: LandingComponent },
  { path: 'login/callback', component: OktaCallbackComponent },
  { path: 'login', component: LoginComponent },
  // [OktaAuthGuard] garantees no one can backdoor the routes or
  // access the routes directly without being authenticated

  {
    path: 'books',
    component: AccountBookListComponent,
    canActivate: [OktaAuthGuard],
    data: { onAuthRequired: sendToLoginPage },
  },
  {
    path: 'books/:id/items',
    component: SpendingItemListComponent,
    canActivate: [OktaAuthGuard],
    data: { onAuthRequired: sendToLoginPage },
  },
  // { path: '', redirectTo: '/books', pathMatch: 'full' },
  // { path: '**', redirectTo: '/books', pathMatch: 'full' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
