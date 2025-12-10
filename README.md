📚 Digital Library Management System

A Spring Boot-based web application designed to manage a digital library efficiently.
It allows admins to manage books, users, and transactions, while students/users can browse, borrow, and return books online.

The system provides an easy-to-use interface, REST APIs, and a secure backend architecture.

🚀 Overview

This project automates the traditional library system with a digital interface where:

Admins can add, update, delete, and issue books

Users can view available books, borrow/return them, and check due dates

All data is securely stored and managed through Spring Boot + Database

🧩 Features
👩‍🏫 Admin Module

Add, edit, and delete book records

Manage registered users

View issued and returned book details

Track overdue books and penalties

👨‍🎓 User Module

Register / Login

Search and view book details

Borrow and return books

View issued books and due dates

⚙️ System Features

RESTful API architecture

Role-based access control (Admin/User)

Spring Data JPA for database operations

Validation for book issuance limits and availability

Exception handling and logging

| Layer                   | Technologies Used                     |
| ----------------------- | ------------------------------------- |
| **Backend Framework**   | Spring Boot                           |
| **Language**            | Java 17+                              |
| **Database**            | MySQL / H2 / PostgreSQL               |
| **ORM**                 | Spring Data JPA / Hibernate           |
| **Frontend (optional)** | HTML, CSS, JS, Thymeleaf              |
| **Build Tool**          | Maven                                 |
| **Security**            | Spring Security (JWT / Session-based) |
