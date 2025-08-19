function generateId() {
  return "user-" + Math.random().toString(36).substr(2, 9);
}

export function getUserId() {
  let id = localStorage.getItem("userId");
  if (!id) {
    id = generateId();
    localStorage.setItem("userId", id);
  }
  return id;
}
