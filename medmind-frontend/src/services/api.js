// src/services/api.js
import axios from "axios";

const BASE_URL =
  import.meta.env.VITE_API_BASE?.trim() || "http://localhost:8081";

const api = axios.create({
  baseURL: BASE_URL,
  withCredentials: false,
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error("API Error:", error);
    return Promise.reject(error);
  }
);

export default {
  get: (url, opts) => api.get(url, opts),
  post: (url, data, opts) => api.post(url, data, opts),
  put: (url, data, opts) => api.put(url, data, opts),
  delete: (url, opts) => api.delete(url, opts),

  // ✅ New helper
  sendTestNotification: () => api.post("/api/sendTestNotification"),
};
