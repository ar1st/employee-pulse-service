export function formatDateTime(dateTime) {
  // dateTime is "2025,3,22,9,0"
  const [year, month, day, hour, minute] = dateTime.map(Number);

  const date = new Date(year, month - 1, day, hour, minute);

  return date.toLocaleString('en', {
    day: 'numeric',
    month: 'long',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
}
