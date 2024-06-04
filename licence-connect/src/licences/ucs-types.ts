import { License } from './licence-types';

export type UCSClass = { name: string; id: string; licenses?: License[] };
export type UCSWorkgroup = { name: string; id: string; licenses?: License[] };
export type UCSRoles = string;

export type UCSStudent = {
  id: string;
  first_name: string;
  last_name: string;
  licenses?: License[];
  context: {
    [key: string]: {
      licenses?: License[];
      classes: UCSClass[];
      workgroups: UCSWorkgroup[];
      school_authority: string;
      school_identifier: string;
      school_name: string;
      roles: UCSRoles[];
    };
  };
};

export type MVStudent = {
  'http://www.bildungslogin.de/licenses': UCSStudent;
};
