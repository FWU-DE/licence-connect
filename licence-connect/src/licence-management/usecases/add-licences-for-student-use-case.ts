import { LicenceRepository } from '@licences/domain/licence-repository';
import { StudentId } from '@licences/domain/student';
import { Licence } from '@licences/domain/licence';

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
