package no.edgeworks.kotlinbeer.model.dao

import java.io.Serializable
import javax.persistence.*

@Entity(name = "WALLETS")
data class WalletDAO(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wallet_id_generator")
    val id: Long = -1,
    @OneToOne
    val user: UserDAO,
    // If this were for real money we should BigDecimal instead of Int
    val cashBalance: Int,
    val totalSpent: Int,
    @OneToOne(cascade = [CascadeType.ALL])
    val latestTransaction: TransactionDAO? = null
) : Serializable
