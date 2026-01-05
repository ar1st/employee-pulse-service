import { Card, CardBody, FormGroup, Label, Input, Row, Col, Button } from "reactstrap";
import { useEmployeeFilter } from "./EmployeeFilterContext.jsx";

export default function EmployeeFilters() {
  const { filterValues, setFilterValues, resetFilters } = useEmployeeFilter();

  const handleFilterChange = (field, value) => {
    setFilterValues({ [field]: value });
  };

  const hasActiveFilters = Object.values(filterValues).some(f => f);

  return (
    <Card className="mt-3" style={{backgroundColor: '#f8f9fa'}}>
      <CardBody>
        <Row className="align-items-end g-3">
          <Col md={1}>
            <FormGroup className="mb-0">
              <Label for="filter-id" className="small mb-1">ID</Label>
              <Input
                id="filter-id"
                type="text"
                placeholder="Filter ID"
                value={filterValues.id}
                onChange={(e) => handleFilterChange('id', e.target.value)}
                bsSize="sm"
              />
            </FormGroup>
          </Col>
          <Col md={1}>
            <FormGroup className="mb-0">
              <Label for="filter-firstName" className="small mb-1">First Name</Label>
              <Input
                id="filter-firstName"
                type="text"
                placeholder="Filter First Name"
                value={filterValues.firstName}
                onChange={(e) => handleFilterChange('firstName', e.target.value)}
                bsSize="sm"
              />
            </FormGroup>
          </Col>
          <Col md={1}>
            <FormGroup className="mb-0">
              <Label for="filter-lastName" className="small mb-1">Last Name</Label>
              <Input
                id="filter-lastName"
                type="text"
                placeholder="Filter Last Name"
                value={filterValues.lastName}
                onChange={(e) => handleFilterChange('lastName', e.target.value)}
                bsSize="sm"
              />
            </FormGroup>
          </Col>
          <Col md={2}>
            <FormGroup className="mb-0">
              <Label for="filter-email" className="small mb-1">Email</Label>
              <Input
                id="filter-email"
                type="text"
                placeholder="Filter Email"
                value={filterValues.email}
                onChange={(e) => handleFilterChange('email', e.target.value)}
                bsSize="sm"
              />
            </FormGroup>
          </Col>
          <Col md={1}>
            <FormGroup className="mb-0">
              <Label for="filter-hireDate" className="small mb-1">Hire Date</Label>
              <Input
                id="filter-hireDate"
                type="text"
                placeholder="Filter Date"
                value={filterValues.hireDate}
                onChange={(e) => handleFilterChange('hireDate', e.target.value)}
                bsSize="sm"
              />
            </FormGroup>
          </Col>
          <Col md={2}>
            <FormGroup className="mb-0">
              <Label for="filter-department" className="small mb-1">Department</Label>
              <Input
                id="filter-department"
                type="text"
                placeholder="Filter Department"
                value={filterValues.department}
                onChange={(e) => handleFilterChange('department', e.target.value)}
                bsSize="sm"
              />
            </FormGroup>
          </Col>
          <Col md={2}>
            <FormGroup className="mb-0">
              <Label for="filter-occupation" className="small mb-1">Occupation</Label>
              <Input
                id="filter-occupation"
                type="text"
                placeholder="Filter Occupation"
                value={filterValues.occupation}
                onChange={(e) => handleFilterChange('occupation', e.target.value)}
                bsSize="sm"
              />
            </FormGroup>
          </Col>
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
    </Card>
  );
}

