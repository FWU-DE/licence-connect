import { StudentId } from './student';
import { AvailableLicences } from './licence';

export interface LicenceStrategy {
  getLicencesForStudentId(studentId: StudentId): AvailableLicences;
}
