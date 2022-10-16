import {Account} from "./account";

export class Payment {
  sourceAccount: Account = {id: 0,isEditable: false, currency: "EUR", amount: 0, name: ""};
  targetAccount: Account = {id: 0,isEditable: false, currency: "EUR", amount: 0, name: ""};
  paymentAmount: number = 0;
}
