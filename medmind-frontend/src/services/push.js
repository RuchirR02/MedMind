// // src/services/push.js
// import api from './api'; // your axios wrapper

// /**
//  * Convert a Base64 public key to a Uint8Array for pushManager
//  */
// function urlBase64ToUint8Array(base64String) {
//   const padding = '='.repeat((4 - (base64String.length % 4)) % 4);
//   const base64 = (base64String + padding)
//     .replace(/\-/g, '+')
//     .replace(/_/g, '/');

//   const rawData = atob(base64);
//   const outputArray = new Uint8Array(rawData.length);

//   for (let i = 0; i < rawData.length; ++i) {
//     outputArray[i] = rawData.charCodeAt(i);
//   }
//   return outputArray;
// }

// /**
//  * Subscribe user for push notifications
//  */
// export async function subscribeForPush() {
//   try {
//     // 1. Ask backend for public VAPID key
//     const { data } = await api.get('/api/vapidPublicKey');
//     const vapidPublicKey =
//       typeof data === 'string' ? JSON.parse(data).vapidPublicKey : data.vapidPublicKey;

//     if (!vapidPublicKey) {
//       throw new Error('No VAPID public key received from backend.');
//     }

//     // 2. Register Service Worker
//     const registration = await navigator.serviceWorker.register('/serviceWorker.js');

//     // 3. Request notification permission
//     const permission = await Notification.requestPermission();
//     if (permission !== 'granted') {
//       throw new Error('Permission not granted for Notification');
//     }

//     // 4. Subscribe to push service
//     const subscription = await registration.pushManager.subscribe({
//       userVisibleOnly: true,
//       applicationServerKey: urlBase64ToUint8Array(vapidPublicKey),
//     });

//     // 5. Send subscription to backend
//     await api.post('/api/subscribe', subscription);

//     alert('Push notifications enabled!');
//   } catch (err) {
//     console.error('Push subscription failed:', err);
//     alert('Failed to enable push notifications. Check console for details.');
//   }
// }
