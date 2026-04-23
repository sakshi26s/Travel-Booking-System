# Travel-Booking-System
Travel Booking System using Java and MySQL with automated pricing, booking, and review management.

## Overview

The Travel Booking System is a database-driven application that simulates a real-world platform for browsing travel packages, making bookings, processing payments, and managing reviews. The system emphasizes automation and data integrity using core DBMS concepts.

---

## Key Features

* Automated package cost calculation using triggers
* Transaction-safe booking and payment handling via stored procedures
* Loyalty-based discount system using SQL functions
* Real-time package availability checks
* Automatic rating updates from user reviews
* Loyalty points updated after successful payments

---

## Database Design

The system includes entities such as User, Customer, Admin, Destination, Hotel, Transport, Package, Booking, Payment, and Review.


---

## Tech Stack

* Java
* MySQL
* JDBC, SQL, DBMS concepts (Triggers, Procedures, Functions)

---

## How to Run

1. Set up the database using the provided SQL script
2. Update database credentials in the Java file
3. Compile and run:
   javac TravelBookingSystem.java
   java TravelBookingSystem

---

## Summary

This project demonstrates the use of advanced DBMS features to build an automated and consistent backend system, where critical operations like cost calculation and data updates are handled efficiently at the database level.
