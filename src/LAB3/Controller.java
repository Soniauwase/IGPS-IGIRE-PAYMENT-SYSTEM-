package LAB3;

import LAB1.Account;
import LAB1.Customer;
import LAB2.AccountDao;
import LAB2.CustomException;
import LAB2.CustomerDao;
import LAB2.TransactionServices;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Controller {
     static final Scanner scanner = new Scanner(System.in);
     static final CustomerDao customerDao = new CustomerDao();
     static final AccountDao accountDao = new AccountDao();
     static final TransactionServices transactionServices=new TransactionServices();
     boolean condition = true;
     int choice;

     public void customerMenu() throws SQLException {
               System.out.println("===== welcome to customer management system =========");
               System.out.println("1.register customer");
               System.out.println("2.update customer information");
               System.out.println("3. view customer account");
               System.out.println("4. exit");
               System.out.println(" enter your choice:");
               int choice = scanner.nextInt();
               scanner.nextLine();
               switch (choice) {
                    case 1 -> {
                         System.out.println("====== welcome to register customer =========");
                         System.out.println("enter customer ID: ");
                         String customer_id = scanner.next();
                         scanner.nextLine();
                         System.out.println("enter customer name:");
                         String customer_name = scanner.nextLine();
                         System.out.println("enter your email:");
                         String email = scanner.nextLine();
                         System.out.println("enter your phone number:");
                         String phone_number = scanner.nextLine();
                         customerDao.registerCustomer(new Customer(customer_id, customer_name, email, phone_number));
                         System.out.println("customer registered successfully!");

                    }
                    case 2 -> {
                         System.out.println("========= Update Customer ==========");
                         System.out.print("Enter Customer ID you want to update: ");
                         String customerId = scanner.next();
                         scanner.nextLine();
                         Customer existingCustomer = customerDao.findById(customerId);

                         if (existingCustomer == null) {
                              System.out.println("Customer with ID " + customerId + " does not exist!");
                              return;
                         }

                         System.out.println("--- existing Customer Details ---");
                         System.out.println("Name: " + existingCustomer.getCustomerName());
                         System.out.println("Email: " + existingCustomer.getEmail());
                         System.out.println("Phone: " + existingCustomer.getPhoneNumber());
                         System.out.println("--------------------------------");

                         System.out.print("Enter new customer name: ");
                         String newName = scanner.nextLine();

                         System.out.print("Enter new email: ");
                         String newEmail = scanner.nextLine();

                         System.out.print("Enter new phone number: ");
                         String newPhone = scanner.nextLine();

                         existingCustomer.setCustomerName(newName);
                         existingCustomer.setEmail(newEmail);
                         existingCustomer.setPhoneNumber(newPhone);

                         customerDao.updateCustomer(existingCustomer);

                    }
                    case 3 -> {
                         System.out.println("===== welcome to viewing customer account===========");


                    }
                    case 4->{
                         System.out.println(" thank you for using our system");
                         return;

                    }


               }

          };


     public void accountMenu() throws SQLException {
               System.out.println("====== Welcome to Account Management===========");
               System.out.println("1. create wallet account");
               System.out.println("2. create  saving account");
               System.out.println("3.view account balance");
               System.out.println("4.delete inactive balance");
               System.out.println("5.Exit");

               System.out.println("enter your choice");
               int choice = scanner.nextInt();
               scanner.nextLine();
               switch (choice) {
                    case 1 -> {
                         System.out.println("====== welcome to create wallet account=========");
                         System.out.println("enter the account id:");
                         String account_id = scanner.next();
                         System.out.println("enter the account Number");
                         String account_number = scanner.next();
                         scanner.nextLine();
                         System.out.println("enter the account Type");
                         String account_type = scanner.nextLine();
                         System.out.println("enter your balance");
                         double balance = scanner.nextDouble();
                         scanner.nextLine();
                         System.out.println("customer Id");
                         String customer_id = scanner.nextLine();
                         Account account = new Account(account_id, account_number, account_type, balance, LocalDateTime.now(), customer_id);
                         Account created = accountDao.creatingAccount(account);
                         if (created != null) {
                              System.out.println("  account:" + account.getAccountType() + "successfully created  for  this customer :" + customer_id);
                         }
                    }
                    case 2 -> {
                         System.out.println("====== welcome to create  Saving account=========");
                         System.out.println("enter the account id:");
                         String account_id = scanner.next();
                         System.out.println("enter the account Number");
                         String account_number = scanner.next();
                         scanner.nextLine();
                         System.out.println("enter the account Type");
                         String account_type = scanner.nextLine();
                         System.out.println("enter your balance");
                         double balance = scanner.nextDouble();
                         scanner.nextLine();
                         System.out.println("customer Id");
                         String customer_id = scanner.nextLine();
                         Account account = new Account(account_id, account_number, account_type, balance, LocalDateTime.now(), customer_id);
                         Account created = accountDao.creatingAccount(account);
                         if (created != null) {
                              System.out.println("  account:" + account.getAccountType() + "successfully created  for  this customer :" + customer_id);
                         }


                    }
                    case 3 -> {
                         System.out.println("====== welcome to view account balance======");
                         System.out.println("enter account ID");
                         String account_id = scanner.next();
                         Account account = accountDao.findAccountById(account_id);
                         if (account != null) {
                              System.out.println(account.getAccountNumber());
                              System.out.println(account.getAccountType());
                              System.out.println(account.getBalance());
                         } else {
                              System.out.println("account with this account_id" + account_id + "is not found!");
                         }

                    }
                    case 4 -> {
                         System.out.println("===== welcome to  delete the inactive account");
                         System.out.println("enter  account id you want to delete");
                         String account_id = scanner.next();
                         boolean isDeleted = accountDao.deleteAccount(account_id);
                         if (isDeleted) {
                              System.out.println("account  with ID :" + account_id + "is deleted successfully!");
                         } else {
                              System.out.println("the deletion failed!");
                         }
                    }
                    case 5 -> {
                         System.out.println(" exiting the account management system");
                         return;

                    }
                    default -> {
                         System.out.println("you made the wrong choice , try again ! inter choice between 1-5 ");
                    }


               }


          };

     public void transactionMenu() throws SQLException {
               System.out.println("============= welcome to transaction management system==============");
               System.out.println("1. Deposit Money");
               System.out.println("2. Withdraw Money");
               System.out.println("3. Transfer Money");
               System.out.println("4. Exit");
               System.out.print("Choose your option ");
               int option = scanner.nextInt();

               try {
                    switch (choice) {
                         case 1:
                              System.out.print("Enter Account ID: ");
                              int depositAccount = Integer.parseInt(scanner.nextLine());

                              System.out.print("Enter Amount to Deposit: ");
                              double depositAmount = Double.parseDouble(scanner.nextLine());
                              String depositRef = "DEP-" + UUID.randomUUID().toString().substring(0, 8);

                              boolean depositResult = transactionServices.deposit(depositRef, depositAccount, depositAmount);
                              if (depositResult) {
                                   System.out.println(" Deposit successful! Reference ID: " + depositRef);
                              }


                         case 2:
                              System.out.print("Enter Account ID: ");
                              int withdrawAccount = Integer.parseInt(scanner.nextLine());

                              System.out.print("Enter Amount to Withdraw: ");
                              double withdrawAmount = Double.parseDouble(scanner.nextLine());

                              String withdrawRef = "WTH-" + UUID.randomUUID().toString().substring(0, 8);

                              boolean withdrawResult = transactionServices.withdraw(withdrawRef, withdrawAccount, withdrawAmount);
                              if (withdrawResult) {
                                   System.out.println("✔ Withdrawal successful! Reference ID: " + withdrawRef);
                              }
                              break;

                         case 3:
                              System.out.print("Enter Sender Account ID: ");
                              int senderAccount = Integer.parseInt(scanner.nextLine());

                              System.out.print("Enter Receiver Account ID: ");
                              int receiverAccount = Integer.parseInt(scanner.nextLine());

                              System.out.print("Enter Amount to Transfer: ");
                              double transferAmount = Double.parseDouble(scanner.nextLine());

                              String transferRef = "TRF-" + UUID.randomUUID().toString().substring(0, 8);

                              boolean transferResult = transactionServices.transfer(transferRef, senderAccount, receiverAccount, transferAmount);
                              if (transferResult) {
                                   System.out.println("✔ Transfer successful! Reference ID: " + transferRef);
                              }
                              break;

                         case 4:
                              System.out.println("Thank you for using IGIRE Payment System. Goodbye!");
                              scanner.close();
                              return;

                         default:
                              System.out.println("Invalid option! Please choose between 1 and 4.");
                    }

               } catch (CustomException.InvalidAmountException | CustomException.DuplicateTransactionException |
                        CustomException.InsufficientBalanceException | CustomException.AccountNotFoundException |
                        CustomException.DatabaseException e){
                    System.out.println(" Please enter valid numeric values for IDs and amounts.");
               } catch (Exception e) {
                    System.out.println( e.getMessage());
               }

               }

          };

