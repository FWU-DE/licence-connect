export type UcsLicence = string;
export type AvailableUcsLicences = UcsLicence[];

export type UcsClass = {
  name: string;
  id: string;
  licenses?: AvailableUcsLicences;
};

export type UcsWorkgroup = {
  name: string;
  id: string;
  licenses?: AvailableUcsLicences;
};
export type UcsRoles = string;

export type UcsStudentId = string;

export type UcsStudentContext = {
  licenses?: AvailableUcsLicences;
  classes?: UcsClass[];
  workgroups?: UcsWorkgroup[];
  school_authority?: string;
  school_identifier?: string;
  school_name?: string;
  roles: UcsRoles[];
};

export type UcsStudent = {
  id: string;
  first_name?: string;
  last_name?: string;
  licenses?: AvailableUcsLicences;
  context: {
    [key: string]: UcsStudentContext;
  };
};
