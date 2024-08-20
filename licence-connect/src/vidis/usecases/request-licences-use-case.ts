import { StudentId } from '@vidis/domain/student';
import { AvailableLicences } from '@vidis/domain/licence';
import { LicenceStrategy } from '@vidis/domain/licence-strategy';

export type RequestLicencesForStudentUseCase = (
  studentId: StudentId,
  applicationId: string,
) => AvailableLicences;

export const createRequestLicencesForStudentUseCase =
  (licenceRepository: LicenceStrategy) =>
  (studentId: StudentId, _applicationId: string) => {
    const licencesForUser =
      licenceRepository.getLicencesForStudentId(studentId);

    return licencesForUser;
  };
