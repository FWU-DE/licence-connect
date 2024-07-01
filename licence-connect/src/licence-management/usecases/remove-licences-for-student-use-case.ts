import { Licence } from '@licences/domain/licence';
import { StudentId } from '@licences/domain/student';
import { LicenceRepository } from '@licences/domain/licence-repository';

export type RemoveLicencesForStudentUseCase = (
  studentId: StudentId,
  licencesToAdd?: Licence[],
) => void;

export const createRemoveLicenceForStudentUseCase: (
  LicenceRepository,
) => RemoveLicencesForStudentUseCase =
  (licenceRepository: LicenceRepository) =>
  (studentId: StudentId, licencesToRemove?: Licence[]) => {
    if (licencesToRemove) {
      licenceRepository.removeLicencesForStudentId(studentId, licencesToRemove);
    } else {
      licenceRepository.removeAllLicencesForStudentId(studentId);
    }
  };
