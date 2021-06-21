package limitmutability

import java.lang.Exception

class InsufficientFunds: Exception()

class BankAccount {
    var balance = 0.0
        private set

    fun deposit(depositAmount: Double) {
        balance += depositAmount
    }

    @Throws
    fun withdraw(withdrawAmount: Double) {
        if(balance < withdrawAmount) {
            throw InsufficientFunds()
        } else {
            balance -= withdrawAmount
        }
    }
}

fun main() {
    val account = BankAccount()
    println(account.balance)
    account.deposit(100.0)
    println(account.balance)
    account.withdraw(50.0)
    println(account.balance)
}