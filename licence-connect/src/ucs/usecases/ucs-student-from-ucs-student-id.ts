import { UCSLicenceSource } from '../domain/ucs-licences-source';
import { UCSStudentId, UCSStudent } from '../domain/ucs-types';

export class UCSStudentFromUCSStudentId {
  constructor() {}

  public async execute(
    ucsLicencesSource: UCSLicenceSource,
    ucsStudentId: UCSStudentId,
  ): Promise<UCSStudent> {
    const ucsStudent = ucsLicencesSource.getUCSStudentFromId(ucsStudentId);
    return ucsStudent;
  }
}
