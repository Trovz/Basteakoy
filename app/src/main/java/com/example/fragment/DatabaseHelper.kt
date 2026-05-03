package com.example.fragment

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        const val DB_NAME    = "basteakoy.db"
        const val DB_VERSION = 3

        //  employees
        const val TBL_EMP        = "employees"
        const val EMP_ID         = "id"
        const val EMP_CASHIER_ID = "cashier_id"
        const val EMP_PASSWORD   = "password"
        const val EMP_NAME       = "name"
        const val EMP_ROLE       = "role"
        const val EMP_EMAIL      = "email"
        const val EMP_PHONE      = "phone"
        const val EMP_SHIFT      = "shift"

        //  menu_items
        const val TBL_MENU       = "menu_items"
        const val MENU_ID        = "id"
        const val MENU_NAME      = "name"
        const val MENU_DESC      = "description"
        const val MENU_CATEGORY  = "category"
        const val MENU_PRICE_S   = "price_s"
        const val MENU_PRICE_M   = "price_m"
        const val MENU_PRICE_L   = "price_l"
        const val MENU_PRICE_REG = "price_regular"
        const val MENU_IMAGE     = "image_name"
        const val MENU_ACTIVE    = "is_active"

        // transactions
        const val TBL_TXN        = "transactions"
        const val TXN_ID         = "id"
        const val TXN_TICKET     = "ticket_number"
        const val TXN_CASHIER_ID = "cashier_id"
        const val TXN_TOTAL      = "total_amount"
        const val TXN_DATE       = "date_created"
        const val TXN_CASH       = "cash_tendered"
        const val TXN_CHANGE     = "change_amount"

        //  transaction_items
        const val TBL_ITEMS      = "transaction_items"
        const val ITEM_ID        = "id"
        const val ITEM_TXN_ID    = "transaction_id"
        const val ITEM_NAME      = "item_name"
        const val ITEM_SIZE      = "item_size"
        const val ITEM_PRICE     = "price"
        const val ITEM_QTY       = "quantity"
        const val ITEM_SUBTOTAL  = "subtotal"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TBL_EMP (
                $EMP_ID         INTEGER PRIMARY KEY AUTOINCREMENT,
                $EMP_CASHIER_ID TEXT    NOT NULL UNIQUE,
                $EMP_PASSWORD   TEXT    NOT NULL,
                $EMP_NAME       TEXT    NOT NULL,
                $EMP_ROLE       TEXT    DEFAULT 'cashier',
                $EMP_EMAIL      TEXT,
                $EMP_PHONE      TEXT,
                $EMP_SHIFT      TEXT    DEFAULT 'Morning Shift (6AM - 2PM)'
            )
        """)
        db.execSQL("""
            CREATE TABLE $TBL_MENU (
                $MENU_ID        INTEGER PRIMARY KEY AUTOINCREMENT,
                $MENU_NAME      TEXT    NOT NULL,
                $MENU_DESC      TEXT,
                $MENU_CATEGORY  TEXT    NOT NULL,
                $MENU_PRICE_S   REAL    DEFAULT 0,
                $MENU_PRICE_M   REAL    DEFAULT 0,
                $MENU_PRICE_L   REAL    DEFAULT 0,
                $MENU_PRICE_REG REAL    DEFAULT 0,
                $MENU_IMAGE     TEXT    DEFAULT 'ic_food',
                $MENU_ACTIVE    INTEGER DEFAULT 1
            )
        """)
        db.execSQL("""
            CREATE TABLE $TBL_TXN (
                $TXN_ID         INTEGER PRIMARY KEY AUTOINCREMENT,
                $TXN_TICKET     INTEGER NOT NULL,
                $TXN_CASHIER_ID TEXT,
                $TXN_TOTAL      REAL    DEFAULT 0,
                $TXN_DATE       TEXT    NOT NULL,
                $TXN_CASH       REAL    DEFAULT 0,
                $TXN_CHANGE     REAL    DEFAULT 0
            )
        """)
        db.execSQL("""
            CREATE TABLE $TBL_ITEMS (
                $ITEM_ID        INTEGER PRIMARY KEY AUTOINCREMENT,
                $ITEM_TXN_ID    INTEGER NOT NULL,
                $ITEM_NAME      TEXT    NOT NULL,
                $ITEM_SIZE      TEXT,
                $ITEM_PRICE     REAL    DEFAULT 0,
                $ITEM_QTY       INTEGER DEFAULT 1,
                $ITEM_SUBTOTAL  REAL    DEFAULT 0,
                FOREIGN KEY($ITEM_TXN_ID) REFERENCES $TBL_TXN($TXN_ID)
            )
        """)
        seedData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, old: Int, new: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TBL_ITEMS")
        db.execSQL("DROP TABLE IF EXISTS $TBL_TXN")
        db.execSQL("DROP TABLE IF EXISTS $TBL_MENU")
        db.execSQL("DROP TABLE IF EXISTS $TBL_EMP")
        onCreate(db)
    }

    //  Seed default data

    private fun seedData(db: SQLiteDatabase) {
        // Employees
        insertEmployee(db, "1001", "basteakoy123", "Juan Dela Cruz", "cashier",
            "juan@basteakoy.com", "+63 912 345 6789", "Morning Shift (6AM - 2PM)")
        insertEmployee(db, "9999", "admin@basteakoy", "Administrator", "admin",
            "admin@basteakoy.com", "+63 900 000 0000", "All Day")

        // Fruits
        insertMenu(db, "Mango",       "Sweet and juicy tropical fruit",    "Fruits", 50.0, 78.0, 100.0, 0.0, "mango")
        insertMenu(db, "Melon",       "Cool and refreshing summer fruit",  "Fruits", 55.0, 80.0, 105.0, 0.0, "melon")
        insertMenu(db, "Avocado",     "Creamy and nutritious green fruit", "Fruits", 60.0, 85.0, 110.0, 0.0, "avocado")
        insertMenu(db, "Strawberry",  "Sweet and tangy red berries",       "Fruits", 65.0, 90.0, 115.0, 0.0, "strawberry")
        insertMenu(db, "Dragon Fruit","Exotic and colorful tropical fruit","Fruits", 70.0, 95.0, 120.0, 0.0, "dragonfrt")
        // Pizza
        insertMenu(db, "Pepperoni",   "Classic pepperoni with mozzarella", "Pizza",  99.0,149.0, 199.0, 0.0, "pepperoni")
        insertMenu(db, "Beef and Mushroom",  "Fresh tomato and basil pizza",      "Pizza",  89.0,139.0, 189.0, 0.0, "beefandmushroom")
        insertMenu(db, "Supreme Special", "Smoky BBQ with grilled chicken",    "Pizza",  99.0,149.0, 199.0, 0.0, "supremespecial")
        insertMenu(db, "Hawaiian",    "Ham and pineapple tropical pizza",  "Pizza",  89.0,139.0, 189.0, 0.0, "hawaiian")
        insertMenu(db, "Ham and Cheese", "Blend of four premium cheeses",     "Pizza", 109.0,159.0, 209.0, 0.0, "hamandcheese")
        // Soda
        insertMenu(db, "Coke",   "Ice cold Coca-Cola",             "Soda", 0.0, 0.0, 0.0, 35.0, "coke")
        insertMenu(db, "Royal",  "Refreshing orange soda",         "Soda", 0.0, 0.0, 0.0, 35.0, "royal")
        insertMenu(db, "Sprite", "Cool and crisp lemon-lime soda", "Soda", 0.0, 0.0, 0.0, 35.0, "sprite")
        // Snacks
        insertMenu(db, "Siomai", "Steamed pork dumplings", "Snacks", 0.0, 0.0, 0.0, 25.0, "siomai")
        insertMenu(db, "Fries",  "Crunchy and tasty snack","Snacks", 0.0, 0.0, 0.0, 15.0, "prize")
    }

    private fun insertEmployee(db: SQLiteDatabase, cashierId: String, password: String,
        name: String, role: String, email: String, phone: String, shift: String) {
        db.insert(TBL_EMP, null, ContentValues().apply {
            put(EMP_CASHIER_ID, cashierId); put(EMP_PASSWORD, password)
            put(EMP_NAME, name);            put(EMP_ROLE, role)
            put(EMP_EMAIL, email);          put(EMP_PHONE, phone)
            put(EMP_SHIFT, shift)
        })
    }

    private fun insertMenu(db: SQLiteDatabase, name: String, desc: String, cat: String,
        ps: Double, pm: Double, pl: Double, pr: Double, img: String) {
        db.insert(TBL_MENU, null, ContentValues().apply {
            put(MENU_NAME, name);     put(MENU_DESC, desc);   put(MENU_CATEGORY, cat)
            put(MENU_PRICE_S, ps);    put(MENU_PRICE_M, pm);  put(MENU_PRICE_L, pl)
            put(MENU_PRICE_REG, pr);  put(MENU_IMAGE, img)
        })
    }

    fun getEmployee(cashierId: String): Employee? {
        val db = readableDatabase
        val cur = db.query(TBL_EMP, null, "$EMP_CASHIER_ID=?", arrayOf(cashierId), null, null, null)
        return if (cur.moveToFirst()) {
            Employee(
                id       = cur.getInt(cur.getColumnIndexOrThrow(EMP_ID)),
                cashierId= cur.getString(cur.getColumnIndexOrThrow(EMP_CASHIER_ID)),
                password = cur.getString(cur.getColumnIndexOrThrow(EMP_PASSWORD)),
                name     = cur.getString(cur.getColumnIndexOrThrow(EMP_NAME)),
                role     = cur.getString(cur.getColumnIndexOrThrow(EMP_ROLE)),
                email    = cur.getString(cur.getColumnIndexOrThrow(EMP_EMAIL)) ?: "",
                phone    = cur.getString(cur.getColumnIndexOrThrow(EMP_PHONE)) ?: "",
                shift    = cur.getString(cur.getColumnIndexOrThrow(EMP_SHIFT)) ?: ""
            ).also { cur.close() }
        } else { cur.close(); null }
    }
    fun getAllMenuItems(activeOnly: Boolean = true): List<MenuItemData> {
        val db = readableDatabase
        val where = if (activeOnly) "$MENU_ACTIVE=1" else null
        val cur = db.query(TBL_MENU, null, where, null, null, null, "$MENU_CATEGORY, $MENU_NAME")
        val list = mutableListOf<MenuItemData>()
        while (cur.moveToNext()) list.add(curToMenu(cur))
        cur.close()
        return list
    }

    fun getMenuByCategory(category: String): List<MenuItemData> {
        val db = readableDatabase
        val cur = db.query(TBL_MENU, null,
            "$MENU_CATEGORY=? AND $MENU_ACTIVE=1", arrayOf(category),
            null, null, MENU_NAME)
        val list = mutableListOf<MenuItemData>()
        while (cur.moveToNext()) list.add(curToMenu(cur))
        cur.close()
        return list
    }

    fun searchMenu(query: String): List<MenuItemData> {
        val db = readableDatabase
        val cur = db.query(TBL_MENU, null,
            "$MENU_NAME LIKE ? AND $MENU_ACTIVE=1", arrayOf("%$query%"),
            null, null, MENU_NAME)
        val list = mutableListOf<MenuItemData>()
        while (cur.moveToNext()) list.add(curToMenu(cur))
        cur.close()
        return list
    }

    fun addMenuItem(item: MenuItemData): Long {
        val db = writableDatabase
        return db.insert(TBL_MENU, null, ContentValues().apply {
            put(MENU_NAME, item.name);        put(MENU_DESC, item.description)
            put(MENU_CATEGORY, item.category);put(MENU_PRICE_S, item.priceS)
            put(MENU_PRICE_M, item.priceM);   put(MENU_PRICE_L, item.priceL)
            put(MENU_PRICE_REG, item.priceReg); put(MENU_IMAGE, item.imageName)
        })
    }

    fun updateMenuItem(item: MenuItemData): Int {
        val db = writableDatabase
        return db.update(TBL_MENU, ContentValues().apply {
            put(MENU_NAME, item.name);        put(MENU_DESC, item.description)
            put(MENU_CATEGORY, item.category);put(MENU_PRICE_S, item.priceS)
            put(MENU_PRICE_M, item.priceM);   put(MENU_PRICE_L, item.priceL)
            put(MENU_PRICE_REG, item.priceReg)
        }, "$MENU_ID=?", arrayOf(item.id.toString()))
    }

    fun deleteMenuItem(id: Int): Int {
        val db = writableDatabase
        // Soft delete - mark inactive
        return db.update(TBL_MENU, ContentValues().apply {
            put(MENU_ACTIVE, 0)
        }, "$MENU_ID=?", arrayOf(id.toString()))
    }

    private fun curToMenu(cur: android.database.Cursor) = MenuItemData(
        id          = cur.getInt(cur.getColumnIndexOrThrow(MENU_ID)),
        name        = cur.getString(cur.getColumnIndexOrThrow(MENU_NAME)),
        description = cur.getString(cur.getColumnIndexOrThrow(MENU_DESC)) ?: "",
        category    = cur.getString(cur.getColumnIndexOrThrow(MENU_CATEGORY)),
        priceS      = cur.getDouble(cur.getColumnIndexOrThrow(MENU_PRICE_S)),
        priceM      = cur.getDouble(cur.getColumnIndexOrThrow(MENU_PRICE_M)),
        priceL      = cur.getDouble(cur.getColumnIndexOrThrow(MENU_PRICE_L)),
        priceReg    = cur.getDouble(cur.getColumnIndexOrThrow(MENU_PRICE_REG)),
        imageName   = cur.getString(cur.getColumnIndexOrThrow(MENU_IMAGE)) ?: "ic_food"
    )

    fun saveTransaction(txn: TransactionRecord): Long {
        val db = writableDatabase
        val txnId = db.insert(TBL_TXN, null, ContentValues().apply {
            put(TXN_TICKET,     txn.ticketNumber)
            put(TXN_CASHIER_ID, txn.cashierId)
            put(TXN_TOTAL,      txn.total)
            put(TXN_DATE,       txn.date)
            put(TXN_CASH,       txn.cashTendered)
            put(TXN_CHANGE,     txn.changeAmount)
        })
        txn.items.forEach { item ->
            db.insert(TBL_ITEMS, null, ContentValues().apply {
                put(ITEM_TXN_ID,  txnId)
                put(ITEM_NAME,    item.name)
                put(ITEM_SIZE,    item.size)
                put(ITEM_PRICE,   item.price)
                put(ITEM_QTY,     item.quantity)
                put(ITEM_SUBTOTAL,item.price * item.quantity)
            })
        }
        return txnId
    }

    fun getAllTransactions(): List<TransactionRecord> {
        val db = readableDatabase
        val cur = db.query(TBL_TXN, null, null, null, null, null, "$TXN_DATE DESC")
        val list = mutableListOf<TransactionRecord>()
        while (cur.moveToNext()) {
            val txnId = cur.getInt(cur.getColumnIndexOrThrow(TXN_ID))
            list.add(TransactionRecord(
                id           = txnId,
                ticketNumber = cur.getInt(cur.getColumnIndexOrThrow(TXN_TICKET)),
                cashierId    = cur.getString(cur.getColumnIndexOrThrow(TXN_CASHIER_ID)) ?: "",
                total        = cur.getDouble(cur.getColumnIndexOrThrow(TXN_TOTAL)),
                date         = cur.getString(cur.getColumnIndexOrThrow(TXN_DATE)),
                cashTendered = cur.getDouble(cur.getColumnIndexOrThrow(TXN_CASH)),
                changeAmount = cur.getDouble(cur.getColumnIndexOrThrow(TXN_CHANGE)),
                items        = getTransactionItems(txnId)
            ))
        }
        cur.close()
        return list
    }

    fun getTransactionsByDate(datePrefix: String): List<TransactionRecord> {
        val db = readableDatabase
        val cur = db.query(TBL_TXN, null,
            "$TXN_DATE LIKE ?", arrayOf("$datePrefix%"),
            null, null, "$TXN_DATE DESC")
        val list = mutableListOf<TransactionRecord>()
        while (cur.moveToNext()) {
            val txnId = cur.getInt(cur.getColumnIndexOrThrow(TXN_ID))
            list.add(TransactionRecord(
                id           = txnId,
                ticketNumber = cur.getInt(cur.getColumnIndexOrThrow(TXN_TICKET)),
                cashierId    = cur.getString(cur.getColumnIndexOrThrow(TXN_CASHIER_ID)) ?: "",
                total        = cur.getDouble(cur.getColumnIndexOrThrow(TXN_TOTAL)),
                date         = cur.getString(cur.getColumnIndexOrThrow(TXN_DATE)),
                cashTendered = cur.getDouble(cur.getColumnIndexOrThrow(TXN_CASH)),
                changeAmount = cur.getDouble(cur.getColumnIndexOrThrow(TXN_CHANGE)),
                items        = getTransactionItems(txnId)
            ))
        }
        cur.close()
        return list
    }

    private fun getTransactionItems(txnId: Int): List<OrderLineItem> {
        val db = readableDatabase
        val cur = db.query(TBL_ITEMS, null, "$ITEM_TXN_ID=?", arrayOf(txnId.toString()), null, null, null)
        val items = mutableListOf<OrderLineItem>()
        while (cur.moveToNext()) {
            items.add(OrderLineItem(
                name     = cur.getString(cur.getColumnIndexOrThrow(ITEM_NAME)),
                size     = cur.getString(cur.getColumnIndexOrThrow(ITEM_SIZE)) ?: "",
                price    = cur.getDouble(cur.getColumnIndexOrThrow(ITEM_PRICE)),
                quantity = cur.getInt(cur.getColumnIndexOrThrow(ITEM_QTY))
            ))
        }
        cur.close()
        return items
    }

    fun getTodaySalesTotal(datePrefix: String): Double {
        val db = readableDatabase
        val cur = db.rawQuery(
            "SELECT SUM($TXN_TOTAL) FROM $TBL_TXN WHERE $TXN_DATE LIKE ?",
            arrayOf("$datePrefix%"))
        val total = if (cur.moveToFirst()) cur.getDouble(0) else 0.0
        cur.close()
        return total
    }

    fun getTodayOrderCount(datePrefix: String): Int {
        val db = readableDatabase
        val cur = db.rawQuery(
            "SELECT COUNT(*) FROM $TBL_TXN WHERE $TXN_DATE LIKE ?",
            arrayOf("$datePrefix%"))
        val count = if (cur.moveToFirst()) cur.getInt(0) else 0
        cur.close()
        return count
    }

    fun getBestSellingItems(datePrefix: String): List<BestSellerItem> {
        val db = readableDatabase
        val cur = db.rawQuery("""
            SELECT i.$ITEM_NAME, i.$ITEM_SIZE,
                   SUM(i.$ITEM_QTY) as total_qty,
                   SUM(i.$ITEM_SUBTOTAL) as total_revenue
            FROM $TBL_ITEMS i
            INNER JOIN $TBL_TXN t ON i.$ITEM_TXN_ID = t.$TXN_ID
            WHERE t.$TXN_DATE LIKE ?
            GROUP BY i.$ITEM_NAME, i.$ITEM_SIZE
            ORDER BY total_qty DESC
        """, arrayOf("$datePrefix%"))
        val list = mutableListOf<BestSellerItem>()
        while (cur.moveToNext()) {
            list.add(BestSellerItem(
                name     = cur.getString(0),
                size     = cur.getString(1) ?: "",
                totalQty = cur.getInt(2),
                totalRev = cur.getDouble(3)
            ))
        }
        cur.close()
        return list
    }

    fun getNextTicketNumber(): Int {
        val db = readableDatabase
        val cur = db.rawQuery("SELECT MAX($TXN_TICKET) FROM $TBL_TXN", null)
        val max = if (cur.moveToFirst()) cur.getInt(0) else 0
        cur.close()
        return max + 1
    }

    fun registerEmployee(
        cashierId: String, password: String, name: String,
        email: String, phone: String, shift: String
    ): RegisterResult {
        if (cashierId.length < 4) return RegisterResult.INVALID_ID
        if (password.length < 6)  return RegisterResult.WEAK_PASSWORD
        if (getEmployee(cashierId) != null) return RegisterResult.ID_EXISTS
        val db = writableDatabase
        val rows = db.insert(TBL_EMP, null, ContentValues().apply {
            put(EMP_CASHIER_ID, cashierId)
            put(EMP_PASSWORD, password)
            put(EMP_NAME, name)
            put(EMP_ROLE, "cashier")
            put(EMP_EMAIL, email)
            put(EMP_PHONE, phone)
            put(EMP_SHIFT, shift)
        })
        return if (rows > 0) RegisterResult.SUCCESS else RegisterResult.ERROR
    }

    enum class RegisterResult {
        SUCCESS, ID_EXISTS, INVALID_ID, WEAK_PASSWORD, ERROR
    }

    //  Update product image path
    fun updateMenuImagePath(id: Int, imagePath: String): Int {
        val db = writableDatabase
        return db.update(TBL_MENU,
            ContentValues().apply { put(MENU_IMAGE, imagePath) },
            "$MENU_ID=?", arrayOf(id.toString()))
    }

    // Hard delete (for admin manage products)
    fun hardDeleteMenuItem(id: Int): Int {
        val db = writableDatabase
        return db.delete(TBL_MENU, "$MENU_ID=?", arrayOf(id.toString()))
    }

    //  Get all menu items including inactive (for admin manage)
    fun getAllMenuItemsAdmin(): List<MenuItemData> {
        val db = readableDatabase
        val cur = db.query(TBL_MENU, null, null, null, null, null, "$MENU_CATEGORY, $MENU_NAME")
        val list = mutableListOf<MenuItemData>()
        while (cur.moveToNext()) list.add(curToMenu(cur))
        cur.close()
        return list
    }
}
