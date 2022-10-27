# BankAccountSimulator
This is a bank account simulator app where the user can create, edit and delete bank accounts.
In addition, the user can transfer funds between his accounts and convert accounts to use either
USD or EUR as currency. The system validates if the user has enough funds for the transfer and that the transfer amount
isn't zero or negative.

### Guides
The following guide details how to run the application:

* Execute mvn clean install -U to build the project, install node and other dependencies
* Run the main method in class BankAccountSimulatorApplication.java to launch the Java backend.
* Front end is written using Angular, so to launch it, navigate to bank-fe/angularclient/ and run 'ng serve' to launch the Angular front end.
* Open app at address http://localhost:4200/
* Tests for the application are located in the BankAccountSimulatorApplicationTests.java class

