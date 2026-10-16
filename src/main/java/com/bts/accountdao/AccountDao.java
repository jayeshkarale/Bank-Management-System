package com.bts.accountdao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.bts.config.ConnectionConfig;
import com.bts.model.Account;
import com.bts.model.Transaction;

public class AccountDao {

	public String createAccount(Account acc) throws SQLException {
		String sql = "insert into account (bank_id, name, mobileno, balance, account_type) VALUES (?, ?, ?, ?, ?)";

		try (Connection con = ConnectionConfig.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, acc.getBank_id());
			ps.setString(2, acc.getName());
			ps.setString(3, acc.getMobileno());
			ps.setDouble(4, acc.getBalance());
			ps.setString(5, acc.getAccountType());

			int row = ps.executeUpdate();
			if (row > 0) {
				return "Account Created Successfully.";
			}
			return "Account Not Created!";
		}
	}

	public Account viewAccount(int id) throws SQLException {
		String sql = "select * from account where bank_id = ?";

		try (Connection con = ConnectionConfig.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return new Account(rs.getInt("bank_id"), rs.getString("name"),
							rs.getString("mobileno"), rs.getDouble("balance"),
							rs.getString("account_type"));
				}
			}
		}
		return null;
	}

	public List<Account> viewAllAccounts() throws SQLException {
		String sql = "select * from account";
		List<Account> list = new ArrayList<>();

		try (Connection con = ConnectionConfig.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				list.add(new Account(rs.getInt("bank_id"), rs.getString("name"),
						rs.getString("mobileno"), rs.getDouble("balance"),
						rs.getString("account_type")));
			}
		}
		return list;
	}

	public String updateAccount(int id, String name, String mobileno) throws SQLException {
		String sql = "update account set name = ?, mobileno = ? where bank_id = ?";

		try (Connection con = ConnectionConfig.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, name);
			ps.setString(2, mobileno);
			ps.setInt(3, id);

			return ps.executeUpdate() > 0 ? "Account Updated Successfully." : "Account Not Found.";
		}
	}

	public String deleteAccount(int id) throws SQLException {
		String sql = "delete from account where bank_id = ?";

		try (Connection con = ConnectionConfig.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, id);

			return ps.executeUpdate() > 0 ? "Account Deleted Successfully." : "Account Not Found.";
		}
	}

	// writes one row into transaction_history using the SAME connection
	private void logTransaction(Connection con, int id, String type, double amount, Integer related)
			throws SQLException {
		String sql = "insert into transaction_history (bank_id, txn_type, amount, balance_after, related_account) "
				+ "values (?, ?, ?, (select balance from account where bank_id = ?), ?)";

		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, id);
			ps.setString(2, type);
			ps.setDouble(3, amount);
			ps.setInt(4, id);
			if (related == null) {
				ps.setNull(5, java.sql.Types.INTEGER);
			} else {
				ps.setInt(5, related);
			}
			ps.executeUpdate();
		}
	}

	public String deposit(int id, double amount) throws SQLException {
		String sql = "update account set balance = balance + ? where bank_id = ?";

		try (Connection con = ConnectionConfig.getConnection()) {
			con.setAutoCommit(false);
			try (PreparedStatement ps = con.prepareStatement(sql)) {
				ps.setDouble(1, amount);
				ps.setInt(2, id);

				if (ps.executeUpdate() == 0) {
					con.rollback();
					return "Account Not Found.";
				}
				logTransaction(con, id, "DEPOSIT", amount, null);
				con.commit();
				return "Amount Deposited Successfully.";
			} catch (SQLException e) {
				con.rollback();
				throw e;
			}
		}
	}

	public String withdraw(int id, double amount) throws SQLException {
		String sql = "update account set balance = balance - ? where bank_id = ? and balance >= ?";

		try (Connection con = ConnectionConfig.getConnection()) {
			con.setAutoCommit(false);
			try (PreparedStatement ps = con.prepareStatement(sql)) {
				ps.setDouble(1, amount);
				ps.setInt(2, id);
				ps.setDouble(3, amount);

				if (ps.executeUpdate() == 0) {
					con.rollback();
					return "Account Not Found or Insufficient Balance.";
				}
				logTransaction(con, id, "WITHDRAW", amount, null);
				con.commit();
				return "Amount Withdrawn Successfully.";
			} catch (SQLException e) {
				con.rollback();
				throw e;
			}
		}
	}

	public String transfer(int fromId, int toId, double amount) throws SQLException {
		if (fromId == toId) {
			return "Cannot transfer to the same account.";
		}

		String debit = "update account set balance = balance - ? where bank_id = ? and balance >= ?";
		String credit = "update account set balance = balance + ? where bank_id = ?";

		try (Connection con = ConnectionConfig.getConnection()) {
			con.setAutoCommit(false);
			try {
				try (PreparedStatement ps = con.prepareStatement(debit)) {
					ps.setDouble(1, amount);
					ps.setInt(2, fromId);
					ps.setDouble(3, amount);
					if (ps.executeUpdate() == 0) {
						con.rollback();
						return "Sender Not Found or Insufficient Balance.";
					}
				}

				try (PreparedStatement ps = con.prepareStatement(credit)) {
					ps.setDouble(1, amount);
					ps.setInt(2, toId);
					if (ps.executeUpdate() == 0) {
						con.rollback();
						return "Receiver Account Not Found. Transfer Cancelled.";
					}
				}

				logTransaction(con, fromId, "TRANSFER_OUT", amount, toId);
				logTransaction(con, toId, "TRANSFER_IN", amount, fromId);

				con.commit();
				return "Transfer Successful.";
			} catch (SQLException e) {
				con.rollback();
				throw e;
			}
		}
	}

	public List<Transaction> getHistory(int id) throws SQLException {
		String sql = "select * from transaction_history where bank_id = ? order by txn_date desc, txn_id desc";
		List<Transaction> list = new ArrayList<>();

		try (Connection con = ConnectionConfig.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					int related = rs.getInt("related_account");
					Integer relatedObj = rs.wasNull() ? null : related;

					list.add(new Transaction(rs.getInt("txn_id"), rs.getInt("bank_id"),
							rs.getString("txn_type"), rs.getDouble("amount"),
							rs.getDouble("balance_after"), relatedObj,
							rs.getTimestamp("txn_date")));
				}
			}
		}
		return list;
	}
}