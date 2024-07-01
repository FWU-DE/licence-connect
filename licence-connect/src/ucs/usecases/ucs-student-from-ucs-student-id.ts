import { UCSLicenceSource } from '../domain/ucs-licences-source';
import { UCSStudentId, UCSStudent } from '../domain/ucs-types';

export class UCSStudentFromUCSStudentId {
  constructor() {}

  public execute(
    ucsLicencesSource: UCSLicenceSource,
    ucsStudentId: UCSStudentId,
  ): UCSStudent {
    const ucsStudent = ucsLicencesSource.getUCSStudentFromId(ucsStudentId);
    return ucsStudent;
  }
}
