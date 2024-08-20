import { Licence } from './licence';
import { StudentId } from './student';

export interface LicenceRepository {
  addLicencesForStudentId(studentId: StudentId, licencesToAdd: Licence[]): void;
  removeLicencesForStudentId(
    studentId: StudentId,
    licencesToAdd: Licence[],
  ): void;
  removeAllLicencesForStudentId(studentId: StudentId): void;
}
