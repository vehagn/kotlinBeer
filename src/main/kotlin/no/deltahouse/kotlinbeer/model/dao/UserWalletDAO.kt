package no.deltahouse.kotlinbeer.model.dao

import java.io.Serializable
import javax.persistence.*

@Entity(name = "USER_WALLETS")
data class UserWalletDAO(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_wallet_id_generator")
    val id: Long = -1,
    @OneToOne
    val user: UserDAO,
    val cashBalance: Int,
    val totalSpent: Int,
    @OneToOne(cascade = [CascadeType.ALL])
    val latestTransaction: TransactionDAO? = null
) : Serializable
