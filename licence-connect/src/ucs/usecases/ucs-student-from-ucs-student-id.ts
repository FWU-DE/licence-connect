import { UcsStudentRepository } from '../domain/ucs-student-repository';
import { UcsStudentId, UcsStudent } from '../domain/ucs-types';

export class UCSStudentFromUCSStudentId {
  constructor() {}

  public async execute(
    ucsLicencesSource: UcsStudentRepository,
    ucsStudentId: UcsStudentId,
  ): Promise<UcsStudent> {
    const ucsStudent = ucsLicencesSource.getUCSStudentFromId(ucsStudentId);
    return ucsStudent;
  }
}
