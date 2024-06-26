import { Licence } from 'domain/licence';
import { LicenceRepository } from 'domain/licence-repository';
import { StudentId } from 'domain/student';

export type AddLicencesForStudentUseCase = (
  studentId: StudentId,
  licencesToAdd: Licence[],
) => void;

export const createAddLicenceForStudentUseCase: (
  LicenceRepository,
) => AddLicencesForStudentUseCase =
  (licenceRepository: LicenceRepository) =>
  (studentId: StudentId, licencesToAdd: Licence[]) => {
    licenceRepository.addLicencesForStudentId(studentId, licencesToAdd);
  };
