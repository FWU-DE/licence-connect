import { Injectable } from '@nestjs/common';
import { AvailableLicences, Licence } from 'domain/licence';
import { LicenceRepository } from 'domain/licence-repository';
import { StudentId } from 'domain/student';

@Injectable()
export class InMemoryRepositoryService implements LicenceRepository {
  private licences: Map<StudentId, AvailableLicences>;

  constructor() {
    this.licences = new Map();
  }

  public addLicencesForStudentId(studentId: string, licencesToAdd: Licence[]) {
    const availableLicences = this.licences.get(studentId) ?? [];
    this.licences.set(studentId, [...availableLicences, ...licencesToAdd]);
  }

  public getLicencesForStudentId(userId: StudentId): AvailableLicences {
    return this.licences.get(userId) ?? [];
  }
}
