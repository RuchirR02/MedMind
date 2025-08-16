// scripts/generateVapidKeys.js
const webpush = require('web-push');

const vapidKeys = webpush.generateVAPIDKeys();

console.log('==== Your VAPID Keys ====');
console.log('Public Key:', vapidKeys.publicKey);
console.log('Private Key:', vapidKeys.privateKey);
