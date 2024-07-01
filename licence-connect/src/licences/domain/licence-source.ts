import { AvailableLicences } from './licence';
import { StudentId } from './student';

export interface LicenceSource {
  getLicencesForStudentId(studentId: StudentId): AvailableLicences;
}
