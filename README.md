# 📱 Orbit Foods Order Management App

A Kotlin-based Android application designed to digitize bakery order operations for Orbit Foods. The app replaces manual order sheets with a structured, database-driven system and generates professional PDF copies for both customers and office use.

---

## 🚀 Features

### 🔐 Authentication

* User Registration with email validation (@gmail, @yahoo, custom domains)
* Email verification via OTP
* Secure Login system
* Forgot Password with OTP reset flow

---

### 📦 Product Management

* Add Product (পণ্যের নাম in Bangla)
* Update Product
* Delete Product
* View Product List

---

### 🧾 Order Management

* Create Order with:

  * Date (তারিখ - Bangla format)
  * Customer Name (নাম)
  * Address (ঠিকানা)
* Select products from database (dropdown)
* Input Quantity (পরিমাণ) and Rate (দর)
* Auto calculation of Amount (টাকা)
* Dynamic multi-product entry
* Auto serial number (ক্রমিক নং)

---

### 📊 Order Features

* View Orders
* Update Orders
* Delete Orders
* Real-time total calculation (মোট টাকা)

---

### 📄 PDF Generation

* Generate two types of order sheets:

  * Customer Copy
  * Office Copy
* Bangla formatted layout
* Auto filename:

  ```
  [type]_[serial]_[date].pdf
  ```
* Includes:

  * Company logo
  * Watermark branding
  * Signature fields
  * Dual-column layout (auto-adjust)

---

## 🎨 UI & Design

* XML-based Android UI
* Clean professional layout
* Bangla-friendly interface
* Color Scheme:

  * Background: #FAF8F5
  * Table/Header: #5A939E
  * Text: #333333

---

## ☁️ Backend

* Firebase Authentication
* Firebase Firestore (NoSQL database)

---

## 🛠️ Tech Stack

* Kotlin
* XML Layouts
* Firebase Auth
* Firestore Database
* PDF Generation (Android)

---

## 📂 Project Structure (High-Level)

```
app/
 ├── ui/
 ├── auth/
 ├── product/
 ├── order/
 ├── pdf/
 ├── model/
 └── utils/
```

---

## ⚙️ Setup Instructions

1. Clone the repository:

   ```
   git clone https://github.com/your-username/orbit-foods-order-app.git
   ```

2. Open in Android Studio

3. Connect Firebase:

   * Add `google-services.json`
   * Enable Authentication (Email)
   * Enable Firestore

4. Run the project on emulator/device

---

## 🔒 Security Notes

* Password must be minimum 6 characters
* Email verification required before login
* OTP-based password reset

---

## 📌 Future Improvements

* Admin panel
* Role-based access
* Offline mode
* Bluetooth printer support

---

## 👤 Author

Developed as an **internal business** solution for my **Family Businees Bakery Factory order Sheet Automation.**

---


Just tell me 👍
