package com.example.fragment

// DB-backed menu item
data class MenuItemData(
    val id          : Int    = 0,
    val name        : String,
    val description : String = "",
    val category    : String,
    val priceS      : Double = 0.0,
    val priceM      : Double = 0.0,
    val priceL      : Double = 0.0,
    val priceReg    : Double = 0.0,
    val imageName   : String = "ic_food"
) {
    val hasSizes: Boolean get() = priceS > 0 || priceM > 0 || priceL > 0
    fun priceForSize(size: String): Double = when (size) {
        "S" -> priceS; "M" -> priceM; "L" -> priceL
        else -> priceReg
    }
    fun defaultPrice(): Double = if (hasSizes) priceM else priceReg
    fun defaultSize(): String  = if (hasSizes) "M" else "Regular"
}

//  A single line in the cart / receipt
data class CartLineItem(
    val menuItem : MenuItemData,
    val size     : String,
    val price    : Double,
    var quantity : Int = 1
) {
    val subtotal: Double get() = price * quantity
}

//  Saved order line (for DB)
data class OrderLineItem(
    val name    : String,
    val size    : String,
    val price   : Double,
    val quantity: Int
) {
    val subtotal: Double get() = price * quantity
}

//  Completed transaction
data class TransactionRecord(
    val id           : Int    = 0,
    val ticketNumber : Int,
    val cashierId    : String,
    val total        : Double,
    val date         : String,
    val cashTendered : Double = 0.0,
    val changeAmount : Double = 0.0,
    val items        : List<OrderLineItem> = emptyList()
)

//  Employee
data class Employee(
    val id        : Int,
    val cashierId : String,
    val password  : String,
    val name      : String,
    val role      : String,
    val email     : String,
    val phone     : String,
    val shift     : String
)

//  Best seller summary
data class BestSellerItem(
    val name    : String,
    val size    : String,
    val totalQty: Int,
    val totalRev: Double
)
