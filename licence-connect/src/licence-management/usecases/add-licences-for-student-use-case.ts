import { LicenceRepository } from '@vidis/domain/licence-repository';
import { StudentId } from '@vidis/domain/student';
import { Licence } from '@vidis/domain/licence';

export type AddLicencesForStudentUseCase = (
  studentId: StudentId,
  licencesToAdd: Licence[],
) => void;

export const createAddLicenceForStudentUseCase: (
  licenceRepository: LicenceRepository,
) => AddLicencesForStudentUseCase =
  (licenceRepository: LicenceRepository) =>
  (studentId: StudentId, licencesToAdd: Licence[]) => {
    licenceRepository.addLicencesForStudentId(studentId, licencesToAdd);
  };
