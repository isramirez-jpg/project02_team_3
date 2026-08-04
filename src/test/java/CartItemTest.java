import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CartItemTest {

    @Test
    public void getItemTotalCalculatesCorrectTotal() {

        CartItem item = new CartItem(
                1,
                1,
                10,
                "Blue Shirt",
                new BigDecimal("19.99"),
                3
        );

        BigDecimal expectedTotal =
                new BigDecimal("59.97");

        assertEquals(
                expectedTotal,
                item.getItemTotal()
        );
    }

    @Test
    public void constructorRejectsZeroQuantity() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new CartItem(
                        1,
                        1,
                        10,
                        "Blue Shirt",
                        new BigDecimal("19.99"),
                        0
                )
        );
    }
}