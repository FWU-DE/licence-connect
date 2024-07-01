import { Licence } from './licence';
import { StudentId } from './student';
import { LicenceSource } from './licence-source';

export interface LicenceRepository extends LicenceSource {
  addLicencesForStudentId(studentId: StudentId, licencesToAdd: Licence[]);
  removeLicencesForStudentId(studentId: StudentId, licencesToAdd: Licence[]);
  removeAllLicencesForStudentId(studentId: StudentId);
}
