# MediCare - Hospital Management System

A desktop Hospital Management System built with **Java (Swing GUI)** and **MySQL**, designed with a modern, responsive UI and an layered MVC/Repository architecture.

---

## Features

- **Role-Based Authentication**: Secure login supporting **Admin**, **Receptionist**, and **Doctor** roles with database authentication and offline fallback.
- **Patient Management**:
  - Register new patients with automatic sequential ID generation (`P-101`, `P-102`, etc.).
  - Real-time search/filter by name, phone, or ID.
  - View full patient medical history, contact info, blood group, and diagnosis.
  - Click-to-edit table row selection for instant updating or deletion.
- **Doctor Directory**:
  - Manage hospital doctors, visiting hours, departments, room numbers, and consultation fees.
  - Filter directory by medical specialization (Cardiology, Neurology, Pediatrics, Orthopedics, etc.).
- **Appointment Scheduling**:
  - Schedule patient visits with specific doctors and time slots.
  - Track appointment statuses (`Scheduled`, `Completed`, `Cancelled`) with custom colored status tags.
  - Search and filter appointments by patient, doctor, or status.

---

## Project Structure

```
Hospital Management System/
│── .vscode/
│   └── settings.json           # VS Code Java configuration
│── bin/                        # Compiled bytecode (git-ignored)
│── lib/
│   └── mysql-connector-j-26.7.0.jar  # MySQL JDBC Driver
│── src/
│   └── hospital/
│       │── App.java            # Main application entry point
│       │── model/              # Data models (Patient, Doctor, Appointment)
│       │── repository/         # Database access layer (CRUD queries)
│       │── services/           # Business logic layer
│       │── ui/                 # Swing GUI (LoginFrame, MainFrame, Panels)
│       └── utils/              # DbConnection helper & table initializer
│── .gitignore
│── README.md
│── run.bat                     # One-click Windows runner
└── schema.sql                  # MySQL database schema & sample seed data
```

---

## Prerequisites & Database Setup

1. **Java Development Kit (JDK)**: JDK 17 or later installed.
2. **MySQL / XAMPP**: Ensure MySQL service is running on `localhost:3306`.
3. **Database Setup**:
   - The database name is `hopital_db` (or `hospital_db`).
   - Default credentials configured in `DbConnection.java`:
     - **Username**: `root`
     - **Password**: `1234`
   - You can import [`schema.sql`](schema.sql) in **phpMyAdmin** or MySQL Workbench, or allow the application to auto-generate all required tables on first launch.

---

## Default Login Credentials

| Role | Username | Password |
| :--- | :--- | :--- |
| **Admin** | `admin` | `admin123` |
| **Receptionist** | `staff` | `staff123` |
| **Doctor** | `doctor` | `doc123` |

---

## How to Run

### Option 1: Quick Run (Windows)
Double-click [`run.bat`](run.bat) or run in your terminal:
```cmd
run.bat
```

### Option 2: Run in VS Code
1. Open the project folder in **Visual Studio Code**.
2. Open [`src/hospital/App.java`](src/hospital/App.java).
3. Click the **Run** button at the top-right corner (or press `F5`).

### Option 3: Manual Command Line
```powershell
javac -cp "lib/*" -sourcepath src -d bin src\hospital\*.java src\hospital\model\*.java src\hospital\repository\*.java src\hospital\services\*.java src\hospital\ui\*.java src\hospital\utils\*.java
java -cp "bin;lib/*" hospital.App
```
