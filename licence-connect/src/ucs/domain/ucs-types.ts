export type UCSLicence = string;
export type AvailableUCSLicences = UCSLicence[];

export type UCSClass = {
  name: string;
  id: string;
  licenses?: AvailableUCSLicences;
};

export type UCSWorkgroup = {
  name: string;
  id: string;
  licenses?: AvailableUCSLicences;
};
export type UCSRoles = string;

export type UCSStudentId = string;

export type UCSStudentContext = {
  licenses?: AvailableUCSLicences;
  classes: UCSClass[];
  workgroups: UCSWorkgroup[];
  school_authority: string;
  school_identifier: string;
  school_name: string;
  roles: UCSRoles[];
};

export type UCSStudent = {
  id: string;
  first_name: string;
  last_name: string;
  licenses?: AvailableUCSLicences;
  context: {
    [key: string]: UCSStudentContext;
  };
};

export type ResponseFromUCS = {
  'http://www.bildungslogin.de/licenses': UCSStudent;
};
