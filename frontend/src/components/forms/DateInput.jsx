import { Input } from "reactstrap";
import { useRef } from "react";

/**
 * Reusable date input that:
 * - Uses the native HTML date input (type="date") so browsers with support show their picker
 * - Adds an explicit calendar button that calls showPicker() when available
 * - Falls back to focus/click if showPicker is not implemented
 */
export default function DateInput(props) {
  const inputRef = useRef(null);

  const handleOpenPicker = () => {
    const input = inputRef.current;
    if (!input) return;

    if (typeof input.showPicker === "function") {
      input.showPicker();
    } else {
      input.focus();
      if (typeof input.click === "function") {
        input.click();
      }
    }
  };

  const { style, ...rest } = props;

  return (
    <div className="position-relative date-input-wrapper">
      <Input
        type="date"
        innerRef={inputRef}
        style={{ paddingRight: "2.5rem", ...(style || {}) }}
        {...rest}
      />
      <button
        type="button"
        onClick={handleOpenPicker}
        className="btn btn-outline-secondary border-0 position-absolute top-50 end-0 translate-middle-y"
        style={{ right: "0.25rem", padding: "0.25rem 0.5rem" }}
        aria-label="Open date picker"
      >
        <i className="bi bi-calendar-event" />
      </button>
    </div>
  );
}


