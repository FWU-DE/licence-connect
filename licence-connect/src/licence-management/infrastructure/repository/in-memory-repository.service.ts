import { Injectable } from '@nestjs/common';
import { LicenceRepository } from '@vidis/domain/licence-repository';
import { StudentId } from '@vidis/domain/student';
import { AvailableLicences, Licence } from '@vidis/domain/licence';
import { LicenceStrategy } from '@vidis/domain/licence-strategy';

@Injectable()
export class InMemoryRepositoryService
  implements LicenceRepository, LicenceStrategy
{
  private licences: Map<StudentId, AvailableLicences>;

  constructor() {
    this.licences = new Map();
  }

  public addLicencesForStudentId(
    studentId: StudentId,
    licencesToAdd: Licence[],
  ) {
    const availableLicences = this.collectLicencesForStudentId(studentId);
    this.licences.set(studentId, [...availableLicences, ...licencesToAdd]);
  }

  public getLicencesForStudentId(studentId: StudentId): AvailableLicences {
    return this.collectLicencesForStudentId(studentId);
  }

  public removeLicencesForStudentId(
    studentId: StudentId,
    licencesToRemove: Licence[],
  ) {
    if (this.licences.has(studentId)) {
      const idsToRemove = licencesToRemove.map(
        (licence) => licence.license_code,
      );

      const availableLicences = this.collectLicencesForStudentId(
        studentId,
      ).filter((licence) => !idsToRemove.includes(licence.license_code));
      this.licences.set(studentId, availableLicences);
    }
  }
  public removeAllLicencesForStudentId(studentId: StudentId) {
    if (this.licences.has(studentId)) {
      this.licences.delete(studentId);
    }
  }

  private collectLicencesForStudentId(studentId: StudentId): AvailableLicences {
    return this.licences.get(studentId) ?? [];
  }
}
