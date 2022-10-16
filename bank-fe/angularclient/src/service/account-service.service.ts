import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import { Account } from '../model/account';
import {Observable} from "rxjs";
import {Payment} from "../model/payment";

@Injectable()
export class AccountService {

  private accountUrl: string;

  constructor(private http: HttpClient) {
      this.accountUrl = 'http://localhost:8080/accounts';
   }

   public findAll(): Observable<Account[]> {
        return this.http.get<Account[]>(this.accountUrl);
    }

    public saveAccount(account: Account){
      return this.http.put<Account>(this.accountUrl,account);
    }

    public createAccount(account: Account){
      return this.http.post<Account>(this.accountUrl,account);
    }

    public deleteAccount(account: Account){
      return this.http.delete<Account>(this.accountUrl, {body: account});
    }

  public transferFundsBetweenAccounts(payment: Payment){
    return this.http.put<Payment>(this.accountUrl+"/transfer",payment);
  }

}
