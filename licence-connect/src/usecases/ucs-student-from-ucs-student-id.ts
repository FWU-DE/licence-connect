import { IUCSLicenceSource } from '@domain/ucs/ucs-licences-source';
import { UCSStudent, UCSStudentId } from '@domain/ucs/ucs-types';

export class UCSStudentFromUCSStudentId {
  constructor() {}

  public execute(
    ucsLicencesSource: IUCSLicenceSource,
    ucsStudentId: UCSStudentId,
  ): UCSStudent {
    const ucsStudent = ucsLicencesSource.getUCSStudentFromId(ucsStudentId);
    return ucsStudent;
  }
}
