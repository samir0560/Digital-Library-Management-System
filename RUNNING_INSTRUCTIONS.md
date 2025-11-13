# Library Management System - Running Instructions

## ✅ All Issues Fixed

### Fixed Issues:
1. ✅ All model classes (Book, User, Issue, Renewal, Reservation, Review, FinePayment, ActivityLog) created
2. ✅ All repositories properly configured for MongoDB
3. ✅ All services implemented with error handling
4. ✅ All controllers created with proper security
5. ✅ Admin dashboard updated with statistics and charts
6. ✅ Student dashboard updated with all features
7. ✅ All HTML templates created
8. ✅ Security configuration updated for new endpoints

## 🚀 How to Run the Application

### Prerequisites:
1. **Java 17** (or higher) installed
2. **MongoDB** installed and running on `localhost:27017`
3. **Maven** (optional, IDE should handle it)

### Steps to Run:

1. **Start MongoDB:**
   ```bash
   # On Windows (if installed as service, it should be running)
   # Or start manually:
   mongod
   
   # Verify MongoDB is running:
   # Open a browser and go to: http://localhost:27017
   ```

2. **Configure MongoDB Connection:**
   - The application is configured to connect to: `mongodb://localhost:27017/librarydb`
   - Database: `librarydb`
   - Collections will be created automatically when you first save data

3. **Run the Application:**
   - **Using IDE (IntelliJ/Eclipse):**
     - Right-click on `LibraryManagementSystemApplication.java`
     - Select "Run" or "Debug"
   
   - **Using Maven:**
     ```bash
     mvn spring-boot:run
     ```
   
   - **Using Java:**
     ```bash
     java -jar target/library-management-system-0.0.1-SNAPSHOT.jar
     ```

4. **Access the Application:**
   - Open browser and go to: `http://localhost:8086`
   - Default admin credentials:
     - Username: `admin`
     - Password: `admin123`

## 📋 Features Available

### Admin Features:
- ✅ Dashboard with statistics and charts
- ✅ Add/Edit/Delete books
- ✅ Issue books to students
- ✅ Return books (with fine calculation)
- ✅ View overdue books
- ✅ View activity logs
- ✅ User management (activate/deactivate)
- ✅ Reports and CSV export
- ✅ View returned books history

### Student Features:
- ✅ View issued books
- ✅ Renew books (max 2 times, 7 days extension)
- ✅ Pay fines
- ✅ Submit book reviews and ratings
- ✅ Update profile (email, phone, address, student ID)
- ✅ Change password

## 🔧 Troubleshooting

### Issue: MongoDB Connection Error
**Solution:** 
- Ensure MongoDB is running
- Check if port 27017 is not blocked
- Verify MongoDB connection string in `application.properties`

### Issue: Port 8086 Already in Use
**Solution:**
- Change port in `application.properties`: `server.port=8087`
- Or stop the application using port 8086

### Issue: Compilation Errors
**Solution:**
- Refresh/rebuild the project in your IDE
- Clean and rebuild: `mvn clean install`
- Ensure all dependencies are downloaded

### Issue: Admin Login Not Working
**Solution:**
- Admin user is created automatically on first login
- Credentials are in `application.properties`
- Check MongoDB connection - admin user needs to be created in database

## 📝 First Time Setup

1. Start MongoDB
2. Run the application
3. Go to: `http://localhost:8086`
4. Click "Sign Up" to create a student account OR
5. Login with admin credentials (admin/admin123)
6. Admin user will be automatically created in MongoDB on first login

## 🎯 Testing the Features

1. **Login as Admin:**
   - Username: `admin`
   - Password: `admin123`

2. **Add a Book:**
   - Go to "Add Book" tab
   - Fill in title, author, quantity
   - Click "Save Book"

3. **Create a Student:**
   - Go to signup page
   - Create a student account

4. **Issue a Book:**
   - Go to "Issue Book" tab
   - Select book and student
   - Click "Issue Book"

5. **Test Renewal:**
   - Login as student
   - Click "Renew" on an issued book
   - Can renew up to 2 times

6. **Test Fine Payment:**
   - Return a book after due date (will calculate fine)
   - Login as student
   - Click "Pay Fine"
   - Enter payment details

7. **Test Reviews:**
   - Return a book
   - Login as student
   - Click "Review" on returned book
   - Submit rating and comment

## 🔐 Security

- All passwords are hashed with BCrypt
- Role-based access control (ADMIN/STUDENT)
- Spring Security enabled
- CSRF protection enabled
- Session management configured

## 📊 Database

- **Database:** MongoDB
- **Database Name:** `librarydb`
- **Collections:**
  - `books` - Book information
  - `users` - User accounts
  - `issues` - Book issue transactions
  - `renewals` - Book renewal records
  - `reservations` - Book reservations
  - `reviews` - Book reviews and ratings
  - `fine_payments` - Fine payment records
  - `activity_logs` - System activity logs

## ✨ All Features Working

✅ Dashboard Statistics  
✅ Book Management  
✅ Issue/Return Books  
✅ Book Renewal  
✅ Book Reservation  
✅ Fine Management  
✅ Payment Tracking  
✅ Book Reviews & Ratings  
✅ User Profile Management  
✅ Activity Logging  
✅ User Management  
✅ Reports & Analytics  
✅ CSV Export  

The application is ready to run! 🚀

