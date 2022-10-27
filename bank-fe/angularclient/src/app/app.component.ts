import {Component, ViewEncapsulation} from '@angular/core';
import {AccountService} from "../service/account-service.service";
import {Account} from "../model/account";
import {Payment} from "../model/payment";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class AppComponent {

  title: string;
  accounts: Account[] = [];
  potentialDepositAccounts: Account[] = [];
  payment: Payment = new Payment();
  message: string = '';
  targetAccountId: string = '';
  sourceAccountId: string = '';
  isErrorMessage: boolean = false;
  timeout: any;

  constructor(private accountService: AccountService) {
    this.title = 'Bank account Simulator';
  }

  public displayMessageForFiveSeconds(message: string) {
    this.message = message;
    clearTimeout(this.timeout);
    if (this.message) {
      this.timeout = setTimeout(() => {
        this.message = '';
        this.isErrorMessage = false;
      }, 5000);
    }
  }

  public onSourceAccountChanged() {
    this.potentialDepositAccounts = [];
    this.potentialDepositAccounts.push(...this.accounts);
    var account = this.accounts.find(account => parseInt(this.sourceAccountId) === account.id);
    var index = this.accounts.indexOf(account ? account : new Account());
    if (index > -1) {
      this.potentialDepositAccounts.splice(index, 1);
    }
  }

  ngOnInit() {
    this.accountService.findAll().subscribe(data => {
      this.accounts = data;
      this.potentialDepositAccounts = data;
    });
  }

  public toggleEdit(account: Account) {
    if (account.isEditable) {
      this.saveAccount(account);
    }
    account.isEditable = !account.isEditable;
  }

  public saveAccount(account: Account) {
    this.accountService.saveAccount(account).subscribe(data => {
      account.name = data.name;
      account.currency = data.currency;
      account.amount = data.amount;
      this.isErrorMessage = false;
      this.displayMessageForFiveSeconds('Successfully edited account ' + account.name);
    }, error => {
      this.isErrorMessage = true;
      this.displayMessageForFiveSeconds('Editing account ' + account.name + ' failed');
    });
    this.onSourceAccountChanged();
  }

  public createAccount(account: Account) {
    this.accountService.createAccount(account).subscribe(data => {
      account.id = data.id;
      account.name = data.name;
      account.currency = data.currency;
      account.amount = data.amount;
      this.accounts.push(account);
      this.onSourceAccountChanged();
      this.isErrorMessage = false;
      this.displayMessageForFiveSeconds('Successfully created new account ' + account.name);
    }, error => {
      this.isErrorMessage = true;
      this.displayMessageForFiveSeconds('Creating account ' + account.name + ' failed');
    });
    this.onSourceAccountChanged();
  }

  public deleteAccount(account: Account) {
    this.accountService.deleteAccount(account).subscribe(data => {
      var index = this.accounts.indexOf(account)
      if (index > -1) { // only splice array when item is found
        this.accounts.splice(index, 1); // 2nd parameter means remove one item only
      }
      this.onSourceAccountChanged();
      this.isErrorMessage = false;
      this.displayMessageForFiveSeconds('Successfully deleted account ' + account.name);
    }, error => {
      this.isErrorMessage = true;
      this.displayMessageForFiveSeconds('Deleting account ' + account.name + ' failed');
    });
  }

  public transferFundsBetweenAccounts() {
    var sourceAccount = this.accounts.find(account => parseInt(this.sourceAccountId) === account.id);
    var targetAccount = this.accounts.find(account => parseInt(this.targetAccountId) === account.id);
    this.payment.sourceAccount = sourceAccount ? sourceAccount : new Account();
    this.payment.targetAccount = targetAccount ? targetAccount : new Account();
    this.accountService.transferFundsBetweenAccounts(this.payment).subscribe(data => {
      var sourceAccountAfterFundsTransfer = this.accounts.find(account => data.sourceAccount.id === account.id);
      if (sourceAccountAfterFundsTransfer) {
        sourceAccountAfterFundsTransfer.amount = data.sourceAccount.amount;
      }
      var targetAccountAfterFundsTransfer = this.accounts.find(account => data.targetAccount.id === account.id);
      if (targetAccountAfterFundsTransfer) {
        targetAccountAfterFundsTransfer.amount = data.targetAccount.amount;
      }
      this.isErrorMessage = false;
      this.displayMessageForFiveSeconds('Successfully transfered ' + this.payment.paymentAmount + ' ' +
        sourceAccountAfterFundsTransfer?.currency + ' from account ' + sourceAccountAfterFundsTransfer?.name + ' to account ' + targetAccountAfterFundsTransfer?.name);
    }, error => {
      this.isErrorMessage = true;
      this.displayMessageForFiveSeconds('Transfering ' + this.payment.paymentAmount + ' ' +
        this.payment.sourceAccount?.currency + ' from account ' + this.payment.sourceAccount?.name + ' to account ' +
        this.payment.targetAccount?.name + ' failed');
    });
  }

  public createEmptyAccountRow() {
    this.createAccount({name: 'Bank account', id: 0, currency: "EUR", amount: 1000, isEditable: false});
  }

}
