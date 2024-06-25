import { Licence } from 'domain/licence';
import { LicenceRepository } from 'domain/licence-repository';
import { StudentId } from 'domain/student';

export type AddLicencesForStudentUseCase = (
  studentId: StudentId,
  licencesToAdd: Licence[],
) => void;

export const createSimpleAddLicenceForStudentUseCase: (
  LicenceRepository,
) => AddLicencesForStudentUseCase =
  (licenceRepository: LicenceRepository) =>
  (studentId: StudentId, licencesToAdd: Licence[]) => {
    licenceRepository.addLicencesForStudentId(studentId, licencesToAdd);
  };
