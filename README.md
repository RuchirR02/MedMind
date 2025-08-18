# 💊 MedMind

MedMind is a full-stack application that helps users **manage their medicines and prescriptions**.  
It allows you to add medicines manually or upload a prescription (OCR-powered via Gemini API).  
Users also receive **push notifications** as reminders to take their medicines.

---

## 🚀 Features
- Add medicines manually with name and time.
- Upload a prescription image → OCR extracts medicine name and time automatically.
- Provides a Schedule View 
- Push notifications via **Web Push API + Service Workers**.

---

## 📂 Project Structure
medmind-backend(demo)/ # Spring Boot backend
├── src/main/java/com/example/demo/
│ ├── controller/ # REST controllers
│ ├── model/ # JPA entities
│ ├── repository/ # Spring Data repositories
│ ├── service/ # Business logic + Gemini API integration
│ └── util/ # Prescription parsing helpers
└── src/main/resources/

medmind-frontend/ # React frontend (Vite + Tailwind)
├── public/ # serviceWorker.js
├── src/
 ├── components/ # MedicineForm, PrescriptionUpload, ScheduleView
 ├── services/ # API calls
 ├── utils/ # pushUtils
 ├── App.jsx
 └── main.jsx

