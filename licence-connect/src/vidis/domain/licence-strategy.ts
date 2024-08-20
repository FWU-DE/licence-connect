import { AvailableLicences } from './licence';
import { StudentId } from './student';

export interface LicenceStrategy {
  getLicencesForStudentId(studentId: StudentId): AvailableLicences;
}
