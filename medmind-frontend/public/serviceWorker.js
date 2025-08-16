self.addEventListener('push', function(event) {
  let payload = {};
  try {
    payload = event.data ? event.data.json() : {};
  } catch (e) {
    console.warn('Push payload is not JSON:', e);
    payload = { title: 'MedMind', body: 'You have a reminder' };
  }

  const title = payload.title || 'MedMind Reminder';
  const options = {
    body: payload.body || 'Time to take your medicine',
    icon: '/icons/medicine-icon.png',
    badge: '/icons/medicine-badge.png',
    data: payload.data || {},
    vibrate: [200, 100, 200] // optional, for attention
  };

  event.waitUntil(
    self.registration.showNotification(title, options)
  );
});

self.addEventListener('notificationclick', function(event) {
  event.notification.close();

  event.waitUntil(
    clients.matchAll({ type: 'window', includeUncontrolled: true }).then(clientList => {
      if (clientList.length > 0) {
        // Focus the most recent tab
        return clientList[0].focus();
      }
      return clients.openWindow('/');
    })
  );
});
