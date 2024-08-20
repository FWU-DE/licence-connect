import { Licence } from '@vidis/domain/licence';
import { StudentId } from '@vidis/domain/student';
import { LicenceRepository } from '@vidis/domain/licence-repository';

export type RemoveLicencesForStudentUseCase = (
  studentId: StudentId,
  licencesToAdd?: Licence[],
) => void;

export const createRemoveLicenceForStudentUseCase: (
  licenceRepositor: LicenceRepository,
) => RemoveLicencesForStudentUseCase =
  (licenceRepository: LicenceRepository) =>
  (studentId: StudentId, licencesToRemove?: Licence[]) => {
    if (licencesToRemove) {
      licenceRepository.removeLicencesForStudentId(studentId, licencesToRemove);
    } else {
      licenceRepository.removeAllLicencesForStudentId(studentId);
    }
  };
