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

export const formatDate = (date) => {
  if (!date) return 'N/A'
  return new Date(date).toLocaleDateString()
}

export const formatDateForInput = (date) => {
  if (!date) return '';
  const d = new Date(date);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};