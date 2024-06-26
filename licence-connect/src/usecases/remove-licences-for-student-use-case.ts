import { Licence } from 'domain/licence';
import { LicenceRepository } from 'domain/licence-repository';
import { StudentId } from 'domain/student';

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
