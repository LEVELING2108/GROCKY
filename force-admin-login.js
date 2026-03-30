// Run this in browser console to force login as admin
// Open http://localhost:3000, press F12, go to Console tab, paste this code:

fetch('/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ email: 'admin@grocky.com', password: 'admin123' })
})
.then(r => r.json())
.then(data => {
  localStorage.setItem('token', data.data.token);
  localStorage.setItem('user', JSON.stringify({
    id: 0,
    userId: data.data.userId,
    email: data.data.email,
    name: data.data.name,
    role: 'ADMIN'  // Force admin role
  }));
  alert('Logged in as ADMIN! Redirecting...');
  window.location.href = '/admin/inventory';
})
.catch(e => alert('Error: ' + e.message));
