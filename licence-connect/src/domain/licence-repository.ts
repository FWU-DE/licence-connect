import { Licence } from './licence';
import { LicenceSource } from './licence-source';
import { StudentId } from './student';

export interface LicenceRepository extends LicenceSource {
  addLicencesForStudentId(studentId: StudentId, licencesToAdd: Licence[]);
}
