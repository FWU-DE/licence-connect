import { Injectable } from '@nestjs/common';
import { LicenceRepository } from '@licences/domain/licence-repository';
import { StudentId } from '@licences/domain/student';
import { AvailableLicences, Licence } from '@licences/domain/licence';

@Injectable()
export class InMemoryRepositoryService implements LicenceRepository {
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
