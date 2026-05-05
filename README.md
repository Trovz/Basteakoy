# 🍕 Basteakoy — Android Point-of-Sale System

Basteakoy is a native Android point-of-sale (POS) application built with Kotlin. It is designed for small food businesses selling items across multiple categories (Fruits, Pizza, Soda, Snacks), with full support for cashier management, cart checkout, and monthly sales reporting — all stored locally using SQLite.

---

## ✨ Features

### 👤 Authentication
- Splash screen on app launch
- Cashier login with ID and password
- New cashier self-registration
- Forgot password flow
- Separate admin login for privileged access

### 🏠 Home / Menu
- Browse menu items organized by category (All, Fruits, Pizza, Soda, Snacks)
- Live search bar to filter items by name
- Supports sized items (S / M / L) and regular (fixed-price) items
- Tap to add items directly to the cart with a toast confirmation

### 🛒 Cart & Checkout
- Add, remove, and adjust item quantities
- Cart persists within the session via `CartManager`
- Enter cash tendered to compute change automatically
- Generates a ticket number for each completed transaction

### 📦 Orders
- View a history of all completed orders/transactions
- Each order shows ticket number, cashier, items, and total

### 📊 Sales Dashboard
- Monthly sales summary showing total revenue and total items sold
- Best-selling items ranked by quantity sold
- Data sourced directly from the SQLite database

### 🔧 Admin — Product Management
- Add new menu items with name, description, category, image, and pricing
- Edit or delete existing items (soft-delete by default, hard-delete for admins)
- View all items including inactive ones

### 👤 Profile
- View logged-in cashier's details: name, ID, shift, email, phone

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | XML Layouts, Fragments, RecyclerView |
| Navigation | Fragment transactions via `MainActivity` |
| Database | SQLite via `SQLiteOpenHelper` |
| Concurrency | Kotlin Coroutines |
| Lifecycle | AndroidX Lifecycle (`lifecycle-runtime-ktx`) |
| Build | Gradle (Kotlin DSL) with version catalogs |

---

## 📁 Project Structure

```
app/src/main/java/com/example/fragment/
│
├── Activities
│   ├── SplashActivity.kt          # App entry point
│   ├── LoginActivity.kt           # Cashier login
│   ├── SignUpActivity.kt          # New cashier registration
│   ├── ForgotPasswordActivity.kt  # Password recovery
│   └── MainActivity.kt            # Main dashboard host
│
├── Fragments
│   ├── HomeFragment.kt            # Menu browsing + add to cart
│   ├── OrderFragment.kt           # Order/transaction history
│   ├── SalesFragment.kt           # Monthly sales report
│   ├── ProfileFragment.kt         # Logged-in employee profile
│   └── AddProductFragment.kt      # Admin: add/edit menu items
│
├── Adapters
│   ├── MenuAdapter.kt             # Menu item list
│   ├── CartAdapter.kt             # Cart item list
│   ├── OrderAdapter.kt            # Order history list
│   ├── SalesAdapter.kt            # Sales list
│   ├── MonthlySalesAdapter.kt     # Best-sellers list
│   └── ManageProductAdapter.kt    # Admin product list
│
├── Data & Logic
│   ├── DatabaseHelper.kt          # SQLite schema, CRUD, queries
│   ├── Models.kt                  # Data classes (MenuItemData, Employee, etc.)
│   ├── CartManager.kt             # In-session cart state
│   ├── CartItem.kt                # Cart item model
│   ├── MenuItem.kt                # Menu item model
│   ├── ProductManager.kt          # Product business logic
│   └── basteakoy_db.kt            # DB utilities
```

---

## 🗄️ Database Schema

The app uses a local SQLite database (`basteakoy.db`) with four tables:

**`employees`** — Stores cashier and admin accounts (cashier ID, password, name, role, email, phone, shift).

**`menu_items`** — Stores all menu items with optional size-based pricing (S/M/L) or a single regular price, plus an `is_active` flag for soft deletes.

**`transactions`** — One record per completed sale (ticket number, cashier ID, total, cash tendered, change, date).

**`transaction_items`** — Line items for each transaction (item name, size, price, quantity, subtotal).

### Default Seed Data

On first install the database is seeded with:
- **Cashier:** ID `1001`, password `basteakoy123`
- **Admin:** ID `??????`, password `????????`
- 15 menu items across Fruits, Pizza, Soda, and Snacks categories

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- Android SDK 24+ (min SDK)
- A physical device or emulator running Android 7.0+

### Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/Trovz/Basteakoy.git
   ```

2. Open the project in Android Studio.

3. Let Gradle sync and download dependencies.

4. Run the app on your device or emulator (▶ Run).

### Default Login Credentials

| Role | Cashier ID | Password |
|---|---|---|
| Cashier | `1001` | `basteakoy123` |
| Admin | `????` | `???????` |

---

## 📋 Requirements

- **Min SDK:** 24 (Android 7.0 Nougat)
- **Target SDK:** 36
- **Compile SDK:** 36
- **AGP:** 9.1.1
- **Kotlin** with Java 11 compatibility

---

## 📄 License

This project is intended for educational purposes. Feel free to fork and adapt for your own use.
