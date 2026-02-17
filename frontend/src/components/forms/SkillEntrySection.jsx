import {Button, Col, FormGroup, Input, Label, Row, Spinner, Table} from "reactstrap";
import Select from 'react-select';
import {
    GET_SKILLS_BY_ORGANIZATION_URL,
  SEARCH_SKILLS_URL,
  GENERATE_SKILL_ENTRIES_URL
} from "../../lib/api/apiUrls.js";
import {axiosGet, axiosPost} from "../../lib/api/client.js";
import {useEffect, useState, useMemo, useRef} from "react";
import useCatch from "../../lib/api/useCatch.js";
import { useOrganization } from "../../context/OrganizationContext.jsx";

export default function SkillEntrySection({ rawText, onRawTextChange, skillEntries, onSkillEntriesChange }) {
  const {cWrapper} = useCatch();
  const { selectedOrganization } = useOrganization();

  const [organizationSkills, setOrganizationSkills] = useState([]);
  const [searchedSkills, setSearchedSkills] = useState([]);
  const [searchingSkills, setSearchingSkills] = useState(false);
  const [skillSearchTerm, setSkillSearchTerm] = useState('');
  const [selectedSkillId, setSelectedSkillId] = useState('');
  const [skillRating, setSkillRating] = useState('');
  const [generatingSkills, setGeneratingSkills] = useState(false);
  const searchTimeoutRef = useRef(null);

  useEffect(() => {
    const orgId = selectedOrganization?.value;

    cWrapper(() =>
      axiosGet(GET_SKILLS_BY_ORGANIZATION_URL(orgId))
        .then((skillsResponse) => {
          setOrganizationSkills(skillsResponse.data);
        })
    );
  }, [cWrapper, selectedOrganization]);

  // Search skills when user types (with debouncing)
  useEffect(() => {
    // Clear previous timeout
    if (searchTimeoutRef.current) {
      clearTimeout(searchTimeoutRef.current);
    }

    // If search term is empty, clear searched skills and show organization skills
    if (!skillSearchTerm.trim()) {
      setSearchedSkills([]);
      return;
    }

    // Debounce the search - wait 300ms after user stops typing
    setSearchingSkills(true);
    searchTimeoutRef.current = setTimeout(() => {
      cWrapper(() =>
        axiosGet(SEARCH_SKILLS_URL(skillSearchTerm.trim()))
          .then((response) => {
            setSearchedSkills(response.data || []);
          })
          .finally(() => setSearchingSkills(false))
      );
    }, 300);

    // Cleanup timeout on unmount or when search term changes
    return () => {
      if (searchTimeoutRef.current) {
        clearTimeout(searchTimeoutRef.current);
      }
    };
  }, [skillSearchTerm, cWrapper]);

  // Determine which skills to show: searched skills if searching, otherwise organization skills
  const skillsToShow = useMemo(() => {
    if (skillSearchTerm.trim()) {
      return searchedSkills;
    }
    return organizationSkills;
  }, [skillSearchTerm, searchedSkills, organizationSkills]);

  const skillOptions = useMemo(
    () =>
      skillsToShow.map((skill) => ({
        value: skill.id,
        label: `${skill.name} (ID: ${skill.id})`
      })),
    [skillsToShow]
  );

  const selectedSkillOption = useMemo(
    () =>
      skillOptions.find(
        (opt) => opt.value?.toString() === selectedSkillId
      ) || null,
    [skillOptions, selectedSkillId]
  );

  const handleAddSkillEntry = () => {
    if (!selectedSkillId || !skillRating) {
      return;
    }

    const skill = skillsToShow.find(s => s.id === parseInt(selectedSkillId));
    if (!skill) return;

    // Check if skill already added
    if (skillEntries.some(entry => entry.skillId === skill.id)) {
      return;
    }

    const newEntry = {
      skillId: skill.id,
      skillName: skill.name,
      rating: parseFloat(skillRating),
      tempId: Date.now() // Temporary ID for display
    };

    onSkillEntriesChange([...skillEntries, newEntry]);
    setSelectedSkillId('');
    setSkillRating('');
    setSkillSearchTerm('');
  };

  const handleSkillChange = (selected) => {
    setSelectedSkillId(selected ? selected.value.toString() : '');
  };

  const handleRemoveSkillEntry = (tempId) => {
    onSkillEntriesChange(skillEntries.filter(entry => entry.tempId !== tempId));
  };

  const handleGenerateSkillEntries = () => {
    if (!rawText || !rawText.trim()) {
      return;
    }

    setGeneratingSkills(true);

    cWrapper(() =>
      axiosPost(GENERATE_SKILL_ENTRIES_URL(), { rawText: rawText })
        .then((response) => {
          const generatedEntries = response.data || [];

          const newEntries = generatedEntries.map((entry) => ({
            skillId: entry.skillId,
            skillName: entry.skillName,
            rating: entry.rating,
            tempId: Date.now() + Math.random() // Unique temporary ID
          }));

          // Filter out duplicates (skills already in the list)
          const existingSkillIds = new Set(skillEntries.map(e => e.skillId));
          const uniqueNewEntries = newEntries.filter(e => !existingSkillIds.has(e.skillId));

          onSkillEntriesChange([...skillEntries, ...uniqueNewEntries]);
        })
        .finally(() => setGeneratingSkills(false))
    );
  };

  return (
    <>
      <FormGroup>
        <Label for="rawText">Performance Review Text</Label>
        <Input
          type="textarea"
          name="rawText"
          id="rawText"
          rows="6"
          value={rawText}
          onChange={onRawTextChange}
          placeholder="Enter the performance review text. This will be analyzed to extract skills and ratings."
        />
        <div className="mt-2">
          <Button
            type="button"
            color="info"
            onClick={handleGenerateSkillEntries}
            disabled={!rawText || !rawText.trim() || generatingSkills}
          >
            {generatingSkills ? (
              <>
                <Spinner size="sm" className="me-2"/>
                Generating...
              </>
            ) : (
              <>
                <i className="bi bi-magic me-2"></i>
                Generate Skill Entries
              </>
            )}
          </Button>
          {!rawText || !rawText.trim() ? (
            <small className="form-text text-muted ms-2">
              Enter performance review text to generate skill entries
            </small>
          ) : null}
        </div>
      </FormGroup>

      <FormGroup>
        <Label><strong>Skill Entries</strong></Label>
        <p className="text-muted">Add skill entries manually to this performance review.</p>

        <Row className="mb-3">
          <Col md={6}>
            <Label for="skillSearch">Select Skill *</Label>
            <Select
              inputId="skillSearch"
              options={skillOptions}
              value={selectedSkillOption}
              onChange={handleSkillChange}
              onInputChange={(value, actionMeta) => {
                if (actionMeta.action === 'input-change') {
                  setSkillSearchTerm(value);
                }
              }}
              isLoading={searchingSkills}
              isClearable
              placeholder="Type to search skills..."
              menuPortalTarget={typeof document !== 'undefined' ? document.body : null}
              styles={{
                menuPortal: (base) => ({ ...base, zIndex: 9999 })
              }}
            />
          </Col>
          <Col md={6}>
            <Label for="skillRating">Rating *</Label>
            <Input
              type="number"
              id="skillRating"
              min="0"
              max="5"
              step="0.1"
              value={skillRating}
              onChange={(e) => setSkillRating(e.target.value)}
              placeholder="0-5"
              disabled={!selectedSkillId}
            />
          </Col>
        </Row>

        <Button
          type="button"
          color="success"
          onClick={handleAddSkillEntry}
          disabled={!selectedSkillId || !skillRating || skillEntries.some(e => e.skillId === parseInt(selectedSkillId))}
          className="mb-3"
        >
          <i className="bi bi-plus-circle me-2"></i>
          Add Skill Entry
        </Button>

        {skillEntries.length > 0 && (
          <div className="mt-3">
            <h5>Added Skill Entries</h5>
            <Table striped bordered hover responsive>
              <thead>
              <tr>
                <th>Skill Name</th>
                <th>Skill ID</th>
                <th>Rating</th>
                <th>Actions</th>
              </tr>
              </thead>
              <tbody>
              {skillEntries.map((entry) => (
                <tr key={entry.tempId}>
                  <td>{entry.skillName}</td>
                  <td>{entry.skillId}</td>
                  <td>{entry.rating.toFixed(1)}</td>
                  <td>
                    <Button
                      type="button"
                      color="danger"
                      size="sm"
                      onClick={() => handleRemoveSkillEntry(entry.tempId)}
                    >
                      <i className="bi bi-trash"></i> Remove
                    </Button>
                  </td>
                </tr>
              ))}
              </tbody>
            </Table>
          </div>
        )}
      </FormGroup>
    </>
  );
}

