package bootiful

import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertTrue

class CustomerShould {
    @Test
    fun `have an identity`() {
        val id = UUID.randomUUID()
        val firstCustomer = Customer.createFrom(
            id = id,
            fullName = "first customer",
            address = "first customer address",
        )
        val copyOfFirstCustomer = Customer.createFrom(
            id = id,
            fullName = "first customer",
            address = "first customer address",
        )

        assertTrue { firstCustomer == copyOfFirstCustomer }
    }
}