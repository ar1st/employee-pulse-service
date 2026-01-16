import {useState} from "react";
import {Card, CardBody, CardHeader, Collapse, FormGroup, Label, Input, Row, Col, Button} from "reactstrap";
import {usePerformanceReviewFilter} from "./PerformanceReviewFilterContext.jsx";

export default function PerformanceReviewFilters() {
  const {filterValues, setFilterValues, resetFilters} = usePerformanceReviewFilter();
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
                  id="filter-employee"
                  type="text"
                  placeholder="Employee"
                  value={filterValues.employee}
                  onChange={(e) => handleFilterChange('employee', e.target.value)}
                  bsSize="sm"
                />
              </FormGroup>
            </Col>
            <Col md={2}>
              <FormGroup className="mb-0">
                <Input
                  id="filter-reporter"
                  type="text"
                  placeholder="Reporter"
                  value={filterValues.reporter}
                  onChange={(e) => handleFilterChange('reporter', e.target.value)}
                  bsSize="sm"
                />
              </FormGroup>
            </Col>
            <Col md={1}>
              <FormGroup className="mb-0">
                <Input
                  id="filter-ratingMin"
                  type="number"
                  placeholder="Min Rating"
                  value={filterValues.overallRatingMin}
                  onChange={(e) => handleFilterChange('overallRatingMin', e.target.value)}
                  bsSize="sm"
                  min="0"
                  max="10"
                  step="0.1"
                />
              </FormGroup>
            </Col>
            <Col md={1}>
              <FormGroup className="mb-0">
                <Input
                  id="filter-ratingMax"
                  type="number"
                  placeholder="Max Rating"
                  value={filterValues.overallRatingMax}
                  onChange={(e) => handleFilterChange('overallRatingMax', e.target.value)}
                  bsSize="sm"
                  min="0"
                  max="10"
                  step="0.1"
                />
              </FormGroup>
            </Col>
          </Row>

          <Row className="align-items-end g-3">
            <Col md={2}>
              <FormGroup className="mb-0">
                <Label for="filter-reviewDateStart" className="small mb-1">Review Date Start</Label>
                <Input
                  id="filter-reviewDateStart"
                  type="date"
                  value={filterValues.reviewDateStart}
                  onChange={(e) => handleFilterChange('reviewDateStart', e.target.value)}
                  bsSize="sm"
                />
              </FormGroup>
            </Col>
            <Col md={2}>
              <FormGroup className="mb-0">
                <Label for="filter-reviewDateEnd" className="small mb-1">Review Date End</Label>
                <Input
                  id="filter-reviewDateEnd"
                  type="date"
                  value={filterValues.reviewDateEnd}
                  onChange={(e) => handleFilterChange('reviewDateEnd', e.target.value)}
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

