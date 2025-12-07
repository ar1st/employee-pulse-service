import {Modal, ModalHeader, ModalBody, ModalFooter, Button, Spinner} from "reactstrap";

export default function ConfirmModal({
  isOpen,
  onToggle,
  onConfirm,
  onCancel,
  title = "Confirm Action",
  message = "Are you sure you want to proceed?",
  itemDetails = null,
  detailsLabel = "Details",
  loading = false,
  loadingText = "Processing...",
  confirmButtonText = "Confirm",
  cancelButtonText = "Cancel",
  confirmButtonColor = "primary",
  showWarning = false,
  warningMessage = "This action cannot be undone."
}) {
  const handleCancel = () => {
    if (onCancel) {
      onCancel();
    } else if (onToggle) {
      onToggle();
    }
  };

  const handleConfirm = () => {
    if (onConfirm) {
      onConfirm();
    }
  };

  return (
    <Modal isOpen={isOpen} toggle={handleCancel}>
      <ModalHeader toggle={handleCancel}>{title}</ModalHeader>
      <ModalBody>
        <p>{message}</p>
        {itemDetails && (
          <div className="mt-3">
            <strong>{detailsLabel}:</strong>
            <ul className="mt-2">
              {Object.entries(itemDetails).map(([key, value]) => (
                <li key={key}>
                  <strong>{key}:</strong> {value || 'N/A'}
                </li>
              ))}
            </ul>
          </div>
        )}
        {showWarning && (
          <p className="text-danger mt-3">
            <strong>{warningMessage}</strong>
          </p>
        )}
      </ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={handleCancel} disabled={loading}>
          {cancelButtonText}
        </Button>
        <Button color={confirmButtonColor} onClick={handleConfirm} disabled={loading}>
          {loading ? (
            <>
              <Spinner size="sm" className="me-2" />
              {loadingText}
            </>
          ) : (
            confirmButtonText
          )}
        </Button>
      </ModalFooter>
    </Modal>
  );
}

