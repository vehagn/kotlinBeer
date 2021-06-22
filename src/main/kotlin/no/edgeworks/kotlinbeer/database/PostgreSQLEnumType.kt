package no.edgeworks.kotlinbeer.database

import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.type.EnumType
import java.sql.PreparedStatement
import java.sql.Types

class PostgreSQLEnumType<T : Enum<T>> : EnumType<T>() {
    override fun nullSafeSet(
        st: PreparedStatement,
        value: Any?,
        index: Int,
        session: SharedSessionContractImplementor
    ) {
        st.setObject(index, if (value != null) (value as Enum<*>).name else null, Types.OTHER)
    }
}