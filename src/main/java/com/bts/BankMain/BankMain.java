package com.bts.BankMain;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.bts.accountdao.AccountDao;
import com.bts.model.Account;
import com.bts.model.Transaction;

public class BankMain {
	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		AccountDao dao = new AccountDao();
		boolean running = true;

		while (running) {
			System.out.println();
			System.out.println("1: add account");
			System.out.println("2: check account");
			System.out.println("3: view all accounts");
			System.out.println("4: update account");
			System.out.println("5: delete account");
			System.out.println("6: deposit");
			System.out.println("7: withdraw");
			System.out.println("8: transfer money");
			System.out.println("9: transaction history");
			System.out.println("10: exit");
			System.out.println("Enter Choice: ");
			int choice = sc.nextInt();

			try {
				switch (choice) {
				case 1:
					System.out.println("Enter Bank id");
					int id = sc.nextInt();
					sc.nextLine();
					System.out.println("Enter name");
					String name = sc.nextLine();
					System.out.println("Enter Mobile Number");
					String mobileno = sc.nextLine();
					System.out.println("Enter initial balance");
					double balance = sc.nextDouble();
					sc.nextLine();
					System.out.println("Enter Account type: ");
					String acctype = sc.nextLine();

					Account a = new Account(id, name, mobileno, balance, acctype);
					System.out.println(dao.createAccount(a));
					break;

				case 2:
					System.out.println("Enter account id: ");
					int acc_id = sc.nextInt();
					Account acc = dao.viewAccount(acc_id);
					if (acc == null) {
						System.out.println("Account Does Not Exist.");
					} else {
						System.out.println("Account Details:");
						System.out.println(acc);
					}
					break;

				case 3:
					List<Account> acclist = dao.viewAllAccounts();
					if (acclist.isEmpty()) {
						System.out.println("Empty list.");
					} else {
						for (Account account : acclist) {
							System.out.println(account);
						}
					}
					break;

				case 4:
					System.out.println("Enter Account Id: ");
					int uid = sc.nextInt();
					sc.nextLine();
					System.out.println("Enter new account holder name: ");
					String newName = sc.nextLine();
					System.out.println("Enter new mobile no: ");
					String newMobile = sc.nextLine();
					System.out.println(dao.updateAccount(uid, newName, newMobile));
					break;

				case 5:
					System.out.println("Enter account id to delete: ");
					int did = sc.nextInt();
					System.out.println(dao.deleteAccount(did));
					break;

				case 6:
					System.out.println("Enter account id: ");
					int depId = sc.nextInt();
					System.out.println("Enter amount to deposit: ");
					double depAmt = sc.nextDouble();

					if (depAmt <= 0) {
						System.out.println("Amount must be greater than 0.");
					} else {
						String depMsg = dao.deposit(depId, depAmt);
						System.out.println(depMsg);
						if (depMsg.equals("Amount Deposited Successfully.")) {
							Account updated = dao.viewAccount(depId);
							System.out.println("Updated Balance: " + updated.getBalance());
						}
					}
					break;

				case 7:
					System.out.println("Enter account id: ");
					int wId = sc.nextInt();
					System.out.println("Enter amount to withdraw: ");
					double wAmt = sc.nextDouble();

					if (wAmt <= 0) {
						System.out.println("Amount must be greater than 0.");
					} else {
						String wMsg = dao.withdraw(wId, wAmt);
						System.out.println(wMsg);
						if (wMsg.equals("Amount Withdrawn Successfully.")) {
							Account updated = dao.viewAccount(wId);
							System.out.println("Updated Balance: " + updated.getBalance());
						}
					}
					break;

				case 8:
					System.out.println("Enter sender account id: ");
					int fromId = sc.nextInt();
					System.out.println("Enter receiver account id: ");
					int toId = sc.nextInt();
					System.out.println("Enter amount to transfer: ");
					double tAmt = sc.nextDouble();

					if (tAmt <= 0) {
						System.out.println("Amount must be greater than 0.");
					} else {
						String tMsg = dao.transfer(fromId, toId, tAmt);
						System.out.println(tMsg);
						if (tMsg.equals("Transfer Successful.")) {
							Account sender = dao.viewAccount(fromId);
							System.out.println("Your Updated Balance: " + sender.getBalance());
						}
					}
					break;

				case 9:
					System.out.println("Enter account id: ");
					int hId = sc.nextInt();
					List<Transaction> history = dao.getHistory(hId);

					if (history.isEmpty()) {
						System.out.println("No transactions found.");
					} else {
						System.out.println("ID   | TYPE         |     AMOUNT |    BALANCE | OTHER A/C | DATE");
						for (Transaction t : history) {
							System.out.println(t);
						}
					}
					break;

				case 10:
					System.out.println("Goodbye!");
					running = false;
					break;

				default:
					System.out.println("Invalid choice, try again.");
					break;
				}
			} catch (SQLException e) {
				System.out.println("Database error: " + e.getMessage());
			}
		}

		sc.close();
	}
}