import { useState } from 'react';
import { Card, CardBody, CardHeader, Collapse } from 'reactstrap';

function OrganizationSkillTimelineSection() {
  const [isOpen, setIsOpen] = useState(true);

  return (
    <Card className="mb-4">
      <CardHeader 
        style={{ cursor: 'pointer' }}
        onClick={() => setIsOpen(!isOpen)}
      >
        <div className="d-flex justify-content-between align-items-center">
          <div>
            <h4 className="mb-0">Organization & Department Skill Timeline</h4>
            <p className="mb-0 text-muted small">
              View timeline data for skills across an organization or specific department.
            </p>
          </div>
          <i className={`bi bi-chevron-${isOpen ? 'up' : 'down'}`}></i>
        </div>
      </CardHeader>
      <Collapse isOpen={isOpen}>
        <CardBody>
          {/* Timeline form and visualization will be implemented here */}
          <p className="text-muted">Timeline form coming soon...</p>
        </CardBody>
      </Collapse>
    </Card>
  );
}

export default OrganizationSkillTimelineSection;

