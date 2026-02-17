import {useState} from "react";
import {Card, CardBody, CardHeader, Collapse, FormGroup, Label, Input, Row, Col, Button} from "reactstrap";
import {useEmployeeFilter} from "./EmployeeFilterContext.jsx";
import DateInput from "../../forms/DateInput.jsx";

export default function EmployeeFilters() {
  const {filterValues, setFilterValues, resetFilters} = useEmployeeFilter();
  const [isOpen, setIsOpen] = useState(false);

  const handleFilterChange = (field, value) => {
    setFilterValues({[field]: value});
  };

  const hasActiveFilters = Object.values(filterValues).some(f => f);

  return (
    <Card className="mt-3" style={{backgroundColor: '#f8f9fa'}}>
      <CardHeader
        style={{cursor: 'pointer'}}
        onClick={() => setIsOpen(!isOpen)}
      >
        <div className="d-flex justify-content-between align-items-center">
          <h6 className="mb-0">Filters</h6>
          <i className={`bi bi-chevron-${isOpen ? 'up' : 'down'}`}></i>
        </div>
      </CardHeader>
      <Collapse isOpen={isOpen}>
        <CardBody>
          <Row className="align-items-end g-3">
            <Col md={1} style={{maxWidth: '80px'}}>
              <FormGroup className="mb-0">
                <Input
                  id="filter-id"
                  type="text"
                  placeholder="ID"
                  value={filterValues.id}
                  onChange={(e) => handleFilterChange('id', e.target.value)}
                  bsSize="sm"
                  style={{maxWidth: '100%'}}
                />
              </FormGroup>
            </Col>
            <Col md={2}>
              <FormGroup className="mb-0">
                <Input
                  id="filter-firstName"
                  type="text"
                  placeholder="First Name"
                  value={filterValues.firstName}
                  onChange={(e) => handleFilterChange('firstName', e.target.value)}
                  bsSize="sm"
                />
              </FormGroup>
            </Col>
            <Col md={2}>
              <FormGroup className="mb-0">
                <Input
                  id="filter-lastName"
                  type="text"
                  placeholder="Last Name"
                  value={filterValues.lastName}
                  onChange={(e) => handleFilterChange('lastName', e.target.value)}
                  bsSize="sm"
                />
              </FormGroup>
            </Col>
            <Col md={2}>
              <FormGroup className="mb-0">
                <Input
                  id="filter-email"
                  type="text"
                  placeholder="Email"
                  value={filterValues.email}
                  onChange={(e) => handleFilterChange('email', e.target.value)}
                  bsSize="sm"
                />
              </FormGroup>
            </Col>
            <Col md={2}>
              <FormGroup className="mb-0">
                <Input
                  id="filter-department"
                  type="text"
                  placeholder="Department"
                  value={filterValues.department}
                  onChange={(e) => handleFilterChange('department', e.target.value)}
                  bsSize="sm"
                />
              </FormGroup>
            </Col>
            <Col md={2}>
              <FormGroup className="mb-0">
                <Input
                  id="filter-occupation"
                  type="text"
                  placeholder="Occupation"
                  value={filterValues.occupation}
                  onChange={(e) => handleFilterChange('occupation', e.target.value)}
                  bsSize="sm"
                />
              </FormGroup>
            </Col>

          </Row>

          <Row className="align-items-end g-3">
            <Col md={2}>
              <FormGroup className="mb-0">
                <Label for="filter-hireDateStart" className="small mb-1">Hire Date Start</Label>
                <DateInput
                  id="filter-hireDateStart"
                  value={filterValues.hireDateStart}
                  onChange={(e) => handleFilterChange('hireDateStart', e.target.value)}
                  bsSize="sm"
                />
              </FormGroup>
            </Col>
            <Col md={2}>
              <FormGroup className="mb-0">
                <Label for="filter-hireDateEnd" className="small mb-1">Hire Date End</Label>
                <DateInput
                  id="filter-hireDateEnd"
                  value={filterValues.hireDateEnd}
                  onChange={(e) => handleFilterChange('hireDateEnd', e.target.value)}
                  bsSize="sm"
                />
              </FormGroup>
            </Col>
          </Row>
          <Row className="align-items-end g-3">
            <Col md={2} className="d-flex align-items-end">
              {hasActiveFilters && (
                <Button
                  color="secondary"
                  size="sm"
                  onClick={resetFilters}
                  className="w-100"
                >
                  <i className="bi bi-x-circle me-1"></i>
                  Clear Filters
                </Button>
              )}
            </Col>
          </Row>

        </CardBody>
      </Collapse>
    </Card>
  );
}

